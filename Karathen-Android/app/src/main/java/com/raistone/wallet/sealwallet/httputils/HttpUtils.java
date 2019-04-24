package com.raistone.wallet.sealwallet.httputils;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class HttpUtils {

    public static RequestBody toRequestBody(String methon, Object object)  {
        Map<String,Object> params=new HashMap<>();
        params.put("id", "1");
        params.put("method", methon);
        params.put("jsonrpc", "2.0");
        params.put("params", object);
        return RequestBody.create(MediaType.parse("application/json;charset=utf-8"), new JSONObject(params).toString());
    }
}
