package com.raistone.wallet.sealwallet.datavases;

import com.raistone.wallet.sealwallet.entity.WalletInfo;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.List;

public class WalletInfoDaoUtils {

    public static List<WalletInfo> findAllWallet() {
        List<WalletInfo> walletInfos = SQLite.select().from(WalletInfo.class).queryList();
        return walletInfos;
    }


    public static String getAccountId() {
        WalletInfo walletInfo = SQLite.select().from(WalletInfo.class).querySingle();
        String accountId = walletInfo.getAccountId();
        return accountId;
    }

    public static String getWalletPwd() {
        WalletInfo walletInfo = SQLite.select().from(WalletInfo.class).querySingle();
        String walletPwd = walletInfo.getWalletPwd();
        return walletPwd;
    }

}
