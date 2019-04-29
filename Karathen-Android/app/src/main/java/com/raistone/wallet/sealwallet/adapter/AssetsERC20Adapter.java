package com.raistone.wallet.sealwallet.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.raistone.wallet.sealwallet.R;
import com.raistone.wallet.sealwallet.entity.AssetsInfo;

import java.util.List;

public class AssetsERC20Adapter extends BaseQuickAdapter<AssetsInfo.DataBean,BaseViewHolder>{
    public AssetsERC20Adapter(@Nullable List<AssetsInfo.DataBean> data) {
        super(R.layout.assets_item_layout, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, AssetsInfo.DataBean item) {

        helper.setText(R.id.assets_name_tv,item.getTokenSynbol())
                .setText(R.id.token_name_tv,item.getTokenName())
                .setText(R.id.assets_price_tv,item.getBalance()+"")
                .setText(R.id.assets_rate_price_tv,item.getPriceUSD()+"")
                .addOnClickListener(R.id.more_ll);
    }

}
