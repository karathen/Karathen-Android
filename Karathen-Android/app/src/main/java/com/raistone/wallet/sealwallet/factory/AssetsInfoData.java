package com.raistone.wallet.sealwallet.factory;

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
