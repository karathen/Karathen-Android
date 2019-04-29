package com.raistone.wallet.sealwallet.entity;

import com.raistone.wallet.sealwallet.datavases.AppDataBase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.io.Serializable;
import java.math.BigInteger;

@Table(database = AppDataBase.class)
public class TransferInfo extends BaseModel implements Serializable{


    @PrimaryKey(autoincrement = true)
    private Integer transId;//主键id

    @Column
    private String fromAddress; //付款地址

    @Column
    private String toAddress;// 收款地址

    @Column
    private BigInteger blockHeighe;// 块高

    @Column
    private String blockTime;//时间

    @Column
    private String coinType; //类型

    @Column
    private String data;

    @Column
    private String gaslimit;

    @Column
    private String gasPrice;

    @Column
    private String remark;//备注

    @Column
    private String tokenAddress;//代币地址

    @Column
    private String tokenSynbol;

    @Column
    private String txId;//哈希值

    @Column
    private String status;//状态

    @Column
    private String value;//值

    @Column
    private String walletAddress;//钱包地址

    @Column
    private String minerCost;//交易手续费

    @Column
    private String transferTime;//交易时间

    @Column
    private String nonce;//交易随机数

    public Integer getTransId() {
        return transId;
    }

    public void setTransId(Integer transId) {
        this.transId = transId;
    }

    public String getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

    public String getToAddress() {
        return toAddress;
    }

    public void setToAddress(String toAddress) {
        this.toAddress = toAddress;
    }

    public BigInteger getBlockHeighe() {
        return blockHeighe;
    }

    public void setBlockHeighe(BigInteger blockHeighe) {
        this.blockHeighe = blockHeighe;
    }

    public String getBlockTime() {
        return blockTime;
    }

    public void setBlockTime(String blockTime) {
        this.blockTime = blockTime;
    }

    public String getCoinType() {
        return coinType;
    }

    public void setCoinType(String coinType) {
        this.coinType = coinType;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getGaslimit() {
        return gaslimit;
    }

    public void setGaslimit(String gaslimit) {
        this.gaslimit = gaslimit;
    }

    public String getGasPrice() {
        return gasPrice;
    }

    public void setGasPrice(String gasPrice) {
        this.gasPrice = gasPrice;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getTokenAddress() {
        return tokenAddress;
    }

    public void setTokenAddress(String tokenAddress) {
        this.tokenAddress = tokenAddress;
    }

    public String getTxId() {
        return txId;
    }

    public void setTxId(String txId) {
        this.txId = txId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getWalletAddress() {
        return walletAddress;
    }

    public void setWalletAddress(String walletAddress) {
        this.walletAddress = walletAddress;
    }

    public String getMinerCost() {
        return minerCost;
    }

    public void setMinerCost(String minerCost) {
        this.minerCost = minerCost;
    }

    public String getTransferTime() {
        return transferTime;
    }

    public void setTransferTime(String transferTime) {
        this.transferTime = transferTime;
    }

    public String getTokenSynbol() {
        return tokenSynbol;
    }

    public void setTokenSynbol(String tokenSynbol) {
        this.tokenSynbol = tokenSynbol;
    }

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }
}
