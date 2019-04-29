package com.raistone.wallet.sealwallet.entity;

public class NeoResultInfo {

    /**
     * id : 1
     * jsonrpc : 2.0
     * result : false
     */

    private String id;
    private String jsonrpc;
    private boolean result;

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

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }
}
