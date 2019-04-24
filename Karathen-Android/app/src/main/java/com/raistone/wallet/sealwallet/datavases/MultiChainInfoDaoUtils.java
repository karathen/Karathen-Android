package com.raistone.wallet.sealwallet.datavases;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.raistone.wallet.sealwallet.entity.ETHWallet;
import com.raistone.wallet.sealwallet.entity.MultiChainInfo;
import com.raistone.wallet.sealwallet.entity.MultiChainInfo_Table;
import com.raistone.wallet.sealwallet.utils.MultiChainCreateManager;
import com.raistone.wallet.sealwallet.utils.SharePreUtil;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.ModelAdapter;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;
import com.raizlabs.android.dbflow.structure.database.transaction.ITransaction;
import com.raizlabs.android.dbflow.structure.database.transaction.Transaction;

import java.util.List;
import java.util.Random;

/**
 * 多链管理
 */
public class MultiChainInfoDaoUtils {

    /**
     * 根据账户Id 查询多链信息
     */
    public static List<MultiChainInfo> findMultChainByAccountId(String accountId) {
        List<MultiChainInfo> multiChainInfos = SQLite.select().from(MultiChainInfo.class).where(MultiChainInfo_Table.accountId.eq(accountId)).and(MultiChainInfo_Table.isCurrent.eq(true)).queryList();

        return multiChainInfos;
    }

    /**
     * 根据链类型查询对应地址列表
     *
     * @param coinType 链类型
     */
    public static List<MultiChainInfo> loadAllByType(String coinType) {
        List<MultiChainInfo> ethWallets = SQLite.select().from(MultiChainInfo.class).where(MultiChainInfo_Table.coinType.eq(coinType)).queryList();
        return ethWallets;
    }


    /**
     * 根据链类型查询选中的地址
     *
     * @param coinType 链类型
     */
    public static MultiChainInfo loadByTypeInAccount(String coinType, String accountId) {
        MultiChainInfo ethWallets = SQLite.select().from(MultiChainInfo.class).where(MultiChainInfo_Table.coinType.eq(coinType)).and(MultiChainInfo_Table.accountId.eq(accountId)).and(MultiChainInfo_Table.isCurrent.eq(true)).querySingle();
        return ethWallets;
    }

    public static MultiChainInfo loadByTypeInAccount(String coinType) {


        MultiChainInfo ethWallets = SQLite.select().from(MultiChainInfo.class).where(MultiChainInfo_Table.coinType.eq(coinType)).and(MultiChainInfo_Table.isCurrent.eq(true)).querySingle();
        return ethWallets;
    }

    /**
     * 根据链类型查询选中的地址
     *
     * @param coinType 链类型
     */
    public static MultiChainInfo loadByTypeInAccountTwo(String coinType, String accountId) {
        MultiChainInfo ethWallets = SQLite.select().from(MultiChainInfo.class).where(MultiChainInfo_Table.coinType.eq(coinType)).and(MultiChainInfo_Table.accountId.eq(accountId)).and(MultiChainInfo_Table.isCurrent.eq(true)).orderBy(MultiChainInfo_Table.selectStatus, false).querySingle();
        return ethWallets;
    }

    /**
     * 插入新创建钱包
     *
     * @param ethWallet 新创建钱包
     */
    public static void insertNewWallet(MultiChainInfo ethWallet) {
        //updateCurrent(-1);
        ethWallet.setCurrent(true);
        ethWallet.insert();
    }


    /**
     * 修改
     */
    public static void updatePin(String pin) {
        SQLite.update(MultiChainInfo.class).set(MultiChainInfo_Table.password.eq(pin)).executeUpdateDelete();
    }


    /**
     * 更新选中钱包
     *
     * @param id 钱包ID
     */
    public static MultiChainInfo findSingData(long id, String coinTyoe) {

        MultiChainInfo ethWallet = SQLite.select().from(MultiChainInfo.class).where(MultiChainInfo_Table.id.eq(id)).and(MultiChainInfo_Table.coinType.eq(coinTyoe)).querySingle();

        return ethWallet;
    }

