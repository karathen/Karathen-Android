package com.raistone.wallet.sealwallet.utils;

import android.graphics.Bitmap;
import android.util.Base64;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Type;

public class GsonUtils {
    public static String encodeJSON(Object src) throws JsonSyntaxException {
        Gson gson = new Gson();
        return gson.toJson(src);
    }

    //解析json
    public static <T> T decodeJSON(String jsonString, Class<T> cls)
            throws JsonSyntaxException {
        Gson gson = new Gson();

        T model = gson.fromJson(jsonString, cls);
        return model;
    }

    public static <T> T decodeJSON(String jsonString, Type typeOfT)
            throws JsonSyntaxException {
        Gson gson = new Gson();
        return gson.fromJson(jsonString, typeOfT);
    }


    /**
     * Bitmap 转为 Base64
     * @param bitmap
     * @return
     */
    public static String bitmap2Base64(Bitmap bitmap) {
        if (bitmap != null) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] bytes = byteArrayOutputStream.toByteArray();
            return Base64.encodeToString(bytes, Base64.DEFAULT);
        }
        return null;
    }
}
