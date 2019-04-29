package com.raistone.wallet.sealwallet.datavases;

import android.content.Context;

import com.raistone.wallet.sealwallet.daoutils.AssetsDetailDaoUtils;
import com.raistone.wallet.sealwallet.entity.AssetsInfo;
import com.raistone.wallet.sealwallet.factory.AssetsDeatilInfo;
import com.raistone.wallet.sealwallet.factory.AssetsInfoData;
import com.raistone.wallet.sealwallet.greendao.AssetsDeatilInfoDao;
import com.raistone.wallet.sealwallet.utils.CommonUtils;
import com.raistone.wallet.sealwallet.utils.GsonUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class LocalDataUtils {


    public static void setAddresByEthAssets(String address, Context context) {

        String json = CommonUtils.getJson("assets.json", context);

        AssetsInfo assetsInfo = GsonUtils.decodeJSON(json, AssetsInfo.class);

        List<AssetsInfo.DataBean> result = assetsInfo.getResult();
        for (int i = 0; i < result.size(); i++) {
            AssetsInfo.DataBean dataBean = result.get(i);
            dataBean.setWalletAddress(address);
            dataBean.insert();
        }
    }

    public static void setAddresByAssets(String accountId,String fileName,Long chainAddressId, String address,Context context) {

        String json = CommonUtils.getJson(fileName, context);

        AssetsInfoData assetsInfo = GsonUtils.decodeJSON(json, AssetsInfoData.class);

        List<AssetsDeatilInfo> result = assetsInfo.getResult();
        for (int i = 0; i < result.size(); i++) {
            AssetsDeatilInfo dataBean = result.get(i);

            dataBean.setAsset_name(accountId);

            dataBean.setChainAddressId(chainAddressId);

            dataBean.setWalletAddress(address);

            AssetsDetailDaoUtils.insertNewAssets(dataBean);
        }

    }

    public static void setAddresByNeoAssets(String address, Context context) {

        String json = CommonUtils.getJson("neo_assets.json", context);

        AssetsInfo assetsInfo = GsonUtils.decodeJSON(json, AssetsInfo.class);

        List<AssetsInfo.DataBean> result = assetsInfo.getResult();
        for (int i = 0; i < result.size(); i++) {
            AssetsInfo.DataBean dataBean = result.get(i);
            dataBean.setWalletAddress(address);
            dataBean.insert();
        }
    }


    public static void setAddresByOntAssets(String address, Context context) {

        String json = CommonUtils.getJson("ont_assets.json", context);

        AssetsInfo assetsInfo = GsonUtils.decodeJSON(json, AssetsInfo.class);

        List<AssetsInfo.DataBean> result = assetsInfo.getResult();
        for (int i = 0; i < result.size(); i++) {
            AssetsInfo.DataBean dataBean = result.get(i);
            dataBean.setWalletAddress(address);
            dataBean.insert();
        }
    }
}
