package com.raistone.wallet.sealwallet.ui;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.raistone.wallet.sealwallet.R;
import com.raistone.wallet.sealwallet.ui.webview.config.FullscreenHolder;
import com.raistone.wallet.sealwallet.ui.webview.config.IWebPageView;
import com.raistone.wallet.sealwallet.ui.webview.config.ImageClickInterface;
import com.raistone.wallet.sealwallet.ui.webview.config.SealWebChromeClient;
import com.raistone.wallet.sealwallet.ui.webview.config.SealWebViewClient;
import com.raistone.wallet.sealwallet.utils.CommonUtils;
import com.raistone.wallet.sealwallet.utils.Constant;
import com.raistone.wallet.sealwallet.utils.LocalManageUtil;
import com.raistone.wallet.sealwallet.utils.SealUtils;
import com.raistone.wallet.sealwallet.utils.StatusBarUtil;


public class WebViewActivity extends BaseActivity implements IWebPageView {
    private ProgressBar mProgressBar;
    private WebView mWebView;
    private FrameLayout videoFullView;
    private Toolbar mTitleToolBar;
    private SealWebChromeClient mWebChromeClient;
    private String mTitleStr;
    private String mUrl;
    private TextView mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_view_activity);
        getIntentData();

        ActivityManager.getInstance().pushActivity(this);
        initTitle();
        initWebView();
        mWebView.loadUrl(mUrl);
    }

    private void getIntentData() {
        if (getIntent() != null) {
            mTitleStr = getIntent().getStringExtra(Constant.IntentKey.WEB_TITLE);
            mUrl = getIntent().getStringExtra(Constant.IntentKey.WEB_URL);
        }
    }

    private void initTitle() {
        //StatusBarUtil.setColor(this, CommonUtils.getColor(R.color.base_background_white), 0);
        mProgressBar = findViewById(R.id.pb_progress);
        mWebView = findViewById(R.id.webview_detail);
        videoFullView = findViewById(R.id.video_fullView);
        mTitleToolBar = findViewById(R.id.title_tool_bar);
        mTitle = findViewById(R.id.tv_title);

        initToolBar();
    }

    private void initToolBar() {
        setSupportActionBar(mTitleToolBar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            //去除默认Title显示
            actionBar.setDisplayShowTitleEnabled(false);
        }
        mTitle.postDelayed(new Runnable() {
            @Override
            public void run() {
                mTitle.setSelected(true);
            }
        }, 2000);

        mTitleToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        setTitle(mTitleStr);
    }

    public void setTitle(String str) {
        if (TextUtils.isEmpty(mTitle.getText())) {
            mTitle.setText(str);
        }
    }

    private void initWebView() {
        mProgressBar.setVisibility(View.VISIBLE);
        WebSettings ws = mWebView.getSettings();
        ws.setLoadWithOverviewMode(false);
        ws.setSaveFormData(true);
        ws.setSupportZoom(true);
        ws.setBuiltInZoomControls(true);
        ws.setDisplayZoomControls(false);
        ws.setAppCacheEnabled(true);
        ws.setCacheMode(WebSettings.LOAD_DEFAULT);
        ws.setUseWideViewPort(true);
        mWebView.setInitialScale(100);
        ws.setJavaScriptEnabled(true);
        ws.setBlockNetworkImage(false);
        ws.setDomStorageEnabled(true);
        ws.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ws.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        ws.setTextZoom(100);

        mWebChromeClient = new SealWebChromeClient(this);
        mWebView.setWebChromeClient(mWebChromeClient);
        mWebView.addJavascriptInterface(new ImageClickInterface(this), "injectedObject");
        mWebView.setWebViewClient(new SealWebViewClient(this));
    }

    @Override
    public void hindProgressBar() {
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void showWebView() {
        mWebView.setVisibility(View.VISIBLE);
    }

    @Override
    public void hindWebView() {
        mWebView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void fullViewAddView(View view) {
        FrameLayout decor = (FrameLayout) getWindow().getDecorView();
        videoFullView = new FullscreenHolder(WebViewActivity.this);
        videoFullView.addView(view);
        decor.addView(videoFullView);
    }

    @Override
    public void showVideoFullView() {
        videoFullView.setVisibility(View.VISIBLE);
    }

    @Override
    public void hindVideoFullView() {
        videoFullView.setVisibility(View.GONE);
    }

    @Override
    public void startProgress(int newProgress) {
        mProgressBar.setVisibility(View.VISIBLE);
        mProgressBar.setProgress(newProgress);
        if (newProgress == 100) {
            mProgressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void addImageClickListener() {

        mWebView.loadUrl("javascript:(function(){" +
                "var objs = document.getElementsByTagName(\"img\");" +
                "for(var i=0;i<objs.length;i++)" +
                "{" +
                "objs[i].onclick=function(){window.injectedObject.imageClick(this.getAttribute(\"src\"),this.getAttribute(\"has_link\"));}" +
                "}" +
                "})()");

        mWebView.loadUrl("javascript:(function(){" +
                "var objs =document.getElementsByTagName(\"a\");" +
                "for(var i=0;i<objs.length;i++)" +
                "{" +
                "objs[i].onclick=function(){" +
                "window.injectedObject.textClick(this.getAttribute(\"type\"),this.getAttribute(\"item_pk\"));}" +
                "}" +
                "})()");
    }

    public FrameLayout getVideoFullView() {
        return videoFullView;
    }


    public void hideCustomView() {
        mWebChromeClient.onHideCustomView();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == SealWebChromeClient.FILECHOOSER_RESULTCODE) {
            mWebChromeClient.mUploadMessage(intent, resultCode);
        } else if (requestCode == SealWebChromeClient.FILECHOOSER_RESULTCODE_FOR_ANDROID_5) {
            mWebChromeClient.mUploadMessageForAndroid5(intent, resultCode);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mWebChromeClient.inCustomView()) {
                hideCustomView();
                return true;
            } else if (mWebView.canGoBack()) {
                mWebView.goBack();
                return true;
            } else {
                finish();
            }
        }
        return false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        mWebView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mWebView.onResume();
        mWebView.resumeTimers();
        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        videoFullView.removeAllViews();
        if (mWebView != null) {
            mWebView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            mWebView.clearHistory();
            ViewGroup parent = (ViewGroup) mWebView.getParent();
            if (parent != null) {
                parent.removeView(mWebView);
            }
            mWebView.removeAllViews();
            mWebView.stopLoading();
            mWebView.setWebChromeClient(null);
            mWebView.setWebViewClient(null);
            mWebView.destroy();
            mWebView = null;
        }
    }

    public static void loadUrl(Context mContext, String mUrl, String mTitle) {
        if (SealUtils.isNetworkConnected(mContext)) {
            Intent intent = new Intent(mContext, WebViewActivity.class);
            intent.putExtra("mUrl", mUrl);
            intent.putExtra("mTitle", mTitle == null ? "" : mTitle);
            mContext.startActivity(intent);
        } else {
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocalManageUtil.setLocal(newBase));
    }

}
