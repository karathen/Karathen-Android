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

    /**
     * 没有网络
     */
    private static final int NETWORK_NONE = -1;
    /**
     * 移动网络
     */
    private static final int NETWORK_MOBILE = 0;
    /**
     * 无线网络
     */
    private static final int NETWORK_WIFI = 1;

    /**
     * 获取当前版本号
     * @param mContext
     * @return
     * @throws Exception
     */
    public static String getVersionName(Context mContext){
        // 获取packagemanager的实例

        PackageManager packageManager = mContext.getPackageManager();
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = null;
        try {
            packInfo = packageManager.getPackageInfo(mContext.getPackageName(),0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();

        }
        String version = packInfo.versionName;
        return version;
    }

    /**
     * 获取 versionCode
     * @param context
     * @return
     */
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

    /**
     * 判段应用在手机上是否安装
     * @param context
     * @param packageName 包名
     * @return
     */
    public static boolean isInstalled(Context context, String packageName){
        PackageManager packageManager = context.getPackageManager();// 获取packagemanager
        List<PackageInfo> installedList = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
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
        //得到连接管理器对象
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();
        //如果网络连接，判断该网络类型
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
            if (activeNetworkInfo.getType() == (ConnectivityManager.TYPE_WIFI)) {
                return NETWORK_WIFI;//wifi
            } else if (activeNetworkInfo.getType() == (ConnectivityManager.TYPE_MOBILE)) {
                return NETWORK_MOBILE;//mobile
            }
        } else {
            //网络异常
            return NETWORK_NONE;
        }
        return NETWORK_NONE;
    }

}
