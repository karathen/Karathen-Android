package com.raistone.wallet.sealwallet.daoutils;

import com.raistone.wallet.sealwallet.WalletApplication;
import com.raistone.wallet.sealwallet.factory.ChainDataInfo;
import com.raistone.wallet.sealwallet.greendao.ChainDataInfoDao;
import java.util.List;

public class ChainDaoUtils {
    public static ChainDataInfoDao chainDataInfoDao = WalletApplication.getsInstance().getDaoSession().getChainDataInfoDao();

    /**
     * 插入新数据
     *
     * @param chainDataInfo 新创建钱包
     */
    public static void insertNewChain(ChainDataInfo chainDataInfo) {
        chainDataInfoDao.insert(chainDataInfo);
    }


    /**
     * 查询所有链
     */
    public static List<ChainDataInfo> findAll(){
        List<ChainDataInfo> hdWallets = chainDataInfoDao.loadAll();

        return hdWallets;

    }



    /**
     * 根据钱包Id查询所有的链表信息
     */
    public static List<ChainDataInfo> findAllChainByWalletId(long walletId){

        //List<ChainDataInfo> chainInfos= chainDataInfoDao.queryBuilder().orderAsc(ChainDataInfoDao.Properties.OrderInfo).list();
        List<ChainDataInfo> chainInfos= chainDataInfoDao.queryBuilder().where(ChainDataInfoDao.Properties.WalletId.eq(walletId)).orderAsc(ChainDataInfoDao.Properties.OrderInfo).list();

        return chainInfos;
    }



    /**
     * 根据账户Id查询所有的链表信息
     */
    public static List<ChainDataInfo> findAllChainByAccount(String accountId){

        List<ChainDataInfo> chainInfos= chainDataInfoDao.queryBuilder().where(ChainDataInfoDao.Properties.AccountId.eq(accountId),ChainDataInfoDao.Properties.IsShow.eq(true)).orderAsc(ChainDataInfoDao.Properties.OrderInfo).list();

        return chainInfos;
    }



    /**
     * 根据账户Id查询所有的链表信息
     */
    public static List<ChainDataInfo> findAllChainByAccountIsSelect(String accountId){

        List<ChainDataInfo> chainInfos= chainDataInfoDao.queryBuilder().where(ChainDataInfoDao.Properties.AccountId.eq(accountId),ChainDataInfoDao.Properties.IsShow.eq(true)).orderAsc(ChainDataInfoDao.Properties.IsSelect).list();

        return chainInfos;
    }

    /**
     * 根据账户Id查询所有的链表信息
     */
    public static List<ChainDataInfo> findAllChains(){

        List<ChainDataInfo> chainInfos= chainDataInfoDao.queryBuilder().where(ChainDataInfoDao.Properties.IsShow.eq(true)).orderAsc(ChainDataInfoDao.Properties.OrderInfo).list();

        return chainInfos;
    }


    /**
     * 根据账户Id查询所有的链表信息
     */
    public static List<ChainDataInfo> findAllChainsByManager(){

        List<ChainDataInfo> chainInfos= chainDataInfoDao.queryBuilder().orderAsc(ChainDataInfoDao.Properties.OrderInfo).list();
        return chainInfos;
    }


    /**
     * 根据 链名称 修改链排列顺序
     */
    public static void updateOrder(ChainDataInfo chainDataInfo){

        chainDataInfoDao.update(chainDataInfo);

    }

    /**
     *  删除所有链数据
     */
    public static void deleteAllData(){

        chainDataInfoDao.detachAll();
        chainDataInfoDao.deleteAll();

    }

    /**
     *  删除所有链数据
     */
    public static void deleteDataById(ChainDataInfo info){

        chainDataInfoDao.delete(info);

    }
}
