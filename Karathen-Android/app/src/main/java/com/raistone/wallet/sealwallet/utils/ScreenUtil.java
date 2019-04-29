package com.raistone.wallet.sealwallet.utils;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import java.util.Objects;


public class ScreenUtil {

    private static DisplayMetrics metric;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private static DisplayMetrics initDisplayMetrics(Context context) {
        if (context != null) {
            WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            metric = new DisplayMetrics();
            Objects.requireNonNull(windowManager).getDefaultDisplay().getMetrics(metric);
        }
        return metric;
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static int getScreenWidth(Context context) {
        DisplayMetrics displayMetrics = (metric == null ? initDisplayMetrics(context) : metric);
        return displayMetrics == null ? -1 : displayMetrics.widthPixels;
    }

    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }
}
