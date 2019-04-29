package com.raistone.wallet.sealwallet.factory;

import org.greenrobot.greendao.annotation.Entity;

import java.util.List;


@Entity
public class HardwareWallet extends WalletInfData {



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
