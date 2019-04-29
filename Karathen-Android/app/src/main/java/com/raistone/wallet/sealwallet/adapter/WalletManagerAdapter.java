package com.raistone.wallet.sealwallet.adapter;

import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.raistone.wallet.sealwallet.R;
import com.raistone.wallet.sealwallet.WalletApplication;
import com.raistone.wallet.sealwallet.entity.MultiChainInfo;
import com.raistone.wallet.sealwallet.factory.HdWallet;

import java.util.List;

public class WalletManagerAdapter extends BaseQuickAdapter<HdWallet,BaseViewHolder> {


    public WalletManagerAdapter(@Nullable List<HdWallet> data) {
        super(R.layout.wallet_manager_item_layout, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, HdWallet item) {
        helper
        .addOnClickListener(R.id.more_ll);

        TextView nameTv=helper.getView(R.id.wallet_name_tv);

        RelativeLayout bgRl=helper.getView(R.id.wallet_bg_rl);

        nameTv.setText(item.getWalletName());

        ImageView view = helper.getView(R.id.item_iv);

        int walletType = item.getWalletType();

        boolean current = item.getIsCurrent();

        switch (walletType){
            case 0:
                if(current) {
                    Glide.with(mContext)
                            .load(R.drawable.hd_min_icon_select)
                            .into(view);
                }else {
                    Glide.with(mContext)
                            .load(R.drawable.hd_min_icon)
                            .into(view);
                }
                break;
            case 1:
                if(current) {
                    Glide.with(mContext)
                            .load(R.drawable.hardware_min_icon_select)
                            .into(view);
                }
                else {
                    Glide.with(mContext)
                            .load(R.drawable.hardware_min_icon)
                            .into(view);
                }
                break;
            case 2:
                if(current) {
                    Glide.with(mContext)
                            .load(R.drawable.cloud_min_icon_select)
                            .into(view);
                }else {
                    Glide.with(mContext)
                            .load(R.drawable.cloud_min_icon)
                            .into(view);
                }
                break;
        }


        LinearLayout status = helper.getView(R.id.not_backup_ll);

        boolean backup = item.getIsBackup();

        if(backup){
            status.setVisibility(View.GONE);
        }else {
            status.setVisibility(View.VISIBLE);
        }

        ImageView gouView=helper.getView(R.id.select_iv);


        if(current){
            gouView.setVisibility(View.VISIBLE);
            nameTv.setTextColor(Color.parseColor("#5575fe"));
            bgRl.setBackground(mContext.getDrawable(R.drawable.item_bg_icon));
        }else {
            gouView.setVisibility(View.GONE);
            bgRl.setBackground(mContext.getDrawable(R.drawable.item_bg_un_icon));
            nameTv.setTextColor(Color.parseColor("#333333"));
        }


    }


}
