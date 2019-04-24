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
import android.widget.RelativeLayout;

import com.raistone.wallet.sealwallet.R;
import com.raistone.wallet.sealwallet.factory.HdWallet;

import razerdp.basepopup.BasePopupWindow;


/**
 * 钱包管理 PopupWindow
 */
public class WalletManagerPopup extends BasePopupWindow implements View.OnClickListener {

    RelativeLayout multi_management_rl; //多链管理

    RelativeLayout wallet_update_name_rl; //修改名称

    RelativeLayout reset_pwd_rl; //重置密码

    RelativeLayout pwd_tips_rl;//密码提示

    RelativeLayout back_rl; //备份钱包

    RelativeLayout delete_rl;// 删除钱包


    private Handler mHandler;

    private OnCommentPopupClickListener mOnCommentPopupClickListener;


    public WalletManagerPopup(Context context) {
        super(context);

        mHandler = new Handler();

        //导出私钥
        multi_management_rl = findViewById(R.id.multi_management_rl);
        //导出助记词
        wallet_update_name_rl = findViewById(R.id.wallet_update_name_rl);
        //导出WIF
        reset_pwd_rl = findViewById(R.id.reset_pwd_rl);
        //修改名称
        pwd_tips_rl = findViewById(R.id.pwd_tips_rl);
        //浏览器查询
        back_rl = findViewById(R.id.back_rl);
        //删除地址
        delete_rl = findViewById(R.id.delete_rl);


        multi_management_rl.setOnClickListener(this);
        wallet_update_name_rl.setOnClickListener(this);
        reset_pwd_rl.setOnClickListener(this);
        pwd_tips_rl.setOnClickListener(this);
        back_rl.setOnClickListener(this);

        delete_rl.setOnClickListener(this);

        buildAnima();
        setBackground(0);
        setAllowDismissWhenTouchOutside(true);
        setAutoLocatePopup(true);
        setPopupGravity(Gravity.BOTTOM | Gravity.LEFT);
        setBlurBackgroundEnable(true);

    }

    public WalletManagerPopup(Context context, HdWallet wallet) {
        super(context);

        mHandler = new Handler();

        //多链管理
        multi_management_rl = findViewById(R.id.multi_management_rl);
        //修改名称
        wallet_update_name_rl = findViewById(R.id.wallet_update_name_rl);
        //重置密码
        reset_pwd_rl = findViewById(R.id.reset_pwd_rl);
        //密码提示
        pwd_tips_rl = findViewById(R.id.pwd_tips_rl);
        //备份钱包
        back_rl = findViewById(R.id.back_rl);
        //删除钱包
        delete_rl = findViewById(R.id.delete_rl);

        boolean isImport = wallet.getIsImport();

        if(isImport && wallet.getImportType()!=0){
            multi_management_rl.setVisibility(View.GONE);
            wallet_update_name_rl.setVisibility(View.VISIBLE);
            reset_pwd_rl.setVisibility(View.VISIBLE);
            pwd_tips_rl.setVisibility(View.VISIBLE);
            back_rl.setVisibility(View.GONE);
            delete_rl.setVisibility(View.VISIBLE);
        }else {
            multi_management_rl.setVisibility(View.VISIBLE);
            wallet_update_name_rl.setVisibility(View.VISIBLE);
            reset_pwd_rl.setVisibility(View.VISIBLE);
            pwd_tips_rl.setVisibility(View.VISIBLE);
            back_rl.setVisibility(View.VISIBLE);
            delete_rl.setVisibility(View.VISIBLE);
        }


        multi_management_rl.setOnClickListener(this);
        wallet_update_name_rl.setOnClickListener(this);
        reset_pwd_rl.setOnClickListener(this);
        pwd_tips_rl.setOnClickListener(this);
        back_rl.setOnClickListener(this);

        delete_rl.setOnClickListener(this);

        buildAnima();
        setBackground(0);
        setAllowDismissWhenTouchOutside(true);
        setAutoLocatePopup(true);
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
        return createPopupById(R.layout.wallet_manager_popwindow_layout);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.multi_management_rl:
                if (mOnCommentPopupClickListener != null) {
                    mOnCommentPopupClickListener.onCommentClick(v);
                }
                break;
            case R.id.wallet_update_name_rl:
                if (mOnCommentPopupClickListener != null) {
                    mOnCommentPopupClickListener.onCommentClick(v);
                }
                break;
            case R.id.reset_pwd_rl:
                if (mOnCommentPopupClickListener != null) {
                    mOnCommentPopupClickListener.onCommentClick(v);
                }
                break;
            case R.id.pwd_tips_rl:
                if (mOnCommentPopupClickListener != null) {
                    mOnCommentPopupClickListener.onCommentClick(v);
                }
                break;
            case R.id.back_rl:
                if (mOnCommentPopupClickListener != null) {
                    mOnCommentPopupClickListener.onCommentClick(v);
                }
                break;
            case R.id.delete_rl:
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
