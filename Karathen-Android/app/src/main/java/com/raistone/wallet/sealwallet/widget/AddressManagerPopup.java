package com.raistone.wallet.sealwallet.widget;

import android.content.Context;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.raistone.wallet.sealwallet.R;
import com.raistone.wallet.sealwallet.factory.ChainAddressInfo;
import com.raistone.wallet.sealwallet.utils.ChainAddressCreateManager;

import razerdp.basepopup.BasePopupWindow;

public class AddressManagerPopup extends BasePopupWindow implements View.OnClickListener {

    RelativeLayout export_private_ll;

    RelativeLayout export_mnemonic_ll;

    RelativeLayout export_keystore_ll;

    RelativeLayout export_wif_ll;

    RelativeLayout update_name_ll;

    RelativeLayout browser_query_ll;

    RelativeLayout delete_ll;

    RelativeLayout claim_ll;

    TextView claim_tv;

    private Handler mHandler;

    private ChainAddressInfo addressInfo;
    private OnCommentPopupClickListener mOnCommentPopupClickListener;


    public AddressManagerPopup(Context context) {
        super(context);

        mHandler = new Handler();

        export_private_ll = findViewById(R.id.export_private_ll);
        export_keystore_ll = findViewById(R.id.export_keystore_ll);
        export_mnemonic_ll = findViewById(R.id.export_mnemonic_ll);
        export_wif_ll = findViewById(R.id.export_wif_ll);
        update_name_ll = findViewById(R.id.update_name_ll);
        browser_query_ll = findViewById(R.id.browser_query_ll);
        delete_ll = findViewById(R.id.delete_ll);
        claim_ll = findViewById(R.id.claim_ll);

        claim_tv=findViewById(R.id.claim_tv);


        export_private_ll.setOnClickListener(this);
        export_mnemonic_ll.setOnClickListener(this);
        export_wif_ll.setOnClickListener(this);
        update_name_ll.setOnClickListener(this);
        browser_query_ll.setOnClickListener(this);
        delete_ll.setOnClickListener(this);
        claim_ll.setOnClickListener(this);

        buildAnima();
        setBackground(0);
        setAllowDismissWhenTouchOutside(true);
        setAutoLocatePopup(true);
        setPopupGravity(Gravity.BOTTOM | Gravity.LEFT);
        setBlurBackgroundEnable(true);

    }

    public AddressManagerPopup(Context context,ChainAddressInfo addressInfo) {
        super(context);

        this.addressInfo=addressInfo;

        mHandler = new Handler();



        export_private_ll = findViewById(R.id.export_private_ll);
        export_mnemonic_ll = findViewById(R.id.export_mnemonic_ll);

        export_keystore_ll = findViewById(R.id.export_keystore_ll);
        export_wif_ll = findViewById(R.id.export_wif_ll);
        update_name_ll = findViewById(R.id.update_name_ll);
        browser_query_ll = findViewById(R.id.browser_query_ll);
        delete_ll = findViewById(R.id.delete_ll);
        claim_ll = findViewById(R.id.claim_ll);

        claim_tv=findViewById(R.id.claim_tv);



        String chainType = addressInfo.getCoinType();



        if(!addressInfo.getIsImport()){

            export_private_ll.setVisibility(View.VISIBLE);
            update_name_ll.setVisibility(View.VISIBLE);
            browser_query_ll.setVisibility(View.VISIBLE);

            if (chainType.equals(ChainAddressCreateManager.ETH_COIN_TYPE)) {
                export_keystore_ll.setVisibility(View.VISIBLE);
                export_wif_ll.setVisibility(View.GONE);
                claim_ll.setVisibility(View.GONE);
            } else {
                export_keystore_ll.setVisibility(View.GONE);
                export_wif_ll.setVisibility(View.VISIBLE);
                claim_ll.setVisibility(View.VISIBLE);
                if (chainType.equals(ChainAddressCreateManager.NEO_COIN_TYPE)) {
                    claim_tv.setText(context.getResources().getString(R.string.claim_gas_string));
                } else {
                    claim_tv.setText(context.getResources().getString(R.string.claim_ong_string));
                }
            }

        }else {
            export_private_ll.setVisibility(View.VISIBLE);
            update_name_ll.setVisibility(View.VISIBLE);
            browser_query_ll.setVisibility(View.VISIBLE);

            export_mnemonic_ll.setVisibility(View.GONE);

            if (chainType.equals(ChainAddressCreateManager.ETH_COIN_TYPE)) {
                export_wif_ll.setVisibility(View.GONE);
                claim_ll.setVisibility(View.GONE);
                export_keystore_ll.setVisibility(View.VISIBLE);
            } else {
                export_wif_ll.setVisibility(View.VISIBLE);
                claim_ll.setVisibility(View.VISIBLE);
                export_keystore_ll.setVisibility(View.GONE);
                if (chainType.equals(ChainAddressCreateManager.NEO_COIN_TYPE)) {
                    claim_tv.setText(context.getResources().getString(R.string.claim_gas_string));
                } else {
                    claim_tv.setText(context.getResources().getString(R.string.claim_ong_string));
                }
            }
        }


        export_private_ll.setOnClickListener(this);
        export_mnemonic_ll.setOnClickListener(this);
        export_wif_ll.setOnClickListener(this);
        update_name_ll.setOnClickListener(this);
        browser_query_ll.setOnClickListener(this);
        delete_ll.setOnClickListener(this);
        claim_ll.setOnClickListener(this);
        export_keystore_ll.setOnClickListener(this);

        buildAnima();
        setBackground(0);
        setAllowDismissWhenTouchOutside(true);
        setPopupGravity(Gravity.BOTTOM | Gravity.LEFT);
        setBlurBackgroundEnable(true);

    }

