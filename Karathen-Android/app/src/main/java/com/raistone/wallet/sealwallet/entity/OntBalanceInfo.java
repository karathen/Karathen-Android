package com.raistone.wallet.sealwallet.entity;

public class OntBalanceInfo {


    /**
     * desc : SUCCESS
     * error : 0
     * id : 1
     * jsonrpc : 2.0
     * result : {"ont":"0","ong":"0"}
     */

    private String desc;
    private int error;
    private String id;
    private String jsonrpc;
    private ResultBean result;

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getError() {
        return error;
    }

    public void setError(int error) {
        this.error = error;
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

    public ResultBean getResult() {
        return result;
    }

    public void setResult(ResultBean result) {
        this.result = result;
    }

    public static class ResultBean {
        /**
         * ont : 0
         * ong : 0
         */

        private String ont;
        private String ong;

        public String getOnt() {
            return ont;
        }

        public void setOnt(String ont) {
            this.ont = ont;
        }

        public String getOng() {
            return ong;
        }

        public void setOng(String ong) {
            this.ong = ong;
        }
    }
}
