package com.raistone.wallet.sealwallet.entity;

import java.util.List;

public class OntTransactionByHashInfo {

    /**
     * desc : SUCCESS
     * error : 0
     * id : 1
     * jsonrpc : 2.0
     * result : {"Version":0,"Nonce":1544510015,"GasPrice":500,"GasLimit":20000,"Payer":"AURRtYmGrrg1hLiZWcWf62AiZN1SG5s7Vk","TxType":209,"Payload":{"Code":"00c66b148abbd080869814d1d195ef7c90ca133a55f7abc96a7cc8148abbd080869814d1d195ef7c90ca133a55f7abc96a7cc8516a7cc86c51c1087472616e736665721400000000000000000000000000000000000000010068164f6e746f6c6f67792e4e61746976652e496e766f6b65"},"Attributes":[],"Sigs":[{"PubKeys":["03450b3d8d342aa1beb3f977a645f3e432af22b7bc15f4c9b148f55bd83e871c82"],"M":1,"SigData":["9c8596edf34a5b69c9fb53e837e5027f29ea383bdb3fa58678cb0f5ea1d545bf32181d258cfffd336d0b0ee29f5c2ecdd74f67d410a396c07f3a6ba9a0ae619e"]}],"Hash":"8b7c25b18e4d34d1cbd0b2bd6b3064419bd22c3af1453f0581e73a9d190feafb","Height":1177727}
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
         * Version : 0
         * Nonce : 1544510015
         * GasPrice : 500
         * GasLimit : 20000
         * Payer : AURRtYmGrrg1hLiZWcWf62AiZN1SG5s7Vk
         * TxType : 209
         * Payload : {"Code":"00c66b148abbd080869814d1d195ef7c90ca133a55f7abc96a7cc8148abbd080869814d1d195ef7c90ca133a55f7abc96a7cc8516a7cc86c51c1087472616e736665721400000000000000000000000000000000000000010068164f6e746f6c6f67792e4e61746976652e496e766f6b65"}
         * Attributes : []
         * Sigs : [{"PubKeys":["03450b3d8d342aa1beb3f977a645f3e432af22b7bc15f4c9b148f55bd83e871c82"],"M":1,"SigData":["9c8596edf34a5b69c9fb53e837e5027f29ea383bdb3fa58678cb0f5ea1d545bf32181d258cfffd336d0b0ee29f5c2ecdd74f67d410a396c07f3a6ba9a0ae619e"]}]
         * Hash : 8b7c25b18e4d34d1cbd0b2bd6b3064419bd22c3af1453f0581e73a9d190feafb
         * Height : 1177727
         */

        private int Version;
        private int Nonce;
        private int GasPrice;
        private int GasLimit;
        private String Payer;
        private int TxType;
        private PayloadBean Payload;
        private String Hash;
        private int Height;
        private List<?> Attributes;
        private List<SigsBean> Sigs;

        public int getVersion() {
            return Version;
        }

        public void setVersion(int Version) {
            this.Version = Version;
        }

        public int getNonce() {
            return Nonce;
        }

        public void setNonce(int Nonce) {
            this.Nonce = Nonce;
        }

        public int getGasPrice() {
            return GasPrice;
        }

        public void setGasPrice(int GasPrice) {
            this.GasPrice = GasPrice;
        }

        public int getGasLimit() {
            return GasLimit;
        }

        public void setGasLimit(int GasLimit) {
            this.GasLimit = GasLimit;
        }

        public String getPayer() {
            return Payer;
        }

        public void setPayer(String Payer) {
            this.Payer = Payer;
        }

        public int getTxType() {
            return TxType;
        }

        public void setTxType(int TxType) {
            this.TxType = TxType;
        }

        public PayloadBean getPayload() {
            return Payload;
        }

        public void setPayload(PayloadBean Payload) {
            this.Payload = Payload;
        }

        public String getHash() {
            return Hash;
        }

        public void setHash(String Hash) {
            this.Hash = Hash;
        }

        public int getHeight() {
            return Height;
        }

        public void setHeight(int Height) {
            this.Height = Height;
        }

        public List<?> getAttributes() {
            return Attributes;
        }

        public void setAttributes(List<?> Attributes) {
            this.Attributes = Attributes;
        }

        public List<SigsBean> getSigs() {
            return Sigs;
        }

        public void setSigs(List<SigsBean> Sigs) {
            this.Sigs = Sigs;
        }

        public static class PayloadBean {
            /**
             * Code : 00c66b148abbd080869814d1d195ef7c90ca133a55f7abc96a7cc8148abbd080869814d1d195ef7c90ca133a55f7abc96a7cc8516a7cc86c51c1087472616e736665721400000000000000000000000000000000000000010068164f6e746f6c6f67792e4e61746976652e496e766f6b65
             */

            private String Code;

            public String getCode() {
                return Code;
            }

            public void setCode(String Code) {
                this.Code = Code;
            }
        }

        public static class SigsBean {
            /**
             * PubKeys : ["03450b3d8d342aa1beb3f977a645f3e432af22b7bc15f4c9b148f55bd83e871c82"]
             * M : 1
             * SigData : ["9c8596edf34a5b69c9fb53e837e5027f29ea383bdb3fa58678cb0f5ea1d545bf32181d258cfffd336d0b0ee29f5c2ecdd74f67d410a396c07f3a6ba9a0ae619e"]
             */

            private int M;
            private List<String> PubKeys;
            private List<String> SigData;

            public int getM() {
                return M;
            }

            public void setM(int M) {
                this.M = M;
            }

            public List<String> getPubKeys() {
                return PubKeys;
            }

            public void setPubKeys(List<String> PubKeys) {
                this.PubKeys = PubKeys;
            }

            public List<String> getSigData() {
                return SigData;
            }

            public void setSigData(List<String> SigData) {
                this.SigData = SigData;
            }
        }
    }
}
