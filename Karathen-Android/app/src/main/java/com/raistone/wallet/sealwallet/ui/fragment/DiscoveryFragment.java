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
import trust.Call;
import trust.OnCompleteListener;
import trust.Response;
import trust.SignTransactionRequest;
import trust.Trust;
import trust.core.entity.Address;

/**
 * 发现
 */
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

    Call<SignTransactionRequest> requestCall;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_discovery, container, false);
        unbinder = ButterKnife.bind(this, view);
        getFindPage();
        initWebView();
        //https://appserver.trinity.ink/dapp/?lang=en
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        requestCall.onActivityResult(requestCode, resultCode, data, new OnCompleteListener<SignTransactionRequest>() {
            @Override
            public void onComplete(Response<SignTransactionRequest> response) {
                ToastHelper.showToast("回调完成 ====="+response);
            }
        });

    }

    public void getFindPage() {

        String[] s = new String[]{};

        EasyHttp.getInstance().setBaseUrl("https://appserver.trinity.ink/").post("/")
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
        // 网页内容的宽度是否可大于WebView控件的宽度
        ws.setLoadWithOverviewMode(false);
        // 保存表单数据
        ws.setSaveFormData(true);
        // 是否应该支持使用其屏幕缩放控件和手势缩放
        ws.setSupportZoom(true);
        ws.setBuiltInZoomControls(true);
        ws.setDisplayZoomControls(false);
        // 启动应用缓存
        ws.setAppCacheEnabled(true);
        // 设置缓存模式
        ws.setCacheMode(WebSettings.LOAD_DEFAULT);
        // setDefaultZoom  api19被弃用
        // 设置此属性，可任意比例缩放。
        ws.setUseWideViewPort(true);
        // 不缩放
        webviewDetail.setInitialScale(100);
        // 告诉WebView启用JavaScript执行。默认的是false。
        ws.setJavaScriptEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webviewDetail.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        //  页面加载好以后，再放开图片
        ws.setBlockNetworkImage(false);
        // 使用localStorage则必须打开
        ws.setDomStorageEnabled(true);


        // 排版适应屏幕
        ws.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        // WebView是否新窗口打开(加了后可能打不开网页)
//        ws.setSupportMultipleWindows(true);

        // webview从5.0开始默认不允许混合模式,https中不能加载http资源,需要设置开启。
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ws.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        /** 设置字体默认缩放大小(改变网页字体大小,setTextSize  api14被弃用)*/
        ws.setTextZoom(100);


        webviewDetail.setWebChromeClient(new WebChromeClient());
        // 与js交互
        //webviewDetail.addJavascriptInterface(new ImageClickInterface(getActivity()), "injectedObject");

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
            //ToastHelper.showToast(url);
            //webviewDetail.loadUrl(url);
            Intent intent = new Intent(context, WebViewActivity.class);
            intent.putExtra(Constant.IntentKey.WEB_URL, url);
            intent.putExtra(Constant.IntentKey.WEB_TITLE, "");
            startActivity(intent);
        }
    }


    class MyWebViewClient extends WebViewClient {
        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
            if (url.contains("trust.js")) {//加载指定.js时 引导服务端加载本地Assets/www文件夹下的cordova.js
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
