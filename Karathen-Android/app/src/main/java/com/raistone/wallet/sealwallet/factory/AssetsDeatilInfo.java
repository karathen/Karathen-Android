package com.raistone.wallet.sealwallet.factory;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

import java.io.Serializable;

@Entity
public class AssetsDeatilInfo implements Serializable{

    private static final long serialVersionUID = 6502762829848436945L;
    @Id(autoincrement = true)
    private Long assets_id;

    private String asset_name; //资产名称

    private boolean isDefault; //是否默认

    private boolean notDelete; //能否删除

    private String tokenAddress; //合约地址

    private String tokenDecimal; //精度

    private String tokenIcon; //资产icon

    private String tokenName;//资产名称

    private String tokenSynbol;//资产代币

    private String coinType;//币种类型

    private String tokenType;//币种类型

    private String balance;//余额

    private String price;//人民币

    private String priceUSD;//美元

    private int status;//状态

    private String walletAddress;//钱包地址

    private String priceFlag;//后台查询人民币价格

    private String priceUsdFlag;//后台查询美元价格

    private Long chainAddressId;//对应链ID



    @Generated(hash = 1979719608)
    public AssetsDeatilInfo() {
    }

    @Generated(hash = 1303637175)
    public AssetsDeatilInfo(Long assets_id, String asset_name, boolean isDefault,
            boolean notDelete, String tokenAddress, String tokenDecimal,
            String tokenIcon, String tokenName, String tokenSynbol, String coinType,
            String tokenType, String balance, String price, String priceUSD,
            int status, String walletAddress, String priceFlag, String priceUsdFlag,
            Long chainAddressId) {
        this.assets_id = assets_id;
        this.asset_name = asset_name;
        this.isDefault = isDefault;
        this.notDelete = notDelete;
        this.tokenAddress = tokenAddress;
        this.tokenDecimal = tokenDecimal;
        this.tokenIcon = tokenIcon;
        this.tokenName = tokenName;
        this.tokenSynbol = tokenSynbol;
        this.coinType = coinType;
        this.tokenType = tokenType;
        this.balance = balance;
        this.price = price;
        this.priceUSD = priceUSD;
        this.status = status;
        this.walletAddress = walletAddress;
        this.priceFlag = priceFlag;
        this.priceUsdFlag = priceUsdFlag;
        this.chainAddressId = chainAddressId;
    }

    public Long getAssets_id() {
        return this.assets_id;
    }

    public void setAssets_id(Long assets_id) {
        this.assets_id = assets_id;
    }

    public String getAsset_name() {
        return this.asset_name;
    }

    public void setAsset_name(String asset_name) {
        this.asset_name = asset_name;
    }

    public boolean getIsDefault() {
        return this.isDefault;
    }

    public void setIsDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    public boolean getNotDelete() {
        return this.notDelete;
    }

    public void setNotDelete(boolean notDelete) {
        this.notDelete = notDelete;
    }

    public String getTokenAddress() {
        return this.tokenAddress;
    }

    public void setTokenAddress(String tokenAddress) {
        this.tokenAddress = tokenAddress;
    }

    public String getTokenDecimal() {
        return this.tokenDecimal;
    }

    public void setTokenDecimal(String tokenDecimal) {
        this.tokenDecimal = tokenDecimal;
    }

    public String getTokenIcon() {
        return this.tokenIcon;
    }

    public void setTokenIcon(String tokenIcon) {
        this.tokenIcon = tokenIcon;
    }

    public String getTokenName() {
        return this.tokenName;
    }

    public void setTokenName(String tokenName) {
        this.tokenName = tokenName;
    }

    public String getTokenSynbol() {
        return this.tokenSynbol;
    }

    public void setTokenSynbol(String tokenSynbol) {
        this.tokenSynbol = tokenSynbol;
    }

    public String getCoinType() {
        return this.coinType;
    }

    public void setCoinType(String coinType) {
        this.coinType = coinType;
    }

    public String getTokenType() {
        return this.tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public String getBalance() {
        return this.balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getPrice() {
        return this.price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPriceUSD() {
        return this.priceUSD;
    }

    public void setPriceUSD(String priceUSD) {
        this.priceUSD = priceUSD;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getWalletAddress() {
        return this.walletAddress;
    }

    public void setWalletAddress(String walletAddress) {
        this.walletAddress = walletAddress;
    }

    public String getPriceFlag() {
        return this.priceFlag;
    }

    public void setPriceFlag(String priceFlag) {
        this.priceFlag = priceFlag;
    }

    public String getPriceUsdFlag() {
        return this.priceUsdFlag;
    }

    public void setPriceUsdFlag(String priceUsdFlag) {
        this.priceUsdFlag = priceUsdFlag;
    }

    public Long getChainAddressId() {
        return this.chainAddressId;
    }

    public void setChainAddressId(Long chainAddressId) {
        this.chainAddressId = chainAddressId;
    }
}
