package com.raistone.wallet.sealwallet.factory;


import com.raistone.wallet.sealwallet.daoutils.HdWalletDaoUtils;

import java.util.Arrays;

public class WalletFactoryManager {

    public static final int HEWALLET_TYPE=0;

    // Version 2
    private static WalletFactoryManager instance;

    private WalletFactoryManager() {
    }

    public static synchronized WalletFactoryManager getInstance() {
        if (instance == null) {
            instance = new WalletFactoryManager();
        }
        return instance;
    }


    public WalletInfData createWallet(int type) {
        switch (type) {
            case 0:
                HdWallet hdWallet=new HdWallet();
                hdWallet.setWalletType(type);
                HdWalletDaoUtils.insertNewWallet(hdWallet);
                return hdWallet;
        }
        return null;
    }



    public static void main(String[] args) {
        WalletFactoryManager walletFactoryManager = new WalletFactoryManager();

        HdWallet wallet = (HdWallet) walletFactoryManager.createWallet(0);
        wallet.createWallet("E-S", "ss");
        wallet.setAccountId("sssss");
        wallet.setIsBackup(false);
        wallet.setCnyPrice("0");
        wallet.setIsCurrent(true);
        wallet.setIsHDWallet(true);
        wallet.setIsImport(false);
        wallet.setImportType(0);
        wallet.setPwdTips("没有密码");
        wallet.setWalletPwd("123456");

        HdWalletDaoUtils.insertNewWallet(wallet);

        wallet.createChain(wallet.getWalletId(), Arrays.asList("ETH", "NEO", "ONT"));


        System.out.println(wallet.getAccountId());
    }


}
