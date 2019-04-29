package com.raistone.wallet.sealwallet.daoutils;

import android.text.TextUtils;

import com.raistone.wallet.sealwallet.WalletApplication;
import com.raistone.wallet.sealwallet.datavases.Student;
import com.raistone.wallet.sealwallet.factory.AssetsDeatilInfo;
import com.raistone.wallet.sealwallet.factory.ChainAddressInfo;
import com.raistone.wallet.sealwallet.greendao.ChainAddressInfoDao;
import com.raistone.wallet.sealwallet.utils.ChainAddressCreateManager;
import com.raistone.wallet.sealwallet.utils.MultiChainCreateManager;

import org.greenrobot.greendao.query.DeleteQuery;
import org.greenrobot.greendao.query.Query;
import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;


public class ChainAddressDaoUtils {
    public static ChainAddressInfoDao addressInfoDao = WalletApplication.getsInstance().getDaoSession().getChainAddressInfoDao();


    public static boolean chainAddressIsExist(String coinType, String address) {
        List<ChainAddressInfo> addressInfos = loadAllByCoinType(coinType);
        for (int i = 0; i < addressInfos.size(); i++) {
            if (addressInfos.get(i).getAddress().equals(address)) {
                return true;
            }

        }
        return false;
    }


    public static boolean checkAddressEq(String address){
        List<ChainAddressInfo> addressInfos = loadAllAddress();
        for (ChainAddressInfo info:addressInfos) {
            if(info.getAddress().toLowerCase().equals(address.toLowerCase())){
                return true;
            }
        }
        return false;
    }

    public static boolean checkAddressEq(String coinType,String address){
        List<ChainAddressInfo> addressInfos = loadAllByCoinType(coinType);
        for (ChainAddressInfo info:addressInfos) {
            if(info.getAddress().toLowerCase().equals(address.toLowerCase())){
                return true;
            }
        }
        return false;
    }


    public static boolean checkAddressEqAccountiD(String coinType,String accountId,String address){
        List<ChainAddressInfo> addressInfos = loadAllByCoinTypeAndAccount(coinType,accountId);
        for (ChainAddressInfo info:addressInfos) {
            if(info.getAddress().toLowerCase().equals(address.toLowerCase())){
                return true;
            }
        }
        return false;
    }



    public static ChainAddressInfo loadByTypeInAccount(String coinType, String accountId) {

        QueryBuilder<ChainAddressInfo> queryBuilder = addressInfoDao.queryBuilder().where(ChainAddressInfoDao.Properties.CoinType.eq(coinType));

        queryBuilder.where(ChainAddressInfoDao.Properties.AccountId.eq(accountId));

        queryBuilder.where(ChainAddressInfoDao.Properties.IsCurrent.eq(true));

        Query<ChainAddressInfo> build = queryBuilder.build();

        ChainAddressInfo addressInfo = build.unique();

        return addressInfo;
    }



    public static void insertNewAddress(ChainAddressInfo chainAddressInfo) {
        //updateCurrent(-1);
        addressInfoDao.insert(chainAddressInfo);
    }


    public static ChainAddressInfo updateCurrent(long id) {
        List<ChainAddressInfo> addressInfos = addressInfoDao.loadAll();
        ChainAddressInfo currentAdress = null;
        for (ChainAddressInfo addressInfo : addressInfos) {
            if (id != -1 && addressInfo.getId() == id) {
                addressInfo.setIsCurrent(true);
                currentAdress = addressInfo;
            } else {
                addressInfo.setIsCurrent(false);
            }
            addressInfoDao.update(addressInfo);
        }
        return currentAdress;
    }


    public static ChainAddressInfo updateCurrent(long id, String coinType,String accountId) {
        List<ChainAddressInfo> addressInfos = loadAllByCoinTypeAndAccount(coinType,accountId);
        ChainAddressInfo currentAdress = null;
        for (ChainAddressInfo addressInfo : addressInfos) {
            if (id != -1 && addressInfo.getId() == id) {
                addressInfo.setIsCurrent(true);
                currentAdress = addressInfo;
            } else {
                addressInfo.setIsCurrent(false);
            }
            addressInfoDao.update(addressInfo);
        }
        return currentAdress;
    }




    public static ChainAddressInfo getCurrentByCoinType(String coinType,String accountId) {

        List<ChainAddressInfo> addressInfos = addressInfoDao.queryBuilder().where(ChainAddressInfoDao.Properties.CoinType.eq(coinType),ChainAddressInfoDao.Properties.AccountId.eq(accountId)).list();
        for (ChainAddressInfo addressInfo : addressInfos) {
            if (addressInfo.getIsCurrent()) {
                addressInfo.setIsCurrent(true);
                return addressInfo;
            }
        }
        return null;
    }

    public static List<ChainAddressInfo> loadAllAddress() {
        List<ChainAddressInfo> addressInfos = addressInfoDao.loadAll();
        return addressInfos;
    }


    public static List<ChainAddressInfo> loadAllByCoinType(String coinType) {

        List<ChainAddressInfo> addressInfos = addressInfoDao.queryBuilder().where(ChainAddressInfoDao.Properties.CoinType.eq(coinType)).list();

        return addressInfos;
    }

    public static List<ChainAddressInfo> loadAllByCoinTypeAndAccount(String coinType,String accountId) {

        List<ChainAddressInfo> addressInfos = addressInfoDao.queryBuilder().where(ChainAddressInfoDao.Properties.CoinType.eq(coinType),ChainAddressInfoDao.Properties.AccountId.eq(accountId)).list();

        return addressInfos;
    }

