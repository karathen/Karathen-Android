package com.raistone.wallet.sealwallet.datavases;

import com.raistone.wallet.sealwallet.entity.ETHWallet;
import com.raistone.wallet.sealwallet.entity.ETHWallet_Table;
import com.raistone.wallet.sealwallet.entity.WalletInfo;
import com.raistone.wallet.sealwallet.entity.WalletInfo_Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.List;

public class WalletInfoDaoUtils {
    /**
     * 查询所有钱包
     */
    public static List<WalletInfo> findAllWallet() {
        List<WalletInfo> walletInfos = SQLite.select().from(WalletInfo.class).queryList();
        return walletInfos;
    }

    /**
     * 获取账户id
     */
    public static String getAccountId() {
        WalletInfo walletInfo = SQLite.select().from(WalletInfo.class).querySingle();
        String accountId = walletInfo.getAccountId();
        return accountId;
    }

    /**
     * 根据账户Id 查询钱包信息
     *
     * @param accountId 账户id
     */
    public static WalletInfo findWalletByAccount(String accountId) {
        WalletInfo walletInfo = SQLite.select().from(WalletInfo.class).where(WalletInfo_Table.accountId.eq(accountId)).querySingle();
        return walletInfo;
    }

    /**
     * 获取钱包密码
     */
    public static String getWalletPwd() {
        WalletInfo walletInfo = SQLite.select().from(WalletInfo.class).querySingle();
        String walletPwd = walletInfo.getWalletPwd();
        return walletPwd;
    }

    /**
     * 修改钱包密码
     */
    public static void updateWalletPwd(String pwd) {

        SQLite.update(WalletInfo.class).set(WalletInfo_Table.walletPwd.eq(pwd)).execute();
    }

    /**
     * 删除所有钱包信息
     */
    public static void deleteAllData() {
        SQLite.delete(WalletInfo.class);
    }
}
