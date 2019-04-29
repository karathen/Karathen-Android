package com.raistone.wallet.sealwallet.factory;

import com.raistone.wallet.sealwallet.daoutils.HdWalletDaoUtils;

import java.util.ArrayList;
import java.util.List;

public class WalletManager {

    private static WalletManager instance;

    private WalletManager() {
    }

    public static synchronized WalletManager getInstance() {
        if (instance == null) {
            instance = new WalletManager();
        }
        return instance;
    }

    public HdWallet getWalletChains(){
        List<HdWallet> wallets = HdWalletDaoUtils.findAllWallet();

        HdWallet hdWallet = wallets.get(0);

        return hdWallet;
    }

    public HdWallet getWalletChains(long walletId){
        HdWallet wallet = HdWalletDaoUtils.findAllWalletById(walletId);

        return wallet;
    }

    public  List<ChainDataInfo> getWalletSupportChains(){
        HdWallet hdWallet = getWalletChains();

        List<ChainDataInfo> chainDataInfos = hdWallet.getChainDataInfos();

        List<ChainAddressInfo> datas=new ArrayList<>();

        for (int i=0;i<chainDataInfos.size();i++){
            ChainDataInfo dataInfo = chainDataInfos.get(i);
            List<ChainAddressInfo> addressInfos = dataInfo.getChainAddressInfos();

            for (ChainAddressInfo addressInfo:addressInfos) {
                if(addressInfo.getIsCurrent()){
                    datas.add(addressInfo);
                }
            }
        }

        return chainDataInfos;
    }


    public static void main(String[] args){
        getInstance().getWalletSupportChains();
    }
}
