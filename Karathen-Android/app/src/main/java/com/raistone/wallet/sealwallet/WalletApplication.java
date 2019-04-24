package com.raistone.wallet.sealwallet;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.webkit.WebSettings;

import com.baidu.mobstat.StatService;
import com.blankj.utilcode.util.Utils;
import com.chenenyu.router.Configuration;
import com.chenenyu.router.Router;
import com.hss01248.glidepicker.GlideIniter;
import com.raistone.wallet.sealwallet.daoutils.MyOpenHelper;
import com.raistone.wallet.sealwallet.greendao.DaoMaster;
import com.raistone.wallet.sealwallet.greendao.DaoSession;
import com.raistone.wallet.sealwallet.utils.AppFilePath;
import com.raistone.wallet.sealwallet.utils.LocalManageUtil;
import com.raistone.wallet.sealwallet.utils.MultiLanguageUtil;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.scwang.smartrefresh.header.MaterialHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.DefaultRefreshHeaderCreator;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.tencent.bugly.crashreport.CrashReport;
import com.zhouyou.http.EasyHttp;
import com.zhouyou.http.model.HttpHeaders;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import me.iwf.photopicker.PhotoPickUtils;
import okhttp3.Protocol;


public class WalletApplication extends Application {
    public static Context context = null;
    public static Map<Integer, Drawable> IconDrawable;


    private static WalletApplication sInstance;

    private DaoSession daoSession;

    public DaoSession getDaoSession() {
        return daoSession;
    }

    static {
        SmartRefreshLayout.setDefaultRefreshHeaderCreator(new DefaultRefreshHeaderCreator() {
            @Override
            public RefreshHeader createRefreshHeader(Context context, RefreshLayout layout) {
                layout.setPrimaryColorsId(R.color.main_color, R.color.main_color);//全局设置主题颜色

                MaterialHeader materialHeader = new MaterialHeader(context);

                materialHeader.setColorSchemeResources(R.color.main_color_three, R.color.main_color, R.color.main_color_three);
                return materialHeader;//.setTimeFormat(new DynamicTimeFormat("更新于 %s"));//指定为经典Header，默认是 贝塞尔雷达Header
            }
        });
    }


    @Override
    public void onCreate() {
        super.onCreate();
        context = this;

        sInstance=this;


        EasyHttp.init(this);

        HttpHeaders headers = new HttpHeaders();

        headers.put("User-Agent", WebSettings.getDefaultUserAgent(context));

        StatService.autoTrace(context,true,true);

        EasyHttp.getOkHttpClientBuilder().protocols(Collections.singletonList(Protocol.HTTP_1_1));


        PhotoPickUtils.init(context,new GlideIniter());

        Utils.init(this);

        Router.initialize(new Configuration.Builder()
                .setDebuggable(BuildConfig.DEBUG)
                .registerModules("app")
                .build());
        AppFilePath.init(this);
        FlowManager.init(new FlowConfig.Builder(this).build());
        getIconDrawable();
        LocalManageUtil.setApplicationLanguage(this);
        //MultiLanguageUtil.init(this);
        initGreenDao();
    }

    private void initGreenDao() {
        //创建数据库表
        DaoMaster.OpenHelper mHelper=new MyOpenHelper(this,"wallet_seal");
        SQLiteDatabase db = mHelper.getWritableDatabase();
        daoSession = new DaoMaster(db).newSession();
    }

    public static WalletApplication getsInstance() {
        return sInstance;
    }


    public static Context getAppContext() {
        return WalletApplication.context;
    }


    @Override
    protected void attachBaseContext(Context base) {
        LocalManageUtil.saveSystemCurrentLanguage(base);
        super.attachBaseContext(LocalManageUtil.setLocal(base));
    }

    @Override
    public void onConfigurationChanged(android.content.res.Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        LocalManageUtil.onConfigurationChanged(getApplicationContext());
    }
    private void getIconDrawable() {

        IconDrawable = new HashMap<>();
        IconDrawable.put(0, getResources().getDrawable(R.drawable.address_01_icon));
        IconDrawable.put(1, getResources().getDrawable(R.drawable.address_02_icon));
        IconDrawable.put(2, getResources().getDrawable(R.drawable.address_03_icon));
        IconDrawable.put(3, getResources().getDrawable(R.drawable.address_04_icon));
        IconDrawable.put(4, getResources().getDrawable(R.drawable.address_05_icon));
        IconDrawable.put(5, getResources().getDrawable(R.drawable.address_06_icon));
        IconDrawable.put(6, getResources().getDrawable(R.drawable.address_07_icon));
        IconDrawable.put(7, getResources().getDrawable(R.drawable.address_08_icon));
        IconDrawable.put(8, getResources().getDrawable(R.drawable.address_09_icon));
        IconDrawable.put(9, getResources().getDrawable(R.drawable.address_10_icon));
        IconDrawable.put(10, getResources().getDrawable(R.drawable.address_11_icon));
        IconDrawable.put(11, getResources().getDrawable(R.drawable.address_12_icon));
        IconDrawable.put(12, getResources().getDrawable(R.drawable.address_13_icon));
        IconDrawable.put(13, getResources().getDrawable(R.drawable.address_14_icon));
        IconDrawable.put(14, getResources().getDrawable(R.drawable.address_15_icon));
        IconDrawable.put(15, getResources().getDrawable(R.drawable.address_16_icon));
        IconDrawable.put(16, getResources().getDrawable(R.drawable.address_17_icon));
        IconDrawable.put(17, getResources().getDrawable(R.drawable.address_18_icon));
        IconDrawable.put(18, getResources().getDrawable(R.drawable.address_19_icon));
        IconDrawable.put(19, getResources().getDrawable(R.drawable.address_20_icon));
        IconDrawable.put(20, getResources().getDrawable(R.drawable.address_21_icon));
        IconDrawable.put(21, getResources().getDrawable(R.drawable.address_22_icon));
        IconDrawable.put(22, getResources().getDrawable(R.drawable.address_23_icon));
        IconDrawable.put(23, getResources().getDrawable(R.drawable.address_24_icon));
        IconDrawable.put(24, getResources().getDrawable(R.drawable.address_25_icon));
    }




}
