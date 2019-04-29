package com.raistone.wallet.sealwallet.httputils;

import com.raistone.wallet.sealwallet.utils.Test;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public class SealApi extends BaseApi{
    public static RequestBody toRequestBody(String methon,Object object)  {
        Map<String,Object> params=new HashMap<>();
        params.put("id", "1");
        params.put("method", methon);
        params.put("jsonrpc", "2.0");
        params.put("params", object);
        return RequestBody.create(MediaType.parse("application/json;charset=utf-8"), new JSONObject(params).toString());
    }

    private ApiStore mApiStore;


    public SealApi() {
        mApiStore = mRetrofit.create(ApiStore.class);
    }



    public interface ApiStore {
        @POST("/")
        Call<Test> getResult(@Body RequestBody body);
    }

    public Call<Test> getPublicInfo(String methon,Object o){
        Call<Test> call = ((ApiStore) mApiStore).getResult(toRequestBody(methon,o));
        return call;
    }


}
