package com.raistone.wallet.sealwallet.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.raistone.wallet.sealwallet.R;
import com.raistone.wallet.sealwallet.daoutils.HdWalletDaoUtils;
import com.raistone.wallet.sealwallet.entity.GasInfo;
import com.raistone.wallet.sealwallet.factory.HdWallet;
import com.raistone.wallet.sealwallet.service.GetBlockHightService;
import com.raistone.wallet.sealwallet.utils.CommonUtils;
import com.raistone.wallet.sealwallet.utils.Constant;
import com.raistone.wallet.sealwallet.utils.GsonUtils;
import com.raistone.wallet.sealwallet.utils.LocalManageUtil;
import com.raistone.wallet.sealwallet.utils.SharePreUtil;
import com.zhouyou.http.EasyHttp;
import com.zhouyou.http.callback.SimpleCallBack;
import com.zhouyou.http.exception.ApiException;

import java.util.List;
import io.reactivex.disposables.Disposable;
import retrofit2.converter.gson.GsonConverterFactory;


public class SplashActivity extends AppCompatActivity {




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

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
                    Intent intent = new Intent(getApplicationContext(), GuideActivityNews.class);
                    startActivity(intent);

                } else {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                }
                finish();
            }
        }, 3000);
    }




    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocalManageUtil.setLocal(newBase));
    }

}
