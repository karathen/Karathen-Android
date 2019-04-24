package com.raistone.wallet.sealwallet.ui.webview.config;

import android.view.View;

public interface IWebPageView {

    //隐藏进度条
    void hindProgressBar();

    //显示webView
    void showWebView();

    //隐藏webView
    void hindWebView();

    /**
     * 进度条变化时调用
     */
    void startProgress(int newProgress);

    /**
     * 添加js监听
     */
    void addImageClickListener();

    /**
     * 播放网络视频全屏调用
     */
    void fullViewAddView(View view);

    void showVideoFullView();

    void hindVideoFullView();
}
