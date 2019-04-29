package com.raistone.wallet.sealwallet.daoutils;

import com.raistone.wallet.sealwallet.WalletApplication;
import com.raistone.wallet.sealwallet.factory.ChainDataInfo;
import com.raistone.wallet.sealwallet.greendao.ChainDataInfoDao;
import java.util.List;

public class ChainDaoUtils {
    public static ChainDataInfoDao chainDataInfoDao = WalletApplication.getsInstance().getDaoSession().getChainDataInfoDao();


    public static void insertNewChain(ChainDataInfo chainDataInfo) {
        chainDataInfoDao.insert(chainDataInfo);
    }


    public static List<ChainDataInfo> findAllChainByWalletId(long walletId){

        List<ChainDataInfo> chainInfos= chainDataInfoDao.queryBuilder().where(ChainDataInfoDao.Properties.WalletId.eq(walletId)).orderAsc(ChainDataInfoDao.Properties.OrderInfo).list();

        return chainInfos;
    }


    public static List<ChainDataInfo> findAllChainByAccount(String accountId){

        List<ChainDataInfo> chainInfos= chainDataInfoDao.queryBuilder().where(ChainDataInfoDao.Properties.AccountId.eq(accountId),ChainDataInfoDao.Properties.IsShow.eq(true)).orderAsc(ChainDataInfoDao.Properties.OrderInfo).list();

        return chainInfos;
    }


    public static void updateOrder(ChainDataInfo chainDataInfo){

        chainDataInfoDao.update(chainDataInfo);

    }

    public static void deleteDataById(ChainDataInfo info){

        chainDataInfoDao.delete(info);

    }
}
