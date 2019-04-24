package com.raistone.wallet.sealwallet.datavases;

import com.raistone.wallet.sealwallet.entity.ChainInfo;
import com.raistone.wallet.sealwallet.entity.ChainInfo_Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.List;

public class ChainInfoDaoUtils {
    /**
     * 根据账户Id查询所有的链表信息
     */
    public static List<ChainInfo> findAllChainByAccount(String accountId){
        List<ChainInfo> chainInfos = SQLite.select().from(ChainInfo.class).where(ChainInfo_Table.accountId.eq(accountId)).and(ChainInfo_Table.isShow.eq(true)).orderBy(ChainInfo_Table.OrderInfo,true).queryList();
        return chainInfos;
    }



    /**
     * 根据账户Id查询所有的链表信息
     */
    public static List<ChainInfo> findAllChainByAccountIsSelect(String accountId){
        List<ChainInfo> chainInfos = SQLite.select().from(ChainInfo.class).where(ChainInfo_Table.accountId.eq(accountId)).and(ChainInfo_Table.isShow.eq(true)).orderBy(ChainInfo_Table.isSelect,false).queryList();
        return chainInfos;
    }

    /**
     * 根据账户Id查询所有的链表信息
     */
    public static List<ChainInfo> findAllChains(){
        List<ChainInfo> chainInfos = SQLite.select().from(ChainInfo.class).where(ChainInfo_Table.isShow.eq(true)).orderBy(ChainInfo_Table.OrderInfo,true).queryList();
        return chainInfos;
    }


    /**
     * 根据账户Id查询所有的链表信息
     */
    public static List<ChainInfo> findAllChainsByManager(){
        List<ChainInfo> chainInfos = SQLite.select().from(ChainInfo.class).orderBy(ChainInfo_Table.OrderInfo,true).queryList();
        return chainInfos;
    }


    /**
     * 根据 链名称 修改链排列顺序
     */
    public static void updateOrder(String chaName,int order){

        SQLite.update(ChainInfo.class).set(ChainInfo_Table.OrderInfo.eq(order)).where(ChainInfo_Table.chaName.eq(chaName)).execute();

    }

    /**
     *  删除所有链数据
     */
    public static void deleteAllData(){

        SQLite.delete(ChainInfo.class);

    }


}
