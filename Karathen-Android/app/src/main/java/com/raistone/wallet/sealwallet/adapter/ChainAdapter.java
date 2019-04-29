package com.raistone.wallet.sealwallet.adapter;

import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.raistone.wallet.sealwallet.R;
import com.raistone.wallet.sealwallet.entity.ChainInfo;
import com.raistone.wallet.sealwallet.factory.ChainDataInfo;
import com.raistone.wallet.sealwallet.utils.MultiChainCreateManager;

import java.util.List;

public class ChainAdapter extends BaseQuickAdapter<ChainDataInfo,BaseViewHolder>{
    public ChainAdapter(@Nullable List<ChainDataInfo> data) {
        super(R.layout.chain_manager_item_layout, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, ChainDataInfo item) {

        ImageView iconView = helper.getView(R.id.chain_icon);

        ImageButton imageButton = helper.getView(R.id.off_iv);

        helper.setText(R.id.chain_name,item.getChainName())
                .setText(R.id.chain_tokenName,item.getChainType())
                .addOnClickListener(R.id.off_ll)
                .addOnClickListener(R.id.off_iv);

        String chaType = item.getChainType();

        boolean show = item.getIsShow();

        if(show){
            imageButton.setBackground(mContext.getResources().getDrawable(R.drawable.open_icon));
        }else {
            imageButton.setBackground(mContext.getResources().getDrawable(R.drawable.close_icon));
        }

        if(chaType.equals(MultiChainCreateManager.ETH_COIN_TYPE)){
            Glide.with(mContext).load(R.drawable.ehd_def_icon).into(iconView);
        }


        if(chaType.equals(MultiChainCreateManager.NEO_COIN_TYPE)){
            Glide.with(mContext).load(R.drawable.neo_def_icon).into(iconView);
        }

        if(chaType.equals(MultiChainCreateManager.ONT_COIN_TYPE)){
            Glide.with(mContext).load(R.drawable.ont_def_icon).into(iconView);
        }
    }
}
