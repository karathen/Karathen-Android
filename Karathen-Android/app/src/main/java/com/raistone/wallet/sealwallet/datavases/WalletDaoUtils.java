package com.raistone.wallet.sealwallet.datavases;

import android.text.TextUtils;

import com.raistone.wallet.sealwallet.entity.ETHWallet;
import com.raistone.wallet.sealwallet.entity.ETHWallet_Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.List;

public class WalletDaoUtils {


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

        return currentWallet;
    }

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

    public static List<ETHWallet> loadAll() {
        List<ETHWallet> ethWallets = SQLite.select().from(ETHWallet.class).queryList();
        return ethWallets;
    }

    public static List<ETHWallet> loadAllByCreate() {
        List<ETHWallet> ethWallets = SQLite.select().from(ETHWallet.class).where(ETHWallet_Table.isImport.eq(false)).orderBy(ETHWallet_Table.id, false).queryList();
        return ethWallets;
    }

    public static boolean walletNameChecking(String name) {
        List<ETHWallet> ethWallets = loadAll();
        for (ETHWallet ethWallet : ethWallets) {
            if (TextUtils.equals(ethWallet.getName(), name)) {
                return true;
            }
        }
        return false;
    }

    public static boolean setIsBackup(long walletId) {

        //ETHWallet

        ETHWallet ethWallet = SQLite.select().from(ETHWallet.class).where(ETHWallet_Table.id.eq(walletId)).querySingle();

        ethWallet.setBackup(true);

        boolean update = ethWallet.update();

        return update;
    }


    public static ETHWallet loadSingle(long id) {
        ETHWallet ethWallet = SQLite.select().from(ETHWallet.class).where(ETHWallet_Table.id.eq(id)).querySingle();
        return ethWallet;
    }


}
