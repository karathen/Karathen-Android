package com.raistone.wallet.sealwallet.entity;

public class UpdateInfo {


    /**
     * id : 1
     * jsonrpc : 2.0
     * result : {"content":"first version","forceUpdate":false,"needUpdate":true,"url":"https://tyimg.zjrs.ltd/sealWallet.apk","version":"1.0.1"}
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
         * content : first version
         * forceUpdate : false
         * needUpdate : true
         * url : https://tyimg.zjrs.ltd/sealWallet.apk
         * version : 1.0.1
         */

        private String content;
        private boolean forceUpdate;
        private boolean needUpdate;
        private String url;
        private String version;

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public boolean isForceUpdate() {
            return forceUpdate;
        }

        public void setForceUpdate(boolean forceUpdate) {
            this.forceUpdate = forceUpdate;
        }

        public boolean isNeedUpdate() {
            return needUpdate;
        }

        public void setNeedUpdate(boolean needUpdate) {
            this.needUpdate = needUpdate;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }
    }
}
