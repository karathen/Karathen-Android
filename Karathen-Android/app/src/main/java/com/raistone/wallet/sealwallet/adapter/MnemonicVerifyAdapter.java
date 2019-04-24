package com.raistone.wallet.sealwallet.adapter;

import android.support.annotation.Nullable;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.raistone.wallet.sealwallet.R;
import com.raistone.wallet.sealwallet.entity.WordsBean;

import java.util.List;

public class MnemonicVerifyAdapter extends BaseQuickAdapter<WordsBean,BaseViewHolder> {
    public MnemonicVerifyAdapter(@Nullable List<WordsBean> data) {
        super(R.layout.menmonics_verify_item_layout, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, WordsBean item) {
        TextView tv_word=helper.getView(R.id.tv_word);
        tv_word.setText(item.getWord());
        if(item.isSelect()) {
            tv_word.setBackground(mContext.getResources().getDrawable(R.drawable.tag_item_recyclerview_bg));
           tv_word.setTextColor(mContext.getResources().getColor(R.color.common_title_color));
        }else {
            tv_word.setBackground(mContext.getResources().getDrawable(R.drawable.tag_right_top_bg));
            tv_word.setTextColor(mContext.getResources().getColor(R.color.white));
        }
    }
}
