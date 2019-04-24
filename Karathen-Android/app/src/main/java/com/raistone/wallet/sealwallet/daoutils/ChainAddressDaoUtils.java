package com.raistone.wallet.sealwallet.daoutils;

import android.text.TextUtils;

import com.raistone.wallet.sealwallet.WalletApplication;
import com.raistone.wallet.sealwallet.datavases.Student;
import com.raistone.wallet.sealwallet.factory.AssetsDeatilInfo;
import com.raistone.wallet.sealwallet.factory.ChainAddressInfo;
import com.raistone.wallet.sealwallet.greendao.ChainAddressInfoDao;
import com.raistone.wallet.sealwallet.utils.MultiChainCreateManager;

import org.greenrobot.greendao.query.DeleteQuery;
import org.greenrobot.greendao.query.Query;
import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

/**
 * 多链地址管理
 */
public class ChainAddressDaoUtils {
    public static ChainAddressInfoDao addressInfoDao = WalletApplication.getsInstance().getDaoSession().getChainAddressInfoDao();


    /**
     * 判断链地址是否存在
     */
    public static boolean chainAddressIsExist(String coinType, String address) {
        List<ChainAddressInfo> addressInfos = loadAllByCoinType(coinType);
        for (int i = 0; i < addressInfos.size(); i++) {
            if (addressInfos.get(i).getAddress().equals(address)) {
                return true;
            }

        }
        return false;
    }

    /**
     * 查看数据库是否有同样的地址
     */
    public static boolean checkAddressEq(String address){
        List<ChainAddressInfo> addressInfos = loadAllAddress();
        for (ChainAddressInfo info:addressInfos) {
            if(info.getAddress().toLowerCase().equals(address.toLowerCase())){
                return true;
            }
        }
        return false;
    }

    /**
     * 查看数据库是否有同样的地址
     */
    public static boolean checkAddressEq(String coinType,String address){
        List<ChainAddressInfo> addressInfos = loadAllByCoinType(coinType);
        for (ChainAddressInfo info:addressInfos) {
            if(info.getAddress().toLowerCase().equals(address.toLowerCase())){
                return true;
            }
        }
        return false;
    }


    /**
     * 查看数据库是否有同样的地址
     */
    public static boolean checkAddressEqAccountiD(String coinType,String accountId,String address){
        List<ChainAddressInfo> addressInfos = loadAllByCoinTypeAndAccount(coinType,accountId);
        for (ChainAddressInfo info:addressInfos) {
            if(info.getAddress().toLowerCase().equals(address.toLowerCase())){
                return true;
            }
        }
        return false;
    }






    /**
     * 根据账户Id 查询多链信息
     */
    public static List<ChainAddressInfo> findMultChainByAccountId(String accountId) {

        QueryBuilder<ChainAddressInfo> queryBuilder = addressInfoDao.queryBuilder().where(ChainAddressInfoDao.Properties.AccountId.eq(accountId));

        queryBuilder.where(ChainAddressInfoDao.Properties.IsCurrent.eq(true)).list();

        Query<ChainAddressInfo> build = queryBuilder.build();

        List<ChainAddressInfo> addressInfos = build.list();

        return addressInfos;
    }

    /**
     * 根据链类型查询对应地址列表
     *
     * @param coinType 链类型
     */
    public static List<ChainAddressInfo> loadAllByType(String coinType) {

        List<ChainAddressInfo> addressInfos = addressInfoDao.queryBuilder().where(ChainAddressInfoDao.Properties.CoinType.eq(coinType)).list();

        return addressInfos;
    }


    /**
     * 根据链类型查询选中的地址
     *
     * @param coinType 链类型
     */
    public static ChainAddressInfo loadByTypeInAccount(String coinType, String accountId) {

        QueryBuilder<ChainAddressInfo> queryBuilder = addressInfoDao.queryBuilder().where(ChainAddressInfoDao.Properties.CoinType.eq(coinType));

        queryBuilder.where(ChainAddressInfoDao.Properties.AccountId.eq(accountId));

        queryBuilder.where(ChainAddressInfoDao.Properties.IsCurrent.eq(true));

        Query<ChainAddressInfo> build = queryBuilder.build();

        ChainAddressInfo addressInfo = build.unique();

        return addressInfo;
    }


