package com.raistone.wallet.sealwallet.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Window;
import android.view.WindowManager;

import com.baidu.mobstat.StatService;
import com.raistone.wallet.sealwallet.R;
import com.raistone.wallet.sealwallet.daoutils.HdWalletDaoUtils;
import com.raistone.wallet.sealwallet.entity.GasInfo;
import com.raistone.wallet.sealwallet.factory.HdWallet;
import com.raistone.wallet.sealwallet.httputils.TestApiResult6;
import com.raistone.wallet.sealwallet.service.GetBlockHightService;
import com.raistone.wallet.sealwallet.utils.CommonUtils;
import com.raistone.wallet.sealwallet.utils.Constant;
import com.raistone.wallet.sealwallet.utils.GsonUtils;
import com.raistone.wallet.sealwallet.utils.LocalManageUtil;
import com.raistone.wallet.sealwallet.utils.SharePreUtil;
import com.zhouyou.http.EasyHttp;
import com.zhouyou.http.callback.CallClazzProxy;
import com.zhouyou.http.callback.SimpleCallBack;
import com.zhouyou.http.exception.ApiException;
import com.zhouyou.http.subsciber.BaseSubscriber;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import retrofit2.converter.gson.GsonConverterFactory;


public class SplashActivity extends AppCompatActivity {


    Disposable polldisposable;

    //public static Integer convertsToInt=5;
    public Long blockNumber = 0l;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StatService.start(this);
        //getBlockNumber1();
        //getGasPrice1();

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        context = this;

        Intent startIntent = new Intent(this, GetBlockHightService.class);
        startService(startIntent);
        List<HdWallet> allWallet = HdWalletDaoUtils.findAllWallet();


        if(allWallet!=null){
            if(allWallet.size()==1){
                allWallet.get(0).setIsCurrent(true);
                HdWalletDaoUtils.updateCurrent(allWallet.get(0).getWalletId());
            }
        }


        ActivityManager.getInstance().pushActivity(this);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Boolean isFirst = SharePreUtil.getBoolean(getApplicationContext(), "isFirst", true);
                if (isFirst) {
                    //进入包含了viewpager那个导航界面
                    Intent intent = new Intent(getApplicationContext(), GuideActivityNews.class);
                    startActivity(intent);
                    //将isFirst改为false,并且在本地持久化

                } else {
                    //进入应用程序主界面
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                }
                finish();
            }
        }, 3000);
    }


    public void getBlockNumber1() {
        EasyHttp.getInstance().get("api")
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(Constant.HttpServiceUrl.MAIN_URL)
                .params("module", "proxy")
                .params("action", "eth_blockNumber")
                .execute(new SimpleCallBack<String>() {
                    @Override
                    public void onError(ApiException e) {
                    }

                    @Override
                    public void onSuccess(String s) {


                        //showToast(content);
                        GasInfo baseInfo = GsonUtils.decodeJSON(s, GasInfo.class);

                        blockNumber = CommonUtils.binaryConversion(baseInfo.getResult());

                        SharePreUtil.saveLong(context, "blockNumber", blockNumber);

                    }
                });
    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocalManageUtil.setLocal(newBase));
    }

}
