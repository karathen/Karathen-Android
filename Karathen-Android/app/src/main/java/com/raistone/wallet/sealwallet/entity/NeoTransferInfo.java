package com.raistone.wallet.sealwallet.entity;

public class NeoTransferInfo {


    /**
     * id : 1
     * jsonrpc : 2.0
     * result : {"txData":"8000022055c6f3493f9c0117fcefdb8eeb5b08d37b164104f0045bffb03501d49cbe86eafef41e625b2f13c1f9ceffb28186445a06b57db1fde1bdba9c297a0000019b7cffdaa674beae0f930ebe6085af9093e5fe56b34a5c220ccdcf6efc336fc500e1f5050000000055c6f3493f9c0117fcefdb8eeb5b08d37b164104","txid":"0xb4786dd03e81f8113a4e555959a6a58b621f90e9e2b7a50625e6dc2e8375e41f","witness":"014140{signature}2321{pubkey}ac"}
     */

    private String id;
    private String jsonrpc;
    private ResultBean result;

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
         * txData : 8000022055c6f3493f9c0117fcefdb8eeb5b08d37b164104f0045bffb03501d49cbe86eafef41e625b2f13c1f9ceffb28186445a06b57db1fde1bdba9c297a0000019b7cffdaa674beae0f930ebe6085af9093e5fe56b34a5c220ccdcf6efc336fc500e1f5050000000055c6f3493f9c0117fcefdb8eeb5b08d37b164104
         * txid : 0xb4786dd03e81f8113a4e555959a6a58b621f90e9e2b7a50625e6dc2e8375e41f
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
