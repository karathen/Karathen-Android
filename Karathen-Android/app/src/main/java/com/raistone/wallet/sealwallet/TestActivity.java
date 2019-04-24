package com.raistone.wallet.sealwallet;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ListView;

import com.blankj.utilcode.util.LogUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.extropies.common.CommonUtility;
import com.extropies.common.MiddlewareInterface;
import com.raistone.wallet.sealwallet.ui.BaseActivity;
import com.raistone.wallet.sealwallet.ui.MainActivity;
import com.raistone.wallet.sealwallet.utils.ToastHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class TestActivity extends BaseActivity {


    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    private List<String> devList = new ArrayList<>();

    private DevListAdapter devListAdapter;

    private String m_devListCurrentDevName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        requestPermissions(this);

        ButterKnife.bind(this);


        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        recyclerview.setLayoutManager(layoutManager);

        devListAdapter = new DevListAdapter(devList);

        recyclerview.setAdapter(devListAdapter);

        devListAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

                m_devListCurrentDevName = devList.get(position);
                class ConnectResponse {
                    private long m_devHandle;
                    private int m_returnValue;

                    ConnectResponse(long devHandle, int returnValue) {
                        m_devHandle = devHandle;
                        m_returnValue = returnValue;
                    }

                    long getM_devHandle() {
                        return m_devHandle;
                    }

                    int getM_returnValue() {
                        return m_returnValue;
                    }
                }

                Observable<ConnectResponse> m_connectObe = Observable.create(new ObservableOnSubscribe<ConnectResponse>() {
                    @Override
                    public void subscribe(ObservableEmitter<ConnectResponse> emitter) throws Exception {
                        long[] contextHandles = new long[1];
                        int iRtn = MiddlewareInterface.initContextWithDevName(getApplicationContext(), m_devListCurrentDevName, new CommonUtility.heartBeatCallback() {
                            @Override
                            public void pushHeartBeatData(byte[] heartBeatData) {

                            }

                            @Override
                            public void pushConnectState(boolean connected) {

                            }
                        }, contextHandles);
                        emitter.onNext(new ConnectResponse(contextHandles[0], iRtn));
                        emitter.onComplete();
                    }
                }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());

                Observer<ConnectResponse> m_connectOb = new Observer<ConnectResponse>() {
                    private long m_contextHandle = 0;
                    private int m_retValue = MiddlewareInterface.PAEW_RET_UNKNOWN_FAIL;

                    @Override
                    public void onSubscribe(Disposable d) {
                        startProgressDialog("正在连接中...");
                    }

                    @Override
                    public void onNext(ConnectResponse connectResponse) {
                        if (connectResponse != null) {
                            m_retValue = connectResponse.getM_returnValue();
                            m_contextHandle = connectResponse.getM_devHandle();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                        stopProgressDialog();

                        //m_bConnectStarted = false;

                        if (m_retValue == MiddlewareInterface.PAEW_RET_SUCCESS) {
                           /* Intent intent = new Intent(MainActivity.this, DevTestActivity.class);
                            intent.putExtra("contextHandle", m_contextHandle);
                            intent.putExtra("devIndex", 0);
                            startActivityForResult(intent, DEV_TSET_ACTIVITY);*/
                            ToastHelper.showToast("连接成功...");
                        } else {
                            ToastHelper.showToast("连接失败...");
                        }
                    }
                };

                m_connectObe.subscribe(m_connectOb);
            }


        });


    }

    @SuppressLint("CheckResult")
    public void scanBluet(View v) {

        Observable<String[]> m_getDeviceListObe = Observable.create(new ObservableOnSubscribe<String[]>() {

            @Override
            public void subscribe(final ObservableEmitter<String[]> e) throws Exception {
                startProgressDialog("正在扫描...");
                int[] devCount = new int[1];

                devCount[0] = MiddlewareInterface.PAEW_MAX_DEV_COUNT;

                String[] strDeviceNames = new String[MiddlewareInterface.PAEW_MAX_DEV_COUNT];

                MiddlewareInterface.getDeviceList(getApplicationContext(), "", 20000, new CommonUtility.enumCallback() {

                    @Override
                    public void discoverDevice(String[] strings) {
                        e.onNext(strings);
                    }
                }, strDeviceNames, devCount);

                e.onComplete();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());


        Observer<String[]> m_getDeviceListOb = new Observer<String[]>() {
            @Override
            public void onSubscribe(Disposable d) {
                devList.clear();
                startProgressDialog("正在扫描...");
            }

            @Override
            public void onNext(String[] strings) {

                if (strings != null) {
                    for (String s : strings) {
                        LogUtils.d(s);
                        devList.add(s);
                    }
                    devListAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onError(Throwable e) {
                stopProgressDialog();
            }

            @Override
            public void onComplete() {
                stopProgressDialog();

                ToastHelper.showToast("获取到了" + devList.size() + "个设备信息");

                //devListAdapter.notifyDataSetChanged();
            }
        };


        m_getDeviceListObe.subscribe(m_getDeviceListOb);

    }


    class DevListAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

        public DevListAdapter(@Nullable List<String> data) {
            super(R.layout.test_item_layout, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, String item) {

            helper.setText(R.id.dev_name, item);

        }
    }


    //request
    public static int BLUETOOTH_PERMISSIONS_REQUEST = 0;

    private static void requestPermissions(Activity activity) {
        if (activity == null) {
            return;
        }

        if (Build.VERSION.SDK_INT >= 23) {
            //check location permission
            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, BLUETOOTH_PERMISSIONS_REQUEST);
            }
        }

        //check blue tooth permission
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.BLUETOOTH}, BLUETOOTH_PERMISSIONS_REQUEST);
        }

        //check blue tooth admin permission
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.BLUETOOTH_ADMIN}, BLUETOOTH_PERMISSIONS_REQUEST);
        }
    }

}
