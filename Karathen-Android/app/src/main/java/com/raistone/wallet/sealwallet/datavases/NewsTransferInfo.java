package com.raistone.wallet.sealwallet.datavases;

import com.raistone.wallet.sealwallet.entity.TransferDetailInfo;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.List;

public class NewsTransferInfo {

    /**
     * 查询所有网络交易记录
     * @return
     */
    public static List<TransferDetailInfo.ResultBean> findAll(){
        List<TransferDetailInfo.ResultBean> beanList = SQLite.select().from(TransferDetailInfo.ResultBean.class).queryList();

        return beanList;
    }
}
