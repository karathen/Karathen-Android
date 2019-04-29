package com.raistone.wallet.sealwallet.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.chenenyu.router.annotation.Route;
import com.github.ontio.network.rest.UrlConsts;
import com.raistone.wallet.sealwallet.R;
import com.raistone.wallet.sealwallet.adapter.AssetsAdapter;
import com.raistone.wallet.sealwallet.datavases.TransferDaoUtils;
import com.raistone.wallet.sealwallet.entity.RemarkInfo;
import com.raistone.wallet.sealwallet.entity.TransferDetailInfo;
import com.raistone.wallet.sealwallet.entity.TransferInfo;
import com.raistone.wallet.sealwallet.httputils.SealApi;
import com.raistone.wallet.sealwallet.utils.BigDecimalUtils;
import com.raistone.wallet.sealwallet.utils.ChainAddressCreateManager;
import com.raistone.wallet.sealwallet.utils.CommonUtils;
import com.raistone.wallet.sealwallet.utils.Constant;
import com.raistone.wallet.sealwallet.utils.GsonUtils;
import com.raistone.wallet.sealwallet.utils.StatusBarUtil;
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
import okhttp3.RequestBody;
import retrofit2.converter.gson.GsonConverterFactory;

@Route(value = "TransactionDetailsActivity")
public class TransactionDetailsActivity extends BaseActivity {

    @BindView(R.id.titleBar)
    TitleBar titleBar;
    @BindView(R.id.money_tv)
    TextView moneyTv;
    @BindView(R.id.coin_name_tv)
    TextView coinNameTv;
    @BindView(R.id.hx_Tv)
    TextView hxTv;
    @BindView(R.id.ic_iv)
    ImageView icIv;
    @BindView(R.id.collection_address)
    TextView collectionAddress;
    @BindView(R.id.paying_address_tv)
    TextView payingAddressTv;
    @BindView(R.id.miner_cost_tv)
    TextView minerCostTv;
    @BindView(R.id.transaction_time_tv)
    TextView transactionTimeTv;
    @BindView(R.id.hx_value_tv)
    TextView hxValueTv;
    @BindView(R.id.hx_rl)
    LinearLayout hxRl;
    @BindView(R.id.remark_ll)
    LinearLayout remarkLl;

    private String txid, decimal, tokenSynbol;
    private boolean isLocal;

    private TransferDetailInfo.ResultBean resultBean;

    TransferInfo transferHash;
    private String coinType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_details);
        ButterKnife.bind(this);
        txid = getIntent().getStringExtra("txid");

        resultBean = (TransferDetailInfo.ResultBean) getIntent().getSerializableExtra("resultBean");

        isLocal = getIntent().getBooleanExtra("isLocal", true);
        decimal = getIntent().getStringExtra("decimal");
        tokenSynbol = getIntent().getStringExtra("tokenSynbol");
        coinType = getIntent().getStringExtra("coinType");


        setTitle(titleBar, getString(R.string.transfer_detail_string), true);

        ActivityManager.getInstance().pushActivity(this);

        if (isLocal) {
            transferHash = TransferDaoUtils.getTransferHash(txid);

            if (transferHash != null) {
                moneyTv.setText(transferHash.getValue());

                coinNameTv.setText(transferHash.getTokenSynbol());

                hxValueTv.setText(txid);

                collectionAddress.setText(transferHash.getFromAddress());

                payingAddressTv.setText(transferHash.getToAddress());


                transactionTimeTv.setText(transferHash.getTransferTime());

                if (!TextUtils.isEmpty(transferHash.getRemark())) {
                    remarkLl.setVisibility(View.VISIBLE);
                    minerCostTv.setText(transferHash.getRemark());
                } else {
                    remarkLl.setVisibility(View.GONE);
                }


            }
        } else {
            if (resultBean != null) {


                String tokenDecimal = WalletItemDetailActivity.assetsTokenDecimal;

                String tokenSynbol = WalletItemDetailActivity.assetsTokenSynbol;

                String tokenType= WalletItemDetailActivity.assetsTokenType;


                if (!TextUtils.isEmpty(tokenDecimal)) {

                    double value2 = Math.pow(10, Double.parseDouble(tokenDecimal));


                    BigDecimal bigDecimal = BigDecimalUtils.div(resultBean.getValue(), value2 + "");

                    if(tokenType.equals("NEP-5")){
                        moneyTv.setText(resultBean.getValue());
                    }else {

                        moneyTv.setText(AssetsAdapter.subZeroAndDot(bigDecimal.toPlainString()));
                    }

                } else {
                    moneyTv.setText(resultBean.getValue());
                }


                coinNameTv.setText(tokenSynbol);

                hxValueTv.setText(txid);

                collectionAddress.setText(resultBean.getAddressFrom());

                payingAddressTv.setText(resultBean.getAddressTo());


                transactionTimeTv.setText(CommonUtils.conversionTime(resultBean.getBlockTime()));

                getTxRemark(txid);
            }
        }

        titleBar.setIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityManager.getInstance().finishAllActivity();
            }
        });

    }

    @OnClick({R.id.ic_iv, R.id.hx_rl})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ic_iv:
                CommonUtils.copyString(hxValueTv);
                break;
            case R.id.hx_rl:

                String type = "";

                if (isLocal) {
                    type = transferHash.getCoinType();
                } else {
                    type = coinType;
                }

                String url = "";
                if (type.equals(ChainAddressCreateManager.ETH_COIN_TYPE)) {
                    url = UrlConsts.URL_ETH_SCAN_BYHASH;
                }

                if (type.equals(ChainAddressCreateManager.NEO_COIN_TYPE)) {
                    url = UrlConsts.URL_NEO_SCAN_BYHASH;

                    if (txid.startsWith("0x")){
                        txid=txid.substring(2);
                    }
                }
                if (type.equals(ChainAddressCreateManager.ONT_COIN_TYPE)) {
                    url = UrlConsts.URL_ONT_SCAN_BYHASH;
                }

                Intent intent = new Intent(this, WebViewActivity.class);
                intent.putExtra(Constant.IntentKey.WEB_URL, url + txid);
                intent.putExtra(Constant.IntentKey.WEB_TITLE, "");
                startActivity(intent);
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            ActivityManager.getInstance().finishAllActivity();
        }
        return super.onKeyDown(keyCode, event);
    }

    public void getTxRemark(String txid) {

        List<String> data = new ArrayList<>();
        data.add(txid);
        RequestBody body = SealApi.toRequestBody("getTxRemark", data);
        EasyHttp.getInstance().setBaseUrl("").post("/")
                .addConverterFactory(GsonConverterFactory.create()).requestBody(body).execute(new SimpleCallBack<String>() {
            @Override
            public void onError(ApiException e) {

            }

            @Override
            public void onSuccess(String s) {
                //ToastHelper.showToast(s);
                RemarkInfo remarkInfo = GsonUtils.decodeJSON(s, RemarkInfo.class);
                if (!TextUtils.isEmpty(remarkInfo.getResult())) {
                    remarkLl.setVisibility(View.VISIBLE);
                    minerCostTv.setText(remarkInfo.getResult());
                } else {
                    remarkLl.setVisibility(View.GONE);
                }
            }
        });
    }

}
