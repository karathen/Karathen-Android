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

    public static AssetsDeatilInfo findSignAssets(String tokenAddress,String walletAddress,String accountId) {

        AssetsDeatilInfo deatilInfo = assetsDeatilInfoDao.queryBuilder().where(AssetsDeatilInfoDao.Properties.TokenAddress.eq(tokenAddress),AssetsDeatilInfoDao.Properties.WalletAddress.eq(walletAddress),AssetsDeatilInfoDao.Properties.Asset_name.eq(accountId)).unique();


        return deatilInfo;
    }

    public static List<AssetsDeatilInfo> findAllAssetsByAddress(String walletAddress) {

        List<AssetsDeatilInfo> deatilInfos = assetsDeatilInfoDao.queryBuilder().where(AssetsDeatilInfoDao.Properties.WalletAddress.eq(walletAddress)).list();

        return deatilInfos;
    }

    public static List<AssetsDeatilInfo> findAllAssetsByCoinType(String type,String walletAddress) {

        List<AssetsDeatilInfo> deatilInfos = assetsDeatilInfoDao.queryBuilder().where(AssetsDeatilInfoDao.Properties.CoinType.eq(type)).where(AssetsDeatilInfoDao.Properties.WalletAddress.eq(walletAddress)).list();

        return deatilInfos;
    }

    public static List<AssetsDeatilInfo> findAllAssets() {

        List<AssetsDeatilInfo> deatilInfos = assetsDeatilInfoDao.loadAll();

        return deatilInfos;
    }

    public static List<AssetsDeatilInfo> likeFind(String like, String walletAddress) {


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




    public static AssetsDeatilInfo findAssterByTokenAddressByAccount(String tokenAddress,String walletAddress,String accountId) {

        AssetsDeatilInfo deatilInfo = assetsDeatilInfoDao.queryBuilder()
                .where(AssetsDeatilInfoDao.Properties.TokenAddress.eq(tokenAddress))
                .where(AssetsDeatilInfoDao.Properties.WalletAddress.eq(walletAddress))
                .where(AssetsDeatilInfoDao.Properties.Asset_name.eq(accountId))
                .unique();

        return deatilInfo;
    }



    public static void updatePrice(AssetsDeatilInfo bean) {


        assetsDeatilInfoDao.update(bean);

    }

    public static List<AssetsDeatilInfo> filterData(int filterParam, boolean assetsZero, String address) {


        List<AssetsDeatilInfo> dataBeans = new ArrayList<>();

        if (filterParam == 0) {
            if (!assetsZero) {

                dataBeans=assetsDeatilInfoDao.queryBuilder().where(AssetsDeatilInfoDao.Properties.WalletAddress.eq(address)).list();
            } else {

                dataBeans=assetsDeatilInfoDao.queryBuilder().where(AssetsDeatilInfoDao.Properties.Balance.notEq("0")).where(AssetsDeatilInfoDao.Properties.WalletAddress.eq(address)).list();
            }

        } else if (filterParam == 1) {
            dataBeans = hideZeroData(assetsZero, "ERC-20", address);

        } else if (filterParam == 2) {
            dataBeans = hideZeroData(assetsZero, "ERC-721", address);
        }

        return dataBeans;
    }


    public static List<AssetsDeatilInfo> filterDataNeo(int filterParam, boolean assetsZero, String address) {


        List<AssetsDeatilInfo> dataBeans = new ArrayList<>();

        if (filterParam == 0) {
            if (!assetsZero) {

                dataBeans= assetsDeatilInfoDao.queryBuilder().where(AssetsDeatilInfoDao.Properties.WalletAddress.eq(address)).list();
            } else {

                dataBeans= assetsDeatilInfoDao.queryBuilder().where(AssetsDeatilInfoDao.Properties.Balance.notEq("0")).where(AssetsDeatilInfoDao.Properties.WalletAddress.eq(address)).list();
            }

        }

        if (filterParam == 1) {
            dataBeans = hideZeroData(assetsZero, "NEO", address);

        }
        if (filterParam == 2) {
            dataBeans = hideZeroData(assetsZero, "GAS", address);
        }

        if (filterParam == 3) {
            dataBeans = hideZeroData(assetsZero, "NEP-5", address);
        }


        return dataBeans;
    }


    public static List<AssetsDeatilInfo> filterDataOnt(int filterParam, boolean assetsZero, String address) {


        List<AssetsDeatilInfo> dataBeans = new ArrayList<>();

        if (filterParam == 0) {
            if (!assetsZero) {

                dataBeans=assetsDeatilInfoDao.queryBuilder().where(AssetsDeatilInfoDao.Properties.WalletAddress.eq(address)).list();
            } else {

                dataBeans=assetsDeatilInfoDao.queryBuilder().where(AssetsDeatilInfoDao.Properties.Balance.notEq("0")).where(AssetsDeatilInfoDao.Properties.WalletAddress.eq(address)).list();
            }

        }

        if (filterParam == 1) {
            dataBeans = hideZeroData(assetsZero, "ONT", address);

        }
        if (filterParam == 2) {
            dataBeans = hideZeroData(assetsZero, "ONG", address);
        }


        return dataBeans;
    }

    public static List<AssetsDeatilInfo> hideZeroData(boolean assetsZero, String filterParam, String address) {
        List<AssetsDeatilInfo> dataBeans = new ArrayList<>();
        if (assetsZero) {

            dataBeans=assetsDeatilInfoDao.queryBuilder().where(AssetsDeatilInfoDao.Properties.TokenType.eq(filterParam)).where(AssetsDeatilInfoDao.Properties.Balance.notEq("0")).where(AssetsDeatilInfoDao.Properties.WalletAddress.eq(address)).list();


        } else {

            dataBeans=assetsDeatilInfoDao.queryBuilder().where(AssetsDeatilInfoDao.Properties.TokenType.eq(filterParam)).where(AssetsDeatilInfoDao.Properties.WalletAddress.eq(address)).list();
        }

        return dataBeans;
    }


    public static void deleteAssetsByAddress(AssetsDeatilInfo deatilInfo){
        assetsDeatilInfoDao.delete(deatilInfo);
    }

    public static AssetsDeatilInfo addressIsEx(String walletAddress,String tokenAddress,String accountId){
        AssetsDeatilInfo unique = assetsDeatilInfoDao.queryBuilder().where(AssetsDeatilInfoDao.Properties.WalletAddress.eq(walletAddress),AssetsDeatilInfoDao.Properties.TokenAddress.eq(tokenAddress)).where(AssetsDeatilInfoDao.Properties.Asset_name.eq(accountId)).unique();
        if(unique!=null){
           return unique;
        }else {
            return null;
        }
    }

}
