package com.raistone.wallet.sealwallet.ui.webview.config;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.raistone.wallet.sealwallet.ui.WebViewActivity;
import com.raistone.wallet.sealwallet.utils.SealUtils;



public class SealWebViewClient extends WebViewClient {

    private IWebPageView mIWebPageView;
    private WebViewActivity mActivity;

    public SealWebViewClient(IWebPageView mIWebPageView) {
        this.mIWebPageView = mIWebPageView;
        mActivity = (WebViewActivity) mIWebPageView;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        if (TextUtils.isEmpty(url)) {
            return false;
        }
        if (url.startsWith("http:") || url.startsWith("https:")) {
            if (url.contains(".apk")) {
                handleOtherwise(mActivity, url);
                return true;
            }
            return false;
        }

        handleOtherwise(mActivity, url);
        return true;
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        if (!SealUtils.isNetworkConnected(mActivity)) {
            mIWebPageView.hindProgressBar();
        }
        mIWebPageView.addImageClickListener();
        super.onPageFinished(view, url);
    }

    @Override
    public void onScaleChanged(WebView view, float oldScale, float newScale) {
        super.onScaleChanged(view, oldScale, newScale);
        if (newScale - oldScale > 7) {
            view.setInitialScale((int) (oldScale / newScale * 100));
        }
    }

    private void handleOtherwise(Activity activity, String url) {
        String appPackageName = "";
        if (url.contains("alipays")) {
            appPackageName = "com.eg.android.AlipayGphone";

        } else if (url.contains("weixin://wap/pay")) {
            appPackageName = "com.tencent.mm";

        } else if (url.contains("openapp.jdmobile")) {
            appPackageName = "com.jingdong.app.mall";
        } else {
            startActivity(url);
        }
        if (SealUtils.isApplicationAviliable(activity, appPackageName)) {
            startActivity(url);
        }
    }

    private void startActivity(String url) {
        try {
            Intent intent1 = new Intent();
            intent1.setAction("android.intent.action.VIEW");
            Uri uri = Uri.parse(url);
            intent1.setData(uri);
            intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mActivity.startActivity(intent1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
