package com.raistone.wallet.sealwallet.factory;

import com.raistone.wallet.sealwallet.datavases.AppDataBase;
import com.raistone.wallet.sealwallet.entity.AssetsInfo;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;


public class AssetsInfoData {


    private String id;

    private String jsonrpc;
    private List<AssetsDeatilInfo> result;

    public List<AssetsDeatilInfo> getResult() {
        return result;
    }

    public void setResult(List<AssetsDeatilInfo> result) {
        this.result = result;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getJsonrpc() {
        return jsonrpc;
    }

    public void setJsonrpc(String jsonrpc) {
        this.jsonrpc = jsonrpc;
    }
}
