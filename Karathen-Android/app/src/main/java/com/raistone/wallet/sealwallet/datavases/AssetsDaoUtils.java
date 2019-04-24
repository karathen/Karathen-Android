package com.raistone.wallet.sealwallet.datavases;

import android.text.TextUtils;

import com.google.zxing.common.DefaultGridSampler;
import com.raistone.wallet.sealwallet.entity.AssetsInfo;
import com.raistone.wallet.sealwallet.entity.DataBean_Table;
import com.raistone.wallet.sealwallet.entity.ETHWallet;
import com.raistone.wallet.sealwallet.entity.ETHWallet_Table;
import com.raistone.wallet.sealwallet.entity.MessageEvent;
import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.greenrobot.eventbus.EventBus;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class AssetsDaoUtils {


    /**
     * 插入新资产
     */
    public static void insertNewAssets(AssetsInfo.DataBean bean, String address) {
        //updateCurrent(-1);
        bean.setWalletAddress(address);
        bean.insert();
    }

    /**
     * 查询地址资产信息
     *
     * @param
     * @return
     */
    public static AssetsInfo.DataBean findSignAssets(String tokenAddress,String walletAddress) {
        AssetsInfo.DataBean dataBean = SQLite.select().from(AssetsInfo.DataBean.class).where(DataBean_Table.tokenAddress.eq(tokenAddress)).and(DataBean_Table.walletAddress.eq(walletAddress)).querySingle();

        return dataBean;
    }

    /**
     * 根据地址查询所有资产
     *
     * @param
     * @return
     */
    public static List<AssetsInfo.DataBean> findAllAssetsByAddress(String walletAddress) {
        List<AssetsInfo.DataBean> dataBeans = SQLite.select().from(AssetsInfo.DataBean.class).where(DataBean_Table.walletAddress.eq(walletAddress)).queryList();
        return dataBeans;
    }


    /**
     * 查询所有资产
     *
     * @param
     * @return
     */
    public static List<AssetsInfo.DataBean> findAllAssetsByCoinType(String type,String walletAddress) {
        List<AssetsInfo.DataBean> dataBeans = SQLite.select().from(AssetsInfo.DataBean.class).where(DataBean_Table.coinType.eq(type)).and(DataBean_Table.walletAddress.eq(walletAddress)).queryList();

        return dataBeans;
    }

    /**
     * 查询所有资产
     *
     * @param
     * @return
     */
    public static List<AssetsInfo.DataBean> findAllAssets() {
        List<AssetsInfo.DataBean> dataBeans = SQLite.select().from(AssetsInfo.DataBean.class).queryList();

        return dataBeans;
    }

    /**
     * 模糊查询 用来筛选首页资产
     *
     * @param like
     * @return
     */
    public static List<AssetsInfo.DataBean> likeFind(String like, String walletAddress) {
        List<AssetsInfo.DataBean> dataBeans = SQLite.select().from(AssetsInfo.DataBean.class).where(DataBean_Table.asset_name.like(like + '%'))
                .or(DataBean_Table.tokenName.like(like + '%')).or(DataBean_Table.tokenAddress.like(like + '%'))
                .or(DataBean_Table.tokenSynbol.like(like + '%')).queryList();


        List<AssetsInfo.DataBean> newsData = new ArrayList<>();
        for (int i = 0; i < dataBeans.size(); i++) {
            AssetsInfo.DataBean bean = dataBeans.get(i);
            if(!TextUtils.isEmpty(bean.getWalletAddress())) {
                if (bean.getWalletAddress().equals(walletAddress)) {
                    newsData.add(bean);
                }
            }
        }

        return newsData;
    }


    /**
     * 根据tokenAddress 查询单条数据
     */
    public static AssetsInfo.DataBean findAssterByTokenAddress(String tokenAddress) {
        AssetsInfo.DataBean bean = SQLite.select().from(AssetsInfo.DataBean.class).where(DataBean_Table.tokenAddress.eq(tokenAddress)).querySingle();
        return bean;
    }

    /**
     * 删除全部数据
     */
    public static void deleteAllAssets() {
        Delete.table(AssetsInfo.DataBean.class);
    }

    /**
     * 根据钱包地址查询对应资产列表
     *
     * @param address
     * @return
     */
    public static List<AssetsInfo.DataBean> findAllAssetsByWalletAddress(String address) {
        List<AssetsInfo.DataBean> dataBeans = SQLite.select().from(AssetsInfo.DataBean.class).where(DataBean_Table.walletAddress.eq(address)).queryList();
        return dataBeans;
    }


    /**
     * 更新Balance 根据address
     *
     * @param address 钱包地址
     * @return
     */
    public static void updateBalance(AssetsInfo.DataBean bean, String address) {
        SQLite.update(AssetsInfo.DataBean.class).set(DataBean_Table.balance.eq(bean.getBalance())).where(DataBean_Table.walletAddress.eq(address)).and(DataBean_Table.tokenAddress.eq(bean.getTokenAddress())).async().execute();
    }

    /**
     * 更新Balance 根据address
     *
     * @param address 钱包地址
     * @return
     */
    public static void updateBalance(BigDecimal balance, String tokenAddress, String address) {
        SQLite.update(AssetsInfo.DataBean.class).set(DataBean_Table.balance.eq(balance)).where(DataBean_Table.walletAddress.eq(address)).and(DataBean_Table.tokenAddress.eq(tokenAddress)).async().execute();
    }

    /**
     * 价格 根据address
     *
     * @param address 钱包地址
     * @return
     */
    public static void updatePrice(AssetsInfo.DataBean bean, String address) {
        SQLite.update(AssetsInfo.DataBean.class).set(DataBean_Table.price.eq(bean.getPrice()), DataBean_Table.priceUSD.eq(bean.getPriceUSD())).where(DataBean_Table.walletAddress.eq(address)).and(DataBean_Table.tokenAddress.eq(bean.getTokenAddress())).async().execute();

        //EventBus.getDefault().post(new MessageEvent(address));

    }

    /**
     * 筛选数据 用来做首页筛选
     *
     * @param filterParam 筛选参数 ERC-20  0 所有   1 ERC20  2 ERC721
     * @param assetsZero  是否隐藏资产为零的   true 不隐藏      false 隐藏
     * @param address     钱包地址
     */
    public static List<AssetsInfo.DataBean> filterData(int filterParam, boolean assetsZero, String address) {


        List<AssetsInfo.DataBean> dataBeans = new ArrayList<>();

        //查询所有
        if (filterParam == 0) {
            if (!assetsZero) {
                dataBeans = SQLite.select().from(AssetsInfo.DataBean.class).where(DataBean_Table.walletAddress.eq(address)).queryList();
            } else {
                dataBeans = SQLite.select().from(AssetsInfo.DataBean.class).where(DataBean_Table.balance.notEq(BigDecimal.ZERO)).and(DataBean_Table.walletAddress.eq(address)).queryList();
            }

            //ERC-20
        } else if (filterParam == 1) {
            dataBeans = hideZeroData(assetsZero, "ERC-20", address);

            //ERC-721
        } else if (filterParam == 2) {
            dataBeans = hideZeroData(assetsZero, "ERC-721", address);
        }

        boolean conEth = isConEth(dataBeans);

        if(!conEth){
            AssetsInfo.DataBean dataBean = SQLite.select().from(AssetsInfo.DataBean.class).where(DataBean_Table.walletAddress.eq(address)).and(DataBean_Table.coinType.eq("ETH")).querySingle();
            dataBeans.add(0,dataBean);
        }


        return dataBeans;
    }




    /**
     * 筛选NEO数据 用来做首页筛选
     *
     * @param filterParam 筛选参数 ERC-20  0 所有   1 ERC20  2 ERC721
     * @param assetsZero  是否隐藏资产为零的   true 不隐藏      false 隐藏
     * @param address     钱包地址
     */
    public static List<AssetsInfo.DataBean> filterDataNeo(int filterParam, boolean assetsZero, String address) {


        List<AssetsInfo.DataBean> dataBeans = new ArrayList<>();

        //查询所有
        if (filterParam == 0) {
            if (!assetsZero) {
                dataBeans = SQLite.select().from(AssetsInfo.DataBean.class).where(DataBean_Table.walletAddress.eq(address)).queryList();
            } else {
                dataBeans = SQLite.select().from(AssetsInfo.DataBean.class).where(DataBean_Table.balance.notEq(BigDecimal.ZERO)).and(DataBean_Table.walletAddress.eq(address)).queryList();
            }

            //ERC-20
        }

        if (filterParam == 1) {
            dataBeans = hideZeroData(assetsZero, "NEO", address);

            //ERC-721
        }
        if (filterParam == 2) {
            dataBeans = hideZeroData(assetsZero, "GAS", address);
        }

        if (filterParam == 3) {
            dataBeans = hideZeroData(assetsZero, "NEP-5", address);
        }


        return dataBeans;
    }


    /**
     * 筛选ONT数据 用来做首页筛选
     *
     * @param filterParam 筛选参数 ERC-20  0 所有   1 ERC20  2 ERC721
     * @param assetsZero  是否隐藏资产为零的   true 不隐藏      false 隐藏
     * @param address     钱包地址
     */
    public static List<AssetsInfo.DataBean> filterDataOnt(int filterParam, boolean assetsZero, String address) {


        List<AssetsInfo.DataBean> dataBeans = new ArrayList<>();

        //查询所有
        if (filterParam == 0) {
            if (!assetsZero) {
                dataBeans = SQLite.select().from(AssetsInfo.DataBean.class).where(DataBean_Table.walletAddress.eq(address)).queryList();
            } else {
                dataBeans = SQLite.select().from(AssetsInfo.DataBean.class).where(DataBean_Table.balance.notEq(BigDecimal.ZERO)).and(DataBean_Table.walletAddress.eq(address)).queryList();
            }

            //ERC-20
        }

        if (filterParam == 1) {
            dataBeans = hideZeroData(assetsZero, "ONT", address);

            //ERC-721
        }
        if (filterParam == 2) {
            dataBeans = hideZeroData(assetsZero, "ONG", address);
        }


        return dataBeans;
    }
    /**
     * 是否隐藏资产为零的数据
     * isZero true 就是隐藏为零的数据 false 就是查询全部数据
     */
    public static List<AssetsInfo.DataBean> hideZeroData(boolean assetsZero, String filterParam, String address) {
        List<AssetsInfo.DataBean> dataBeans = new ArrayList<>();
        if (assetsZero) {
            dataBeans = SQLite.select().from(AssetsInfo.DataBean.class).where(DataBean_Table.tokenType.eq(filterParam)).and(DataBean_Table.balance.notEq(BigDecimal.ZERO)).and(DataBean_Table.walletAddress.eq(address)).queryList();


            //查询资产不0的数据
        } else {
            dataBeans = SQLite.select().from(AssetsInfo.DataBean.class).where(DataBean_Table.tokenType.eq(filterParam)).and(DataBean_Table.walletAddress.eq(address)).queryList();
        }

        return dataBeans;
    }

    /**
     * 判断列表是否包含ETH
     */
    public static boolean isConEth(List<AssetsInfo.DataBean> dataBeans){
        for (AssetsInfo.DataBean bean: dataBeans) {
            if(bean.getTokenSynbol().equals("ETH")){
                return true;
            }
        }
        return false;
    }

    /**
     * 根据地址删除对应资产
     */
    public static void deleteAssetsByAddress(String walletAddress,String tokenAddress){
        SQLite.delete().from(AssetsInfo.DataBean.class).where(DataBean_Table.tokenAddress.eq(tokenAddress)).and(DataBean_Table.walletAddress.eq(walletAddress)).async().execute();
    }
}