    /**
     * 更新选中钱包
     *
     * @param id 钱包ID
     */
    public static boolean updateCurrent(long id, String coinTyoe) {

        List<MultiChainInfo> ethWallets = SQLite.select().from(MultiChainInfo.class).where(MultiChainInfo_Table.coinType.eq(coinTyoe)).queryList();
        MultiChainInfo currentWallet = null;
        boolean result=false;
        for (MultiChainInfo ethwallet : ethWallets) {
            if (ethwallet.getId() == id) {
                ethwallet.setCurrent(true);
                result=ethwallet.update();
            } else {
                ethwallet.setCurrent(false);
                result=ethwallet.update();
            }

            currentWallet = ethwallet;
        }

        result= currentWallet.update();
        return result;
    }


    synchronized public static List<MultiChainInfo> updateCurrentTwo(final long id, final String coinType) {

        FlowManager.getDatabase(AppDataBase.class).beginTransactionAsync(new ITransaction() {
            @Override
            public void execute(DatabaseWrapper databaseWrapper) {


                //查询当前币对所有地址
               /* List<MultiChainInfo> ethWallets = SQLite.select().from(MultiChainInfo.class).where(MultiChainInfo_Table.coinType.eq(coinType)).queryList();
                for (MultiChainInfo info : ethWallets) {
                    info.setCurrent(false);
                    info.update();
                }*/

                SQLite.update(MultiChainInfo.class).set(MultiChainInfo_Table.isCurrent.eq(false)).where(MultiChainInfo_Table.id.notEq(id)).and(MultiChainInfo_Table.coinType.eq(coinType)).async().execute();
                //查询当前选中地址
                //MultiChainInfo querySingle = SQLite.select().from(MultiChainInfo.class).where(MultiChainInfo_Table.coinType.eq(coinType)).and(MultiChainInfo_Table.id.eq(id)).querySingle();


                SQLite.update(MultiChainInfo.class).set(MultiChainInfo_Table.isCurrent.eq(true)).where(MultiChainInfo_Table.id.eq(id)).and(MultiChainInfo_Table.coinType.eq(coinType)).async().execute();
/*
                querySingle.setCurrent(true);

                querySingle.update();*/

            }
        }).success(new Transaction.Success() {
            @Override
            public void onSuccess(@NonNull Transaction transaction) {
                //transaction.execute();
                Log.d("onSuccess", "onSuccess");
                Log.d("onSuccess", "onSuccess");
            }
        }).error(new Transaction.Error() {
            @Override
            public void onError(@NonNull Transaction transaction, @NonNull Throwable error) {

                Log.d("onError", "onError");
                Log.d("onError", "onError");
            }
        }).build().execute();

        List<MultiChainInfo> ethWallets = SQLite.select().from(MultiChainInfo.class).where(MultiChainInfo_Table.coinType.eq(coinType)).queryList();

        return ethWallets;
    }

    synchronized public static void updateCurrentThree(final long id, final String coinType) {


        SQLite.update(MultiChainInfo.class).set(MultiChainInfo_Table.isCurrent.eq(false)).where(MultiChainInfo_Table.id.notEq(id)).and(MultiChainInfo_Table.coinType.eq(coinType)).async().execute();


        SQLite.update(MultiChainInfo.class).set(MultiChainInfo_Table.isCurrent.eq(true)).where(MultiChainInfo_Table.id.eq(id)).and(MultiChainInfo_Table.coinType.eq(coinType)).async().execute();

    }

    /**
     * 获取当前选中钱包
     *
     * @return 钱包对象
     */
    public static MultiChainInfo getCurrent() {

        List<MultiChainInfo> ethWallets = SQLite.select().from(MultiChainInfo.class).queryList();
        for (MultiChainInfo ethwallet : ethWallets) {
            if (ethwallet.isCurrent()) {
                ethwallet.setCurrent(true);
                return ethwallet;
            }
        }
        return null;
    }

    /**
     * 获取当前选中钱包
     *
     * @return 钱包对象
     */
    public static MultiChainInfo getCurrentByCoinType(String coinType) {

        List<MultiChainInfo> ethWallets = SQLite.select().from(MultiChainInfo.class).where(MultiChainInfo_Table.coinType.eq(coinType)).queryList();
        for (MultiChainInfo ethwallet : ethWallets) {
            if (ethwallet.isCurrent()) {
                ethwallet.setCurrent(true);
                return ethwallet;
            }
        }
        return null;
    }

