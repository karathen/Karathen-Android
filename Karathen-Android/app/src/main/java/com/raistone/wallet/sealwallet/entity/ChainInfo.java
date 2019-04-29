package com.raistone.wallet.sealwallet.entity;

import com.raistone.wallet.sealwallet.datavases.AppDataBase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.io.Serializable;

/**
 * 链表
 */

@Table(database = AppDataBase.class)
public class ChainInfo extends BaseModel implements Serializable{

    @PrimaryKey(autoincrement = true)
    private int chaId; //链id


    @Column
    private String chaName;//链名称


    @Column
    private String chaType;//链类型


    @Column
    private boolean isShow;//链是否可用

    @Column
    private String accountId;//账户Id

    @Column
    private String chaTokenName;//别名

    @Column
    private int OrderInfo; //排列顺序

    @Column
    private boolean isSelect;// 是否被选中


    public int getChaId() {
        return chaId;
    }

    public void setChaId(int chaId) {
        this.chaId = chaId;
    }

    public String getChaName() {
        return chaName;
    }

    public void setChaName(String chaName) {
        this.chaName = chaName;
    }

    public String getChaType() {
        return chaType;
    }

    public void setChaType(String chaType) {
        this.chaType = chaType;
    }

    public boolean isShow() {
        return isShow;
    }

    public void setShow(boolean show) {
        isShow = show;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getChaTokenName() {
        return chaTokenName;
    }

    public void setChaTokenName(String chaTokenName) {
        this.chaTokenName = chaTokenName;
    }

    public int getOrderInfo() {
        return OrderInfo;
    }

    public void setOrderInfo(int orderInfo) {
        OrderInfo = orderInfo;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }
}

