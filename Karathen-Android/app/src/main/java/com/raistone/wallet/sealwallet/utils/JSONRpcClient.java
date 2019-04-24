/*
package com.raistone.wallet.sealwallet.utils;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.trinity.util.net.JSONRpcErrorUtil;
import org.trinity.wallet.ConfigList;
import org.trinity.wallet.WalletApplication;
import org.trinity.wallet.net.jsonrpc.RequestJSONRpcBean;

import java.io.IOException;
import java.util.Arrays;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class JSONRpcClient extends AbstractClient {

    private static final MediaType MEDIA_TYPE = MediaType.parse(ConfigList.CLIENT_MEDIA_TYPE);
    private String url;
    private String json;

    JSONRpcClient(Builder builder) {
        this.url = builder.netUrl;
        this.json = WalletApplication.getGson().toJson(builder.requestJSONRpcBean);
        if (url == null || "".equals(url.trim())) {
            url = WalletApplication.getNetUrl();
        }
    }

    public String getJson() {
        return json;
    }

    public String post() {
        RequestBody requestBody = RequestBody.create(MEDIA_TYPE, json);
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        Response response;
        try {
            response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                return null;
            }
            ResponseBody body = response.body();
            if (body == null) {
                return null;
            }
            String bodyString = body.string();
            boolean hasError = JSONRpcErrorUtil.hasError(bodyString);
            if (hasError) {
                return null;
            }
            return bodyString;
        } catch (IOException ignored) {
            return null;
        }
    }

    public static final class Builder {
        String netUrl;
        RequestJSONRpcBean requestJSONRpcBean;

        public Builder() {
            requestJSONRpcBean = new RequestJSONRpcBean();
            requestJSONRpcBean.setJsonrpc("2.0");
            requestJSONRpcBean.setId("1");
        }

        public Builder net(@NonNull String netUrl) {
            this.netUrl = netUrl;
            return this;
        }

        public Builder jsonrpc(@NonNull String jsonrpc) {
            requestJSONRpcBean.setJsonrpc(jsonrpc);
            return this;
        }

        public Builder method(@NonNull String method) {
            requestJSONRpcBean.setMethod(method);
            return this;
        }

        public Builder params(@NonNull String... params) {
            requestJSONRpcBean.setParams(Arrays.asList(params));
            return this;
        }

        public Builder id(@Nullable String id) {
            requestJSONRpcBean.setId(id);
            return this;
        }

        public JSONRpcClient build() {
            return new JSONRpcClient(this);
        }
    }
}
*/
