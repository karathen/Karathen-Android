package com.raistone.wallet.sealwallet.factory;

import java.util.List;

public abstract class WalletInfData {


    public abstract void createChain(Long wallId,List<String> chain);
    public abstract HdWallet createWallet(String walletName,String walletPwd);
    public abstract HdWallet importWallet(String walletName,String walletPwd);



}
