package com.raistone.wallet.sealwallet.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;

import com.raistone.wallet.sealwallet.ui.BaseActivity;

public class NetBroadcastReceiver extends BroadcastReceiver{
    public NetChangeListener listener = BaseActivity.listener;

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        Log.i("NetBroadcastReceiver", "NetBroadcastReceiver changed");
        if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            int netWorkState = ConfigUtils.getNetWorkState(context);
            if (listener != null) {
                listener.onChangeListener(netWorkState);
            }
        }
    }

    public interface NetChangeListener {
        void onChangeListener(int status);
    }

}
