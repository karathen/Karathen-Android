package com.raistone.wallet.sealwallet.adapter;

import android.graphics.Color;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.raistone.wallet.sealwallet.R;
import com.raistone.wallet.sealwallet.entity.AddAssetsInfo;

import java.util.List;

public class AssetsAddressAdapter extends BaseQuickAdapter<AddAssetsInfo.ResultBean,BaseViewHolder>{
    public AssetsAddressAdapter(@Nullable List<AddAssetsInfo.ResultBean> data) {
        super(R.layout.addassets_item_layout,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, AddAssetsInfo.ResultBean item) {

        helper.setText(R.id.assets_name_tv,item.getTokenSynbol())
                .setText(R.id.token_name_tv,item.getTokenName())
                .setText(R.id.token_address_tv,item.getTokenAddress());



        ImageView icon=helper.getView(R.id.assets_icon_iv);

        TextView addTv=helper.getView(R.id.assets_add_tv);

        String tokenIcon = item.getTokenIcon();

        if(!TextUtils.isEmpty(tokenIcon)){
            Glide.with(mContext).load(item.getTokenIcon()).into(icon);
        }else {
            Glide.with(mContext).load(R.drawable.eth_defalult_icon).into(icon);
        }


        if(item.getAddFlag()==0){
            addTv.setBackground(null);
            addTv.setTextColor(Color.parseColor("#7e7e7e"));
            addTv.setText(mContext.getResources().getString(R.string.added_string));
        }else {
            addTv.setBackground(mContext.getResources().getDrawable(R.drawable.add_btn_bg));
            addTv.setText(mContext.getResources().getString(R.string.add_string));
            addTv.setTextColor(mContext.getResources().getColor(R.color.main_color));
            helper.addOnClickListener(R.id.assets_add_tv);
        }


    }
}
