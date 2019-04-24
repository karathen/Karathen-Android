package com.raistone.wallet.sealwallet.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.chenenyu.router.annotation.Route;
import com.raistone.wallet.sealwallet.R;
import com.raistone.wallet.sealwallet.utils.SharePreUtil;
import com.raistone.wallet.sealwallet.utils.StatusBarUtil;
import com.raistone.wallet.sealwallet.widget.TitleBar;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 货币单位
 */

@Route(value = "CurrencySettingActivity")
public class CurrencySettingActivity extends BaseActivity {

    @BindView(R.id.iv_icon)
    ImageView ivIcon;
    @BindView(R.id.rl_cny)
    RelativeLayout rlCny;
    @BindView(R.id.iv_icon1)
    ImageView ivIcon1;
    @BindView(R.id.rl_usd)
    RelativeLayout rlUsd;
    @BindView(R.id.titleBar)
    TitleBar titleBar;

    private boolean mDefaultCh = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currency_setting);
        ButterKnife.bind(this);
        setTitle(titleBar,getString(R.string.currency_string),true);
        //StatusBarUtil.setTransparent(this);
        ActivityManager.getInstance().pushActivity(this);
        mDefaultCh= SharePreUtil.getBoolean(this,"CurrencyUnit",true);
        updateState();
    }

    @OnClick({R.id.rl_cny, R.id.rl_usd})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_cny:
                if (!mDefaultCh) {
                    mDefaultCh = true;
                    updateState();
                }
                break;
            case R.id.rl_usd:
                if (mDefaultCh) {
                    mDefaultCh = false;
                    updateState();
                }
                break;
        }
    }

    private void updateState() {
        if (mDefaultCh) {
            ivIcon.setVisibility(View.VISIBLE);
            ivIcon1.setVisibility(View.GONE);
            SharePreUtil.saveBoolean(this,"CurrencyUnit",true);
        } else {
            ivIcon.setVisibility(View.GONE);
            ivIcon1.setVisibility(View.VISIBLE);
            SharePreUtil.saveBoolean(this,"CurrencyUnit",false);
        }
        //fixme refresh the data change the currency
    }
}
