package com.raistone.wallet.sealwallet.factory;

import com.raistone.wallet.sealwallet.daoutils.HdWalletDaoUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 钱包管理对象
 */
public class WalletManager {

    // Version 2
    private static WalletManager instance;

    private WalletManager() {
    }

    public static synchronized WalletManager getInstance() {
        if (instance == null) {
            instance = new WalletManager();
        }
        return instance;
    }

    /**
     * 获取钱包对象
     * @return
     */
    public HdWallet getWalletChains(){
        List<HdWallet> wallets = HdWalletDaoUtils.findAllWallet();

        HdWallet hdWallet = wallets.get(0);

        return hdWallet;
    }


    /**
     * 根据钱包id获取钱包
     */
    public HdWallet getWalletChains(long walletId){
        HdWallet wallet = HdWalletDaoUtils.findAllWalletById(walletId);

        return wallet;
    }

    /**
     * 获取当前钱包支持的链
     */
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

    /**
     *  根据下标获取当前选中的是哪条链
     * @param selectChainId
     */
    public ChainDataInfo getSelectChain(int selectChainId){
        HdWallet chains = WalletManager.getInstance().getWalletChains();

        //当前选中的链
        ChainDataInfo dataInfo = chains.getChainDataInfos().get(selectChainId);

        return dataInfo;
    }


    /**
     * 根据选中的链获取当前选中地址的资产
     */
    public List<AssetsDeatilInfo> getSelectChainAddress(int selectChainId,int chainId){
        List<AssetsDeatilInfo> assetsInfoDataList = getSelectChain(selectChainId).getChainAddressInfos().get(chainId).getAssetsInfoDataList();

        return assetsInfoDataList;
    }


    public static void main(String[] args){
        getInstance().getWalletSupportChains();
    }
}
