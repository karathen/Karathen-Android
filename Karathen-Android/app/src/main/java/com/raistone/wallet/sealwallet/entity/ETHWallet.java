package com.raistone.wallet.sealwallet.entity;

import com.raistone.wallet.sealwallet.datavases.AppDataBase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.io.Serializable;
import java.util.List;


@Table(database = AppDataBase.class)
public class ETHWallet extends BaseModel implements Serializable{
    @PrimaryKey(autoincrement = true)
    private Long id;//钱包id

    @Column
    private String address;//钱包地址

    @Column
    private String name;//钱包名称

    @Column
    private String password;//密码

    @Column
    private String keystorePath;//keyStore 文件

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


    @Column String type_flag;// 通用的以太坊基于bip44协议的助记词路径  m/44'/60'/0'/0/0

    @Column
    private boolean isHDWallet; //是否是 HD 钱包

    @Column
    private boolean isImport; //是否是导入钱包

    @Column
    private String coinType;//钱包类型 ETH  NEO ONT

    @Column
    private int imagePath;

    @Column
    private boolean account;//是否是主账号

    @Column
    private int importType;//0 助记词导入 1 私钥导入

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getKeystorePath() {
        return keystorePath;
    }

    public void setKeystorePath(String keystorePath) {
        this.keystorePath = keystorePath;
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

    public String getType_flag() {
        return type_flag;
    }

    public void setType_flag(String type_flag) {
        this.type_flag = type_flag;
    }

    public String getCoinType() {
        return coinType;
    }

    public void setCoinType(String coinType) {
        this.coinType = coinType;
    }

    public int getImagePath() {
        return imagePath;
    }

    public void setImagePath(int imagePath) {
        this.imagePath = imagePath;
    }

    public boolean isAccount() {
        return account;
    }

    public void setAccount(boolean account) {
        this.account = account;
    }

    public int getImportType() {
        return importType;
    }

    public void setImportType(int importType) {
        this.importType = importType;
    }


}
