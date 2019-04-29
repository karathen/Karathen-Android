package com.raistone.wallet.sealwallet.factory;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;

import java.io.Serializable;
import java.util.List;
import org.greenrobot.greendao.annotation.ToMany;
import com.raistone.wallet.sealwallet.daoutils.ChainDaoUtils;
import com.raistone.wallet.sealwallet.daoutils.HdWalletDaoUtils;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;
import com.raistone.wallet.sealwallet.greendao.DaoSession;
import com.raistone.wallet.sealwallet.greendao.ChainDataInfoDao;
import com.raistone.wallet.sealwallet.greendao.HdWalletDao;

@Entity
public class HdWallet extends WalletInfData implements Serializable{

    private static final long serialVersionUID = 2771643488088293651L;
    @Id(autoincrement = true)
    private Long walletId;

    private String walletName;

    private String accountId;

    private String walletPwd;

    private String mnemonic;

    private String privateScrect;

    private String publicScrect;

    private boolean isCurrent;

    private boolean isBackup;

    private boolean isHDWallet;

    private boolean isImport;

    private int walletType;

    private String wif;

    private int importType;

    private String pwdTips;

    private String cnyPrice;

    private String usdtPrice;

    private String keystore;

    @ToMany(referencedJoinProperty = "walletId")
    private List<ChainDataInfo> chainDataInfos;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 1861664411)
    private transient HdWalletDao myDao;


    @Generated(hash = 1442179253)
    public HdWallet(Long walletId, String walletName, String accountId, String walletPwd,
            String mnemonic, String privateScrect, String publicScrect, boolean isCurrent,
            boolean isBackup, boolean isHDWallet, boolean isImport, int walletType, String wif,
            int importType, String pwdTips, String cnyPrice, String usdtPrice, String keystore) {
        this.walletId = walletId;
        this.walletName = walletName;
        this.accountId = accountId;
        this.walletPwd = walletPwd;
        this.mnemonic = mnemonic;
        this.privateScrect = privateScrect;
        this.publicScrect = publicScrect;
        this.isCurrent = isCurrent;
        this.isBackup = isBackup;
        this.isHDWallet = isHDWallet;
        this.isImport = isImport;
        this.walletType = walletType;
        this.wif = wif;
        this.importType = importType;
        this.pwdTips = pwdTips;
        this.cnyPrice = cnyPrice;
        this.usdtPrice = usdtPrice;
        this.keystore = keystore;
    }

    @Generated(hash = 249289086)
    public HdWallet() {
    }


    /**
     * 创建链
     * @param chains 当前钱包支持的链
     */
    @Override
    public void createChain(Long wallId,List<String> chains) {

        for (int i = 0; i < chains.size(); i++) {
            ChainDataInfo chainInfo = new ChainDataInfo();
            chainInfo.setAccountId(accountId);
            chainInfo.setChainName(chains.get(i));
            chainInfo.setChainType(chains.get(i));
            chainInfo.setOrderInfo(i);
            chainInfo.setIsShow(true);
            chainInfo.setWalletId(wallId);

            ChainDaoUtils.insertNewChain(chainInfo);
        }

    }

    @Override
    public HdWallet createWallet(String walletName, String walletPwd) {

        HdWallet hdWallet=new HdWallet();
        hdWallet.setWalletName(walletName);
        hdWallet.setWalletPwd(walletPwd);

        HdWalletDaoUtils.insertNewWallet(hdWallet);

        return hdWallet;
    }

    @Override
    public HdWallet importWallet(String walletName, String walletPwd) {
        return null;
    }

    public Long getWalletId() {
        return this.walletId;
    }

    public void setWalletId(Long walletId) {
        this.walletId = walletId;
    }

    public String getWalletName() {
        return this.walletName;
    }

    public void setWalletName(String walletName) {
        this.walletName = walletName;
    }

    public String getAccountId() {
        return this.accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getWalletPwd() {
        return this.walletPwd;
    }

    public void setWalletPwd(String walletPwd) {
        this.walletPwd = walletPwd;
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

    public int getWalletType() {
        return this.walletType;
    }

    public void setWalletType(int walletType) {
        this.walletType = walletType;
    }

    public String getWif() {
        return this.wif;
    }

    public void setWif(String wif) {
        this.wif = wif;
    }

    public int getImportType() {
        return this.importType;
    }

    public void setImportType(int importType) {
        this.importType = importType;
    }

    public String getPwdTips() {
        return this.pwdTips;
    }

    public void setPwdTips(String pwdTips) {
        this.pwdTips = pwdTips;
    }

    public String getCnyPrice() {
        return this.cnyPrice;
    }

    public void setCnyPrice(String cnyPrice) {
        this.cnyPrice = cnyPrice;
    }

    public String getUsdtPrice() {
        return this.usdtPrice;
    }

    public void setUsdtPrice(String usdtPrice) {
        this.usdtPrice = usdtPrice;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 1718840652)
    public List<ChainDataInfo> getChainDataInfos() {
        if (chainDataInfos == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            ChainDataInfoDao targetDao = daoSession.getChainDataInfoDao();
            List<ChainDataInfo> chainDataInfosNew = targetDao
                    ._queryHdWallet_ChainDataInfos(walletId);
            synchronized (this) {
                if (chainDataInfos == null) {
                    chainDataInfos = chainDataInfosNew;
                }
            }
        }
        return chainDataInfos;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 1129225270)
    public synchronized void resetChainDataInfos() {
        chainDataInfos = null;
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
    @Generated(hash = 53409267)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getHdWalletDao() : null;
    }

    public String getKeystore() {
        return this.keystore;
    }

    public void setKeystore(String keystore) {
        this.keystore = keystore;
    }


   
}
