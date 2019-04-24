package com.raistone.wallet.sealwallet.ui;

import android.app.Activity;
import android.content.Context;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.blankj.utilcode.util.NetworkUtils;
import com.raistone.wallet.sealwallet.R;
import com.raistone.wallet.sealwallet.entity.NetStatus;
import com.raistone.wallet.sealwallet.utils.LocalManageUtil;
import com.raistone.wallet.sealwallet.utils.MultiLanguageUtil;
import com.raistone.wallet.sealwallet.utils.NetBroadcastReceiver;
import com.raistone.wallet.sealwallet.utils.ToastHelper;
import com.raistone.wallet.sealwallet.widget.CustomProgressDialog;
import com.raistone.wallet.sealwallet.widget.TitleBar;

import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

public class BaseActivity extends AppCompatActivity implements NetBroadcastReceiver.NetChangeListener{
    private CustomProgressDialog progressDialog = null;

    private NetBroadcastReceiver netBroadcastReceiver;


    public static NetBroadcastReceiver.NetChangeListener listener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
            localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
        }
        this.getWindow()
                .getDecorView()
                .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        MIUISetStatusBarLightMode(this.getWindow(), true);
        FlymeSetStatusBarLightMode(this.getWindow(), true);

        listener=this;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //实例化IntentFilter对象
            IntentFilter filter = new IntentFilter();
            filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
            netBroadcastReceiver = new NetBroadcastReceiver();
            //注册广播接收
            registerReceiver(netBroadcastReceiver, filter);
        }

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocalManageUtil.setLocal(newBase));
    }

    public void setTitle(TitleBar navTitleBar, String title) {
        navTitleBar.setTitle(title);
    }

    public void setTitle(TitleBar navTitleBar, String title,boolean showLeft) {
        if(showLeft)setBack(navTitleBar,this);
        navTitleBar.setTitle(title);
    }

    public void setBack(TitleBar navTitleBar, final Activity activity) {
        navTitleBar.setIcon(R.drawable.icon_back);
        navTitleBar.setIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.finish();
            }
        });
    }

    public static boolean FlymeSetStatusBarLightMode(Window window, boolean dark) {
        boolean result = false;
        if (window != null) {
            try {
                WindowManager.LayoutParams lp = window.getAttributes();
                Field darkFlag = WindowManager.LayoutParams.class
                        .getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
                Field meizuFlags = WindowManager.LayoutParams.class
                        .getDeclaredField("meizuFlags");
                darkFlag.setAccessible(true);
                meizuFlags.setAccessible(true);
                int bit = darkFlag.getInt(null);
                int value = meizuFlags.getInt(lp);
                if (dark) {
                    value |= bit;
                } else {
                    value &= ~bit;
                }
                meizuFlags.setInt(lp, value);
                window.setAttributes(lp);
                result = true;
            } catch (Exception e) {

            }
        }
        return result;
    }

    public static boolean MIUISetStatusBarLightMode(Window window, boolean dark) {
        boolean result = false;
        if (window != null) {
            Class clazz = window.getClass();
            try {
                int darkModeFlag = 0;
                Class layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
                Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
                darkModeFlag = field.getInt(layoutParams);
                Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
                if (dark) {
                    extraFlagField.invoke(window, darkModeFlag, darkModeFlag);//状态栏透明且黑色字体
                } else {
                    extraFlagField.invoke(window, 0, darkModeFlag);//清除黑色字体
                }
                result = true;
            } catch (Exception e) {

            }
        }
        return result;
    }
    public void startProgressDialog() {
        try{
            if (progressDialog == null) {
                progressDialog = CustomProgressDialog.createDialog(this);
                progressDialog.setCanceledOnTouchOutside(false);
            }
            progressDialog.show();
        }catch(Exception e){
        }

    }
    public void startProgressDialog(boolean value) {
        try{
            if (progressDialog == null) {
                progressDialog = CustomProgressDialog.createDialog(this);
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.setCancelable(value);
            }
            progressDialog.show();
        }catch(Exception e){
        }

    }
    public void startProgressDialog(String message) {
        try{
            if (progressDialog == null) {
                progressDialog = CustomProgressDialog.createDialog(this);
                progressDialog.setMessage(message);
                progressDialog.setCanceledOnTouchOutside(false);
            }
            progressDialog.show();
        }catch(Exception e){
        }

    }
    public void startProgressDialog(String message,boolean v) {
        try{
            if (progressDialog == null) {
                progressDialog = CustomProgressDialog.createDialog(this);
                progressDialog.setMessage(message);
                progressDialog.setCanceledOnTouchOutside(v);
                progressDialog.setCancelable(v);
            }
            progressDialog.show();
        }catch(Exception e){
        }

    }
    public void stopProgressDialog() {
        if (progressDialog != null) {
            progressDialog.cancel();
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    public static String subZeroAndDot(String s) {
        if (s.indexOf(".") > 0) {
            s = s.replaceAll("0+?$", "");//去掉多余的0
            s = s.replaceAll("[.]$", "");//如最后一位是.则去掉
        }
        return s;
    }

    @Override
    public void onChangeListener(int status) {
        if (!NetworkUtils.isConnected()) {
            EventBus.getDefault().post(new NetStatus(false));
        } else {
            EventBus.getDefault().post(new NetStatus(true));
        }
    }

    public static void showKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) view.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            view.requestFocus();
            imm.showSoftInput(view, 0);
        }
    }

    public static void hideKeyboard(View view){
        InputMethodManager imm = (InputMethodManager) view.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(),0);
        }
    }
    public static void  toggleSoftInput(View view){
        InputMethodManager imm = (InputMethodManager) view.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.toggleSoftInput(0,0);
        }
    }

}
