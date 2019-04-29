package com.raistone.wallet.sealwallet.factory;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.ToMany;

import java.io.Serializable;
import java.util.List;
import org.greenrobot.greendao.DaoException;
import com.raistone.wallet.sealwallet.greendao.DaoSession;
import com.raistone.wallet.sealwallet.greendao.ChainAddressInfoDao;
import com.raistone.wallet.sealwallet.greendao.ChainDataInfoDao;



@Entity
public class ChainDataInfo implements Serializable{

    private static final long serialVersionUID = -5789061602835567603L;
    @Id(autoincrement = true)
    private Long id;


    private Long walletId;

    private String chainName;

    private String chainType;

    private boolean isShow;

    private String accountId;

    private String chainTokenName;

    private int orderInfo;

    private boolean isSelect;

    @ToMany(referencedJoinProperty = "chainId")
    private List<ChainAddressInfo> chainAddressInfos;


    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;


    /** Used for active entity operations. */
    @Generated(hash = 1901605672)
    private transient ChainDataInfoDao myDao;

    @Generated(hash = 101726573)
    public ChainDataInfo(Long id, Long walletId, String chainName, String chainType,
            boolean isShow, String accountId, String chainTokenName, int orderInfo,
            boolean isSelect) {
        this.id = id;
        this.walletId = walletId;
        this.chainName = chainName;
        this.chainType = chainType;
        this.isShow = isShow;
        this.accountId = accountId;
        this.chainTokenName = chainTokenName;
        this.orderInfo = orderInfo;
        this.isSelect = isSelect;
    }

    @Generated(hash = 293853961)
    public ChainDataInfo() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getWalletId() {
        return this.walletId;
    }

    public void setWalletId(Long walletId) {
        this.walletId = walletId;
    }

    public String getChainName() {
        return this.chainName;
    }

    public void setChainName(String chainName) {
        this.chainName = chainName;
    }

    public String getChainType() {
        return this.chainType;
    }

    public void setChainType(String chainType) {
        this.chainType = chainType;
    }

    public boolean getIsShow() {
        return this.isShow;
    }

    public void setIsShow(boolean isShow) {
        this.isShow = isShow;
    }

    public String getAccountId() {
        return this.accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getChainTokenName() {
        return this.chainTokenName;
    }

    public void setChainTokenName(String chainTokenName) {
        this.chainTokenName = chainTokenName;
    }

    public int getOrderInfo() {
        return this.orderInfo;
    }

    public void setOrderInfo(int orderInfo) {
        this.orderInfo = orderInfo;
    }

    public boolean getIsSelect() {
        return this.isSelect;
    }

    public void setIsSelect(boolean isSelect) {
        this.isSelect = isSelect;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 823135862)
    public List<ChainAddressInfo> getChainAddressInfos() {
        if (chainAddressInfos == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            ChainAddressInfoDao targetDao = daoSession.getChainAddressInfoDao();
            List<ChainAddressInfo> chainAddressInfosNew = targetDao
                    ._queryChainDataInfo_ChainAddressInfos(id);
            synchronized (this) {
                if (chainAddressInfos == null) {
                    chainAddressInfos = chainAddressInfosNew;
                }
            }
        }
        return chainAddressInfos;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 142401660)
    public synchronized void resetChainAddressInfos() {
        chainAddressInfos = null;
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
    @Generated(hash = 727425999)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getChainDataInfoDao() : null;
    }



}
