package com.raistone.wallet.sealwallet.httputils;

import com.raistone.wallet.sealwallet.ui.SealApi;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiManager {
    private static ApiManager instance;
    private SealApi sealApi;
    private static String BASE_URL="";

    public static ApiManager getInstance() {
        if (instance == null) {
            instance = new ApiManager();
        }
        return instance;
    }

    public SealApi pointApiService() {
        if (sealApi == null) {
            sealApi = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    //.addCallAdapterFactory(new GsonConverterFactory())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(SealApi.class);
        }
        return sealApi;
    }
}
