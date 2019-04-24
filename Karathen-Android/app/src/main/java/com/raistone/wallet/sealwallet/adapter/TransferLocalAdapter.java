package com.raistone.wallet.sealwallet.adapter;

import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.raistone.wallet.sealwallet.R;
import com.raistone.wallet.sealwallet.entity.TransferDetailInfo;
import com.raistone.wallet.sealwallet.entity.TransferInfo;
import com.raistone.wallet.sealwallet.ui.WalletItemDetailActivity;
import com.raistone.wallet.sealwallet.utils.CommonUtils;

import java.util.List;

public class TransferLocalAdapter extends BaseQuickAdapter<TransferInfo, BaseViewHolder> {

    public TransferLocalAdapter(@Nullable List<TransferInfo> data) {
        super(R.layout.transfer_item_layout, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, TransferInfo item) {
        helper.setText(R.id.content_tv, item.getTxId())
                .setText(R.id.value_tv, "-" + item.getValue()+" "+ item.getTokenSynbol())
                .setText(R.id.time_tv, item.getTransferTime());

        TextView view = helper.getView(R.id.status_tv);

        ImageView iconLv = helper.getView(R.id.icon_iv);

        Glide.with(mContext).load(R.drawable.in_icon).into(iconLv);

        if (item.getStatus().equals("1")) {
            view.setText(mContext.getResources().getString(R.string.success_string));
        } else if (item.getStatus().equals("-1")) {
            view.setText(mContext.getResources().getString(R.string.failure_string));
        } else {
            view.setText(item.getData());
        }
    }
}
