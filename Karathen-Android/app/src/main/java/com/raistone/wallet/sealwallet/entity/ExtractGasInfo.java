package com.raistone.wallet.sealwallet.entity;

public class ExtractGasInfo {


    /**
     * id : 1
     * jsonrpc : 2.0
     * result : {"txData":"02000129e1fa07f0c52a8c3691779971ef9f5e17f785bfff03b288cfda15b23afca6b80000000001e72d286979ee6cb1b7e65dfddfb2e384100b8d148e7758de42e4168b71792c6000eb040000000000d904f61978b83b706445d2c418e336de4d6261d4 ","txid":"0x11bb31ee8e3cf36fd5c7e28c8714299b4c61088f83abc3b3dbdc10ca3a952dc1 ","witness":"014140{signature}2321{pubkey}ac"}
     */

    private int id;
    private String jsonrpc;
    private ResultBean result;

    public int getId() {
        return id;
    }

    public void setId(int id) {
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
         * txData : 02000129e1fa07f0c52a8c3691779971ef9f5e17f785bfff03b288cfda15b23afca6b80000000001e72d286979ee6cb1b7e65dfddfb2e384100b8d148e7758de42e4168b71792c6000eb040000000000d904f61978b83b706445d2c418e336de4d6261d4
         * txid : 0x11bb31ee8e3cf36fd5c7e28c8714299b4c61088f83abc3b3dbdc10ca3a952dc1
         * witness : 014140{signature}2321{pubkey}ac
         */

        private String txData;
        private String txid;
        private String witness;

        public String getTxData() {
            return txData;
        }

        public void setTxData(String txData) {
            this.txData = txData;
        }

        public String getTxid() {
            return txid;
        }

        public void setTxid(String txid) {
            this.txid = txid;
        }

        public String getWitness() {
            return witness;
        }

        public void setWitness(String witness) {
            this.witness = witness;
        }
    }
}
