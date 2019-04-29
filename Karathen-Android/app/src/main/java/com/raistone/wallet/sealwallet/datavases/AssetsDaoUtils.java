package com.raistone.wallet.sealwallet.datavases;


import com.raistone.wallet.sealwallet.entity.AssetsInfo;
import com.raistone.wallet.sealwallet.entity.DataBean_Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.List;

public class AssetsDaoUtils {


    public static void insertNewAssets(AssetsInfo.DataBean bean, String address) {
        bean.setWalletAddress(address);
        bean.insert();
    }

    public static AssetsInfo.DataBean findSignAssets(String tokenAddress,String walletAddress) {
        AssetsInfo.DataBean dataBean = SQLite.select().from(AssetsInfo.DataBean.class).where(DataBean_Table.tokenAddress.eq(tokenAddress)).and(DataBean_Table.walletAddress.eq(walletAddress)).querySingle();

        return dataBean;
    }

    public static List<AssetsInfo.DataBean> findAllAssetsByAddress(String walletAddress) {
        List<AssetsInfo.DataBean> dataBeans = SQLite.select().from(AssetsInfo.DataBean.class).where(DataBean_Table.walletAddress.eq(walletAddress)).queryList();
        return dataBeans;
    }


    public static List<AssetsInfo.DataBean> findAllAssets() {
        List<AssetsInfo.DataBean> dataBeans = SQLite.select().from(AssetsInfo.DataBean.class).queryList();

        return dataBeans;
    }



    public static List<AssetsInfo.DataBean> findAllAssetsByWalletAddress(String address) {
        List<AssetsInfo.DataBean> dataBeans = SQLite.select().from(AssetsInfo.DataBean.class).where(DataBean_Table.walletAddress.eq(address)).queryList();
        return dataBeans;
    }


    public static void updatePrice(AssetsInfo.DataBean bean, String address) {
        SQLite.update(AssetsInfo.DataBean.class).set(DataBean_Table.price.eq(bean.getPrice()), DataBean_Table.priceUSD.eq(bean.getPriceUSD())).where(DataBean_Table.walletAddress.eq(address)).and(DataBean_Table.tokenAddress.eq(bean.getTokenAddress())).async().execute();


    }


}
