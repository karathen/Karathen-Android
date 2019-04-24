package com.raistone.wallet.sealwallet.widget;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.raistone.wallet.sealwallet.R;
import com.raistone.wallet.sealwallet.entity.AssetsInfo;
import com.raistone.wallet.sealwallet.factory.AssetsDeatilInfo;


/**
 * Created by DongJr on 2017/2/27.
 */

public class AssetsFitPopupUtil implements View.OnClickListener {

    private View contentView;

    private Activity context;

    private TextView assets_sign_value_tv;
    private TextView assets_name__value_tv;
    private TextView assets_type_value_tv;
    private TextView contract_address_value_tv;

    private LinearLayout copy_ll;

    private FitPopupWindow mPopupWindow;

    private OnCommitClickListener listener;

    private  AssetsDeatilInfo asBean;

    public AssetsFitPopupUtil(Activity context,AssetsDeatilInfo bean) {

        this.context = context;
        this.asBean=bean;

        LayoutInflater inflater = LayoutInflater.from(context);
        contentView = inflater.inflate(R.layout.layout_popupwindow_flag, null);
        assets_sign_value_tv =  contentView.findViewById(R.id.assets_sign_value_tv);

        assets_sign_value_tv.setText(bean.getTokenSynbol());

        assets_name__value_tv = contentView.findViewById(R.id.assets_name_value_tv);
        assets_type_value_tv =  contentView.findViewById(R.id.assets_type_value_tv);
        contract_address_value_tv =  contentView.findViewById(R.id.contract_address_value_tv);
        copy_ll =  contentView.findViewById(R.id.copy_ll);


        assets_name__value_tv.setText(bean.getTokenName());

        assets_type_value_tv.setText(bean.getTokenType());

        contract_address_value_tv.setText(bean.getTokenAddress());


        copy_ll.setOnClickListener(this);


    }

    public void setOnClickListener(OnCommitClickListener listener) {
        this.listener = listener;
    }

    /**
     * 弹出自适应位置的popupwindow
     *
     * @param anchorView 目标view
     */
    public View showPopup(View anchorView) {
        if (mPopupWindow == null) {
            mPopupWindow = new FitPopupWindow(context, ScreenUtils.getScreenWidth(context) - DensityUtils.dp2px(20),
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
        }

        mPopupWindow.setView(contentView, anchorView);
        mPopupWindow.show();
        return contentView;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.copy_ll:
                if (listener != null) {
                    listener.onClick(getReason());
                }
                mPopupWindow.dismiss();
                break;
        }


    }

    public String getReason() {
        String content1 = contract_address_value_tv.getText().toString();

        return content1;

    }

    public interface OnCommitClickListener {
        void onClick(String reason);
    }

}
