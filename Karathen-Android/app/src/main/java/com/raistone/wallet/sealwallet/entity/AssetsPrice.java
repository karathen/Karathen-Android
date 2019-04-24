package com.raistone.wallet.sealwallet.entity;

import com.raistone.wallet.sealwallet.datavases.AppDataBase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

import java.math.BigDecimal;

/**
 * 资产价格表
 */

@Table(database = AppDataBase.class)
public class AssetsPrice {

    @PrimaryKey(autoincrement = true)
    private int id;

    @Column
    private String symbol;//资产名称

    @Column
    private BigDecimal cny_price; //资产对应的人民币市场价

    @Column
    private BigDecimal usd_price; //资产对应的美元市场价

    @Column
    private String tokenDecimal;//资产对应的精度

    @Column
    private String showPrice;//计算好的人民币价格

    @Column
    private String showUsdPrice;//计算好的美元价格

    @Column
    private String walletAddress;//当前资产是属于哪个钱包地址

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public BigDecimal getCny_price() {
        return cny_price;
    }

    public void setCny_price(BigDecimal cny_price) {
        this.cny_price = cny_price;
    }

    public BigDecimal getUsd_price() {
        return usd_price;
    }

    public void setUsd_price(BigDecimal usd_price) {
        this.usd_price = usd_price;
    }

    public String getTokenDecimal() {
        return tokenDecimal;
    }

    public void setTokenDecimal(String tokenDecimal) {
        this.tokenDecimal = tokenDecimal;
    }

    public String getShowPrice() {
        return showPrice;
    }

    public void setShowPrice(String showPrice) {
        this.showPrice = showPrice;
    }

    public String getShowUsdPrice() {
        return showUsdPrice;
    }

    public void setShowUsdPrice(String showUsdPrice) {
        this.showUsdPrice = showUsdPrice;
    }

    public String getWalletAddress() {
        return walletAddress;
    }

    public void setWalletAddress(String walletAddress) {
        this.walletAddress = walletAddress;
    }
}
