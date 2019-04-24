package com.raistone.wallet.sealwallet.factory;

import org.greenrobot.greendao.annotation.Entity;

import java.util.List;

/**
 * 硬件钱包
 */

@Entity
public class HardwareWallet extends WalletInfData {

    /**
     * 创建多链
     * @param chain
     */


    @Override
    public void createChain(Long wallId, List<String> chain) {

    }

    @Override
    public HdWallet createWallet(String walletName, String walletPwd) {
        return null;
    }

    @Override
    public HdWallet importWallet(String walletName, String walletPwd) {
        return null;
    }

}
