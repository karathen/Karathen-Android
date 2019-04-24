package com.raistone.wallet.sealwallet.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.text.TextUtils;

import com.raistone.wallet.sealwallet.entity.GasInfo;
import com.raistone.wallet.sealwallet.httputils.TestApiResult6;
import com.raistone.wallet.sealwallet.utils.CommonUtils;
import com.raistone.wallet.sealwallet.utils.Constant;
import com.raistone.wallet.sealwallet.utils.GsonUtils;
import com.raistone.wallet.sealwallet.utils.SharePreUtil;
import com.zhouyou.http.EasyHttp;
import com.zhouyou.http.callback.CallClazzProxy;
import com.zhouyou.http.exception.ApiException;
import com.zhouyou.http.subsciber.BaseSubscriber;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;

public class GetBlockHightService extends Service {

    Disposable polldisposable;

    //public static Integer convertsToInt=5;
    public Long blockNumber = 0l;

    public GetBlockHightService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        getBlockNumber();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (polldisposable != null && !polldisposable.isDisposed()) {
            polldisposable.dispose();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * 获取块高
     */
    public void getBlockNumber() {
        polldisposable = Observable.interval(0, 5, TimeUnit.SECONDS).flatMap(new Function<Long, ObservableSource<String>>() {
            @Override
            public ObservableSource<String> apply(@NonNull Long aLong) throws Exception {
                return EasyHttp.get("api")
                        .baseUrl(Constant.HttpServiceUrl.MAIN_URL)
                        .params("module", "proxy")
                        .params("action", "eth_blockNumber")
                        //采用代理
                        .execute(new CallClazzProxy<TestApiResult6<String>, String>(String.class) {
                        });
            }
        }).subscribeWith(new BaseSubscriber<String>() {
            @Override
            public void onError(ApiException e) {
                //showToast(e.getMessage());
            }

            @Override
            public void onNext(@NonNull String content) {
                //showToast(content);
                GasInfo baseInfo = GsonUtils.decodeJSON(content, GasInfo.class);

                if (baseInfo != null) {

                    String result = baseInfo.getResult();

                    if (!TextUtils.isEmpty(result)) {
                        blockNumber = CommonUtils.binaryConversion(result);
                    }

                    SharePreUtil.saveLong(GetBlockHightService.this, "blockNumber", blockNumber);
                }

            }
        });

    }

}
