package com.raistone.wallet.sealwallet.ui;

import com.google.gson.JsonElement;
import com.raistone.wallet.sealwallet.utils.Test;

import java.math.BigDecimal;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface SealApi {


    @FormUrlEncoded
    @POST("")
    Call<Test> getResult(@FieldMap Map<String, Object> params);


}
