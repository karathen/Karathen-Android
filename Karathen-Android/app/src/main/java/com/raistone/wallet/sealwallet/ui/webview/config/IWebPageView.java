package com.raistone.wallet.sealwallet.ui.webview.config;

import android.view.View;

public interface IWebPageView {

    void hindProgressBar();

    void showWebView();

    void hindWebView();

    void startProgress(int newProgress);

    void addImageClickListener();

    void fullViewAddView(View view);

    void showVideoFullView();

    void hindVideoFullView();
}
