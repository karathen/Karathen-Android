package com.raistone.wallet.sealwallet.utils;

import android.support.annotation.NonNull;

import com.raistone.wallet.sealwallet.httputils.HttpUtils;
import com.vector.update_app.HttpManager;
import com.zhouyou.http.EasyHttp;
import com.zhouyou.http.callback.DownloadProgressCallBack;
import com.zhouyou.http.callback.SimpleCallBack;
import com.zhouyou.http.exception.ApiException;
import com.zhouyou.http.https.HttpsUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.CipherSuite;
import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.TlsVersion;

public class OkGoUpdateHttpUtil implements HttpManager {
    @Override
    public void asyncGet(@NonNull String url, @NonNull Map<String, String> params, @NonNull final Callback callBack) {

        List<Object> datas = new ArrayList<>();
        for (Map.Entry<String, String> entry : params.entrySet()) {

            datas.add(entry.getValue());
        }

        RequestBody body = HttpUtils.toRequestBody("updateApp_android", datas);

        EasyHttp.getInstance().setBaseUrl("https://appserver.trinity.ink/").post("/")
                .requestBody(body)
                .execute(new SimpleCallBack<String>() {
                    @Override
                    public void onError(ApiException e) {
                        //showToast(e.getMessage());
                    }

                    @Override
                    public void onSuccess(String response) {

                        callBack.onResponse(response);
                    }
                });


    }

    @Override
    public void asyncPost(@NonNull String url, @NonNull Map<String, String> params, @NonNull final Callback callBack) {

        List<Object> datas = new ArrayList<>();
        for (Map.Entry<String, String> entry : params.entrySet()) {

            datas.add(entry.getValue());
        }

        RequestBody body = HttpUtils.toRequestBody("updateApp_android", datas);
        EasyHttp.getInstance().setBaseUrl("https://appserver.trinity.ink/").post("/")
                .requestBody(body)
                .execute(new SimpleCallBack<String>() {
                    @Override
                    public void onError(ApiException e) {
                        //showToast(e.getMessage());
                    }

                    @Override
                    public void onSuccess(String response) {
                        callBack.onResponse(response);

                    }
                });
    }

    @Override
    public void download(@NonNull String url, @NonNull String path, @NonNull String fileName, @NonNull final FileCallback callback) {

        HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory(null, null, null);

        ConnectionSpec spec = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                .tlsVersions(TlsVersion.TLS_1_1)
                .tlsVersions(TlsVersion.TLS_1_2)
                .cipherSuites(
                        CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,
                        CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256,
                        CipherSuite.TLS_DHE_RSA_WITH_AES_128_GCM_SHA256)
                .build();

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectionSpecs(Collections.singletonList(spec))
                .sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager).build();

        OkHttpUtils.initClient(okHttpClient).get()
                .url(url)
                .build()
                .execute(new FileCallBack(path, fileName) {
                    @Override
                    public void inProgress(float progress, long total, int id) {
                        callback.onProgress(progress, total);
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e, int id) {
                        callback.onError(validateError(e, response));
                    }

                    @Override
                    public void onResponse(File response, int id) {
                        callback.onResponse(response);

                    }

                    @Override
                    public void onBefore(Request request, int id) {
                        super.onBefore(request, id);
                        callback.onBefore();
                    }
                });


    }
}
