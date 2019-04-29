package com.raistone.wallet.sealwallet.datavases;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.raistone.wallet.sealwallet.entity.MultiChainInfo;
import com.raistone.wallet.sealwallet.entity.MultiChainInfo_Table;
import com.raistone.wallet.sealwallet.utils.MultiChainCreateManager;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;
import com.raizlabs.android.dbflow.structure.database.transaction.ITransaction;
import com.raizlabs.android.dbflow.structure.database.transaction.Transaction;

import java.util.List;


public class MultiChainInfoDaoUtils {



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

    public static List<MultiChainInfo> loadAll() {
        List<MultiChainInfo> ethWallets = SQLite.select().from(MultiChainInfo.class).queryList();
        return ethWallets;
    }

    public static List<MultiChainInfo> loadAllByCoinType(String coinType) {
        List<MultiChainInfo> ethWallets = SQLite.select().from(MultiChainInfo.class).where(MultiChainInfo_Table.coinType.eq(coinType)).queryList();
        return ethWallets;
    }




    public static List<MultiChainInfo> loadAllByCreate(String coinType) {
        List<MultiChainInfo> ethWallets = SQLite.select().from(MultiChainInfo.class).where(MultiChainInfo_Table.isImport.eq(false)).and(MultiChainInfo_Table.coinType.eq(coinType)).orderBy(MultiChainInfo_Table.id, false).queryList();
        return ethWallets;
    }



    public static boolean setIsBackup(long walletId) {

        //ETHWallet

        MultiChainInfo ethWallet = SQLite.select().from(MultiChainInfo.class).where(MultiChainInfo_Table.id.eq(walletId)).querySingle();

        ethWallet.setBackup(true);

        boolean update = ethWallet.update();

        return update;
    }



    public static MultiChainInfo loadSingle(long id) {
        MultiChainInfo ethWallet = SQLite.select().from(MultiChainInfo.class).where(MultiChainInfo_Table.id.eq(id)).querySingle();
        return ethWallet;
    }



    public static long getCountByType(String coinType) {


        long count1 = SQLite.selectCountOf().from(MultiChainInfo.class).where(MultiChainInfo_Table.coinType.eq(coinType)).count();


        return count1;
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
