package com.raistone.wallet.sealwallet.entity;

public class TransactionByHashInfo {


    /**
     * jsonrpc : 2.0
     * id : 1
     * result : {"blockHash":"0x4c0a5e3bf0f110382d5c9900ca0ef0273b868a4200844054382e43653303daa1","blockNumber":"0x61f52d","from":"0x8fc48bbbf0342983c9b14008053940b8d1fc66c6","gas":"0xea60","gasPrice":"0x37e11d600","hash":"0x44e97eff74a57033171285c0a7c0a73d4afd59173c140731b9f1ab41ecec28b5","input":"0xa9059cbb0000000000000000000000008fc48bbbf0342983c9b14008053940b8d1fc66c60000000000000000000000000000000000000000000000000000000005f5e100","nonce":"0x16","to":"0xc9ad73d11d272c95b5a2c48780a55b6b3c726cac","transactionIndex":"0x4d","value":"0x0","v":"0x25","r":"0x203cabd9f8455a20b947d9d138edd4120644d21ec6e65a9ba737206a6a72833d","s":"0x28041af95095c54d2403cf57dfa0eee7f0b47857a491d6494274612401519c8e"}
     */

    private String jsonrpc;
    private int id;
    private ResultBean result;

    public String getJsonrpc() {
        return jsonrpc;
    }

    public void setJsonrpc(String jsonrpc) {
        this.jsonrpc = jsonrpc;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ResultBean getResult() {
        return result;
    }

    public void setResult(ResultBean result) {
        this.result = result;
    }

    public static class ResultBean {
        /**
         * blockHash : 0x4c0a5e3bf0f110382d5c9900ca0ef0273b868a4200844054382e43653303daa1
         * blockNumber : 0x61f52d
         * from : 0x8fc48bbbf0342983c9b14008053940b8d1fc66c6
         * gas : 0xea60
         * gasPrice : 0x37e11d600
         * hash : 0x44e97eff74a57033171285c0a7c0a73d4afd59173c140731b9f1ab41ecec28b5
         * input : 0xa9059cbb0000000000000000000000008fc48bbbf0342983c9b14008053940b8d1fc66c60000000000000000000000000000000000000000000000000000000005f5e100
         * nonce : 0x16
         * to : 0xc9ad73d11d272c95b5a2c48780a55b6b3c726cac
         * transactionIndex : 0x4d
         * value : 0x0
         * v : 0x25
         * r : 0x203cabd9f8455a20b947d9d138edd4120644d21ec6e65a9ba737206a6a72833d
         * s : 0x28041af95095c54d2403cf57dfa0eee7f0b47857a491d6494274612401519c8e
         */

        private String blockHash;
        private String blockNumber;
        private String from;
        private String gas;
        private String gasPrice;
        private String hash;
        private String input;
        private String nonce;
        private String to;
        private String transactionIndex;
        private String value;
        private String v;
        private String r;
        private String s;

        public String getBlockHash() {
            return blockHash;
        }

        public void setBlockHash(String blockHash) {
            this.blockHash = blockHash;
        }

        public String getBlockNumber() {
            return blockNumber;
        }

        public void setBlockNumber(String blockNumber) {
            this.blockNumber = blockNumber;
        }

        public String getFrom() {
            return from;
        }

        public void setFrom(String from) {
            this.from = from;
        }

        public String getGas() {
            return gas;
        }

        public void setGas(String gas) {
            this.gas = gas;
        }

        public String getGasPrice() {
            return gasPrice;
        }

        public void setGasPrice(String gasPrice) {
            this.gasPrice = gasPrice;
        }

        public String getHash() {
            return hash;
        }

        public void setHash(String hash) {
            this.hash = hash;
        }

        public String getInput() {
            return input;
        }

        public void setInput(String input) {
            this.input = input;
        }

        public String getNonce() {
            return nonce;
        }

        public void setNonce(String nonce) {
            this.nonce = nonce;
        }

        public String getTo() {
            return to;
        }

        public void setTo(String to) {
            this.to = to;
        }

        public String getTransactionIndex() {
            return transactionIndex;
        }

        public void setTransactionIndex(String transactionIndex) {
            this.transactionIndex = transactionIndex;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getV() {
            return v;
        }

        public void setV(String v) {
            this.v = v;
        }

        public String getR() {
            return r;
        }

        public void setR(String r) {
            this.r = r;
        }

        public String getS() {
            return s;
        }

        public void setS(String s) {
            this.s = s;
        }
    }
}
