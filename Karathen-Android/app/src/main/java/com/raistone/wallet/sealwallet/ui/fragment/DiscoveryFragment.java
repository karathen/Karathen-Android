package com.raistone.wallet.sealwallet.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.raistone.wallet.sealwallet.R;
import com.raistone.wallet.sealwallet.httputils.SealApi;
import com.raistone.wallet.sealwallet.ui.WebViewActivity;
import com.raistone.wallet.sealwallet.utils.Constant;
import com.raistone.wallet.sealwallet.utils.LanguageType;
import com.raistone.wallet.sealwallet.utils.LocalManageUtil;
import com.raistone.wallet.sealwallet.utils.MultiLanguageUtil;
import com.raistone.wallet.sealwallet.utils.SPUtil;
import com.raistone.wallet.sealwallet.utils.ToastHelper;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zhouyou.http.EasyHttp;
import com.zhouyou.http.callback.SimpleCallBack;
import com.zhouyou.http.exception.ApiException;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class DiscoveryFragment extends BaseFragment implements OnRefreshListener/*implements IWebPageView*/ {


    @BindView(R.id.video_fullView)
    FrameLayout videoFullView;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.title_tool_bar)
    Toolbar titleToolBar;
    @BindView(R.id.webview_detail)
    WebView webviewDetail;
    @BindView(R.id.pb_progress)
    ProgressBar pbProgress;
    Unbinder unbinder;

    int savedLanguageType;
    @BindView(R.id.smartrefreshlayout)
    SmartRefreshLayout smartrefreshlayout;

    private Context context;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_discovery, container, false);
        unbinder = ButterKnife.bind(this, view);
        //getFindPage();
        initWebView();
        context = getActivity();

        savedLanguageType = SPUtil.getInstance(context).getSelectLanguage();

        smartrefreshlayout.setOnRefreshListener(this);

        if (savedLanguageType == 0) {
            String language = LocalManageUtil.getSystemLocale(context).getDisplayLanguage();

            if (language.contains("English") || language.contains("英文")) {
                webviewDetail.loadUrl("https://appserver.trinity.ink/dapp/?lang=en");

            } else {
                webviewDetail.loadUrl("https://appserver.trinity.ink/dapp/?lang=zh-Hans");
            }
        }

        if (savedLanguageType == 1) {
            webviewDetail.loadUrl("https://appserver.trinity.ink/dapp/?lang=zh-Hans");
        }

        if (savedLanguageType == 3) {
            webviewDetail.loadUrl("https://appserver.trinity.ink/dapp/?lang=en");
        }


        return view;
    }


    public void getFindPage() {

        String[] s = new String[]{};

        EasyHttp.getInstance().setBaseUrl("").post("/")
                .params("jsonrpc", "2.0")
                .requestBody(SealApi.toRequestBody("getFindPage", s))
                .execute(new SimpleCallBack<String>() {
                    @Override
                    public void onError(ApiException e) {
                        //showToast(e.getMessage());
                    }

                    @Override
                    public void onSuccess(String response) {

                        //ToastHelper.showToast(response);


                    }
                });
    }


    private void initWebView() {
        pbProgress.setVisibility(View.VISIBLE);
        WebSettings ws = webviewDetail.getSettings();
        ws.setLoadWithOverviewMode(false);
        ws.setSaveFormData(true);
        ws.setSupportZoom(true);
        ws.setBuiltInZoomControls(true);
        ws.setDisplayZoomControls(false);
        ws.setAppCacheEnabled(true);
        ws.setCacheMode(WebSettings.LOAD_DEFAULT);
        ws.setUseWideViewPort(true);
        webviewDetail.setInitialScale(100);
        ws.setJavaScriptEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webviewDetail.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        ws.setBlockNetworkImage(false);
        ws.setDomStorageEnabled(true);


        ws.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ws.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        ws.setTextZoom(100);


        webviewDetail.setWebChromeClient(new WebChromeClient());

        webviewDetail.addJavascriptInterface(new JavaScriptUtils(), "sealWallet");

        webviewDetail.setWebViewClient(new MyWebViewClient());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        refreshLayout.finishRefresh(2000);
        webviewDetail.reload();
    }

    class JavaScriptUtils {

        @JavascriptInterface
        public void getNewUrl(String url) {
            Intent intent = new Intent(context, WebViewActivity.class);
            intent.putExtra(Constant.IntentKey.WEB_URL, url);
            intent.putExtra(Constant.IntentKey.WEB_TITLE, "");
            startActivity(intent);
        }
    }


    class MyWebViewClient extends WebViewClient {
        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
            if (url.contains("trust.js")) {
                try {
                    return new WebResourceResponse("application/x-javascript", "utf-8", getResources().getAssets().open("trust-min.js"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return super.shouldInterceptRequest(view, url);
        }

        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (request.getUrl().toString().contains("trust.js")) {//加载指定.js时 引导服务端加载本地Assets/www文件夹下的cordova.js
                    try {
                        return new WebResourceResponse("application/x-javascript", "utf-8", getResources().getAssets().open("trust-min.js"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return super.shouldInterceptRequest(view, request);
        }
    }


}
