package com.raistone.wallet.sealwallet.entity;

import java.util.List;

public class NeoTransactionByHashInfo {


    /**
     * vouts : [{"value":1,"txid":"74e01e27e49718311c387bf6d0793cc27a3ed7ee59b1f29b1db085c10515d6df","n":0,"asset":"NEO","address_hash":"Aa4QEM38V2kaDFZJvkuA2MZctDnHgPrSix"}]
     * vin : [{"value":1,"txid":"bc9ed39ce8980dae336ddc8ad18308d64bd4a5f7a06bae069b0c1b3071c7bc64","n":0,"asset":"NEO","address_hash":"Aa4QEM38V2kaDFZJvkuA2MZctDnHgPrSix"}]
     * version : 0
     * type : ContractTransaction
     * txid : 74e01e27e49718311c387bf6d0793cc27a3ed7ee59b1f29b1db085c10515d6df
     * time : 1538203107
     * sys_fee : 0
     * size : 229
     * scripts : [{"verification":"2103d7c0a517d4517399f45200801c52ab712772302c30ce66f38d339bedbe60cc56ac","invocation":"40ab8464f5af2e5aa10e0ecfe3e227e1a430ecb60ea82f59252a4692080aa3f12b120cdb3bfeaa96bc7eec4e2b39d30311269bdf85ec0a67272ac563a73ff58bf3"}]
     * pubkey : null
     * nonce : null
     * net_fee : 0
     * description : null
     * contract : null
     * claims : []
     * block_height : 2786864
     * block_hash : 3425fd71e88ce0c3a1784c02315e22579b6e99fa9eea2fadeda4fe83e7544afc
     * attributes : [{"usage":"Script","data":"c892730b17606e976294aeae06f8de83e92680b8"},{"usage":"Remark","data":"5baf1dc7"}]
     * asset : null
     */

    private int version;
    private String type;
    private String txid;
    private int time;
    private int sys_fee;
    private int size;
    private Object pubkey;
    private Object nonce;
    private int net_fee;
    private Object description;
    private Object contract;
    private int block_height;
    private String block_hash;
    private Object asset;
    private List<VoutsBean> vouts;
    private List<VinBean> vin;
    private List<ScriptsBean> scripts;
    private List<?> claims;
    private List<AttributesBean> attributes;

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTxid() {
        return txid;
    }

    public void setTxid(String txid) {
        this.txid = txid;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getSys_fee() {
        return sys_fee;
    }

    public void setSys_fee(int sys_fee) {
        this.sys_fee = sys_fee;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public Object getPubkey() {
        return pubkey;
    }

    public void setPubkey(Object pubkey) {
        this.pubkey = pubkey;
    }

    public Object getNonce() {
        return nonce;
    }

    public void setNonce(Object nonce) {
        this.nonce = nonce;
    }

    public int getNet_fee() {
        return net_fee;
    }

    public void setNet_fee(int net_fee) {
        this.net_fee = net_fee;
    }

    public Object getDescription() {
        return description;
    }

    public void setDescription(Object description) {
        this.description = description;
    }

    public Object getContract() {
        return contract;
    }

    public void setContract(Object contract) {
        this.contract = contract;
    }

    public int getBlock_height() {
        return block_height;
    }

    public void setBlock_height(int block_height) {
        this.block_height = block_height;
    }

    public String getBlock_hash() {
        return block_hash;
    }

    public void setBlock_hash(String block_hash) {
        this.block_hash = block_hash;
    }

    public Object getAsset() {
        return asset;
    }

    public void setAsset(Object asset) {
        this.asset = asset;
    }

    public List<VoutsBean> getVouts() {
        return vouts;
    }

    public void setVouts(List<VoutsBean> vouts) {
        this.vouts = vouts;
    }

    public List<VinBean> getVin() {
        return vin;
    }

    public void setVin(List<VinBean> vin) {
        this.vin = vin;
    }

    public List<ScriptsBean> getScripts() {
        return scripts;
    }

    public void setScripts(List<ScriptsBean> scripts) {
        this.scripts = scripts;
    }

    public List<?> getClaims() {
        return claims;
    }

    public void setClaims(List<?> claims) {
        this.claims = claims;
    }

    public List<AttributesBean> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<AttributesBean> attributes) {
        this.attributes = attributes;
    }

    public static class VoutsBean {
        /**
         * value : 1
         * txid : 74e01e27e49718311c387bf6d0793cc27a3ed7ee59b1f29b1db085c10515d6df
         * n : 0
         * asset : NEO
         * address_hash : Aa4QEM38V2kaDFZJvkuA2MZctDnHgPrSix
         */

        private int value;
        private String txid;
        private int n;
        private String asset;
        private String address_hash;

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }

        public String getTxid() {
            return txid;
        }

        public void setTxid(String txid) {
            this.txid = txid;
        }

        public int getN() {
            return n;
        }

        public void setN(int n) {
            this.n = n;
        }

        public String getAsset() {
            return asset;
        }

        public void setAsset(String asset) {
            this.asset = asset;
        }

        public String getAddress_hash() {
            return address_hash;
        }

        public void setAddress_hash(String address_hash) {
            this.address_hash = address_hash;
        }
    }

    public static class VinBean {
        /**
         * value : 1
         * txid : bc9ed39ce8980dae336ddc8ad18308d64bd4a5f7a06bae069b0c1b3071c7bc64
         * n : 0
         * asset : NEO
         * address_hash : Aa4QEM38V2kaDFZJvkuA2MZctDnHgPrSix
         */

        private int value;
        private String txid;
        private int n;
        private String asset;
        private String address_hash;

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }

        public String getTxid() {
            return txid;
        }

        public void setTxid(String txid) {
            this.txid = txid;
        }

        public int getN() {
            return n;
        }

        public void setN(int n) {
            this.n = n;
        }

        public String getAsset() {
            return asset;
        }

        public void setAsset(String asset) {
            this.asset = asset;
        }

        public String getAddress_hash() {
            return address_hash;
        }

        public void setAddress_hash(String address_hash) {
            this.address_hash = address_hash;
        }
    }

    public static class ScriptsBean {
        /**
         * verification : 2103d7c0a517d4517399f45200801c52ab712772302c30ce66f38d339bedbe60cc56ac
         * invocation : 40ab8464f5af2e5aa10e0ecfe3e227e1a430ecb60ea82f59252a4692080aa3f12b120cdb3bfeaa96bc7eec4e2b39d30311269bdf85ec0a67272ac563a73ff58bf3
         */

        private String verification;
        private String invocation;

        public String getVerification() {
            return verification;
        }

        public void setVerification(String verification) {
            this.verification = verification;
        }

        public String getInvocation() {
            return invocation;
        }

        public void setInvocation(String invocation) {
            this.invocation = invocation;
        }
    }

    public static class AttributesBean {
        /**
         * usage : Script
         * data : c892730b17606e976294aeae06f8de83e92680b8
         */

        private String usage;
        private String data;

        public String getUsage() {
            return usage;
        }

        public void setUsage(String usage) {
            this.usage = usage;
        }

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }
    }
}
