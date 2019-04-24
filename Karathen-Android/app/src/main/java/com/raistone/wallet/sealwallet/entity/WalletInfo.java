package com.raistone.wallet.sealwallet.entity;

import com.raistone.wallet.sealwallet.datavases.AppDataBase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.io.Serializable;

/**
 * 钱包表
 */

@Table(database = AppDataBase.class)
public class WalletInfo extends BaseModel implements Serializable{

    @PrimaryKey(autoincrement = true)
    private int walletId;//钱包Id

    @Column
    private String accountId;//账户Id

    @Column
    private String walletPwd;//钱包密码

    @Column
    private String mnemonic; //助记词

    @Column
    private String privateScrect; //私钥

    @Column
    private String publicScrect; //公钥

    @Column
    private boolean isCurrent; //设置为选中状态

    @Column
    private boolean isBackup; //是否备份

    @Column
    private boolean isHDWallet; //是否是 HD 钱包

    @Column
    private boolean isImport; //是否是导入钱包

    @Column
    private String wif;//钱包类型 ETH  NEO ONT

    @Column
    private int importType;// 0 助记词导入 1 keystore导入 2 私钥导入  3 wif导入

    @Column
    private String pwdTips;//密码提示

    @Column
    private String cnyPrice;//人民币总价格

    @Column
    private String usdtPrice;//美元总价格


    public int getWalletId() {
        return walletId;
    }

    public void setWalletId(int walletId) {
        this.walletId = walletId;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getWalletPwd() {
        return walletPwd;
    }

    public void setWalletPwd(String walletPwd) {
        this.walletPwd = walletPwd;
    }

    public String getMnemonic() {
        return mnemonic;
    }

    public void setMnemonic(String mnemonic) {
        this.mnemonic = mnemonic;
    }

    public String getPrivateScrect() {
        return privateScrect;
    }

    public void setPrivateScrect(String privateScrect) {
        this.privateScrect = privateScrect;
    }

    public String getPublicScrect() {
        return publicScrect;
    }

    public void setPublicScrect(String publicScrect) {
        this.publicScrect = publicScrect;
    }

    public boolean isCurrent() {
        return isCurrent;
    }

    public void setCurrent(boolean current) {
        isCurrent = current;
    }

    public boolean isBackup() {
        return isBackup;
    }

    public void setBackup(boolean backup) {
        isBackup = backup;
    }

    public boolean isHDWallet() {
        return isHDWallet;
    }

    public void setHDWallet(boolean HDWallet) {
        isHDWallet = HDWallet;
    }

    public boolean isImport() {
        return isImport;
    }

    public void setImport(boolean anImport) {
        isImport = anImport;
    }

    public String getWif() {
        return wif;
    }

    public void setWif(String wif) {
        this.wif = wif;
    }

    public int getImportType() {
        return importType;
    }

    public void setImportType(int importType) {
        this.importType = importType;
    }

    public String getPwdTips() {
        return pwdTips;
    }

    public void setPwdTips(String pwdTips) {
        this.pwdTips = pwdTips;
    }

    public String getCnyPrice() {
        return cnyPrice;
    }

    public void setCnyPrice(String cnyPrice) {
        this.cnyPrice = cnyPrice;
    }

    public String getUsdtPrice() {
        return usdtPrice;
    }

    public void setUsdtPrice(String usdtPrice) {
        this.usdtPrice = usdtPrice;
    }
}