    /**
     * 根据链类型 查询地址信息
     *
     * @param coinType
     * @return
     */
    public static ChainAddressInfo loadByTypeInAccount(String coinType) {

        QueryBuilder<ChainAddressInfo> queryBuilder = addressInfoDao.queryBuilder().where(ChainAddressInfoDao.Properties.CoinType.eq(coinType));

        queryBuilder.where(ChainAddressInfoDao.Properties.IsCurrent.eq(true));

        Query<ChainAddressInfo> build = queryBuilder.build();

        ChainAddressInfo addressInfo = build.unique();

        return addressInfo;
    }


    /**
     * 根据链类型查询选中的地址
     *
     * @param coinType 链类型
     */
    public static ChainAddressInfo loadByTypeInAccountTwo(String coinType, String accountId) {


        QueryBuilder<ChainAddressInfo> queryBuilder = addressInfoDao.queryBuilder().where(ChainAddressInfoDao.Properties.CoinType.eq(coinType));

        queryBuilder.where(ChainAddressInfoDao.Properties.AccountId.eq(accountId));

        queryBuilder.where(ChainAddressInfoDao.Properties.IsCurrent.eq(true));

        queryBuilder.orderAsc(ChainAddressInfoDao.Properties.SelectStatus);

        Query<ChainAddressInfo> build = queryBuilder.build();

        ChainAddressInfo addressInfo = build.unique();


        return addressInfo;
    }

    /**
     * 插入新地址
     *
     * @param chainAddressInfo
     */
    public static void insertNewAddress(ChainAddressInfo chainAddressInfo) {
        //updateCurrent(-1);
        addressInfoDao.insert(chainAddressInfo);
    }


    /**
     * 更新选中钱包
     *
     * @param id 钱包ID
     */
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


    /**
     * 更新选中钱包
     *
     * @param id 钱包ID
     */
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

    /**
     * 获取当前选中钱包
     *
     * @return 钱包对象
     */
    public static ChainAddressInfo getAddressByCurrent() {

        List<ChainAddressInfo> addressInfos = addressInfoDao.loadAll();

        for (ChainAddressInfo addressInfo : addressInfos) {
            if (addressInfo.getIsCurrent()) {
                addressInfo.setIsCurrent(true);
                return addressInfo;
            }
        }
        return null;
    }

    /**
     * 获取当前选中钱包
     *
     * @return 钱包对象
     */
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

    /**
     * 查询所有钱包
     */
    public static List<ChainAddressInfo> loadAllAddress() {
        List<ChainAddressInfo> addressInfos = addressInfoDao.loadAll();
        return addressInfos;
    }


    /**
     * 查询所有钱包 根据钱包类型
     */
    public static List<ChainAddressInfo> loadAllByCoinType(String coinType) {

        List<ChainAddressInfo> addressInfos = addressInfoDao.queryBuilder().where(ChainAddressInfoDao.Properties.CoinType.eq(coinType)).list();

        return addressInfos;
    }

    /**
     * 查询所有钱包 根据钱包类型
     */
    public static List<ChainAddressInfo> loadAllByCoinTypeAndAccount(String coinType,String accountId) {

        List<ChainAddressInfo> addressInfos = addressInfoDao.queryBuilder().where(ChainAddressInfoDao.Properties.CoinType.eq(coinType),ChainAddressInfoDao.Properties.AccountId.eq(accountId)).list();

        return addressInfos;
    }


    /**
     * 查询所有创建的钱包 并且通过id进行排序
     */
    public static List<ChainAddressInfo> loadAllByCreate(String coinType) {

        List<ChainAddressInfo> addressInfos = addressInfoDao.queryBuilder()
                .where(ChainAddressInfoDao.Properties.IsImport.eq(false))
                .where(ChainAddressInfoDao.Properties.CoinType.eq(coinType))
                .orderDesc(ChainAddressInfoDao.Properties.Id).list();
        return addressInfos;
    }


    /**
     * 检查钱包名称是否存在
     *
     * @param name
     * @return
     */
    public static boolean walletNameChecking(String name) {
        List<ChainAddressInfo> ethWallets = loadAllAddress();
        for (ChainAddressInfo ethWallet : ethWallets) {
            if (TextUtils.equals(ethWallet.getName(), name)) {
                return true;
            }
        }
        return false;
    }


    /**
     * 以助记词检查钱包是否存在
     *
     * @param mnemonic
     * @return
     */
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


    /**
     * 以WIF检查钱包是否存在
     *
     * @param wif
     * @return
     */
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

    /**
     * 以WIF检查钱包是否存在
     *
     * @param wif
     * @return
     */
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

