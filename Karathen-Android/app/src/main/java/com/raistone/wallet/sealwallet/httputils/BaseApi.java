package com.raistone.wallet.sealwallet.httputils;

import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BaseApi {
    protected Retrofit mRetrofit;

    public BaseApi() {

        mRetrofit = new Retrofit.Builder()
                .baseUrl("https://appserver.trinity.ink/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(getClient())
                .build();

    }
    private OkHttpClient getClient() {


        OkHttpClient builder = new OkHttpClient.Builder().connectTimeout(15000, TimeUnit.MILLISECONDS)
                .readTimeout(15000, TimeUnit.MILLISECONDS)
                .sslSocketFactory(SSLSocketClient.getSSLSocketFactory())
                .hostnameVerifier(SSLSocketClient.getHostnameVerifier())
                .retryOnConnectionFailure(true)
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)

                .build();
        return builder;
    }
}
