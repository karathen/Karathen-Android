package com.raistone.wallet.sealwallet.entity;

import com.raistone.wallet.sealwallet.datavases.AppDataBase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

public class AssetsInfo implements Serializable{

    private List<DataBean> result;

    public List<DataBean> getResult() {
        return result;
    }

    public void setResult(List<DataBean> result) {
        this.result = result;
    }

    @Table(database = AppDataBase.class)
    public static class DataBean extends BaseModel implements Serializable{
        /**
         * asset_id : 1
         * asset_name : ethAsset
         * isDefault : true
         * notDelete : true
         * tokenAddress : 0x0000000000000000000000000000000000000000
         * tokenDecimal : 18
         * tokenIcon : cc_asset_eth
         * tokenName : Ethereum
         * tokenSynbol : ETH
         * coinType : ETH
         * tokenType : CCAseet_ETH_ERC20
         */
        @PrimaryKey(autoincrement = true)
        private int assets_id;
        @Column
        private String asset_name; //资产名称

        @Column
        private boolean isDefault; //是否默认

        @Column
        private boolean notDelete; //能否删除

        @Column
        private String tokenAddress; //合约地址

        @Column
        private String tokenDecimal; //精度

        @Column
        private String tokenIcon; //资产icon

        @Column
        private String tokenName;//资产名称

        @Column
        private String tokenSynbol;//资产代币

        @Column
        private String coinType;//币种类型

        @Column
        private String tokenType;//币种类型

        @Column
        private BigDecimal balance;//余额

        @Column
        private BigDecimal price;//人民币

        @Column
        private BigDecimal priceUSD;//美元

        @Column
        private String status;//状态

        @Column
        private String walletAddress;//钱包地址

        @Column
        private BigDecimal priceFlag;//后台查询人民币价格

        @Column
        private BigDecimal priceUsdFlag;//后台查询美元价格


        public String getAsset_name() {
            return asset_name;
        }

        public void setAsset_name(String asset_name) {
            this.asset_name = asset_name;
        }

        public boolean isDefault() {
            return isDefault;
        }

        public void setDefault(boolean aDefault) {
            isDefault = aDefault;
        }

        public boolean isNotDelete() {
            return notDelete;
        }

        public void setNotDelete(boolean notDelete) {
            this.notDelete = notDelete;
        }

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

        public String getCoinType() {
            return coinType;
        }

        public void setCoinType(String coinType) {
            this.coinType = coinType;
        }

        public String getTokenType() {
            return tokenType;
        }

        public void setTokenType(String tokenType) {
            this.tokenType = tokenType;
        }

        public BigDecimal getBalance() {
            return balance;
        }

        public void setBalance(BigDecimal balance) {
            this.balance = balance;
        }

        public BigDecimal getPrice() {
            return price;
        }

        public void setPrice(BigDecimal price) {
            this.price = price;
        }

        public BigDecimal getPriceUSD() {
            return priceUSD;
        }

        public void setPriceUSD(BigDecimal priceUSD) {
            this.priceUSD = priceUSD;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getWalletAddress() {
            return walletAddress;
        }

        public void setWalletAddress(String walletAddress) {
            this.walletAddress = walletAddress;
        }

        public int getAssets_id() {
            return assets_id;
        }

        public void setAssets_id(int assets_id) {
            this.assets_id = assets_id;
        }

        public BigDecimal getPriceFlag() {
            return priceFlag;
        }

        public void setPriceFlag(BigDecimal priceFlag) {
            this.priceFlag = priceFlag;
        }

        public BigDecimal getPriceUsdFlag() {
            return priceUsdFlag;
        }

        public void setPriceUsdFlag(BigDecimal priceUsdFlag) {
            this.priceUsdFlag = priceUsdFlag;
        }
    }
}
