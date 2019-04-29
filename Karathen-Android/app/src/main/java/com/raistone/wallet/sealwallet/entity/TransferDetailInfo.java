package com.raistone.wallet.sealwallet.entity;


import java.io.Serializable;
import java.util.List;

public class TransferDetailInfo {

    /**
     * id : 1
     * jsonrpc : 2.0
     * result : [{"addressFrom":"0x0df63ff925a5e0e9632a109e5115d9936a33036b","addressTo":"0x64fc642d0824a9d16e79c1c0bd612fa795485468","asset":"0xc9ad73d11d272c95b5a2c48780a55b6b3c726cac","blockNumber":"6666239","blockTime":"1541679803","gas":"60000","gasPrice":"13000000000","txId":"0xcb26a3834f2a55e1179bfb77c3b88a37b43e76a93daaa77c04c7997fc6673a6b","txReceiptStatus":"1","value":"100000000"},{"addressFrom":"0x0df63ff925a5e0e9632a109e5115d9936a33036b","addressTo":"0xfe98659fd7ef96e23fbca95860059acca8babd1a","asset":"0xc9ad73d11d272c95b5a2c48780a55b6b3c726cac","blockNumber":"6412935","blockTime":"1538106334","gas":"60000","gasPrice":"9000000000","gasUsed":"52295","txId":"0xdb03c34b1c3504ba4677ddd94ad492a6683dc0c6361d01f21c99ac764f11ef94","txReceiptStatus":"1","value":"1000000000"},{"addressFrom":"0x8fc48bbbf0342983c9b14008053940b8d1fc66c6","addressTo":"0x0df63ff925a5e0e9632a109e5115d9936a33036b","asset":"0xc9ad73d11d272c95b5a2c48780a55b6b3c726cac","blockNumber":"6372677","blockTime":"1537535979","gas":"60000","gasPrice":"10000000000","gasUsed":"37295","txId":"0x94ae07f6805c8ee1027587dcf4ef8bc94335c5ce8f066cb5dca795a125dfcce9","txReceiptStatus":"1","value":"100000000"},{"addressFrom":"0x8fc48bbbf0342983c9b14008053940b8d1fc66c6","addressTo":"0x0df63ff925a5e0e9632a109e5115d9936a33036b","asset":"0xc9ad73d11d272c95b5a2c48780a55b6b3c726cac","blockNumber":"6372655","blockTime":"1537535579","gas":"60000","gasPrice":"7000000000","gasUsed":"37295","txId":"0x227faaa5310ecef39cdbda2fbf6041145e5eb9bcf26370a16cb2427974182bfc","txReceiptStatus":"1","value":"100000000"},{"addressFrom":"0x0df63ff925a5e0e9632a109e5115d9936a33036b","addressTo":"0xfe98659fd7ef96e23fbca95860059acca8babd1a","asset":"0xc9ad73d11d272c95b5a2c48780a55b6b3c726cac","blockNumber":"6336187","blockTime":"1537014158","gas":"60000","gasPrice":"4000000000","gasUsed":"37295","txId":"0xc842c33946b1c232303c8d5f1a497048b62255ee10bfca4655bf7febd63935e5","txReceiptStatus":"1","value":"1000000000"}]
     */

    private String id;
    private String jsonrpc;
    private List<ResultBean> result;

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

    public List<ResultBean> getResult() {
        return result;
    }

    public void setResult(List<ResultBean> result) {
        this.result = result;
    }

    public static class ResultBean implements Serializable{
        /**
         * addressFrom : 0x0df63ff925a5e0e9632a109e5115d9936a33036b
         * addressTo : 0x64fc642d0824a9d16e79c1c0bd612fa795485468
         * asset : 0xc9ad73d11d272c95b5a2c48780a55b6b3c726cac
         * blockNumber : 6666239
         * blockTime : 1541679803
         * gas : 60000
         * gasPrice : 13000000000
         * txId : 0xcb26a3834f2a55e1179bfb77c3b88a37b43e76a93daaa77c04c7997fc6673a6b
         * txReceiptStatus : 1
         * value : 100000000
         * gasUsed : 52295
         */

        private String addressFrom;

        private String addressTo;

        private String asset;

        private String blockNumber;

        private String blockTime;

        private String gas;

        private String gasPrice;

        private String txId;

        private String txReceiptStatus;

        private String value;

        private String gasUsed;

        public String getAddressFrom() {
            return addressFrom;
        }

        public void setAddressFrom(String addressFrom) {
            this.addressFrom = addressFrom;
        }

        public String getAddressTo() {
            return addressTo;
        }

        public void setAddressTo(String addressTo) {
            this.addressTo = addressTo;
        }

        public String getAsset() {
            return asset;
        }

        public void setAsset(String asset) {
            this.asset = asset;
        }

        public String getBlockNumber() {
            return blockNumber;
        }

        public void setBlockNumber(String blockNumber) {
            this.blockNumber = blockNumber;
        }

        public String getBlockTime() {
            return blockTime;
        }

        public void setBlockTime(String blockTime) {
            this.blockTime = blockTime;
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

        public String getTxId() {
            return txId;
        }

        public void setTxId(String txId) {
            this.txId = txId;
        }

        public String getTxReceiptStatus() {
            return txReceiptStatus;
        }

        public void setTxReceiptStatus(String txReceiptStatus) {
            this.txReceiptStatus = txReceiptStatus;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getGasUsed() {
            return gasUsed;
        }

        public void setGasUsed(String gasUsed) {
            this.gasUsed = gasUsed;
        }
    }
}
