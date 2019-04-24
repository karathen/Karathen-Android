package com.raistone.wallet.sealwallet.factory;


import com.raistone.wallet.sealwallet.daoutils.HdWalletDaoUtils;
import com.raistone.wallet.sealwallet.greendao.HdWalletDao;
import com.raistone.wallet.sealwallet.utils.MultiChainCreateManager;

import java.util.Arrays;
import java.util.List;

/**
 * 钱包工厂管理类
 */
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

    /**
     * 生成一个钱包
     *
     * @param type //钱包类型
     */

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

    /**
     * 根据链类型生成地址
     *
     * @param chainId  链ID
     * @param coinType
     */
    public void createMultiAddress(String chainId, String coinType) {
        switch (coinType) {
            case MultiChainCreateManager.ETH_COIN_TYPE:

                break;
            case MultiChainCreateManager.NEO_COIN_TYPE:
                break;
            case MultiChainCreateManager.ONT_COIN_TYPE:
                break;
        }
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
