package com.raistone.wallet.sealwallet.factory;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;

import java.io.Serializable;
import java.util.List;

import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.ToMany;
import org.greenrobot.greendao.DaoException;
import com.raistone.wallet.sealwallet.greendao.DaoSession;
import com.raistone.wallet.sealwallet.greendao.ChainAddressInfoDao;
import com.raistone.wallet.sealwallet.greendao.AssetsDeatilInfoDao;

/**
 * 多链地址
 */

@Entity
public class ChainAddressInfo implements Serializable{

    private static final long serialVersionUID = -1127146031936387227L;
    @Id(autoincrement = true)
    private Long id;//地址id

    private String address;//链地址

    private String name;//链名称

    private String password;//密码

    private String keystorePath;//keyStore 文件

    private String mnemonic; //助记词

    private String privateScrect; //私钥

    private String publicScrect; //公钥

    private boolean isCurrent; //设置为选中状态

    private boolean isBackup; //是否备份

    private String type_flag;// 通用的以太坊基于bip44协议的助记词路径  m/44'/60'/0'/0/0

    private boolean isHDWallet; //是否是 HD 钱包

    private boolean isImport; //是否是导入钱包

    private String coinType;//钱包类型 ETH  NEO ONT

    private int imagePath;

    private boolean account;//是否是主账号

    private int importType;//0 助记词导入 1 私钥导入 2 wif 导入       4钱包导入

    private String wif;

    private String accountId;//账户ID

    private int walletType;//钱包类型 0 云钱包  1 HD 钱包  2 硬件钱包


    private String cnyTotalPrice;//RMB总价格

    private String usdtTotalPrice;//美元总价格

    private boolean selectStatus;//当前链选中状态

    private int selectIndex;

    private Long chainId;//属于哪条链

    @ToMany(referencedJoinProperty = "chainAddressId")
    private List<AssetsDeatilInfo> assetsInfoDataList;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 1953891649)
    private transient ChainAddressInfoDao myDao;

    @Generated(hash = 1062909104)
    public ChainAddressInfo(Long id, String address, String name, String password,
            String keystorePath, String mnemonic, String privateScrect,
            String publicScrect, boolean isCurrent, boolean isBackup,
            String type_flag, boolean isHDWallet, boolean isImport, String coinType,
            int imagePath, boolean account, int importType, String wif,
            String accountId, int walletType, String cnyTotalPrice,
            String usdtTotalPrice, boolean selectStatus, int selectIndex,
            Long chainId) {
        this.id = id;
        this.address = address;
        this.name = name;
        this.password = password;
        this.keystorePath = keystorePath;
        this.mnemonic = mnemonic;
        this.privateScrect = privateScrect;
        this.publicScrect = publicScrect;
        this.isCurrent = isCurrent;
        this.isBackup = isBackup;
        this.type_flag = type_flag;
        this.isHDWallet = isHDWallet;
        this.isImport = isImport;
        this.coinType = coinType;
        this.imagePath = imagePath;
        this.account = account;
        this.importType = importType;
        this.wif = wif;
        this.accountId = accountId;
        this.walletType = walletType;
        this.cnyTotalPrice = cnyTotalPrice;
        this.usdtTotalPrice = usdtTotalPrice;
        this.selectStatus = selectStatus;
        this.selectIndex = selectIndex;
        this.chainId = chainId;
    }

    @Generated(hash = 320065129)
    public ChainAddressInfo() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getKeystorePath() {
        return this.keystorePath;
    }

    public void setKeystorePath(String keystorePath) {
        this.keystorePath = keystorePath;
    }

    public String getMnemonic() {
        return this.mnemonic;
    }

    public void setMnemonic(String mnemonic) {
        this.mnemonic = mnemonic;
    }

    public String getPrivateScrect() {
        return this.privateScrect;
    }

    public void setPrivateScrect(String privateScrect) {
        this.privateScrect = privateScrect;
    }

    public String getPublicScrect() {
        return this.publicScrect;
    }

    public void setPublicScrect(String publicScrect) {
        this.publicScrect = publicScrect;
    }

    public boolean getIsCurrent() {
        return this.isCurrent;
    }

    public void setIsCurrent(boolean isCurrent) {
        this.isCurrent = isCurrent;
    }

    public boolean getIsBackup() {
        return this.isBackup;
    }

    public void setIsBackup(boolean isBackup) {
        this.isBackup = isBackup;
    }

    public String getType_flag() {
        return this.type_flag;
    }

    public void setType_flag(String type_flag) {
        this.type_flag = type_flag;
    }

    public boolean getIsHDWallet() {
        return this.isHDWallet;
    }

    public void setIsHDWallet(boolean isHDWallet) {
        this.isHDWallet = isHDWallet;
    }

    public boolean getIsImport() {
        return this.isImport;
    }

    public void setIsImport(boolean isImport) {
        this.isImport = isImport;
    }

    public String getCoinType() {
        return this.coinType;
    }

    public void setCoinType(String coinType) {
        this.coinType = coinType;
    }

    public int getImagePath() {
        return this.imagePath;
    }

    public void setImagePath(int imagePath) {
        this.imagePath = imagePath;
    }

    public boolean getAccount() {
        return this.account;
    }

    public void setAccount(boolean account) {
        this.account = account;
    }

    public int getImportType() {
        return this.importType;
    }

    public void setImportType(int importType) {
        this.importType = importType;
    }

    public String getWif() {
        return this.wif;
    }

    public void setWif(String wif) {
        this.wif = wif;
    }

    public String getAccountId() {
        return this.accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public int getWalletType() {
        return this.walletType;
    }

    public void setWalletType(int walletType) {
        this.walletType = walletType;
    }

    public String getCnyTotalPrice() {
        return this.cnyTotalPrice;
    }

    public void setCnyTotalPrice(String cnyTotalPrice) {
        this.cnyTotalPrice = cnyTotalPrice;
    }

    public String getUsdtTotalPrice() {
        return this.usdtTotalPrice;
    }

    public void setUsdtTotalPrice(String usdtTotalPrice) {
        this.usdtTotalPrice = usdtTotalPrice;
    }

    public boolean getSelectStatus() {
        return this.selectStatus;
    }

    public void setSelectStatus(boolean selectStatus) {
        this.selectStatus = selectStatus;
    }

    public int getSelectIndex() {
        return this.selectIndex;
    }

    public void setSelectIndex(int selectIndex) {
        this.selectIndex = selectIndex;
    }

    public Long getChainId() {
        return this.chainId;
    }

    public void setChainId(Long chainId) {
        this.chainId = chainId;
    }



    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 2093164542)
    public synchronized void resetAssetsInfoDataList() {
        assetsInfoDataList = null;
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1687969914)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getChainAddressInfoDao() : null;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 1547384498)
    public List<AssetsDeatilInfo> getAssetsInfoDataList() {
        if (assetsInfoDataList == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            AssetsDeatilInfoDao targetDao = daoSession.getAssetsDeatilInfoDao();
            List<AssetsDeatilInfo> assetsInfoDataListNew = targetDao._queryChainAddressInfo_AssetsInfoDataList(id);
            synchronized (this) {
                if (assetsInfoDataList == null) {
                    assetsInfoDataList = assetsInfoDataListNew;
                }
            }
        }
        return assetsInfoDataList;
    }

}
