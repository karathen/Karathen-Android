package com.raistone.wallet.sealwallet.adapter;

import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.raistone.wallet.sealwallet.R;
import com.raistone.wallet.sealwallet.WalletApplication;
import com.raistone.wallet.sealwallet.entity.ETHWallet;
import com.raistone.wallet.sealwallet.entity.MultiChainInfo;

import java.util.List;

public class AddressManagerAdapter  extends BaseQuickAdapter<MultiChainInfo,BaseViewHolder> {

    private SparseBooleanArray mBooleanArray;

    public AddressManagerAdapter( @Nullable List<MultiChainInfo> data) {
        super(R.layout.wallet_list_item_layout, data);
        mBooleanArray = new SparseBooleanArray(data.size());
    }

    @Override
    protected void convert(BaseViewHolder helper, MultiChainInfo item) {
        helper.setText(R.id.wallet_name_tv,item.getName())
                .setText(R.id.address_tv,item.getAddress())
        .addOnClickListener(R.id.copy_iv)
        .addOnClickListener(R.id.more_ll);

        ImageView view = helper.getView(R.id.item_iv);
        TextView flag_view = helper.getView(R.id.wallet_flag_tv);

        if(item.isImport()){
            flag_view.setText("import");
        }else {
            String typeFlag=item.getType_flag();
            String strh = typeFlag.substring(typeFlag.length() -3,typeFlag.length());
            flag_view.setText("HDM44 "+strh);
        }

        helper.setImageDrawable(R.id.item_iv, WalletApplication.IconDrawable.get(item.getImagePath()));

        ImageView gouView=helper.getView(R.id.select_iv);
        boolean current = item.isCurrent();

        if (!mBooleanArray.get(helper.getAdapterPosition())) {
            gouView.setVisibility(View.GONE);
        } else {
            gouView.setVisibility(View.VISIBLE);
        }

    }

}