    /***
     * 根据钱包id 查询单条记录
     * @param id
     * @return
     */
    public static ChainAddressInfo loadSingle(long id) {
        ChainAddressInfo addressInfo = addressInfoDao.queryBuilder().where(ChainAddressInfoDao.Properties.Id.eq(id)).unique();

        // MultiChainInfo ethWallet = SQLite.select().from(MultiChainInfo.class).where(MultiChainInfo_Table.id.eq(id)).querySingle();
        return addressInfo;
    }

    /**
     * 修改链名称
     *
     * @param walletId
     * @param name
     */
    public static ChainAddressInfo updateChainName(long walletId, String name) {


        ChainAddressInfo ethWallet = loadSingle(walletId);

        ethWallet.setName(name);

        addressInfoDao.update(ethWallet);

        return ethWallet;
    }

    /**
     * 根据地址删除钱包
     *
     * @param address
     */
    public static void deleteAddress(String address) {

        ChainAddressInfo addressInfo = addressInfoDao.queryBuilder().where(ChainAddressInfoDao.Properties.Address.eq(address)).unique();

        if (addressInfo.getIsCurrent()) {
            List<ChainAddressInfo> addressInfos = loadAllAddress();
            if (addressInfos != null && addressInfos.size() > 0) {
                addressInfos.get(0).setIsCurrent(true);
                addressInfoDao.update(addressInfos.get(0));
                addressInfoDao.delete(addressInfo);
            }
        } else {
            addressInfoDao.delete(addressInfo);
        }


    }


    /**
     * 根据Id删除钱包
     *
     * @param id
     */
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


    /**
     * 根据类型获取地址总条数
     *
     * @return
     */
    public static long getCountByType(String coinType,String accountId) {


        long count = addressInfoDao.queryBuilder().where(ChainAddressInfoDao.Properties.CoinType.eq(coinType),ChainAddressInfoDao.Properties.AccountId.eq(accountId)).count();


        return count;
    }

    /**
     * 查询当前选中的钱包
     *
     * @param coinType 链类型
     * @return
     */
    public static ChainAddressInfo getSelectWallet(String coinType) {

        ChainAddressInfo addressInfo = addressInfoDao.queryBuilder()
                .where(ChainAddressInfoDao.Properties.CoinType.eq(coinType))
                .where(ChainAddressInfoDao.Properties.IsCurrent.eq(true)).unique();


        //MultiChainInfo wallet = SQLite.select().from(MultiChainInfo.class).where(MultiChainInfo_Table.coinType.eq(coinType)).and(MultiChainInfo_Table.isCurrent.eq(true)).querySingle();


        return addressInfo;
    }

    /**
     * 查询当前主账号的钱包
     *
     * @param coinType 链类型
     * @return
     */
    public static ChainAddressInfo getAccount(String coinType) {


        ChainAddressInfo addressInfo = addressInfoDao.queryBuilder()
                .where(ChainAddressInfoDao.Properties.CoinType.eq(coinType))
                .where(ChainAddressInfoDao.Properties.Account.eq(true)).unique();

        return addressInfo;
    }

    /**
     * 删除所有数据
     */
    public static void deleteAllData() {
        addressInfoDao.detachAll();
        addressInfoDao.deleteAll();
    }

    public static String getLastType(String coinType) {

        String type = null;

        List<ChainAddressInfo> byCreate = loadAllByCreate(coinType);
        if (byCreate != null && byCreate.size() > 0) {
            ChainAddressInfo ethWallet = byCreate.get(0);

            String type_flag = ethWallet.getType_flag();

            String[] split = type_flag.split("/");

            String index = split[5];

            int anInt = Integer.parseInt(index);

            anInt++;

            if (coinType.equals(MultiChainCreateManager.ETH_COIN_TYPE)) {

                type = "m/44'/60'/0'/0/" + anInt;
            }
            if (coinType.equals(MultiChainCreateManager.NEO_COIN_TYPE)) {

                type = "m/44'/888'/0'/0/" + anInt;
            }
            if (coinType.equals(MultiChainCreateManager.ONT_COIN_TYPE)) {

                type = "m/44'/1024'/0'/0/" + anInt;
            }
        } else {

            if (coinType.equals(MultiChainCreateManager.ETH_COIN_TYPE)) {

                type = "m/44'/60'/0'/0/1";
            }
            if (coinType.equals(MultiChainCreateManager.NEO_COIN_TYPE)) {

                type = "m/44'/888'/0'/0/1";
            }
            if (coinType.equals(MultiChainCreateManager.ONT_COIN_TYPE)) {

                type = "m/44'/1024'/0'/0/1";
            }

        }
        return type;
    }
}
