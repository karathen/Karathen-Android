package com.raistone.wallet.sealwallet.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.raistone.wallet.sealwallet.R;

import java.util.List;

public class MnemonicAdapter extends BaseQuickAdapter<String,BaseViewHolder> {
    public MnemonicAdapter( @Nullable List<String> data) {
        super(R.layout.menmonics_item_layout, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
        helper.setText(R.id.tv_word,item);
    }
}
