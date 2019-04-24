package com.raistone.wallet.sealwallet.ui.webview.config;

import android.content.Context;
import android.text.TextUtils;
import android.webkit.JavascriptInterface;

public class ImageClickInterface {
    private Context context;

    public ImageClickInterface(Context context) {
        this.context = context;
    }

    @JavascriptInterface
    public void imageClick(String imgUrl, String hasLink) {

    }

    @JavascriptInterface
    public void textClick(String type, String item_pk) {
        if (!TextUtils.isEmpty(type) && !TextUtils.isEmpty(item_pk)) {
            //fixme
        }
    }
}
