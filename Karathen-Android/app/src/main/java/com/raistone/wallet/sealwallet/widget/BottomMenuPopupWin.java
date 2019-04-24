package com.raistone.wallet.sealwallet.widget;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.widget.TextView;

import com.raistone.wallet.sealwallet.R;

import razerdp.basepopup.BasePopupWindow;

public class BottomMenuPopupWin extends BasePopupWindow implements View.OnClickListener{


    private TextView ethTv,neoTv,ontTv;

    private WalletManagerPopup.OnCommentPopupClickListener mOnCommentPopupClickListener;

    boolean hideEth=false;

    public BottomMenuPopupWin(Context context) {
        super(context);
        setPopupGravity(Gravity.BOTTOM);
        bindEvent();
    }

    public BottomMenuPopupWin(Context context,boolean hideEth) {
        super(context);
        setPopupGravity(Gravity.BOTTOM);
        this.hideEth=hideEth;
        bindEvent();
    }

    @Override
    protected Animation onCreateShowAnimation() {
        return getTranslateVerticalAnimation(1f, 0, 500);
    }

    @Override
    protected Animation onCreateDismissAnimation() {
        return getTranslateVerticalAnimation(0, 1f, 500);
    }

    @Override
    public View onCreateContentView() {
        return createPopupById(R.layout.popup_slide_from_bottom);
    }

    private void bindEvent() {
        ethTv=findViewById(R.id.eth_tv);
        neoTv=findViewById(R.id.neo_tv);
        ontTv=findViewById(R.id.ont_tv);

        ethTv.setOnClickListener(this);
        neoTv.setOnClickListener(this);
        ontTv.setOnClickListener(this);

        if(hideEth){
            ethTv.setVisibility(View.GONE);
        }else {
            ethTv.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.eth_tv:
                if (mOnCommentPopupClickListener != null) {
                    mOnCommentPopupClickListener.onCommentClick(v);
                }
                break;
            case R.id.neo_tv:
                if (mOnCommentPopupClickListener != null) {
                    mOnCommentPopupClickListener.onCommentClick(v);
                }
                break;
            case R.id.ont_tv:
                if (mOnCommentPopupClickListener != null) {
                    mOnCommentPopupClickListener.onCommentClick(v);
                }
                break;
            default:
                break;
        }

    }


    public WalletManagerPopup.OnCommentPopupClickListener getOnCommentPopupClickListener() {
        return mOnCommentPopupClickListener;
    }

    public void setOnCommentPopupClickListener(WalletManagerPopup.OnCommentPopupClickListener onCommentPopupClickListener) {
        mOnCommentPopupClickListener = onCommentPopupClickListener;
    }

    public interface OnCommentPopupClickListener {

        void onCommentClick(View v);
    }
}
