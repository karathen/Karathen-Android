package com.raistone.wallet.sealwallet.entity;

import java.util.List;

public class AddAssetsInfo {

    /**
     * id : 1
     * jsonrpc : 2.0
     * result : [{"tokenAddress":"0x9cffeb5b651720847a2679ab66805db729f7974b","tokenDecimal":"8","tokenIcon":null,"tokenName":"Contractor Cafe Token","tokenSynbol":"C","tokenType":"ERC-20"},{"tokenAddress":"0x9558286c15952c05c1f90806917c7caca8fc3d8f","tokenDecimal":"0","tokenIcon":null,"tokenName":"TheToken","tokenSynbol":"C","tokenType":"ERC-20"},{"tokenAddress":"0x8b6fec2a4f011ea1de89ff1d24a36efac15f04f4","tokenDecimal":"18","tokenIcon":null,"tokenName":"Computer","tokenSynbol":"C","tokenType":"ERC-20"},{"tokenAddress":"0x32388ea6fc1fda6698a4776c6ea9668899ba0641","tokenDecimal":"8","tokenIcon":null,"tokenName":"CocaCoin","tokenSynbol":"C","tokenType":"ERC-20"},{"tokenAddress":"0x9165fccc1d4e7e6bf97e86a5319d8b723bf4077c","tokenDecimal":"0","tokenIcon":null,"tokenName":"C","tokenSynbol":"C","tokenType":"ERC-20"},{"tokenAddress":"0x1135b1a8b5e6b5f6198dc45cb2b3e33bda9b7c98","tokenDecimal":"2","tokenIcon":null,"tokenName":"CZR","tokenSynbol":"C","tokenType":"ERC-20"},{"tokenAddress":"0x4d8806c030f0cc9908018509313dd98968b1698d","tokenDecimal":"4","tokenIcon":null,"tokenName":"FangCoin","tokenSynbol":"C","tokenType":"ERC-20"},{"tokenAddress":"0x6f903bf053c5bdbde5aac2e66b0c122ddbd72cd0","tokenDecimal":"18","tokenIcon":null,"tokenName":"CoinCab","tokenSynbol":"C","tokenType":"ERC-20"},{"tokenAddress":"0x938b16acad497a91ac137f01ff2959876c734eee","tokenDecimal":"18","tokenIcon":null,"tokenName":"Ccoin","tokenSynbol":"C","tokenType":"ERC-20"},{"tokenAddress":"0x9289582fe88c9665c49cb8cf806a03a34bacf821","tokenDecimal":"18","tokenIcon":null,"tokenName":"Crypto","tokenSynbol":"C","tokenType":"ERC-20"},{"tokenAddress":"0xf0bd4538de711ef792353dad5c676c6f9c6cfefc","tokenDecimal":"18","tokenIcon":null,"tokenName":"C","tokenSynbol":"C","tokenType":"ERC-20"},{"tokenAddress":"0xfdcbe5f1db8607eb29917f4babc357f51e4bcb7d","tokenDecimal":"18","tokenIcon":null,"tokenName":"Caligula","tokenSynbol":"C","tokenType":"ERC-20"},{"tokenAddress":"0x0a33ea94fd4247632fc627fa1f2297c23475cf16","tokenDecimal":"18","tokenIcon":null,"tokenName":"Chain","tokenSynbol":"C","tokenType":"ERC-20"},{"tokenAddress":"0x76976218f3af6b2e361e12446990bf3cfb792ff7","tokenDecimal":"18","tokenIcon":null,"tokenName":"CB","tokenSynbol":"C","tokenType":"ERC-20"},{"tokenAddress":"0x414f07f462ca96fb4c317af977d74dbf5e7fd5b3","tokenDecimal":"18","tokenIcon":null,"tokenName":"CB","tokenSynbol":"C","tokenType":"ERC-20"}]
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

    public static class ResultBean {
        /**
         * tokenAddress : 0x9cffeb5b651720847a2679ab66805db729f7974b
         * tokenDecimal : 8
         * tokenIcon : null
         * tokenName : Contractor Cafe Token
         * tokenSynbol : C
         * tokenType : ERC-20
         */

        private String tokenAddress;
        private String tokenDecimal;
        private String tokenIcon;
        private String tokenName;
        private String tokenSynbol;
        private String tokenType;
        private int addFlag; //0 是已添加， 1 是已添加

        public String getTokenAddress() {
            return tokenAddress;
        }

        public void setTokenAddress(String tokenAddress) {
            this.tokenAddress = tokenAddress;
        }

        public String getTokenDecimal() {
            return tokenDecimal;
        }

        public void setTokenDecimal(String tokenDecimal) {
            this.tokenDecimal = tokenDecimal;
        }

        public String getTokenIcon() {
            return tokenIcon;
        }

        public void setTokenIcon(String tokenIcon) {
            this.tokenIcon = tokenIcon;
        }

        public String getTokenName() {
            return tokenName;
        }

        public void setTokenName(String tokenName) {
            this.tokenName = tokenName;
        }

        public String getTokenSynbol() {
            return tokenSynbol;
        }

        public void setTokenSynbol(String tokenSynbol) {
            this.tokenSynbol = tokenSynbol;
        }

        public String getTokenType() {
            return tokenType;
        }

        public void setTokenType(String tokenType) {
            this.tokenType = tokenType;
        }

        public int getAddFlag() {
            return addFlag;
        }

        public void setAddFlag(int addFlag) {
            this.addFlag = addFlag;
        }
    }
}
