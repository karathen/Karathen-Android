package com.raistone.wallet.sealwallet.entity;

public class NeoBalanceInfo {


    /**
     * id : 1
     * jsonrpc : 2.0
     * result : 0
     */

    private String id;
    private String jsonrpc;
    private String result;

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

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
