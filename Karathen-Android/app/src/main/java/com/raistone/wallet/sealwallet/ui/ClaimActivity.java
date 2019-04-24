package com.raistone.wallet.sealwallet.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.chenenyu.router.annotation.Route;
import com.raistone.wallet.sealwallet.R;
import com.raistone.wallet.sealwallet.entity.ClaimOngInfo;
import com.raistone.wallet.sealwallet.entity.ExtractGasInfo;
import com.raistone.wallet.sealwallet.entity.GasInfo;
import com.raistone.wallet.sealwallet.factory.ChainAddressInfo;
import com.raistone.wallet.sealwallet.httputils.HttpUtils;
import com.raistone.wallet.sealwallet.utils.BigDecimalUtils;
import com.raistone.wallet.sealwallet.utils.Constant;
import com.raistone.wallet.sealwallet.utils.GsonUtils;
import com.raistone.wallet.sealwallet.utils.StatusBarUtil;
import com.raistone.wallet.sealwallet.utils.ToastHelper;
import com.raistone.wallet.sealwallet.widget.TitleBar;
import com.zhouyou.http.EasyHttp;
import com.zhouyou.http.callback.SimpleCallBack;
import com.zhouyou.http.exception.ApiException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import neoutils.Neoutils;
import okhttp3.RequestBody;

/**
 * 提取Gas 或者 ong
 */

@Route(value = "ClaimActivity")
public class ClaimActivity extends BaseActivity {

    @BindView(R.id.titleBar)
    TitleBar titleBar;
    @BindView(R.id.claim_name)
    TextView claimName;
    @BindView(R.id.claim_address)
    TextView claimAddress;
    @BindView(R.id.extractable_tv)
    TextView extractableTv;
    @BindView(R.id.extractable_value_tv)
    TextView extractableValueTv;
    @BindView(R.id.not_extractable_tv)
    TextView notExtractableTv;
    @BindView(R.id.not_extractable_value_tv)
    TextView notExtractableValueTv;
    @BindView(R.id.commit_btn)
    Button commitBtn;
    @BindView(R.id.claim_tips_tv)
    TextView claimTipsTv;


    private ChainAddressInfo chainAddressInfo;


    private String extractableBalance = "0";

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            switch (what) {
                case 0:
                    String message = (String) msg.obj;

                    ToastHelper.showToast(message);
                    break;
                case 1:
                    ToastHelper.showToast(getResources().getString(R.string.claim_success_strimg));
                    extractableBalance="0";
                    extractableValueTv.setText(extractableBalance);
                    break;
                case 2:
                    ToastHelper.showToast(getResources().getString(R.string.claim_failure_strimg));
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_claim);
        ButterKnife.bind(this);


        //StatusBarUtil.setTransparent(this);

        chainAddressInfo = (ChainAddressInfo) getIntent().getSerializableExtra("ethWallet");

        ActivityManager.getInstance().pushActivity(this);

        if (chainAddressInfo.getCoinType().equals("ONT")) {
            setTitle(titleBar, getResources().getString(R.string.claim_strimg) + "ONG", true);
            extractableTv.setText(getResources().getString(R.string.extractable_string) + "ONG");
            notExtractableTv.setText(getResources().getString(R.string.not_extractable_string) + "ONG");
            claimTipsTv.setVisibility(View.VISIBLE);

            startProgressDialog();
            getONG(chainAddressInfo.getAddress());
        }
        if (chainAddressInfo.getCoinType().equals("NEO")) {
            setTitle(titleBar, getResources().getString(R.string.claim_strimg) + "GAS", true);
            extractableTv.setText(getResources().getString(R.string.extractable_string) + "GAS");
            notExtractableTv.setText(getResources().getString(R.string.not_extractable_string) + "GAS");
            claimTipsTv.setVisibility(View.GONE);

            startProgressDialog();
            getUnClaimedGas(chainAddressInfo.getAddress());


        }


        claimName.setText(chainAddressInfo.getName());

