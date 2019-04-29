package com.raistone.wallet.sealwallet;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.chenenyu.router.annotation.Route;
import com.raistone.wallet.sealwallet.ui.ActivityManager;
import com.raistone.wallet.sealwallet.ui.BaseActivity;
import com.raistone.wallet.sealwallet.ui.MainActivity;
import com.raistone.wallet.sealwallet.utils.LocalManageUtil;
import com.raistone.wallet.sealwallet.utils.SPUtil;
import com.raistone.wallet.sealwallet.utils.StatusBarUtil;
import com.raistone.wallet.sealwallet.widget.TitleBar;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@Route(value = "LanguageSettingActivity")
public class LanguageSettingActivity extends BaseActivity {

    @BindView(R.id.titleBar)
    TitleBar titleBar;
    @BindView(R.id.tv_icon)
    ImageView tvIcon;
    @BindView(R.id.rl_chinese)
    RelativeLayout rlChinese;
    @BindView(R.id.tv_icon1)
    ImageView tvIcon1;
    @BindView(R.id.rl_english)
    RelativeLayout rlEnglish;

    private boolean mDefaultCh = true;

    private int savedLanguageType;//,changType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language_setting);
        ButterKnife.bind(this);
        setTitle(titleBar, getString(R.string.language_setting_string), true);

        ActivityManager.getInstance().pushActivity(this);


        savedLanguageType = SPUtil.getInstance(this).getSelectLanguage();

        if (savedLanguageType == 0) {
            String language = LocalManageUtil.getSystemLocale(this).getDisplayLanguage();

            if (language.contains("English") || language.contains("英文")) {

                tvIcon.setVisibility(View.GONE);
                tvIcon1.setVisibility(View.VISIBLE);
            } else {
                tvIcon.setVisibility(View.VISIBLE);
                tvIcon1.setVisibility(View.GONE);
            }
        }
        if (savedLanguageType == 1) {
            tvIcon.setVisibility(View.VISIBLE);
            tvIcon1.setVisibility(View.GONE);
        }

        if (savedLanguageType == 3) {
            tvIcon.setVisibility(View.GONE);
            tvIcon1.setVisibility(View.VISIBLE);
        }

    }

    @OnClick({R.id.rl_chinese, R.id.rl_english})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_chinese:
                savedLanguageType = 1;
                mDefaultCh = true;
                updateState();
                selectLanguage(1);
                break;
            case R.id.rl_english:
                mDefaultCh = false;
                savedLanguageType = 3;
                updateState();
                selectLanguage(3);
                break;
        }
    }

    private void selectLanguage(int select) {
        LocalManageUtil.saveSelectLanguage(this, select);
        MainActivity.reStart(this);
    }


    private void updateState() {
        if (mDefaultCh) {
            tvIcon.setVisibility(View.VISIBLE);
            tvIcon1.setVisibility(View.GONE);
        } else {
            tvIcon.setVisibility(View.GONE);
            tvIcon1.setVisibility(View.VISIBLE);
        }
    }
}
