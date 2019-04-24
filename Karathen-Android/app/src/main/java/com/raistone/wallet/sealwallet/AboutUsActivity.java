package com.raistone.wallet.sealwallet;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.chenenyu.router.annotation.Route;
import com.raistone.wallet.sealwallet.ui.ActivityManager;
import com.raistone.wallet.sealwallet.ui.BaseActivity;
import com.raistone.wallet.sealwallet.ui.MainActivity;
import com.raistone.wallet.sealwallet.ui.WebViewActivity;
import com.raistone.wallet.sealwallet.utils.ConfigUtils;
import com.raistone.wallet.sealwallet.utils.Constant;
import com.raistone.wallet.sealwallet.utils.LocalManageUtil;
import com.raistone.wallet.sealwallet.utils.SPUtil;
import com.raistone.wallet.sealwallet.utils.ScreenUtil;
import com.raistone.wallet.sealwallet.utils.SealUtils;
import com.raistone.wallet.sealwallet.utils.StatusBarUtil;
import com.raistone.wallet.sealwallet.utils.ToastHelper;
import com.raistone.wallet.sealwallet.widget.TitleBar;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 关于我们
 */

@Route(value = "AboutUsActivity")
public class AboutUsActivity extends BaseActivity {

    @BindView(R.id.tv_version_name)
    TextView tvVersionName;
    RelativeLayout rlPrivacy;
    @BindView(R.id.iv_check)
    ImageView ivCheck;
    @BindView(R.id.rl_check_for_updates)
    RelativeLayout rlCheckForUpdates;
    @BindView(R.id.iv_contact)
    ImageView ivContact;
    @BindView(R.id.rl_contact)
    RelativeLayout rlContact;
    @BindView(R.id.tv_website)
    TextView tvWebsite;
    @BindView(R.id.titleBar)
    TitleBar titleBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        ButterKnife.bind(this);
        setTitle(titleBar,getResources().getString(R.string.about_us_string),true);
        //StatusBarUtil.setTransparent(this);
        ActivityManager.getInstance().pushActivity(this);
        tvVersionName.setText(getResources().getString(R.string.currentVersion_string)+ ConfigUtils.getVersionName(this));
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @OnClick({ R.id.rl_check_for_updates,R.id.rl_contact,R.id.tv_website})
    public void onViewClicked(View view) {
        int savedLanguageType = SPUtil.getInstance(this).getSelectLanguage();
        switch (view.getId()) {

            /**
             * 检查更新
             */
            case R.id.rl_check_for_updates:

                ToastHelper.showToast(getResources().getString(R.string.latest_version_string));
                break;
            /**
             * 联系我们
             */
            case R.id.rl_contact:
                showContactUsDialog();
                break;
            /**
             * 网址
             */
            case R.id.tv_website:
                targetToWeb(Constant.SealWebUrl.SEAL_OFFICIAL_WEBSITE_URL, "");
                break;
        }
    }

    public String getBaiduMobAdChannel() {
        String baiduMobAdChannel = "sealwallet.org";
        try {
            ApplicationInfo activityInfo = this.getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);

            baiduMobAdChannel = activityInfo.metaData.getString("BaiduMobAd_CHANNEL");

            return baiduMobAdChannel;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return baiduMobAdChannel;

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void showContactUsDialog() {
        final Dialog dialog = new Dialog(this, R.style.TransparentDialog);
        View layout = LayoutInflater.from(this).inflate(R.layout.only_title_dialog, null);
        final TextView title = layout.findViewById(R.id.tv_dialog_title);
        View container = layout.findViewById(R.id.rl_container);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) container.getLayoutParams();
        params.width = ScreenUtil.getScreenWidth(this) * 300 / 360;

        container.setLayoutParams(params);
        dialog.setCancelable(true);
        dialog.setContentView(layout);
        layout.findViewById(R.id.tv_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    if (null != title) {
                        SealUtils.clipData(AboutUsActivity.this, title.getText().toString());
                        dialog.dismiss();
                    }
            }
        });
        dialog.show();
    }

    private void targetToWeb(String url, String title) {
        Intent intent = new Intent(AboutUsActivity.this, WebViewActivity.class);
        intent.putExtra(Constant.IntentKey.WEB_URL, url);
        if (!TextUtils.isEmpty(title)) {
            intent.putExtra(Constant.IntentKey.WEB_TITLE, title);
        }
        startActivity(intent);
    }
}
