package com.raistone.wallet.sealwallet.datavases;

import android.text.TextUtils;

import com.raistone.wallet.sealwallet.entity.ETHWallet;
import com.raistone.wallet.sealwallet.entity.ETHWallet_Table;
import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.List;

public class WalletDaoUtils {
    /**
     * 插入新创建钱包
     *
     * @param ethWallet 新创建钱包
     */
    public static void insertNewWallet(ETHWallet ethWallet) {
        //updateCurrent(-1);
        ethWallet.setCurrent(true);
        ethWallet.insert();
    }


    /**
     * 修改
     */
    public static void updatePin(String pin){
        SQLite.update(ETHWallet.class).set(ETHWallet_Table.password.eq(pin)).executeUpdateDelete();
    }

    /**
     * 更新选中钱包
     *
     * @param id 钱包ID
     */
    public static ETHWallet updateCurrent(long id) {

        List<ETHWallet> ethWallets = SQLite.select().from(ETHWallet.class).queryList();
        ETHWallet currentWallet = null;
        for (ETHWallet ethwallet : ethWallets) {
            if (ethwallet.getId() == id) {
                ethwallet.setCurrent(true);
                ethwallet.update();
            } else {
                ethwallet.setCurrent(false);
                ethwallet.update();
            }

            currentWallet = ethwallet;
        }


        /*ETHWallet currentWallet = null;
        for (ETHWallet ethwallet : ethWallets) {
            if (id != -1 && ethwallet.getId() == id) {
                ethwallet.setCurrent(true);
                currentWallet = ethwallet;
            } else {
                ethwallet.setCurrent(false);
            }
            currentWallet.update();
        }*/
        return currentWallet;
    }

    /**
     * 获取当前选中钱包
     *
     * @return 钱包对象
     */
    public static ETHWallet getCurrent() {

        List<ETHWallet> ethWallets = SQLite.select().from(ETHWallet.class).queryList();
        for (ETHWallet ethwallet : ethWallets) {
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
    public static List<ETHWallet> loadAll() {
        List<ETHWallet> ethWallets = SQLite.select().from(ETHWallet.class).queryList();
        return ethWallets;
    }

    /**
     * 根据类型查询钱包列表
     */
    public static List<ETHWallet> loadAllByType(String coinType) {
        List<ETHWallet> ethWallets = SQLite.select().from(ETHWallet.class).where(ETHWallet_Table.coinType.eq(coinType)).queryList();
        return ethWallets;
    }

    /**
     * 查询当前选中钱包
     */
    public static List<ETHWallet> loadAllBySelect() {
        List<ETHWallet> ethWallets = SQLite.select().from(ETHWallet.class).where(ETHWallet_Table.isCurrent.eq(true)).queryList();
        return ethWallets;
    }

    /**
     * 查询所有导入的钱包
     */
    public static List<ETHWallet> loadAllByImport(boolean isImport) {
        List<ETHWallet> ethWallets = SQLite.select().from(ETHWallet.class).where(ETHWallet_Table.isImport.eq(isImport)).queryList();
        return ethWallets;
    }

    /**
     * 查询所有创建的钱包 并且通过id进行排序
     */
    public static List<ETHWallet> loadAllByCreate() {
        List<ETHWallet> ethWallets = SQLite.select().from(ETHWallet.class).where(ETHWallet_Table.isImport.eq(false)).orderBy(ETHWallet_Table.id, false).queryList();
        return ethWallets;
    }


    /**
     * 查询所有的钱包 并且通过id进行排序
     */
    public static List<ETHWallet> loadAllByOrder() {
        List<ETHWallet> ethWallets = SQLite.select().from(ETHWallet.class).orderBy(ETHWallet_Table.id, false).queryList();
        return ethWallets;
    }


    /**
     * 检查钱包名称是否存在
     *
     * @param name
     * @return
     */
    public static boolean walletNameChecking(String name) {
        List<ETHWallet> ethWallets = loadAll();
        for (ETHWallet ethWallet : ethWallets) {
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

        ETHWallet ethWallet = SQLite.select().from(ETHWallet.class).where(ETHWallet_Table.id.eq(walletId)).querySingle();

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
    public static boolean checkRepeatByMenmonic(String mnemonic) {
        List<ETHWallet> ethWallets = loadAll();
        for (ETHWallet ethWallet : ethWallets) {
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

    public static boolean checkRepeatByKeystore(String keystore) {
        return false;
    }

    /***
     * 根据钱包id 查询单条记录
     * @param id
     * @return
     */
    public static ETHWallet loadSingle(long id) {
        ETHWallet ethWallet = SQLite.select().from(ETHWallet.class).where(ETHWallet_Table.id.eq(id)).querySingle();
        return ethWallet;
    }

    /**
     * 修改钱包名称
     *
     * @param walletId
     * @param name
     */
    public static boolean updateWalletName(long walletId, String name) {


        ETHWallet ethWallet = loadSingle(walletId);

        ethWallet.setName(name);

        boolean update = ethWallet.update();

        return update;
    }

    /**
     * 根据地址删除钱包
     *
     * @param address
     */
    public static void deleteAddress(String address) {
        //WalletDB walletDB = DBWalletUtils.selectSingleData(address);

        ETHWallet ethWallet = SQLite.select().from(ETHWallet.class).where(ETHWallet_Table.address.eq(address)).querySingle();
        if (ethWallet.isCurrent()) {
            List<ETHWallet> wallets = SQLite.select().from(ETHWallet.class).queryList();
            if(wallets!=null && wallets.size()>0){
                wallets.get(0).setCurrent(true);
                wallets.get(0).update();
                SQLite.delete().from(ETHWallet.class).where(ETHWallet_Table.address.eq(address)).async().execute();
            }
        } else {
            SQLite.delete().from(ETHWallet.class).where(ETHWallet_Table.address.eq(address)).async().execute();
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
        List<ETHWallet> ethWallets = loadAll();
        if (ethWallets != null && ethWallets.size() > 0) {
            ETHWallet ethWallet = ethWallets.get(0);
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

        //SQLite.selectCountOf().(conditions).count();

        long count1 = SQLite.selectCountOf().from(ETHWallet.class).where(ETHWallet_Table.coinType.eq(coinType)).count();


        //long count = SQLite.select().from(ETHWallet.class).where(ETHWallet_Table.coinType.eq(coinType)).count();
        return count1;
    }

    /**
     * 查询当前选中的钱包
     *
     * @param coinType 链类型
     * @return
     */
    public static ETHWallet getSelectWallet(String coinType) {


        ETHWallet wallet = SQLite.select().from(ETHWallet.class).where(ETHWallet_Table.coinType.eq(coinType)).and(ETHWallet_Table.isCurrent.eq(true)).querySingle();


        return wallet;
    }

    /**
     * 查询当前主账号的钱包
     *
     * @param coinType 链类型
     * @return
     */
    public static ETHWallet getAccount(String coinType){

        ETHWallet wallet = SQLite.select().from(ETHWallet.class).where(ETHWallet_Table.coinType.eq(coinType)).and(ETHWallet_Table.account.eq(true)).querySingle();

        return wallet;
    }

    /**
     * 删除所有数据
     */
    public static void deleteETHDataTable() {
        Delete.table(ETHWallet.class);
    }

}