    public static List<ChainAddressInfo> loadAllByCreate(String coinType) {

        List<ChainAddressInfo> addressInfos = addressInfoDao.queryBuilder()
                .where(ChainAddressInfoDao.Properties.IsImport.eq(false))
                .where(ChainAddressInfoDao.Properties.CoinType.eq(coinType))
                .orderDesc(ChainAddressInfoDao.Properties.Id).list();
        return addressInfos;
    }


    public static boolean checkRepeatByMenmonic(String mnemonic, String coinType) {
        List<ChainAddressInfo> ethWallets = loadAllByCoinType(coinType);
        for (ChainAddressInfo ethWallet : ethWallets) {
            if (!TextUtils.isEmpty(ethWallet.getMnemonic())) {
                if (TextUtils.equals(ethWallet.getMnemonic().trim(), mnemonic.trim())) {
                    return true;
                }
            } else {
                return false;
            }
        }
        return false;
    }


    public static boolean checkRepeatByWif(String wif, String coinType) {
        List<ChainAddressInfo> ethWallets = loadAllByCoinType(coinType);
        for (ChainAddressInfo ethWallet : ethWallets) {
            if (!TextUtils.isEmpty(ethWallet.getWif())) {
                if (TextUtils.equals(ethWallet.getWif().trim(), wif.trim())) {
                    return true;
                }
            } else {
                return false;
            }
        }
        return false;
    }

    public static boolean checkRepeatByWifAndAccountId(String wif, String coinType,String accountId) {
        List<ChainAddressInfo> ethWallets = loadAllByCoinTypeAndAccount(coinType,accountId);
        for (ChainAddressInfo ethWallet : ethWallets) {
            if (!TextUtils.isEmpty(ethWallet.getWif())) {
                if (TextUtils.equals(ethWallet.getWif().trim(), wif.trim())) {
                    return true;
                }
            } else {
                return false;
            }
        }
        return false;
    }

    public static ChainAddressInfo loadSingle(long id) {
        ChainAddressInfo addressInfo = addressInfoDao.queryBuilder().where(ChainAddressInfoDao.Properties.Id.eq(id)).unique();

        // MultiChainInfo ethWallet = SQLite.select().from(MultiChainInfo.class).where(MultiChainInfo_Table.id.eq(id)).querySingle();
        return addressInfo;
    }

    public static ChainAddressInfo updateChainName(long walletId, String name) {


        ChainAddressInfo ethWallet = loadSingle(walletId);

        ethWallet.setName(name);

        addressInfoDao.update(ethWallet);

        return ethWallet;
    }


    public static void deleteAddressById(long id) {

        ChainAddressInfo addressInfo = addressInfoDao.queryBuilder().where(ChainAddressInfoDao.Properties.Id.eq(id)).unique();


        List<AssetsDeatilInfo> dataList = addressInfo.getAssetsInfoDataList();

        if(dataList!=null && dataList.size()>0)
        for (AssetsDeatilInfo infos:dataList) {
            AssetsDetailDaoUtils.deleteAssetsByAddress(infos);
        }

        if (addressInfo.getIsCurrent()) {
            List<ChainAddressInfo> addressInfos = loadAllByCoinType(addressInfo.getCoinType());
            if (addressInfos != null && addressInfos.size() > 0) {
                addressInfos.get(0).setIsCurrent(true);
                addressInfoDao.update(addressInfos.get(0));
                addressInfoDao.delete(addressInfo);
            }
        } else {
            addressInfoDao.delete(addressInfo);
        }

        addressInfo.delete();
    }

    public static long getCountByType(String coinType,String accountId) {


        long count = addressInfoDao.queryBuilder().where(ChainAddressInfoDao.Properties.CoinType.eq(coinType),ChainAddressInfoDao.Properties.AccountId.eq(accountId)).count();


        return count;
    }

    public static String getLastType(String coinType,String accountId) {

        String type = null;

        List<ChainAddressInfo> byCreate = loadAllByCoinTypeAndAccount(coinType,accountId);
        if (byCreate != null && byCreate.size() > 0) {

            //ChainAddressInfo ethWallet = byCreate.get(0);

            String type_flag ="m/44'/60'/0'/0/"+byCreate.size();

            String[] split = type_flag.split("/");

            String index = split[5];

            int anInt = Integer.parseInt(index);

            //anInt++;

            if (coinType.equals(ChainAddressCreateManager.ETH_COIN_TYPE)) {

                type = "m/44'/60'/0'/0/" + anInt;
            }
            if (coinType.equals(ChainAddressCreateManager.NEO_COIN_TYPE)) {

                type = "m/44'/888'/0'/0/" + anInt;
            }
            if (coinType.equals(ChainAddressCreateManager.ONT_COIN_TYPE)) {

                type = "m/44'/1024'/0'/0/" + anInt;
            }
        } else {

            if (coinType.equals(ChainAddressCreateManager.ETH_COIN_TYPE)) {

                type = "m/44'/60'/0'/0/1";
            }
            if (coinType.equals(ChainAddressCreateManager.NEO_COIN_TYPE)) {

                type = "m/44'/888'/0'/0/1";
            }
            if (coinType.equals(ChainAddressCreateManager.ONT_COIN_TYPE)) {

                type = "m/44'/1024'/0'/0/1";
            }

        }
        return type;
    }

}