        claimAddress.setText(chainAddressInfo.getAddress());


    }

    @OnClick(R.id.commit_btn)
    public void onViewClicked() {

        //Neoutils.claimONG()

        if (extractableBalance.equals("0")) {
            ToastHelper.showToast(getResources().getString(R.string.insufficient_balance_string));
            return;
        }
        //提取ONG
        if (chainAddressInfo.getCoinType().equals("ONT")) {
            startProgressDialog();


            new Thread() {
                @Override
                public void run() {
                    super.run();

                    try {
                        String claimONG = Neoutils.claimONG(Constant.ONTParams.ONT_BASE_URL, 500l, 20000l, chainAddressInfo.getWif());
                        if (!TextUtils.isEmpty(claimONG)) {
                            stopProgressDialog();

                            Message msg = new Message();
                            msg.what = 1;
                            handler.sendMessage(msg);

                        } else {
                            stopProgressDialog();
                            Message msg = new Message();
                            msg.what = 2;
                            handler.sendMessage(msg);

                        }
                    } catch (Exception e) {
                        stopProgressDialog();
                        Message msg = new Message();
                        msg.what = 0;
                        msg.obj = e.getMessage();
                        handler.sendMessage(msg);
                        e.printStackTrace();
                    }
                }
            }.start();


        }

        // 提取GAS
        if (chainAddressInfo.getCoinType().equals("NEO")) {

            startProgressDialog();
            extractGas(chainAddressInfo.getAddress());

        }
    }


    /**
     * 查询可提取的 gas
     *
     * @param address
     */
    public void getClaimableGas(String address, final String allGas) {

        List<String> params = new ArrayList();
        params.add(address);

        RequestBody body = HttpUtils.toRequestBody("getClaimableGas", params);

        EasyHttp.getInstance().setBaseUrl(Constant.NEOParams.NEO_BASE_URL).post("/")
                .requestBody(body)
                .execute(new SimpleCallBack<String>() {
                    @Override
                    public void onError(ApiException e) {
                        //showToast(e.getMessage());
                        stopProgressDialog();
                    }

                    @Override
                    public void onSuccess(String response) {
                        //showToast(response);

                        GasInfo gasInfo = GsonUtils.decodeJSON(response, GasInfo.class);

                        if (gasInfo != null) {

                            //可提取gas
                            extractableBalance = gasInfo.getResult();


                            extractableValueTv.setText(extractableBalance);

                            //不可提取gas
                            BigDecimal sub = BigDecimalUtils.sub(allGas, extractableBalance);
                            notExtractableValueTv.setText(sub.toPlainString());
                        }
                        stopProgressDialog();
                    }
                });

    }

    /**
     * 查询所有的 gas
     *
     * @param address
     */
    public void getUnClaimedGas(final String address) {

        List<String> params = new ArrayList();
        params.add(address);
        RequestBody body = HttpUtils.toRequestBody("getUnClaimedGas", params);

        EasyHttp.getInstance().setBaseUrl(Constant.NEOParams.NEO_BASE_URL).post("/")
                .requestBody(body)
                .execute(new SimpleCallBack<String>() {
                    @Override
                    public void onError(ApiException e) {
                        //showToast(e.getMessage());
                        stopProgressDialog();
                    }

                    @Override
                    public void onSuccess(String response) {
                        //showToast(response);

                        GasInfo gasInfo = GsonUtils.decodeJSON(response, GasInfo.class);

                        if (gasInfo != null) {
                            getClaimableGas(address, gasInfo.getResult());
                        }


                    }
                });

        //StatService.setAppChannel();
    }

    /**
     * 提取gas
     */
    public void extractGas(final String address) {

        List<String> params = new ArrayList();
        params.add(address);
        RequestBody body = HttpUtils.toRequestBody("extractGas", params);

        EasyHttp.getInstance().setBaseUrl(Constant.NEOParams.NEO_BASE_URL).post("/")
                .requestBody(body)
                .execute(new SimpleCallBack<String>() {
                    @Override
                    public void onError(ApiException e) {
                        //showToast(e.getMessage());
                        stopProgressDialog();
                    }

                    @Override
                    public void onSuccess(String response) {
                        stopProgressDialog();

                        ExtractGasInfo gasInfo = GsonUtils.decodeJSON(response, ExtractGasInfo.class);

                        if (gasInfo != null) {
                            ExtractGasInfo.ResultBean resultBean = gasInfo.getResult();

                            String txid = resultBean.getTxid();

                            if (!TextUtils.isEmpty(txid)) {
                                ToastHelper.showToast(getResources().getString(R.string.claim_success_strimg));
                            } else {
                                ToastHelper.showToast(getResources().getString(R.string.claim_failure_strimg));
                            }
                        }


                    }
                });

    }

    /**
     * 查询所有的 ong
     *
     * @param address
     */
    public void getONG(final String address) {

        // https://explorer.ont.io/api/v1/explorer/address/AURRtYmGrrg1hLiZWcWf62AiZN1SG5s7Vk/0/0

        EasyHttp.getInstance().setBaseUrl(Constant.ONTParams.ONT_BALANCE_URL).get(address)
                .execute(new SimpleCallBack<String>() {
                    @Override
                    public void onError(ApiException e) {
                        //showToast(e.getMessage());
                        stopProgressDialog();
                    }

                    @Override
                    public void onSuccess(String response) {
                        //showToast(response);

                        ClaimOngInfo claimOngInfo = GsonUtils.decodeJSON(response, ClaimOngInfo.class);

                        List<ClaimOngInfo.ResultBean> resultBeans = claimOngInfo.getResult();

                        for (int i = 0; i < resultBeans.size(); i++) {

                            ClaimOngInfo.ResultBean resultBean = resultBeans.get(i);

                            //不可提取
                            boolean waitboundong = resultBean.getAssetName().equals("waitboundong");

                            //不可提取
                            if (waitboundong) {
                                notExtractableValueTv.setText(resultBean.getBalance());
                            }

                            //可提取
                            boolean unboundong = resultBean.getAssetName().equals("unboundong");

                            if (unboundong) {
                                extractableBalance = resultBean.getBalance();
                                extractableValueTv.setText(resultBean.getBalance());
                            }


                        }
                        stopProgressDialog();
                    }
                });


    }


}
