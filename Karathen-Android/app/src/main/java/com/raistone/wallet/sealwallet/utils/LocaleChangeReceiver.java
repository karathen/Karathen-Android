package com.raistone.wallet.sealwallet.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.raistone.wallet.sealwallet.ui.ActivityManager;
import com.raistone.wallet.sealwallet.ui.MainActivity;
import com.raistone.wallet.sealwallet.ui.WalletItemDetailActivity;

import java.util.Locale;

public class LocaleChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_LOCALE_CHANGED)) {
            Log.e("LocaleChangeReceiver", "Language change");

            LocalManageUtil.saveSelectLanguage(context, 0);
            ActivityManager.getInstance().finishAllActivity();
            MainActivity mainActivity = MainActivity.instance;

            WalletItemDetailActivity detailActivity = WalletItemDetailActivity.instance;
            if(mainActivity!=null){
                mainActivity.finish();
            }

            if(detailActivity!=null){
                detailActivity.finish();
            }
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);

        }

    }
}
