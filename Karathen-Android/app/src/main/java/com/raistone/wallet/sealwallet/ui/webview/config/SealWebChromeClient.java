package com.raistone.wallet.sealwallet.ui.webview.config;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.raistone.wallet.sealwallet.R;
import com.raistone.wallet.sealwallet.ui.WebViewActivity;

import static android.app.Activity.RESULT_OK;

public class SealWebChromeClient extends WebChromeClient {

    private ValueCallback<Uri> mUploadMessage;
    private ValueCallback<Uri[]> mUploadMessageForAndroid5;
    public static int FILECHOOSER_RESULTCODE = 1;
    public static int FILECHOOSER_RESULTCODE_FOR_ANDROID_5 = 2;

    private View mXProgressVideo;
    private WebViewActivity mActivity;
    private IWebPageView mIWebPageView;
    private View mXCustomView;
    private CustomViewCallback mXCustomViewCallback;

    public SealWebChromeClient(IWebPageView mIWebPageView) {
        this.mIWebPageView = mIWebPageView;
        this.mActivity = (WebViewActivity) mIWebPageView;
    }

    @Override
    public void onShowCustomView(View view, CustomViewCallback callback) {
        mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        mIWebPageView.hindWebView();
        if (mXCustomView != null) {
            callback.onCustomViewHidden();
            return;
        }

        mActivity.fullViewAddView(view);
        mXCustomView = view;
        mXCustomViewCallback = callback;
        mIWebPageView.showVideoFullView();
    }

    @Override
    public void onHideCustomView() {
        if (mXCustomView == null)
            return;
        mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mXCustomView.setVisibility(View.GONE);
        if (mActivity.getVideoFullView() != null) {
            mActivity.getVideoFullView().removeView(mXCustomView);
        }
        mXCustomView = null;
        mIWebPageView.hindVideoFullView();
        mXCustomViewCallback.onCustomViewHidden();
        mIWebPageView.showWebView();
    }

    @Override
    public View getVideoLoadingProgressView() {
        if (mXProgressVideo == null) {
            LayoutInflater inflater = LayoutInflater.from(mActivity);
            mXProgressVideo = inflater.inflate(R.layout.video_loading_progress, null);
        }
        return mXProgressVideo;
    }

    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        //super.onProgressChanged(view, newProgress);
        mIWebPageView.startProgress(newProgress);
    }

    public boolean inCustomView() {
        return (mXCustomView != null);
    }

    @Override
    public void onReceivedTitle(WebView view, String title) {
        super.onReceivedTitle(view, title);
        // 设置title
        mActivity.setTitle(title);
        this.title = title;
    }

    private String title = "";

    public String getTitle() {
        return title + " ";
    }


    public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
        openFileChooserImpl(uploadMsg);
    }

    public void openFileChooser(ValueCallback<Uri> uploadMsg) {
        openFileChooserImpl(uploadMsg);
    }

    public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
        openFileChooserImpl(uploadMsg);
    }

    @Override
    public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> uploadMsg, FileChooserParams fileChooserParams) {
        openFileChooserImplForAndroid5(uploadMsg);
        return true;
    }

    private void openFileChooserImpl(ValueCallback<Uri> uploadMsg) {
        mUploadMessage = uploadMsg;
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("image/*");
        mActivity.startActivityForResult(Intent.createChooser(i, "文件选择"), FILECHOOSER_RESULTCODE);
    }

    private void openFileChooserImplForAndroid5(ValueCallback<Uri[]> uploadMsg) {
        mUploadMessageForAndroid5 = uploadMsg;
        Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
        contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
        contentSelectionIntent.setType("image/*");

        Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
        chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
        chooserIntent.putExtra(Intent.EXTRA_TITLE, "图片选择");

        mActivity.startActivityForResult(chooserIntent, FILECHOOSER_RESULTCODE_FOR_ANDROID_5);
    }


    public void mUploadMessage(Intent intent, int resultCode) {
        if (null == mUploadMessage)
            return;
        Uri result = intent == null || resultCode != RESULT_OK ? null : intent.getData();
        mUploadMessage.onReceiveValue(result);
        mUploadMessage = null;
    }


    public void mUploadMessageForAndroid5(Intent intent, int resultCode) {
        if (null == mUploadMessageForAndroid5)
            return;
        Uri result = (intent == null || resultCode != RESULT_OK) ? null : intent.getData();
        if (result != null) {
            mUploadMessageForAndroid5.onReceiveValue(new Uri[]{result});
        } else {
            mUploadMessageForAndroid5.onReceiveValue(new Uri[]{});
        }
        mUploadMessageForAndroid5 = null;
    }
}