    /**
     * 查询所有钱包
     */
    public static List<MultiChainInfo> loadAll() {
        List<MultiChainInfo> ethWallets = SQLite.select().from(MultiChainInfo.class).queryList();
        return ethWallets;
    }


    /**
     * 查询所有钱包
     */
    public static List<MultiChainInfo> loadAllByCoinType(String coinType) {
        List<MultiChainInfo> ethWallets = SQLite.select().from(MultiChainInfo.class).where(MultiChainInfo_Table.coinType.eq(coinType)).queryList();
        return ethWallets;
    }


    /**
     * 查询当前选中钱包
     */
    public static List<MultiChainInfo> loadAllBySelect() {
        List<MultiChainInfo> ethWallets = SQLite.select().from(MultiChainInfo.class).where(MultiChainInfo_Table.isCurrent.eq(true)).queryList();
        return ethWallets;
    }

    /**
     * 查询所有导入的钱包
     */
    public static List<MultiChainInfo> loadAllByImport(boolean isImport) {
        List<MultiChainInfo> ethWallets = SQLite.select().from(MultiChainInfo.class).where(MultiChainInfo_Table.isImport.eq(isImport)).queryList();
        return ethWallets;
    }

    /**
     * 查询所有创建的钱包 并且通过id进行排序
     */
    public static List<MultiChainInfo> loadAllByCreate() {
        List<MultiChainInfo> ethWallets = SQLite.select().from(MultiChainInfo.class).where(MultiChainInfo_Table.isImport.eq(false)).orderBy(MultiChainInfo_Table.id, false).queryList();
        return ethWallets;
    }


    /**
     * 查询所有创建的钱包 并且通过id进行排序
     */
    public static List<MultiChainInfo> loadAllByCreate(String coinType) {
        List<MultiChainInfo> ethWallets = SQLite.select().from(MultiChainInfo.class).where(MultiChainInfo_Table.isImport.eq(false)).and(MultiChainInfo_Table.coinType.eq(coinType)).orderBy(MultiChainInfo_Table.id, false).queryList();
        return ethWallets;
    }


    /**
     * 查询所有的钱包 并且通过id进行排序
     */
    public static List<MultiChainInfo> loadAllByOrder() {
        List<MultiChainInfo> ethWallets = SQLite.select().from(MultiChainInfo.class).orderBy(MultiChainInfo_Table.id, false).queryList();
        return ethWallets;
    }

    /**
     * 查询所有的钱包 并且通过id进行排序
     */
    public static List<MultiChainInfo> loadAllOrderBySelelct() {
        List<MultiChainInfo> ethWallets = SQLite.select().from(MultiChainInfo.class).orderBy(MultiChainInfo_Table.selectStatus, false).queryList();
        return ethWallets;
    }

    /**
     * 检查钱包名称是否存在
     *
     * @param name
     * @return
     */
    public static boolean walletNameChecking(String name) {
        List<MultiChainInfo> ethWallets = loadAll();
        for (MultiChainInfo ethWallet : ethWallets) {
            if (TextUtils.equals(ethWallet.getName(), name)) {
                return true;
            }
        }
        return false;
    }


    /**
     * 设置isBackup为已备份
     *
     * @param walletId 钱包Id
     */
    public static boolean setIsBackup(long walletId) {

        //ETHWallet

        MultiChainInfo ethWallet = SQLite.select().from(MultiChainInfo.class).where(MultiChainInfo_Table.id.eq(walletId)).querySingle();

        ethWallet.setBackup(true);

        boolean update = ethWallet.update();

        return update;
    }


