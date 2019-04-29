package com.raistone.wallet.sealwallet.utils;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;

import java.util.Iterator;
import java.util.List;

public class ConfigUtils {

    private static final int NETWORK_NONE = -1;

    private static final int NETWORK_MOBILE = 0;

    private static final int NETWORK_WIFI = 1;

    public static String getVersionName(Context mContext){

        PackageManager packageManager = mContext.getPackageManager();
        PackageInfo packInfo = null;
        try {
            packInfo = packageManager.getPackageInfo(mContext.getPackageName(),0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();

        }
        String version = packInfo.versionName;
        return version;
    }

    public static String getVersionCode(Context context){
        String versionCode=null;
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_CONFIGURATIONS);

            versionCode= String.valueOf(info.versionCode);

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }


    public static boolean isInstalled(Context context, String packageName){
        PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> installedList = packageManager.getInstalledPackages(0);
        Iterator<PackageInfo> iterator = installedList.iterator();

        PackageInfo info;
        String name;
        while(iterator.hasNext())
        {
            info = iterator.next();
            name = info.packageName;
            if(name.equals(packageName))
            {
                return true;
            }
        }
        return false;
    }


    public static int getNetWorkState(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
            if (activeNetworkInfo.getType() == (ConnectivityManager.TYPE_WIFI)) {
                return NETWORK_WIFI;//wifi
            } else if (activeNetworkInfo.getType() == (ConnectivityManager.TYPE_MOBILE)) {
                return NETWORK_MOBILE;//mobile
            }
        } else {
            return NETWORK_NONE;
        }
        return NETWORK_NONE;
    }

}
