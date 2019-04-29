package com.raistone.wallet.sealwallet.datavases;

import com.raistone.wallet.sealwallet.entity.ChainInfo;
import com.raistone.wallet.sealwallet.entity.ChainInfo_Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.List;

public class ChainInfoDaoUtils {



    public static List<ChainInfo> findAllChainByAccountIsSelect(String accountId) {
        List<ChainInfo> chainInfos = SQLite.select().from(ChainInfo.class).where(ChainInfo_Table.accountId.eq(accountId)).and(ChainInfo_Table.isShow.eq(true)).orderBy(ChainInfo_Table.isSelect, false).queryList();
        return chainInfos;
    }

    public static List<ChainInfo> findAllChains(){
        List<ChainInfo> chainInfos = SQLite.select().from(ChainInfo.class).where(ChainInfo_Table.isShow.eq(true)).orderBy(ChainInfo_Table.OrderInfo,true).queryList();
        return chainInfos;
    }



}
