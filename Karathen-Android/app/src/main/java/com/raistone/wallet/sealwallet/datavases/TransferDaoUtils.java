package com.raistone.wallet.sealwallet.datavases;

import com.raistone.wallet.sealwallet.entity.AssetsInfo;
import com.raistone.wallet.sealwallet.entity.TransferInfo;
import com.raistone.wallet.sealwallet.entity.TransferInfo_Table;
import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.math.BigInteger;
import java.util.List;

public class TransferDaoUtils {

    /**
     * @param hash 交易hash
     * @return
     */
    public static TransferInfo getTransferHash(String hash) {
        TransferInfo transferInfo = SQLite.select().from(TransferInfo.class).where(TransferInfo_Table.txId.eq(hash)).querySingle();
        if (transferInfo != null) {
            return  transferInfo;
        }
        return null;
    }

    /**
     *根据 tokenSynbol 查询转账信息
     *
     * @return
     */
  /*  public static List<TransferInfo> selectAllTransfers(String tokenSynbol,String walletAddress) {

        //String upperCase = walletAddress.toUpperCase();
        List<TransferInfo> all = SQLite.select().from(TransferInfo.class)
                .where(TransferInfo_Table.coinType.eq(tokenSynbol))
                .and(TransferInfo_Table.walletAddress.eq(walletAddress))
                .orderBy(TransferInfo_Table.transId,false).queryList();
        return all;
    }
*/
    public static List<TransferInfo> selectAllTransfers(String tokenSynbol,String walletAddress) {

        //String upperCase = walletAddress.toUpperCase();
        List<TransferInfo> all = SQLite.select().from(TransferInfo.class)
                .where(TransferInfo_Table.tokenSynbol.eq(tokenSynbol))
                .and(TransferInfo_Table.walletAddress.eq(walletAddress))
                .orderBy(TransferInfo_Table.transId,false).queryList();
        return all;
    }



    /**
     *根据 tokenSynbol 查询所有转账信息
     *
     * @return
     */
    public static List<TransferInfo> selectAllTransfers() {
        List<TransferInfo> all = SQLite.select().from(TransferInfo.class).queryList();
        return all;
    }

    /**
     *根据 tokenSynbol 查询单条转账信息
     *
     * @return
     */
    public static TransferInfo selectTransfersByHash(String hash) {
        TransferInfo single = SQLite.select().from(TransferInfo.class).where(TransferInfo_Table.txId.eq(hash)).querySingle();
        return single;
    }


    /**
     * 删除全部数据
     */
    public static void deleteAllTransfer() {
        //SQLite.select().from(ETHWallet.class).where(ETHWallet_Table.coinType.eq(coinType)).queryList();tokenAddress      "tokenName": "Gemini dollar",
        //      "tokenSynbol": "GUSD",
        Delete.table(TransferInfo.class);
    }
}
