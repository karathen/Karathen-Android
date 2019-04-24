package com.raistone.wallet.sealwallet.greendao;

import java.util.List;
import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;
import org.greenrobot.greendao.query.Query;
import org.greenrobot.greendao.query.QueryBuilder;

import com.raistone.wallet.sealwallet.factory.ChainDataInfo;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "CHAIN_DATA_INFO".
*/
public class ChainDataInfoDao extends AbstractDao<ChainDataInfo, Long> {

    public static final String TABLENAME = "CHAIN_DATA_INFO";

    /**
     * Properties of entity ChainDataInfo.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property WalletId = new Property(1, Long.class, "walletId", false, "WALLET_ID");
        public final static Property ChainName = new Property(2, String.class, "chainName", false, "CHAIN_NAME");
        public final static Property ChainType = new Property(3, String.class, "chainType", false, "CHAIN_TYPE");
        public final static Property IsShow = new Property(4, boolean.class, "isShow", false, "IS_SHOW");
        public final static Property AccountId = new Property(5, String.class, "accountId", false, "ACCOUNT_ID");
        public final static Property ChainTokenName = new Property(6, String.class, "chainTokenName", false, "CHAIN_TOKEN_NAME");
        public final static Property OrderInfo = new Property(7, int.class, "orderInfo", false, "ORDER_INFO");
        public final static Property IsSelect = new Property(8, boolean.class, "isSelect", false, "IS_SELECT");
    }

    private DaoSession daoSession;

    private Query<ChainDataInfo> hdWallet_ChainDataInfosQuery;

    public ChainDataInfoDao(DaoConfig config) {
        super(config);
    }
    
    public ChainDataInfoDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
        this.daoSession = daoSession;
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"CHAIN_DATA_INFO\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"WALLET_ID\" INTEGER," + // 1: walletId
                "\"CHAIN_NAME\" TEXT," + // 2: chainName
                "\"CHAIN_TYPE\" TEXT," + // 3: chainType
                "\"IS_SHOW\" INTEGER NOT NULL ," + // 4: isShow
                "\"ACCOUNT_ID\" TEXT," + // 5: accountId
                "\"CHAIN_TOKEN_NAME\" TEXT," + // 6: chainTokenName
                "\"ORDER_INFO\" INTEGER NOT NULL ," + // 7: orderInfo
                "\"IS_SELECT\" INTEGER NOT NULL );"); // 8: isSelect
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"CHAIN_DATA_INFO\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, ChainDataInfo entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        Long walletId = entity.getWalletId();
        if (walletId != null) {
            stmt.bindLong(2, walletId);
        }
 
        String chainName = entity.getChainName();
        if (chainName != null) {
            stmt.bindString(3, chainName);
        }
 
        String chainType = entity.getChainType();
        if (chainType != null) {
            stmt.bindString(4, chainType);
        }
        stmt.bindLong(5, entity.getIsShow() ? 1L: 0L);
 
        String accountId = entity.getAccountId();
        if (accountId != null) {
            stmt.bindString(6, accountId);
        }
 
        String chainTokenName = entity.getChainTokenName();
        if (chainTokenName != null) {
            stmt.bindString(7, chainTokenName);
        }
        stmt.bindLong(8, entity.getOrderInfo());
        stmt.bindLong(9, entity.getIsSelect() ? 1L: 0L);
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, ChainDataInfo entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        Long walletId = entity.getWalletId();
        if (walletId != null) {
            stmt.bindLong(2, walletId);
        }
 
        String chainName = entity.getChainName();
        if (chainName != null) {
            stmt.bindString(3, chainName);
        }
 
        String chainType = entity.getChainType();
        if (chainType != null) {
            stmt.bindString(4, chainType);
        }
        stmt.bindLong(5, entity.getIsShow() ? 1L: 0L);
 
        String accountId = entity.getAccountId();
        if (accountId != null) {
            stmt.bindString(6, accountId);
        }
 
        String chainTokenName = entity.getChainTokenName();
        if (chainTokenName != null) {
            stmt.bindString(7, chainTokenName);
        }
        stmt.bindLong(8, entity.getOrderInfo());
        stmt.bindLong(9, entity.getIsSelect() ? 1L: 0L);
    }

    @Override
    protected final void attachEntity(ChainDataInfo entity) {
        super.attachEntity(entity);
        entity.__setDaoSession(daoSession);
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public ChainDataInfo readEntity(Cursor cursor, int offset) {
        ChainDataInfo entity = new ChainDataInfo( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1), // walletId
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // chainName
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // chainType
            cursor.getShort(offset + 4) != 0, // isShow
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // accountId
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // chainTokenName
            cursor.getInt(offset + 7), // orderInfo
            cursor.getShort(offset + 8) != 0 // isSelect
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, ChainDataInfo entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setWalletId(cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1));
        entity.setChainName(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setChainType(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setIsShow(cursor.getShort(offset + 4) != 0);
        entity.setAccountId(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setChainTokenName(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setOrderInfo(cursor.getInt(offset + 7));
        entity.setIsSelect(cursor.getShort(offset + 8) != 0);
     }
    
    @Override
    protected final Long updateKeyAfterInsert(ChainDataInfo entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(ChainDataInfo entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(ChainDataInfo entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
    /** Internal query to resolve the "chainDataInfos" to-many relationship of HdWallet. */
    public List<ChainDataInfo> _queryHdWallet_ChainDataInfos(Long walletId) {
        synchronized (this) {
            if (hdWallet_ChainDataInfosQuery == null) {
                QueryBuilder<ChainDataInfo> queryBuilder = queryBuilder();
                queryBuilder.where(Properties.WalletId.eq(null));
                hdWallet_ChainDataInfosQuery = queryBuilder.build();
            }
        }
        Query<ChainDataInfo> query = hdWallet_ChainDataInfosQuery.forCurrentThread();
        query.setParameter(0, walletId);
        return query.list();
    }

}