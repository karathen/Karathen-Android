package com.raistone.wallet.sealwallet.adapter;

import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.raistone.wallet.sealwallet.R;
import com.raistone.wallet.sealwallet.WalletApplication;
import com.raistone.wallet.sealwallet.entity.MultiChainInfo;
import com.raistone.wallet.sealwallet.factory.ChainAddressInfo;

import java.util.List;

public class AddressManagerAdapterNews extends BaseQuickAdapter<ChainAddressInfo,BaseViewHolder> {

    private SparseBooleanArray mBooleanArray;

    public AddressManagerAdapterNews(@Nullable List<ChainAddressInfo> data) {
        super(R.layout.wallet_list_item_layout, data);
        mBooleanArray = new SparseBooleanArray(data.size());
    }

    @Override
    protected void convert(BaseViewHolder helper, ChainAddressInfo item) {
        helper/*.setText(R.id.wallet_name_tv,item.getName())
                .setText(R.id.address_tv,item.getAddress())*/
        .addOnClickListener(R.id.copyLl)
        .addOnClickListener(R.id.more_ll);


        TextView wallet_name_tv = helper.getView(R.id.wallet_name_tv);
        wallet_name_tv.setText(item.getName());

        TextView address_tv = helper.getView(R.id.address_tv);
        address_tv.setText(item.getAddress());

        TextView flag_view = helper.getView(R.id.wallet_flag_tv);
        RelativeLayout addressRl = helper.getView(R.id.address_rl);

        TextView copyIv = helper.getView(R.id.copy_iv);

        if(item.getIsImport()){
            flag_view.setText("import");
        }else {
            String typeFlag=item.getType_flag();
            String strh = typeFlag.substring(typeFlag.length() -3,typeFlag.length());
            flag_view.setText("HDM44 "+strh);
        }

        if(item.getIsImport() && item.getImportType()==0 || !item.getIsImport()){
            flag_view.setVisibility(View.VISIBLE);
        }else {
            flag_view.setVisibility(View.GONE);
        }

        helper.setImageDrawable(R.id.item_iv, WalletApplication.IconDrawable.get(item.getImagePath()));

        ImageView gouView=helper.getView(R.id.select_iv);
        boolean current = item.getIsCurrent();
        if(current){
            wallet_name_tv.setTextColor(Color.parseColor("#5575fe"));
            address_tv.setTextColor(Color.parseColor("#5575fe"));
            flag_view.setTextColor(Color.parseColor("#5575fe"));
            addressRl.setBackground(mContext.getDrawable(R.drawable.item_bg_icon));
            copyIv.setBackground(mContext.getDrawable(R.drawable.copy_address_icon_select));
            gouView.setVisibility(View.VISIBLE);
        }else {
            gouView.setVisibility(View.GONE);
            wallet_name_tv.setTextColor(Color.parseColor("#333333"));
            address_tv.setTextColor(Color.parseColor("#333333"));
            flag_view.setTextColor(Color.parseColor("#333333"));
            addressRl.setBackground(mContext.getDrawable(R.drawable.item_bg_un_icon));
            copyIv.setBackground(mContext.getDrawable(R.drawable.copy_address_icon));
        }


    }
}
