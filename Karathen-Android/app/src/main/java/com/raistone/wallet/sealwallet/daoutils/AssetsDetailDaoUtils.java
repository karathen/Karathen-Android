package com.raistone.wallet.sealwallet.daoutils;

import android.text.TextUtils;
import com.raistone.wallet.sealwallet.WalletApplication;
import com.raistone.wallet.sealwallet.factory.AssetsDeatilInfo;
import com.raistone.wallet.sealwallet.greendao.AssetsDeatilInfoDao;
import com.raistone.wallet.sealwallet.utils.BigDecimalUtils;

import org.greenrobot.greendao.query.QueryBuilder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class AssetsDetailDaoUtils {

      public static   AssetsDeatilInfoDao assetsDeatilInfoDao = WalletApplication.getsInstance().getDaoSession().getAssetsDeatilInfoDao();



    /**
     * 根据链ID 插入新资产
     */
    public static void insertNewAssets(AssetsDeatilInfo bean) {

        String decimal = bean.getTokenDecimal();
        if(!TextUtils.isEmpty(decimal) && !bean.getTokenDecimal().equals("0")) {
            BigDecimal bigDecimal = BigDecimalUtils.div(bean.getBalance(), String.valueOf(Math.pow(10, Double.parseDouble(bean.getTokenDecimal()))), 8);
            bean.setBalance(bigDecimal.toPlainString());
        }else {
            bean.setBalance(bean.getBalance());
        }

        assetsDeatilInfoDao.insertOrReplace(bean);
    }

    /**
     * 查询地址资产信息
     *
     * @param
     * @return
     */
    public static AssetsDeatilInfo findSignAssets(String tokenAddress,String walletAddress,String accountId) {

        AssetsDeatilInfo deatilInfo = assetsDeatilInfoDao.queryBuilder().where(AssetsDeatilInfoDao.Properties.TokenAddress.eq(tokenAddress),AssetsDeatilInfoDao.Properties.WalletAddress.eq(walletAddress),AssetsDeatilInfoDao.Properties.Asset_name.eq(accountId)).unique();

        //QueryBuilder<AssetsDeatilInfo> queryBuilder = assetsDeatilInfoDao.queryBuilder();

        //queryBuilder.and(AssetsDeatilInfoDao.Properties.TokenAddress.eq(tokenAddress),AssetsDeatilInfoDao.Properties.WalletAddress.eq(walletAddress));

        //List<AssetsDeatilInfo> list = queryBuilder.list();

        //AssetsDeatilInfo deatilInfo=queryBuilder.unique();

        return deatilInfo;
    }

    /**
     * 根据地址查询所有资产
     *
     * @param
     * @return
     */
    public static List<AssetsDeatilInfo> findAllAssetsByAddress(String walletAddress) {

        List<AssetsDeatilInfo> deatilInfos = assetsDeatilInfoDao.queryBuilder().where(AssetsDeatilInfoDao.Properties.WalletAddress.eq(walletAddress)).list();

        return deatilInfos;
    }


    /**
     * 查询所有资产
     *
     * @param
     * @return
     */
    public static List<AssetsDeatilInfo> findAllAssetsByCoinType(String type,String walletAddress) {

        List<AssetsDeatilInfo> deatilInfos = assetsDeatilInfoDao.queryBuilder().where(AssetsDeatilInfoDao.Properties.CoinType.eq(type)).where(AssetsDeatilInfoDao.Properties.WalletAddress.eq(walletAddress)).list();

        return deatilInfos;
    }

    /**
     * 查询所有资产
     *
     * @param
     * @return
     */
    public static List<AssetsDeatilInfo> findAllAssets() {

        List<AssetsDeatilInfo> deatilInfos = assetsDeatilInfoDao.loadAll();

        return deatilInfos;
    }

    /**
     * 模糊查询 用来筛选首页资产
     *
     * @param like
     * @return
     */
    public static List<AssetsDeatilInfo> likeFind(String like, String walletAddress) {



        /*List<AssetsInfo.DataBean> dataBeans = SQLite.select().from(AssetsInfo.DataBean.class).where(DataBean_Table.asset_name.like(like + '%'))
                .or(DataBean_Table.tokenName.like(like + '%')).or(DataBean_Table.tokenAddress.like(like + '%'))
                .or(DataBean_Table.tokenSynbol.like(like + '%')).queryList();*/


        List<AssetsDeatilInfo> deatilInfos = assetsDeatilInfoDao.queryBuilder().whereOr(AssetsDeatilInfoDao.Properties.Asset_name.like("%" + like + "%")
                , AssetsDeatilInfoDao.Properties.TokenName.like("%" + like + "%"),
                AssetsDeatilInfoDao.Properties.TokenAddress.like("%" + like + "%"),
                AssetsDeatilInfoDao.Properties.TokenSynbol.like("%" + like + "%")).list();


        List<AssetsDeatilInfo> newsData = new ArrayList<>();
        for (int i = 0; i < deatilInfos.size(); i++) {
            AssetsDeatilInfo bean = deatilInfos.get(i);
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
    public static AssetsDeatilInfo findAssterByTokenAddress(String tokenAddress,String walletAddress) {
        //AssetsInfo.DataBean bean = SQLite.select().from(AssetsInfo.DataBean.class).where(DataBean_Table.tokenAddress.eq(tokenAddress)).querySingle();

        AssetsDeatilInfo deatilInfo = assetsDeatilInfoDao.queryBuilder().where(AssetsDeatilInfoDao.Properties.TokenAddress.eq(tokenAddress)).where(AssetsDeatilInfoDao.Properties.WalletAddress.eq(walletAddress)).unique();

        return deatilInfo;
    }

    /**
     * 根据tokenAddress 查询单条数据
     */
    public static AssetsDeatilInfo findAssterByTokenAddressByAccount(String tokenAddress,String walletAddress,String accountId) {
        //AssetsInfo.DataBean bean = SQLite.select().from(AssetsInfo.DataBean.class).where(DataBean_Table.tokenAddress.eq(tokenAddress)).querySingle();

        AssetsDeatilInfo deatilInfo = assetsDeatilInfoDao.queryBuilder()
                .where(AssetsDeatilInfoDao.Properties.TokenAddress.eq(tokenAddress))
                .where(AssetsDeatilInfoDao.Properties.WalletAddress.eq(walletAddress))
                .where(AssetsDeatilInfoDao.Properties.Asset_name.eq(accountId))
                .unique();

        return deatilInfo;
    }

    /**
     * 删除全部数据
     */
    public static void deleteAllAssets() {

        assetsDeatilInfoDao.detachAll();

        assetsDeatilInfoDao.deleteAll();
    }

    /**
     * 根据钱包地址查询对应资产列表
     *
     * @param address
     * @return
     */
    public static List<AssetsDeatilInfo> findAllAssetsByWalletAddress(String address) {

        List<AssetsDeatilInfo> deatilInfos = assetsDeatilInfoDao.queryBuilder().where(AssetsDeatilInfoDao.Properties.WalletAddress.eq(address)).list();
        return deatilInfos;
    }




    /**
     * 更新Balance 根据address
     *
     * @param address 钱包地址
     * @return
     */
    public static void updateBalance(AssetsDeatilInfo bean, String address) {
        assetsDeatilInfoDao.insertOrReplace(bean);
        //SQLite.update(AssetsInfo.DataBean.class).set(DataBean_Table.balance.eq(bean.getBalance())).where(DataBean_Table.walletAddress.eq(address)).and(DataBean_Table.tokenAddress.eq(bean.getTokenAddress())).async().execute();
    }

    /**
     * 更新Balance 根据address
     *
     * @param address 钱包地址
     * @return
     */
    public static void updateBalance(AssetsDeatilInfo bean,BigDecimal balance, String tokenAddress, String address) {
        assetsDeatilInfoDao.insertOrReplace(bean);
       // SQLite.update(AssetsInfo.DataBean.class).set(DataBean_Table.balance.eq(balance)).where(DataBean_Table.walletAddress.eq(address)).and(DataBean_Table.tokenAddress.eq(tokenAddress)).async().execute();
    }

    /**
     * 价格 根据address
     *
     * @param bean 钱包地址
     * @return
     */
    public static void updatePrice(AssetsDeatilInfo bean) {
        //SQLite.update(AssetsInfo.DataBean.class).set(DataBean_Table.price.eq(bean.getPrice()), DataBean_Table.priceUSD.eq(bean.getPriceUSD())).where(DataBean_Table.walletAddress.eq(address)).and(DataBean_Table.tokenAddress.eq(bean.getTokenAddress())).async().execute();

        //assetsDeatilInfoDao.insertOrReplace(bean);

        assetsDeatilInfoDao.update(bean);

    }

    /**
     * 筛选数据 用来做首页筛选
     *
     * @param filterParam 筛选参数 ERC-20  0 所有   1 ERC20  2 ERC721
     * @param assetsZero  是否隐藏资产为零的   true 不隐藏      false 隐藏
     * @param address     钱包地址
     */
    public static List<AssetsDeatilInfo> filterData(int filterParam, boolean assetsZero, String address) {


        List<AssetsDeatilInfo> dataBeans = new ArrayList<>();

        //查询所有
        if (filterParam == 0) {
            if (!assetsZero) {
                //dataBeans = SQLite.select().from(AssetsInfo.DataBean.class).where(DataBean_Table.walletAddress.eq(address)).queryList();

                dataBeans=assetsDeatilInfoDao.queryBuilder().where(AssetsDeatilInfoDao.Properties.WalletAddress.eq(address)).list();
            } else {
                //dataBeans = SQLite.select().from(AssetsInfo.DataBean.class).where(DataBean_Table.balance.notEq(BigDecimal.ZERO)).and(DataBean_Table.walletAddress.eq(address)).queryList();

                dataBeans=assetsDeatilInfoDao.queryBuilder().where(AssetsDeatilInfoDao.Properties.Balance.notEq("0")).where(AssetsDeatilInfoDao.Properties.WalletAddress.eq(address)).list();
            }

            //ERC-20
        } else if (filterParam == 1) {
            dataBeans = hideZeroData(assetsZero, "ERC-20", address);

            //ERC-721
        } else if (filterParam == 2) {
            dataBeans = hideZeroData(assetsZero, "ERC-721", address);
        }

      /*  boolean conEth = isConEth(dataBeans);

        if(!conEth){
           // AssetsInfo.DataBean dataBean = SQLite.select().from(AssetsInfo.DataBean.class).where(DataBean_Table.walletAddress.eq(address)).and(DataBean_Table.coinType.eq("ETH")).querySingle();

            AssetsDeatilInfo unique = assetsDeatilInfoDao.queryBuilder().where(AssetsDeatilInfoDao.Properties.WalletAddress.eq(address)).where(AssetsDeatilInfoDao.Properties.CoinType.eq("ETH")).unique();
            dataBeans.add(0,unique);
        }
*/

        return dataBeans;
    }




    /**
     * 筛选NEO数据 用来做首页筛选
     *
     * @param filterParam 筛选参数 ERC-20  0 所有   1 ERC20  2 ERC721
     * @param assetsZero  是否隐藏资产为零的   true 不隐藏      false 隐藏
     * @param address     钱包地址
     */
    public static List<AssetsDeatilInfo> filterDataNeo(int filterParam, boolean assetsZero, String address) {


        List<AssetsDeatilInfo> dataBeans = new ArrayList<>();

        //查询所有
        if (filterParam == 0) {
            if (!assetsZero) {
                //dataBeans = SQLite.select().from(AssetsInfo.DataBean.class).where(DataBean_Table.walletAddress.eq(address)).queryList();

                dataBeans= assetsDeatilInfoDao.queryBuilder().where(AssetsDeatilInfoDao.Properties.WalletAddress.eq(address)).list();
            } else {
               // dataBeans = SQLite.select().from(AssetsInfo.DataBean.class).where(DataBean_Table.balance.notEq(BigDecimal.ZERO)).and(DataBean_Table.walletAddress.eq(address)).queryList();

                dataBeans= assetsDeatilInfoDao.queryBuilder().where(AssetsDeatilInfoDao.Properties.Balance.notEq("0")).where(AssetsDeatilInfoDao.Properties.WalletAddress.eq(address)).list();
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
    public static List<AssetsDeatilInfo> filterDataOnt(int filterParam, boolean assetsZero, String address) {


        List<AssetsDeatilInfo> dataBeans = new ArrayList<>();

        //查询所有
        if (filterParam == 0) {
            if (!assetsZero) {
                //dataBeans = SQLite.select().from(AssetsInfo.DataBean.class).where(DataBean_Table.walletAddress.eq(address)).queryList();

                dataBeans=assetsDeatilInfoDao.queryBuilder().where(AssetsDeatilInfoDao.Properties.WalletAddress.eq(address)).list();
            } else {
                //dataBeans = SQLite.select().from(AssetsInfo.DataBean.class).where(DataBean_Table.balance.notEq(BigDecimal.ZERO)).and(DataBean_Table.walletAddress.eq(address)).queryList();

                dataBeans=assetsDeatilInfoDao.queryBuilder().where(AssetsDeatilInfoDao.Properties.Balance.notEq("0")).where(AssetsDeatilInfoDao.Properties.WalletAddress.eq(address)).list();
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
    public static List<AssetsDeatilInfo> hideZeroData(boolean assetsZero, String filterParam, String address) {
        List<AssetsDeatilInfo> dataBeans = new ArrayList<>();
        if (assetsZero) {
            //dataBeans = SQLite.select().from(AssetsInfo.DataBean.class).where(DataBean_Table.tokenType.eq(filterParam)).and(DataBean_Table.balance.notEq(BigDecimal.ZERO)).and(DataBean_Table.walletAddress.eq(address)).queryList();

            dataBeans=assetsDeatilInfoDao.queryBuilder().where(AssetsDeatilInfoDao.Properties.TokenType.eq(filterParam)).where(AssetsDeatilInfoDao.Properties.Balance.notEq("0")).where(AssetsDeatilInfoDao.Properties.WalletAddress.eq(address)).list();


            //查询资产不0的数据
        } else {
            //dataBeans = SQLite.select().from(AssetsInfo.DataBean.class).where(DataBean_Table.tokenType.eq(filterParam)).and(DataBean_Table.walletAddress.eq(address)).queryList();

            dataBeans=assetsDeatilInfoDao.queryBuilder().where(AssetsDeatilInfoDao.Properties.TokenType.eq(filterParam)).where(AssetsDeatilInfoDao.Properties.WalletAddress.eq(address)).list();
        }

        return dataBeans;
    }

    /**
     * 判断列表是否包含ETH
     */
    public static boolean isConEth(List<AssetsDeatilInfo> dataBeans){
        for (AssetsDeatilInfo bean: dataBeans) {
            if(bean.getTokenSynbol().equals("ETH")){
                return true;
            }
        }
        return false;
    }

    /**
     * 根据地址删除对应资产
     */
    public static void deleteAssetsByAddress(AssetsDeatilInfo deatilInfo){
        //SQLite.delete().from(AssetsInfo.DataBean.class).where(DataBean_Table.tokenAddress.eq(tokenAddress)).and(DataBean_Table.walletAddress.eq(walletAddress)).async().execute();
        assetsDeatilInfoDao.delete(deatilInfo);
    }

    /**
     * 该资产是否在本地已有
     */

    public static AssetsDeatilInfo addressIsEx(String walletAddress,String tokenAddress,String accountId){
        AssetsDeatilInfo unique = assetsDeatilInfoDao.queryBuilder().where(AssetsDeatilInfoDao.Properties.WalletAddress.eq(walletAddress),AssetsDeatilInfoDao.Properties.TokenAddress.eq(tokenAddress)).where(AssetsDeatilInfoDao.Properties.Asset_name.eq(accountId)).unique();
        if(unique!=null){
           return unique;
        }else {
            return null;
        }
    }

}
