package com.raistone.wallet.sealwallet.widget;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.raistone.wallet.sealwallet.R;
import com.raistone.wallet.sealwallet.entity.AssetsInfo;


/**
 * Created by DongJr on 2017/2/27.
 */

public class AssetsFilterFitPopupUtil implements View.OnClickListener {

    private View contentView;

    private Activity context;


    private LinearLayout all_ll;
    private LinearLayout erc_20_ll;
    private LinearLayout erc_721_ll;
    private LinearLayout hide_zero_assets_ll;
    private ImageButton hide_zero_assets_imbutton;

    private FitPopupWindow mPopupWindow;

    private OnCommitClickListener listener;

    private AssetsInfo.DataBean asBean;

    private int filterType = 0;//查询条件 0 所有，1 erc_20 2 erc_721 3 隐藏为0 的资产

    public AssetsFilterFitPopupUtil(Activity context) {

        this.context = context;

        LayoutInflater inflater = LayoutInflater.from(context);
        contentView = inflater.inflate(R.layout.assets_filter_layout, null);

        all_ll = contentView.findViewById(R.id.all_ll);
        erc_20_ll = contentView.findViewById(R.id.all_ll);
        erc_721_ll = contentView.findViewById(R.id.all_ll);
        hide_zero_assets_ll = contentView.findViewById(R.id.hide_zero_assets_ll);
        hide_zero_assets_imbutton = contentView.findViewById(R.id.hide_zero_assets_imbutton);


        all_ll.setOnClickListener(this);
        erc_20_ll.setOnClickListener(this);
        erc_721_ll.setOnClickListener(this);
        hide_zero_assets_ll.setOnClickListener(this);


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
            mPopupWindow = new FitPopupWindow(context,    300,
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
            case R.id.all_ll:
                filterType = 0;
                if (listener != null) {
                    listener.onClick(getReason());
                }
                mPopupWindow.dismiss();
                break;
            case R.id.erc_20_ll:
                filterType = 1;
                if (listener != null) {
                    listener.onClick(getReason());
                }
                mPopupWindow.dismiss();
                break;
            case R.id.erc_721_ll:
                filterType = 2;
                if (listener != null) {
                    listener.onClick(getReason());
                }
                mPopupWindow.dismiss();
                break;
            case R.id.hide_zero_assets_ll:
                filterType = 3;
                if (listener != null) {
                    listener.onClick(getReason());
                }
                mPopupWindow.dismiss();
                break;
        }


    }

    public int getReason() {

        return filterType;

    }

    public interface OnCommitClickListener {
        void onClick(int type);
    }

}
