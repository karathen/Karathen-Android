package com.raistone.wallet.sealwallet.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharePreUtil {
    private static SharedPreferences sp;

    /** 保存数据 **/
    public static void saveBoolean(Context ctx, String key, boolean value) {
        if (sp == null) {
            sp = ctx.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        sp.edit().putBoolean(key, value).apply();
    }

    /** 保存数据 **/
    public static void saveInt(Context ctx, String key, int value) {
        if (sp == null) {
            sp = ctx.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        sp.edit().putInt(key, value).apply();
    }

    /** 保存数据 **/
    public static void saveLong(Context ctx, String key, Long value) {
        if (sp == null) {
            sp = ctx.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        sp.edit().putLong(key, value).apply();
    }

    /** 取出数据 **/
    public static Boolean getBoolean(Context ctx, String key, boolean defValue) {
        if (sp == null) {
            sp = ctx.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        return sp.getBoolean(key, defValue);
    }


    /** 取出数据 **/
    public static int getIntValue(Context ctx, String key, int defValue) {
        if (sp == null) {
            sp = ctx.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        return sp.getInt(key, defValue);
    }

    /** 取出数据 **/
    public static Long getLongValue(Context ctx, String key, Long defValue) {
        if (sp == null) {
            sp = ctx.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        return sp.getLong(key, defValue);
    }
}
