package com.raistone.wallet.sealwallet.entity;

import java.util.List;

public class TransactionReceiptInfo {


    /**
     * jsonrpc : 2.0
     * id : 1
     * result : {"blockHash":"0x4b3a3257e9cc0d67838877875a12b7b3dba4f7700ca8cf7f893ec92d9e694a4f","blockNumber":"0x66d0b8","contractAddress":null,"cumulativeGasUsed":"0x6984bf","from":"0x0df63ff925a5e0e9632a109e5115d9936a33036b","gasUsed":"0x5208","logs":[],"logsBloom":"0x00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000","status":"0x1","to":"0x0df63ff925a5e0e9632a109e5115d9936a33036b","transactionHash":"0xc0832b065e586759bfd514b438208c2e4b5f3b1a9d3e6c7452eec7c8a19cc0d2","transactionIndex":"0x81"}
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
         * blockHash : 0x4b3a3257e9cc0d67838877875a12b7b3dba4f7700ca8cf7f893ec92d9e694a4f
         * blockNumber : 0x66d0b8
         * contractAddress : null
         * cumulativeGasUsed : 0x6984bf
         * from : 0x0df63ff925a5e0e9632a109e5115d9936a33036b
         * gasUsed : 0x5208
         * logs : []
         * logsBloom : 0x00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000
         * status : 0x1
         * to : 0x0df63ff925a5e0e9632a109e5115d9936a33036b
         * transactionHash : 0xc0832b065e586759bfd514b438208c2e4b5f3b1a9d3e6c7452eec7c8a19cc0d2
         * transactionIndex : 0x81
         */

        private String blockHash;
        private String blockNumber;
        private Object contractAddress;
        private String cumulativeGasUsed;
        private String from;
        private String gasUsed;
        private String logsBloom;
        private String status;
        private String to;
        private String transactionHash;
        private String transactionIndex;
        private List<?> logs;

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

        public Object getContractAddress() {
            return contractAddress;
        }

        public void setContractAddress(Object contractAddress) {
            this.contractAddress = contractAddress;
        }

        public String getCumulativeGasUsed() {
            return cumulativeGasUsed;
        }

        public void setCumulativeGasUsed(String cumulativeGasUsed) {
            this.cumulativeGasUsed = cumulativeGasUsed;
        }

        public String getFrom() {
            return from;
        }

        public void setFrom(String from) {
            this.from = from;
        }

        public String getGasUsed() {
            return gasUsed;
        }

        public void setGasUsed(String gasUsed) {
            this.gasUsed = gasUsed;
        }

        public String getLogsBloom() {
            return logsBloom;
        }

        public void setLogsBloom(String logsBloom) {
            this.logsBloom = logsBloom;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getTo() {
            return to;
        }

        public void setTo(String to) {
            this.to = to;
        }

        public String getTransactionHash() {
            return transactionHash;
        }

        public void setTransactionHash(String transactionHash) {
            this.transactionHash = transactionHash;
        }

        public String getTransactionIndex() {
            return transactionIndex;
        }

        public void setTransactionIndex(String transactionIndex) {
            this.transactionIndex = transactionIndex;
        }

        public List<?> getLogs() {
            return logs;
        }

        public void setLogs(List<?> logs) {
            this.logs = logs;
        }
    }
}
