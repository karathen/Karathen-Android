package com.raistone.wallet.sealwallet.datavases;

import com.raistone.wallet.sealwallet.entity.TransferInfo;
import com.raistone.wallet.sealwallet.entity.TransferInfo_Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import java.util.List;

public class TransferDaoUtils {


    public static TransferInfo getTransferHash(String hash) {
        TransferInfo transferInfo = SQLite.select().from(TransferInfo.class).where(TransferInfo_Table.txId.eq(hash)).querySingle();
        if (transferInfo != null) {
            return  transferInfo;
        }
        return null;
    }


    public static List<TransferInfo> selectAllTransfers(String tokenSynbol,String walletAddress) {

        List<TransferInfo> all = SQLite.select().from(TransferInfo.class)
                .where(TransferInfo_Table.tokenSynbol.eq(tokenSynbol))
                .and(TransferInfo_Table.walletAddress.eq(walletAddress))
                .orderBy(TransferInfo_Table.transId,false).queryList();
        return all;
    }

    public static TransferInfo selectTransfersByHash(String hash) {
        TransferInfo single = SQLite.select().from(TransferInfo.class).where(TransferInfo_Table.txId.eq(hash)).querySingle();
        return single;
    }


}
