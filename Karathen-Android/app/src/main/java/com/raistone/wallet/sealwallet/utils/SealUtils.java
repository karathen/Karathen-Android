package com.raistone.wallet.sealwallet.utils;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;


import com.raistone.wallet.sealwallet.R;

import java.util.List;

/**
 * Created by JiaM
 * https://jiam.pro
 * 2018/9/18.
 */
public class SealUtils {

    public static void clipData(Context context, String str) {
        ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData mClipData = ClipData.newPlainText("Label", str);
        if (null != cm) {
            cm.setPrimaryClip(mClipData);

            ToastHelper.showToast(context.getResources().getString(R.string.copy_success_string));
        }
    }

    /**
     * 判断app 是否安装
     */
    public static boolean isApplicationAviliable(Context context, String appPackageName) {
        try {
            //获取packageManager
            PackageManager packageManager = context.getPackageManager();
            //获取所有已安装程序的包信息
            List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
            if (pinfo != null) {
                for (int i = 0; i < pinfo.size(); i++) {
                    String pn = pinfo.get(i).packageName;
                    if (appPackageName.equals(pn)) {
                        return true;
                    }
                }
            }
            return false;
        } catch (Exception ignored) {
            return false;
        }
    }

    /**
     * 判断网络是否连接
     */
    public static boolean isNetworkConnected(Context context) {
        try {
            if (context != null) {
                @SuppressWarnings("static-access")
                ConnectivityManager cm = (ConnectivityManager) context
                        .getSystemService(context.CONNECTIVITY_SERVICE);
                NetworkInfo info = cm.getActiveNetworkInfo();
                return info != null && info.isConnected();
            } else {
                return false; //no internet
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public static String getDeviceId(Context context) {

        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
        @SuppressLint("MissingPermission") String imei = telephonyManager.getDeviceId();

        return imei;
    }

}