    /**
     * 以助记词检查钱包是否存在
     *
     * @param mnemonic
     * @return
     */
    public static boolean checkRepeatByMenmonic(String mnemonic, String coinType) {
        List<MultiChainInfo> ethWallets = loadAllByCoinType(coinType);
        for (MultiChainInfo ethWallet : ethWallets) {
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
        List<MultiChainInfo> ethWallets = loadAllByCoinType(coinType);
        for (MultiChainInfo ethWallet : ethWallets) {
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

    public static boolean checkRepeatByKeystore(String keystore) {
        return false;
    }

    /***
     * 根据钱包id 查询单条记录
     * @param id
     * @return
     */
    public static MultiChainInfo loadSingle(long id) {
        MultiChainInfo ethWallet = SQLite.select().from(MultiChainInfo.class).where(MultiChainInfo_Table.id.eq(id)).querySingle();
        return ethWallet;
    }

    /**
     * 修改钱包名称
     *
     * @param walletId
     * @param name
     */
    public static MultiChainInfo updateWalletName(long walletId, String name) {


        MultiChainInfo ethWallet = loadSingle(walletId);

        ethWallet.setName(name);

        ethWallet.update();

        return ethWallet;
    }

    /**
     * 根据地址删除钱包
     *
     * @param address
     */
    public static void deleteAddress(String address) {
        //WalletDB walletDB = DBWalletUtils.selectSingleData(address);

        MultiChainInfo ethWallet = SQLite.select().from(MultiChainInfo.class).where(MultiChainInfo_Table.address.eq(address)).querySingle();
        if (ethWallet.isCurrent()) {
            List<MultiChainInfo> wallets = SQLite.select().from(MultiChainInfo.class).queryList();
            if (wallets != null && wallets.size() > 0) {
                wallets.get(0).setCurrent(true);
                wallets.get(0).update();
                SQLite.delete().from(MultiChainInfo.class).where(MultiChainInfo_Table.address.eq(address)).async().execute();
            }
        } else {
            SQLite.delete().from(MultiChainInfo.class).where(MultiChainInfo_Table.address.eq(address)).async().execute();
        }
    }


    /**
     * 根据地址删除
     */
    /*public static WalletDB getCurrentWalletDB() {
        String currentSelectedWalletAddress = SPUtils.getParamString(OaxTokenApplication.getInstance(), SPConstant.CURRENT_SELECTED_WALLET, null);
        if (TextUtils.isEmpty(currentSelectedWalletAddress)) {
            return DBWalletUtils.selectFirst(DBWalletUtils.SELECT_END_TYPE);
        } else {
            return DBWalletUtils.selectSingleData(currentSelectedWalletAddress);
        }
    }*/
    public static void setCurrentAfterDelete() {
        List<MultiChainInfo> ethWallets = loadAll();
        if (ethWallets != null && ethWallets.size() > 0) {
            MultiChainInfo ethWallet = ethWallets.get(0);
            ethWallet.setCurrent(true);
            ethWallet.update();
        }
    }


    /**
     * 根据类型获取地址总条数
     *
     * @return
     */
    public static long getCountByType(String coinType) {


        long count1 = SQLite.selectCountOf().from(MultiChainInfo.class).where(MultiChainInfo_Table.coinType.eq(coinType)).count();


        return count1;
    }

    /**
     * 查询当前选中的钱包
     *
     * @param coinType 链类型
     * @return
     */
    public static MultiChainInfo getSelectWallet(String coinType) {


        MultiChainInfo wallet = SQLite.select().from(MultiChainInfo.class).where(MultiChainInfo_Table.coinType.eq(coinType)).and(MultiChainInfo_Table.isCurrent.eq(true)).querySingle();


        return wallet;
    }

    /**
     * 查询当前主账号的钱包
     *
     * @param coinType 链类型
     * @return
     */
    public static MultiChainInfo getAccount(String coinType) {

        MultiChainInfo wallet = SQLite.select().from(MultiChainInfo.class).where(MultiChainInfo_Table.coinType.eq(coinType)).and(MultiChainInfo_Table.account.eq(true)).querySingle();

        return wallet;
    }

    /**
     * 删除所有数据
     */
    public static void deleteAllData() {
        Delete.table(MultiChainInfo.class);
    }

    public static String getLastType(String coinType) {

        String type = null;

        List<MultiChainInfo> byCreate = loadAllByCreate(coinType);
        if (byCreate != null && byCreate.size() > 0) {
            MultiChainInfo ethWallet = byCreate.get(0);

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