    private AnimationSet mAnimationSet;

    private void buildAnima() {
        ScaleAnimation mScaleAnimation = new ScaleAnimation(1f, 2f, 1f, 2f, Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        mScaleAnimation.setDuration(200);
        mScaleAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        mScaleAnimation.setFillAfter(false);

        AlphaAnimation mAlphaAnimation = new AlphaAnimation(1, .2f);
        mAlphaAnimation.setDuration(400);
        mAlphaAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        mAlphaAnimation.setFillAfter(false);

        mAnimationSet = new AnimationSet(false);
        mAnimationSet.setDuration(400);
        mAnimationSet.addAnimation(mScaleAnimation);
        mAnimationSet.addAnimation(mAlphaAnimation);
        mAnimationSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dismiss();
                    }
                }, 150);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    @Override
    protected Animation onCreateShowAnimation() {
        Animation showAnima = new TranslateAnimation(Animation.RELATIVE_TO_PARENT,
                1f,
                Animation.RELATIVE_TO_PARENT,
                0,
                Animation.RELATIVE_TO_PARENT,
                0,
                Animation.RELATIVE_TO_PARENT,
                0);
        showAnima.setInterpolator(new DecelerateInterpolator());
        showAnima.setDuration(350);
        return showAnima;
    }

    @Override
    public void onAnchorTop() {
        super.onAnchorTop();

    }

    @Override
    protected Animation onCreateDismissAnimation() {
        Animation exitAnima = new TranslateAnimation(Animation.RELATIVE_TO_PARENT,
                0f,
                Animation.RELATIVE_TO_PARENT,
                1f,
                Animation.RELATIVE_TO_PARENT,
                0,
                Animation.RELATIVE_TO_PARENT,
                0);
        exitAnima.setInterpolator(new DecelerateInterpolator());
        exitAnima.setDuration(350);
        return exitAnima;
    }

    @Override
    public View onCreateContentView() {
        return createPopupById(R.layout.export_popwindows_layout_news);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.export_private_ll:
                if (mOnCommentPopupClickListener != null) {
                    mOnCommentPopupClickListener.onCommentClick(v);
                }
                break;
            case R.id.export_keystore_ll:
                if (mOnCommentPopupClickListener != null) {
                    mOnCommentPopupClickListener.onCommentClick(v);
                }
                break;
            case R.id.export_mnemonic_ll:
                if (mOnCommentPopupClickListener != null) {
                    mOnCommentPopupClickListener.onCommentClick(v);
                }
                break;
            case R.id.export_wif_ll:
                if (mOnCommentPopupClickListener != null) {
                    mOnCommentPopupClickListener.onCommentClick(v);
                }
                break;
            case R.id.update_name_ll:
                if (mOnCommentPopupClickListener != null) {
                    mOnCommentPopupClickListener.onCommentClick(v);
                }
                break;
            case R.id.browser_query_ll:
                if (mOnCommentPopupClickListener != null) {
                    mOnCommentPopupClickListener.onCommentClick(v);
                }
                break;
            case R.id.delete_ll:
                if (mOnCommentPopupClickListener != null) {
                    mOnCommentPopupClickListener.onCommentClick(v);
                }
                break;
            case R.id.claim_ll:
                if (mOnCommentPopupClickListener != null) {
                    mOnCommentPopupClickListener.onCommentClick(v);
                }
                break;
        }
    }

    public OnCommentPopupClickListener getOnCommentPopupClickListener() {
        return mOnCommentPopupClickListener;
    }

    public void setOnCommentPopupClickListener(OnCommentPopupClickListener onCommentPopupClickListener) {
        mOnCommentPopupClickListener = onCommentPopupClickListener;
    }

    public interface OnCommentPopupClickListener {

        void onCommentClick(View v);
    }
}
