package com.raistone.wallet.sealwallet.factory;

import java.util.List;

public abstract class WalletInfData {


    public abstract void createChain(Long wallId,List<String> chain);//创建链的方法
    public abstract HdWallet createWallet(String walletName,String walletPwd);//创建钱包的方法
    public abstract HdWallet importWallet(String walletName,String walletPwd);//导入钱包的方法



}
