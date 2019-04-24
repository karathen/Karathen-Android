package com.raistone.wallet.sealwallet.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chenenyu.router.Router;
import com.chenenyu.router.annotation.Route;
import com.github.ontio.OntSdk;
import com.raistone.wallet.sealwallet.R;
import com.raistone.wallet.sealwallet.adapter.AssetsAdapter;
import com.raistone.wallet.sealwallet.adapter.TransferAdapter;
import com.raistone.wallet.sealwallet.adapter.TransferLocalAdapter;
import com.raistone.wallet.sealwallet.daoutils.AssetsDetailDaoUtils;
import com.raistone.wallet.sealwallet.datavases.TransferDaoUtils;
import com.raistone.wallet.sealwallet.entity.ETHResponseInfo;
import com.raistone.wallet.sealwallet.entity.NeoBalanceInfo;
import com.raistone.wallet.sealwallet.entity.NeoResultInfo;
import com.raistone.wallet.sealwallet.entity.NeoTransactionByHashInfo;
import com.raistone.wallet.sealwallet.entity.OntBalanceInfo;
import com.raistone.wallet.sealwallet.entity.OntTransferDetailInfo;
import com.raistone.wallet.sealwallet.entity.TransactionByHashInfo;
import com.raistone.wallet.sealwallet.entity.TransactionReceiptInfo;
import com.raistone.wallet.sealwallet.entity.TransferDetailInfo;
import com.raistone.wallet.sealwallet.entity.TransferInfo;
import com.raistone.wallet.sealwallet.factory.AssetsDeatilInfo;
import com.raistone.wallet.sealwallet.factory.ChainAddressInfo;
import com.raistone.wallet.sealwallet.httputils.HttpUtils;
import com.raistone.wallet.sealwallet.httputils.SealApi;
import com.raistone.wallet.sealwallet.httputils.TestApiResult6;
import com.raistone.wallet.sealwallet.utils.BigDecimalUtils;
import com.raistone.wallet.sealwallet.utils.ChainAddressCreateManager;
import com.raistone.wallet.sealwallet.utils.CommonUtils;
import com.raistone.wallet.sealwallet.utils.Constant;
import com.raistone.wallet.sealwallet.utils.GsonUtils;
import com.raistone.wallet.sealwallet.utils.LocalManageUtil;
import com.raistone.wallet.sealwallet.utils.SharePreUtil;
import com.raistone.wallet.sealwallet.utils.ToastHelper;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zhouyou.http.EasyHttp;
import com.zhouyou.http.callback.CallClazzProxy;
import com.zhouyou.http.callback.SimpleCallBack;
import com.zhouyou.http.exception.ApiException;
import com.zhouyou.http.model.HttpParams;
import com.zhouyou.http.subsciber.BaseSubscriber;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import okhttp3.RequestBody;
import retrofit2.converter.gson.GsonConverterFactory;


@Route(value = "WalletItemDetailActivity")
public class WalletItemDetailActivity extends AppCompatActivity implements OnLoadMoreListener, OnRefreshListener {

    @BindView(R.id.ic_back)
    ImageView icBack;
    @BindView(R.id.back_ll)
    RelativeLayout backLl;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_balance)
    TextView tvBalance;
    @BindView(R.id.receipt_ll)
    LinearLayout receiptLl;
    @BindView(R.id.transfer_ll)
    LinearLayout transferLl;
    @BindView(R.id.tv_export)
    TextView tvExport;
    @BindView(R.id.tv_allRecord)
    TextView tvAllRecord;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    @BindView(R.id.sw_view)
    SmartRefreshLayout swView;
    @BindView(R.id.price_tv)
    TextView priceTv;
    @BindView(R.id.recyclerviewLocal)
    RecyclerView recyclerviewLocal;

    private ChainAddressInfo ethWallet;

    private AssetsDeatilInfo dataBean;


    private List<TransferInfo> defList = new ArrayList<>();

    private List<TransferDetailInfo.ResultBean> resultBeans = new ArrayList<>();

    private TransferLocalAdapter localAdapter;

    private TransferAdapter transferAdapter;


    private List<String> blockArray = new ArrayList<>();
    private List<String> statusArray = new ArrayList<>();


    private List<String> neoBlockArray = new ArrayList<>();
    private List<String> neoStatusArray = new ArrayList<>();


    private List<String> ontStatusArray = new ArrayList<>();

    private int pageNo = 1;

    public static String assetsTokenDecimal = "18";
    public static String assetsTokenSynbol = "";
    public static String assetsTokenType = "";
    public static String assetsCoinType = "";
    public static String walletsAddress = "";//钱包地址
    boolean isLocal = true;//默认是本地交易数据

    private List<Object> datas = new ArrayList<>();

    private Context context;

    private Disposable findTokenbalanceDisposable,getOntBalanceDisposable,
            getTransferAllStatusDisposable,getNeoBalanceDisposable, findAllStatusDisposable,findAllOntStatusDisposable;

    private BaseSubscriber<String> getHashSubscriber,getNeoHashSubscriber,findAllNeoStatusSubscriber;

    public static WalletItemDetailActivity instance = null;

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    defList.clear();
                    defList = TransferDaoUtils.selectAllTransfers(dataBean.getTokenSynbol(), walletsAddress);
                    localAdapter.setNewData(defList);
                    recyclerviewLocal.setAdapter(localAdapter);
                    localAdapter.notifyDataSetChanged();
                    break;
                case 1:

                    defList = TransferDaoUtils.selectAllTransfers(dataBean.getTokenSynbol(), walletsAddress);
                    localAdapter.setNewData(defList);
                    recyclerviewLocal.setAdapter(localAdapter);
                    localAdapter.notifyDataSetChanged();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_item_detail);
        ButterKnife.bind(this);

        context = this;
        ethWallet = (ChainAddressInfo) getIntent().getSerializableExtra("ethWallet");
        dataBean = (AssetsDeatilInfo) getIntent().getSerializableExtra("assetsInfo");

        walletsAddress = ethWallet.getAddress();

        tvTitle.setText(ethWallet.getCoinType() + "-" + dataBean.getTokenSynbol());

        swView.setEnableLoadMore(false);

        instance=this;

        recyclerview.setLayoutManager(new LinearLayoutManager(this));
        recyclerviewLocal.setLayoutManager(new LinearLayoutManager(this));

        swView.setOnRefreshListener(this);
        swView.setOnLoadMoreListener(this);

        //ActivityManager.getInstance().pushActivity(this);

        assetsTokenDecimal = dataBean.getTokenDecimal();
        assetsTokenSynbol = dataBean.getTokenSynbol();
        assetsTokenType = dataBean.getTokenType();
        assetsCoinType = dataBean.getCoinType();
        dataBean.getCoinType();
        //精度是否为空
        if (!TextUtils.isEmpty(dataBean.getTokenDecimal())) {

            if (dataBean.getBalance() != null) {

                String decimal = dataBean.getTokenDecimal();

                if (!dataBean.getBalance().equals("0")) {

                    //BigDecimal bigDecimal = BigDecimalUtils.div(dataBean.getBalance().toString(), String.valueOf(Math.pow(10, Double.parseDouble(decimal))), 8);


                    tvBalance.setText(AssetsAdapter.subZeroAndDot(dataBean.getBalance()));
                }
            } else {
                tvBalance.setText(AssetsAdapter.subZeroAndDot(dataBean.getBalance()));
            }

        } else {
            tvBalance.setText(AssetsAdapter.subZeroAndDot(dataBean.getBalance()));
        }

        Boolean unit = SharePreUtil.getBoolean(this, "CurrencyUnit", true);
        if (unit) {
            if (!TextUtils.isEmpty(dataBean.getPrice()) && !dataBean.getPrice().equals("0")) {

                priceTv.setText("≈ ¥ " + AssetsAdapter.subZeroAndDot(dataBean.getPrice()) + "");

            } else {
                priceTv.setText("≈ ¥ " + 0);
            }
        } else {
            if (TextUtils.isEmpty(dataBean.getPriceUSD()) && !dataBean.getPriceUSD().equals("0")) {

                priceTv.setText("≈ $ " + AssetsAdapter.subZeroAndDot(dataBean.getPriceUSD()) + "");

            } else {
                priceTv.setText("≈ $ " + 0);
            }
        }
        //findBalances();


        transferAdapter = new TransferAdapter(resultBeans);

        recyclerview.setAdapter(transferAdapter);


        transferAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

                TransferDetailInfo.ResultBean resultBean = resultBeans.get(position);

                Router.build("TransactionDetailsActivity")
                        .with("txid", resultBean.getTxId())
                        .with("resultBean", resultBean)
                        .with("decimal", dataBean.getTokenDecimal())
                        .with("tokenSynbol", dataBean.getTokenSynbol())
                        .with("coinType", dataBean.getCoinType())
                        .with("isLocal", false)
                        .go(WalletItemDetailActivity.this);
            }
        });


        //getTransferAllStatus(pageNo);

        /**
         * 获取 交易详情
         */
        getAllTransacionInfo(pageNo);

    }

    private void findBalances() {

        if (dataBean.getTokenType().equals(ChainAddressCreateManager.ETH_COIN_TYPE) || dataBean.getTokenType().equals("ERC-20") || dataBean.getTokenType().equals("ERC-721")) {

            if (dataBean.getTokenSynbol().equals(ChainAddressCreateManager.ETH_COIN_TYPE)) {
                findTokenbalance(0);
            } else {
                findTokenbalance(1);
            }
        }

        if (dataBean.getCoinType().equals(ChainAddressCreateManager.NEO_COIN_TYPE)) {

            getNeoBalance(walletsAddress, dataBean.getTokenAddress());

        }

        if (dataBean.getCoinType().equals(ChainAddressCreateManager.ONT_COIN_TYPE)) {

            getOntBalance(walletsAddress);

        }
    }

    private void getAllTransacionInfo(int paNo) {
        if (dataBean.getCoinType().equals(ChainAddressCreateManager.ETH_COIN_TYPE)) {
            datas.clear();

            String address;
            if (dataBean.getTokenSynbol().equals("ETH")) {
                address = "0x";
            } else {
                address = dataBean.getTokenAddress();
            }
            datas.add(ethWallet.getAddress());
            datas.add(address);

            getTransferAllStatus(Constant.ETHParams.BASE_MAIN_URL, datas, paNo);
        }

        if (dataBean.getCoinType().equals(ChainAddressCreateManager.NEO_COIN_TYPE)) {
            datas.clear();

            datas.add(ethWallet.getAddress());
            datas.add(dataBean.getTokenAddress());

            getTransferAllStatus(  Constant.NEOParams.NEO_BASE_URL, datas, paNo);
        }

        if (dataBean.getCoinType().equals(ChainAddressCreateManager.ONT_COIN_TYPE)) {
            datas.clear();

            datas.add(ethWallet.getAddress());

            if (dataBean.getTokenSynbol().equals("ONT")) {
                datas.add("0100000000000000000000000000000000000000");
            }


            if (dataBean.getTokenSynbol().equals("ONG")) {
                datas.add("0200000000000000000000000000000000000000");
            }

            getTransferAllStatus(Constant.ONTParams.ONT_TRAN_URL, datas, paNo);
        }
    }

    @OnClick({R.id.back_ll, R.id.receipt_ll, R.id.transfer_ll, R.id.tv_export, R.id.tv_allRecord})
    public void onViewClicked(View view) {
        switch (view.getId()) {

            case R.id.back_ll:
                finish();
                break;

            case R.id.receipt_ll:

                Router.build("ReceiptActivity")
                        .with("chainAddressInfo", ethWallet)
                        .with("isHome", false)
                        .with("contractAddress", dataBean.getTokenAddress())
                        .with("coinType",ethWallet.getCoinType())
                        .with("coinName", dataBean.getTokenSynbol()).go(this);

                break;
            case R.id.transfer_ll:
                Router.build("TransferActivity")
                        .with("address", ethWallet.getAddress())
                        .with("accountId",ethWallet.getAccountId())
                        .with("coinType", ethWallet.getCoinType())
                        .with("coinName", dataBean.getTokenSynbol())
                        .with("tokenSynbol", dataBean.getTokenSynbol())
                        .with("tokenType", dataBean.getTokenType())
                        .with("tokenAddress", dataBean.getTokenAddress())
                        .with("tokenDecimal", dataBean.getTokenDecimal())
                        .with("balance", dataBean.getBalance().toString())
                        .with("dataBean", dataBean)
                        .go(this);
                break;
            case R.id.tv_export:
                isLocal = true;
                swView.setEnableLoadMore(false);
                tvExport.setTextColor(getResources().getColor(R.color.common_blue_color));
                tvAllRecord.setTextColor(getResources().getColor(R.color.common_title_color));

                recyclerview.setVisibility(View.GONE);
                recyclerviewLocal.setVisibility(View.VISIBLE);

                break;
            case R.id.tv_allRecord:
                isLocal = false;
                swView.setEnableLoadMore(true);
                tvExport.setTextColor(getResources().getColor(R.color.common_title_color));
                tvAllRecord.setTextColor(getResources().getColor(R.color.common_blue_color));

                recyclerview.setVisibility(View.VISIBLE);
                recyclerviewLocal.setVisibility(View.GONE);

                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        findBalances();
        getTransferOutStatus();
    }

    public void findTokenbalance(int type) {

        HttpParams httpParams = new HttpParams();
        httpParams.put("module", "account");

        if (type == 0) {
            httpParams.put("action", "balance");
            httpParams.put("address", ethWallet.getAddress());
            httpParams.put("tag", "latest");
        } else {
            httpParams.put("action", "tokenbalance");
            httpParams.put("contractaddress", dataBean.getTokenAddress());
            httpParams.put("address", ethWallet.getAddress());
            httpParams.put("tag", "latest");
        }

        findTokenbalanceDisposable = EasyHttp.getInstance().setBaseUrl(Constant.HttpServiceUrl.MAIN_URL).post("api")
                .params(httpParams)
                .execute(new SimpleCallBack<String>() {
                    @Override
                    public void onError(ApiException e) {
                        //showToast(e.getMessage());
                    }

                    @Override
                    public void onSuccess(String response) {

                        ETHResponseInfo info = GsonUtils.decodeJSON(response, ETHResponseInfo.class);

                        if (info.getStatus().equals("1")) {

                            String decimal = dataBean.getTokenDecimal();

                            if (!TextUtils.isEmpty(decimal)) {

                                if (!info.getResult().equals("0") && !info.getResult().equals("0.0")) {

                                    BigDecimal bigDecimal = BigDecimalUtils.div(info.getResult().toString(), String.valueOf(Math.pow(10, Double.parseDouble(decimal))), 8);

                                    tvBalance.setText(AssetsAdapter.subZeroAndDot(bigDecimal + ""));

                                    dataBean.setBalance(bigDecimal.toPlainString());

                                    AssetsDetailDaoUtils.updatePrice(dataBean);
                                } else {
                                    tvBalance.setText(AssetsAdapter.subZeroAndDot(info.getResult()));
                                }
                            } else {
                                tvBalance.setText(AssetsAdapter.subZeroAndDot(info.getResult()));
                            }

                        }

                    }
                });
    }

    public void getNeoBalance(final String walletsAddress, final String address) {

        List<String> params = new ArrayList();
        params.add(walletsAddress);
        params.add(address);

        RequestBody body = HttpUtils.toRequestBody("getBalance", params);

        getNeoBalanceDisposable=EasyHttp.getInstance().setBaseUrl(Constant.NEOParams.NEO_BASE_URL).post("/")
                .requestBody(body)
                .execute(new SimpleCallBack<String>() {
                    @Override
                    public void onError(ApiException e) {
                        //showToast(e.getMessage());
                    }

                    @Override
                    public void onSuccess(String response) {
                        //showToast(response);


                        NeoBalanceInfo assetsInfo = GsonUtils.decodeJSON(response, NeoBalanceInfo.class);


                        if (assetsInfo != null) {
                            String result = assetsInfo.getResult();


                            AssetsDeatilInfo assets = AssetsDetailDaoUtils.findSignAssets(address, walletsAddress,ethWallet.getAccountId());

                            if (assets != null) {
                                assets.setBalance(result);

                                dataBean.setBalance(result);

                                AssetsDetailDaoUtils.updatePrice(assets);


                                String decimal = dataBean.getTokenDecimal();

                                if (!TextUtils.isEmpty(decimal)) {

                                    if (!result.equals("0") && !result.equals("0.0")) {

                                        BigDecimal bigDecimal = BigDecimalUtils.div(result, String.valueOf(Math.pow(10, Double.parseDouble(decimal))), 8);
                                        if(dataBean.getTokenSynbol().equals("NEO") /*|| dataBean.getTokenSynbol().equals("GAS")*/) {

                                            tvBalance.setText(AssetsAdapter.subZeroAndDot(AssetsAdapter.subZeroAndDot(result)));

                                            dataBean.setBalance(AssetsAdapter.subZeroAndDot(AssetsAdapter.subZeroAndDot(result)));
                                        }else {
                                            tvBalance.setText(AssetsAdapter.subZeroAndDot(AssetsAdapter.subZeroAndDot(bigDecimal.toPlainString())));

                                            dataBean.setBalance(AssetsAdapter.subZeroAndDot(AssetsAdapter.subZeroAndDot(bigDecimal.toPlainString())));
                                        }

                                    } else {
                                        tvBalance.setText(AssetsAdapter.subZeroAndDot(result));

                                        dataBean.setBalance(AssetsAdapter.subZeroAndDot(result));
                                    }
                                } else {
                                    tvBalance.setText(AssetsAdapter.subZeroAndDot(result));

                                    dataBean.setBalance(AssetsAdapter.subZeroAndDot(result));
                                }


                            }

                        }

                    }
                });

    }

    public void getOntBalance(final String multiAddress) {

        List<String> params = new ArrayList();
        params.add(multiAddress);

        RequestBody body = HttpUtils.toRequestBody("getbalance", params);

        getOntBalanceDisposable=EasyHttp.getInstance().setBaseUrl(Constant.ONTParams.ONT_BASE_URL).post("/")
                .requestBody(body)
                .execute(new SimpleCallBack<String>() {
                    @Override
                    public void onError(ApiException e) {
                        //showToast(e.getMessage());
                    }

                    @Override
                    public void onSuccess(String response) {
                        //showToast(response);
                        OntBalanceInfo ontBalanceInfo = GsonUtils.decodeJSON(response, OntBalanceInfo.class);

                        OntBalanceInfo.ResultBean result = ontBalanceInfo.getResult();


                        if (result != null) {
                            String ont = result.getOnt();

                            String ong = result.getOng();


                            String tokenSynbol = dataBean.getTokenSynbol();

                            String decimal = dataBean.getTokenDecimal();

                            if (tokenSynbol.equals("ONT")) {

                                dataBean.setBalance(ont);
                                if (!TextUtils.isEmpty(decimal)) {

                                    if (!ont.equals("0")) {

                                        BigDecimal bigDecimal = BigDecimalUtils.div(ont, String.valueOf(Math.pow(10, Double.parseDouble(decimal))), 8);

                                        tvBalance.setText(AssetsAdapter.subZeroAndDot(bigDecimal + ""));

                                    } else {
                                        tvBalance.setText(ont);
                                    }
                                } else {
                                    tvBalance.setText(ont);
                                }
                            }

                            if (tokenSynbol.equals("ONG")) {
                                dataBean.setBalance(ong);

                                if (!TextUtils.isEmpty(decimal)) {

                                    if (!ong.equals("0")) {

                                        BigDecimal bigDecimal = BigDecimalUtils.div(ong, String.valueOf(Math.pow(10, Double.parseDouble(decimal))), 8);

                                        tvBalance.setText(AssetsAdapter.subZeroAndDot(bigDecimal + ""));

                                    } else {
                                        tvBalance.setText(AssetsAdapter.subZeroAndDot(ong));
                                    }
                                } else {
                                    tvBalance.setText(AssetsAdapter.subZeroAndDot(ong));
                                }

                            }

                            List<AssetsDeatilInfo> deatilInfos = ethWallet.getAssetsInfoDataList();

                            if (deatilInfos != null && deatilInfos.size() > 0) {
                                for (AssetsDeatilInfo info : deatilInfos) {
                                    if (info.getTokenSynbol().toLowerCase().equals("ont")) {
                                        info.setBalance(ont);
                                        //dataBean.setBalance(ont);
                                        AssetsDetailDaoUtils.updatePrice(info);

                                        break;
                                    }
                                    if (info.getTokenSynbol().toLowerCase().equals("ong")) {

                                        //dataBean.setBalance(ong);

                                        info.setBalance(ong);
                                        AssetsDetailDaoUtils.updatePrice(info);
                                        break;
                                    }
                                }
                            }
                        }

                    }
                });


    }

    public void getTransferOutStatus() {
        defList = TransferDaoUtils.selectAllTransfers(dataBean.getTokenSynbol(), walletsAddress);

        if (defList != null && defList.size() > 0) {

            for (int i = 0; i < defList.size(); i++) {

                TransferInfo transferInfo = defList.get(i);

                if (transferInfo.getBlockHeighe().intValue() == 0 && transferInfo.getStatus().equals("0")) {
                    if (transferInfo.getCoinType().equals(ChainAddressCreateManager.ETH_COIN_TYPE)) {
                        blockArray.add(defList.get(i).getTxId());
                    }

                    if (transferInfo.getCoinType().equals(ChainAddressCreateManager.NEO_COIN_TYPE)) {
                        neoBlockArray.add(defList.get(i).getTxId());
                    }

                }


                if (transferInfo.getCoinType().equals(ChainAddressCreateManager.ETH_COIN_TYPE) && transferInfo.getStatus().equals("0") && transferInfo.getBlockHeighe().intValue() != 0) {
                    if (transferInfo.getCoinType().equals(ChainAddressCreateManager.ETH_COIN_TYPE)) {
                        //blockArray.add(defList.get(i).getTxId());
                        statusArray.add(defList.get(i).getTxId());
                    }

                    if (transferInfo.getStatus().equals("0") && transferInfo.getCoinType().equals(ChainAddressCreateManager.NEO_COIN_TYPE) && transferInfo.getBlockHeighe().intValue() != 0) {
                        //blockArray.add(defList.get(i).getTxId());
                        neoStatusArray.add(defList.get(i).getTxId());
                    }

                }

                if (transferInfo.getCoinType().equals(ChainAddressCreateManager.ONT_COIN_TYPE) && transferInfo.getBlockHeighe().intValue() == 0) {
                    //blockArray.add(defList.get(i).getTxId())
                    ontStatusArray.add(defList.get(i).getTxId());
                }
            }
        }

        localAdapter = new TransferLocalAdapter(defList);
        recyclerviewLocal.setAdapter(localAdapter);

        if (blockArray != null && blockArray.size() > 0) {
            for (int i = 0; i < blockArray.size(); i++) {

                getHash(blockArray.get(i));
            }
        }

        if (statusArray != null && statusArray.size() > 0) {
            for (int i = 0; i < statusArray.size(); i++) {
                findAllStatus(statusArray.get(i));
            }
        }

        if (neoBlockArray != null && neoBlockArray.size() > 0) {
            for (int i = 0; i < neoBlockArray.size(); i++) {
                getNeoHash(neoBlockArray.get(i));
            }
        }


        if (neoStatusArray != null && neoStatusArray.size() > 0) {
            for (int i = 0; i < neoStatusArray.size(); i++) {
                findAllNeoStatus(neoStatusArray.get(i));
            }
        }


        if (ontStatusArray != null && ontStatusArray.size() > 0) {
            for (int i = 0; i < ontStatusArray.size(); i++) {

                findAllOntStatus(ontStatusArray.get(i));
            }
        }


        localAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                TransferInfo info = defList.get(position);

                Router.build("TransactionDetailsActivity").with("txid", info.getTxId()).go(WalletItemDetailActivity.this);
            }
        });

    }

    public void getTransferAllStatus(String baseUrl, List<Object> params, int pageNo) {

        params.add(pageNo);

        RequestBody body = HttpUtils.toRequestBody("getTransactionByAddress", params);

        getTransferAllStatusDisposable = EasyHttp.getInstance().setBaseUrl(baseUrl).post("/")
                .requestBody(body)
                .execute(new SimpleCallBack<String>() {
                    @Override
                    public void onError(ApiException e) {
                        //showToast(e.getMessage());
                    }

                    @Override
                    public void onSuccess(String response) {
                        TransferDetailInfo detailInfo = GsonUtils.decodeJSON(response, TransferDetailInfo.class);

                        List<TransferDetailInfo.ResultBean> infoResult = detailInfo.getResult();

                        if (infoResult == null) {
                            if (!isLocal) {
                                ToastHelper.showToast(getResources().getString(R.string.no_more_data));
                            }
                            return;
                        }
                        if (infoResult != null && infoResult.size() < 8) {

                            resultBeans.addAll(infoResult);

                            transferAdapter.notifyDataSetChanged();
                            if (!isLocal) {
                                ToastHelper.showToast(getResources().getString(R.string.no_more_data));
                            }

                        } else {
                            resultBeans.addAll(infoResult);
                            transferAdapter.setNewData(resultBeans);
                            transferAdapter.notifyDataSetChanged();
                        }


                    }
                });


    }


    int count = 10; //轮询次数 10次

    /**
     * 获取交易详情 查询ETh块高
     *
     * @param txid
     */
    @SuppressLint("CheckResult")
    public void getHash(final String txid) {

        getHashSubscriber = Observable.interval(0, 5, TimeUnit.SECONDS).flatMap(new Function<Long, ObservableSource<String>>() {
            @Override
            public ObservableSource<String> apply(@NonNull Long aLong) throws Exception {
                return EasyHttp.getInstance().setBaseUrl(Constant.HttpServiceUrl.MAIN_URL).get("api?")
                        .params("module", "proxy")
                        .params("action", "eth_getTransactionByHash")
                        .params("txhash", txid)
                        //采用代理
                        .execute(new CallClazzProxy<TestApiResult6<String>, String>(String.class) {
                        });
            }
        }).takeUntil(new Predicate<String>() {
            @Override
            public boolean test(@NonNull String content) throws Exception {
                //结果为true，说明满足条件了，就不在轮询了
                boolean flag = false;

                TransactionByHashInfo byHashInfo = GsonUtils.decodeJSON(content, TransactionByHashInfo.class);

                TransactionByHashInfo.ResultBean result = byHashInfo.getResult();

                if (result != null) {
                    //块高
                    String number = result.getBlockNumber();

                    if (!TextUtils.isEmpty(number)) {
                        flag = true;
                    } else {
                        flag = false;
                    }

                } else {
                    count--;
                    if (count == 0) {
                        TransferInfo transfers = TransferDaoUtils.selectTransfersByHash(txid);
                        transfers.setStatus("-1");
                        transfers.update();
                        flag = true;
                    } else {
                        flag = false;
                    }
                    //flag=false;
                }


                return flag;
            }
        }).subscribeWith(new BaseSubscriber<String>() {
            @Override
            public void onError(ApiException e) {
                //showToast(e.getMessage());
            }

            @Override
            public void onNext(@NonNull String content) {

                TransactionByHashInfo byHashInfo = GsonUtils.decodeJSON(content, TransactionByHashInfo.class);

                TransactionByHashInfo.ResultBean result = byHashInfo.getResult();

                if (result != null) {

                    if (!TextUtils.isEmpty(result.getBlockNumber())) {


                        Long conversion = CommonUtils.binaryConversion(result.getBlockNumber());

                        if (conversion != null) {

                            //long res = SplashActivity.blockNumber - conversion;

                            long res = SharePreUtil.getLongValue(context, "blockNumber", 0l) - conversion;

                            if (res < 12) {
                                statusArray.add(txid);
                            } else {
                                TransferInfo transfers = TransferDaoUtils.selectTransfersByHash(txid);
                                transfers.setStatus("1");
                                transfers.setBlockHeighe(new BigInteger(res + ""));
                                transfers.update();

                                defList.clear();
                                defList = TransferDaoUtils.selectAllTransfers(dataBean.getTokenSynbol(), walletsAddress);
                                localAdapter.setNewData(defList);
                                recyclerviewLocal.setAdapter(localAdapter);
                                localAdapter.notifyDataSetChanged();
                            }

                            if (statusArray != null && statusArray.size() > 0) {
                                for (int i = 0; i < statusArray.size(); i++) {
                                    findAllStatus(statusArray.get(i));
                                }

                            }
                        }

                    }


                } /*else {

                    TransferInfo transfers = TransferDaoUtils.selectTransfersByHash(txid);
                    if (transfers != null) {
                        transfers.setStatus("-1");
                        transfers.update();
                    }
                    //EasyHttp.cancelSubscription(blockdisposable);
                }*/
            }
        });

    }

    /**
     * 查询状态
     *
     * @param txid
     */
    @SuppressLint("CheckResult")
    public void findAllStatus(final String txid) {

        findAllStatusDisposable = EasyHttp.getInstance().setBaseUrl(Constant.HttpServiceUrl.MAIN_URL).get("api")
                .addConverterFactory(GsonConverterFactory.create())
                .params("module", "proxy")
                .params("action", "eth_getTransactionReceipt")
                .params("txhash", txid)
                .execute(new SimpleCallBack<String>() {
                    @Override
                    public void onError(ApiException e) {
                        //showToast(e.getMessage());
                    }

                    @Override
                    public void onSuccess(String response) {

                        blockArray.remove(txid);

                        TransactionReceiptInfo byHashInfo = GsonUtils.decodeJSON(response, TransactionReceiptInfo.class);

                        TransactionReceiptInfo.ResultBean result = byHashInfo.getResult();

                        TransferInfo transfers = TransferDaoUtils.selectTransfersByHash(txid);

                        if (result != null) {

                            //获取状态
                            String status = result.getStatus();

                            //转换为10进制
                            Long conversion = CommonUtils.binaryConversion(status);

                            //状态为1 代表成功
                            if (conversion == 1) {
                                //获取块高
                                if (!TextUtils.isEmpty(result.getBlockNumber())) {


                                    statusArray.remove(txid);

                                    //将块高转换为10进制
                                    Long block = CommonUtils.binaryConversion(result.getBlockNumber());

                                    //状态设为成功
                                    transfers.setStatus("0");


                                    //更新本地块高
                                    transfers.setBlockHeighe(new BigInteger(block + ""));

                                    transfers.update();

                                    //获取实时块高
                                    //getBlockNumber(txid);

                                    eqBlokHeight(txid);
                                }

                            }/* else {
                                transfers.setStatus("-1");
                                transfers.update();
                            }*/
                        }/* else {
                            transfers.setStatus("-1");
                            transfers.update();
                        }*/

                    }
                });

    }

    //比对块高

    Disposable blockSubscribe;

    @SuppressLint("CheckResult")
    public void eqBlokHeight(final String txid) {
        //定时2秒调用一次
        blockSubscribe = Flowable.interval(1, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {

                        //Log.e("------>", (aLong++) + "");


                        Long number = SharePreUtil.getLongValue(context, "blockNumber", 0l);


                        TransferInfo transfers = TransferDaoUtils.selectTransfersByHash(txid);


                        BigInteger hxblockNumber = new BigInteger(transfers.getBlockHeighe() + "");


                        long res = number - hxblockNumber.intValue() + 1;

                        if (res >= 12) {
                            transfers.setStatus("1");
                            transfers.setData("12/12");
                            transfers.setBlockHeighe(hxblockNumber);
                            transfers.update();
                            //EasyHttp.cancelSubscription(blockdisposable);
                        } else {
                            if(res<=0) {
                                transfers.setData(0 + "/12");
                                transfers.setStatus("0");
                                transfers.update();
                            }else {
                                transfers.setData(res + "/12");
                                transfers.setStatus("0");
                                transfers.update();
                            }
                        }

                        defList.clear();
                        defList = TransferDaoUtils.selectAllTransfers(dataBean.getTokenSynbol(), walletsAddress);
                        localAdapter.setNewData(defList);
                        recyclerviewLocal.setAdapter(localAdapter);
                        localAdapter.notifyDataSetChanged();

                    }
                });

    }

 /*   private Disposable findTokenbalanceDisposable,getOntBalanceDisposable,
            getTransferAllStatusDisposable,getNeoBalanceDisposable, findAllStatusDisposable,findAllOntStatusDisposable;

    private BaseSubscriber<String> getHashSubscriber,getNeoHashSubscriber,findAllNeoStatusSubscriber;*/

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (blockSubscribe != null && !blockSubscribe.isDisposed()) {
            blockSubscribe.dispose();
        }

        if (findTokenbalanceDisposable != null && !findTokenbalanceDisposable.isDisposed()) {
            findTokenbalanceDisposable.dispose();
        }

        if (getOntBalanceDisposable != null && !getOntBalanceDisposable.isDisposed()) {
            getOntBalanceDisposable.dispose();
        }
        if (getTransferAllStatusDisposable != null && !getTransferAllStatusDisposable.isDisposed()) {
            getTransferAllStatusDisposable.dispose();
        }

        if (getNeoBalanceDisposable != null && !getNeoBalanceDisposable.isDisposed()) {
            getNeoBalanceDisposable.dispose();
        }

        if (findAllStatusDisposable != null && !findAllStatusDisposable.isDisposed()) {
            findAllStatusDisposable.dispose();
        }

        if (findAllOntStatusDisposable != null && !findAllOntStatusDisposable.isDisposed()) {
            findAllOntStatusDisposable.dispose();
        }


        if (getHashSubscriber != null && !getHashSubscriber.isDisposed()) {
            getHashSubscriber.dispose();
        }

        if (getNeoHashSubscriber != null && !getNeoHashSubscriber.isDisposed()) {
            getNeoHashSubscriber.dispose();
        }
        if (findAllNeoStatusSubscriber != null && !findAllNeoStatusSubscriber.isDisposed()) {
            findAllNeoStatusSubscriber.dispose();
        }

    }


    int neoCount = 10; //轮询次数 10次



    /**
     * 获取块高
     */
    @SuppressLint("CheckResult")
    public void getNeoHash(final String txid) {


        getNeoHashSubscriber = Observable.interval(0, 5, TimeUnit.SECONDS).flatMap(new Function<Long, ObservableSource<String>>() {
            @Override
            public ObservableSource<String> apply(@NonNull Long aLong) throws Exception {
                boolean b = txid.startsWith("0x");//true

                String data = "";
                if (b) {
                    data = txid.substring(2);
                }
                //https://api.neoscan.io/api/main_net/v1/get_transaction/" + data
                return EasyHttp.getInstance().setBaseUrl("https://api.neoscan.io")
                        .setRetryCount(3)//网络不好自动重试3次
                        //设置超时重试间隔时间,默认为500ms,不需要可以设置为0
                        .setRetryDelay(500)//每次延时500ms重试
                        //可以全局统一设置超时重试间隔叠加时间,默认为0ms不叠加
                        .setRetryIncreaseDelay(500)//每次延时叠加500ms
                        .get("/api/main_net/v1/get_transaction/" + data)
                        //采用代理
                        .execute(new CallClazzProxy<TestApiResult6<String>, String>(String.class) {
                        });
            }
        }).takeUntil(new Predicate<String>() {
            @Override
            public boolean test(@NonNull String content) throws Exception {
                boolean flag = false;

                NeoTransactionByHashInfo byHashInfo = GsonUtils.decodeJSON(content, NeoTransactionByHashInfo.class);

                long result = byHashInfo.getBlock_height();

                if (result != 0) {
                    //块高

                    if (result != 0) {
                        flag = true;
                    } else {
                        flag = false;
                    }

                } else {
                    neoCount--;
                    if (neoCount == 0) {
                        TransferInfo transfers = TransferDaoUtils.selectTransfersByHash(txid);
                        transfers.setStatus("-1");
                        transfers.update();
                        flag = true;
                    } else {
                        flag = false;
                    }
                    //flag=false;
                }

                return flag;
            }
        }).subscribeWith(new BaseSubscriber<String>() {
            @Override
            public void onError(ApiException e) {
                //ToastHelper.showToast("======"+e.getMessage());

                if (e.getMessage().contains("HTTP 404")) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            getNeoHash(txid);
                        }
                    }, 2000);
                }
            }

            @Override
            public void onNext(@NonNull String content) {

                NeoTransactionByHashInfo byHashInfo = GsonUtils.decodeJSON(content, NeoTransactionByHashInfo.class);


                TransferInfo transfers = TransferDaoUtils.selectTransfersByHash(txid);

                int block_height = byHashInfo.getBlock_height();

                if (block_height != 0) {

                    transfers.setStatus("0");
                    transfers.setBlockHeighe(BigInteger.valueOf(block_height));
                    transfers.update();

                    neoBlockArray.remove(txid);

                    if (transfers.getTokenSynbol().equals("NEO") || transfers.getTokenSynbol().equals("GAS")) {
                        transfers.setStatus("1");
                        transfers.setBlockHeighe(BigInteger.valueOf(block_height));
                        transfers.update();

                        Message msg = new Message();
                        msg.what = 0;
                        handler.sendMessage(msg);

                        return;
                    }

                    neoStatusArray.add(txid);


                    if (neoStatusArray != null && neoStatusArray.size() > 0) {
                        for (int i = 0; i < neoStatusArray.size(); i++) {
                            findAllNeoStatus(neoStatusArray.get(i));
                        }

                    }


                }/* else {


                    if (transfers != null) {
                        transfers.setStatus("-1");
                        transfers.update();
                    }
                }*/
            }
        });


    }


    /**
     *
     * ============================================================================================= 查询NEO交易状态 =========================================================================================================
     */

    /**
     * 查询neo状态
     *
     * @param txid
     */
    @SuppressLint("CheckResult")
    public void findAllNeoStatus(final String txid) {


        boolean b = txid.startsWith("0x");//true

        String data = "";
        if (b) {
            data = txid.substring(2);
        }

        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add(data);
        final RequestBody tokenInfo = SealApi.toRequestBody("getApplicationLog", arrayList);


        findAllNeoStatusSubscriber = Observable.interval(0, 5, TimeUnit.SECONDS).flatMap(new Function<Long, ObservableSource<String>>() {
            @Override
            public ObservableSource<String> apply(@NonNull Long aLong) throws Exception {
                return EasyHttp.getInstance().setBaseUrl("https://neoapi.trinity.ink/").post("/")
                        .requestBody(tokenInfo)
                        //采用代理
                        .execute(new CallClazzProxy<TestApiResult6<String>, String>(String.class) {
                        });
            }
        }).takeUntil(new Predicate<String>() {
            @Override
            public boolean test(@NonNull String content) throws Exception {
                //如果条件满足，就会终止轮询，这里逻辑可以自己写
                //结果为true，说明满足条件了，就不在轮询了

                NeoResultInfo resultInfo = GsonUtils.decodeJSON(content, NeoResultInfo.class);

                Boolean infoResult = resultInfo.isResult();


                if (infoResult != null) {
                    if (infoResult) {
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            }
        }).subscribeWith(new BaseSubscriber<String>() {
            @Override
            public void onError(ApiException e) {
                //showToast(e.getMessage());
            }

            @Override
            public void onNext(@NonNull String response) {
                //showToast(content.toString());

                NeoResultInfo resultInfo = GsonUtils.decodeJSON(response, NeoResultInfo.class);

                Boolean infoResult = resultInfo.isResult();

                TransferInfo transfers = TransferDaoUtils.selectTransfersByHash(txid);

                if (infoResult != null) {
                    if (infoResult) {
                        neoBlockArray.remove(txid);

                        //状态设为成功
                        transfers.setStatus("1");
                        transfers.setData("1/1");
                        transfers.setBlockHeighe(new BigInteger("1"));
                        transfers.update();

                        defList.clear();
                        defList = TransferDaoUtils.selectAllTransfers(dataBean.getTokenSynbol(), walletsAddress);
                        localAdapter.setNewData(defList);
                        recyclerviewLocal.setAdapter(localAdapter);
                        localAdapter.notifyDataSetChanged();
                        return;
                    } else {
                        transfers.setStatus("0");
                        transfers.update();

                        defList = TransferDaoUtils.selectAllTransfers(dataBean.getTokenSynbol(), walletsAddress);
                        localAdapter.setNewData(defList);
                        recyclerviewLocal.setAdapter(localAdapter);
                        localAdapter.notifyDataSetChanged();
                    }
                } else {
                    //状态设为成功
                    transfers.setStatus("0");
                    transfers.setData("0/1");
                    transfers.setBlockHeighe(new BigInteger("1"));
                    transfers.update();

                    defList.clear();
                    defList = TransferDaoUtils.selectAllTransfers(dataBean.getTokenSynbol(), walletsAddress);
                    localAdapter.setNewData(defList);
                    recyclerviewLocal.setAdapter(localAdapter);
                    localAdapter.notifyDataSetChanged();
                }
            }
        });


    }


    /**
     * ============================================================================================= 查询ONT交易详情 =========================================================================================================
     */
    public void findAllOntStatus(final String txid) {
        findAllOntStatusDisposable = Observable
                .empty()
                .observeOn(Schedulers.io())
                .doOnComplete(new Action() {
                    @Override
                    public void run() throws Exception {

                        OntSdk ontSdk = getOntSdk();

                        TransferInfo transfers = TransferDaoUtils.selectTransfersByHash(txid);


                        //查询当前块高
                        int blockHeight = ontSdk.getConnect().getBlockHeight();

                        //得到该交易哈希所落账的区块的高度
                        int blockJson = ontSdk.getConnect().getBlockHeightByTxHash(txid);

                        if (blockJson > 0) {
                            transfers.setBlockHeighe(new BigInteger(blockJson + ""));
                        }


                        //	得到智能合约执行的结果
                        String ss = (String) ontSdk.getConnect().getSmartCodeEvent(txid).toString();

                        if (!TextUtils.isEmpty(ss)) {
                            OntTransferDetailInfo ontTransferDetailInfo = GsonUtils.decodeJSON(ss, OntTransferDetailInfo.class);

                            int state = ontTransferDetailInfo.getState();


                            if (state == 1) {
                                ontStatusArray.remove(txid);

                                //状态设为成功
                                transfers.setData("1/1");
                                transfers.setStatus("1");
                                transfers.setBlockHeighe(new BigInteger(blockJson + ""));
                                transfers.update();

                                Message msg = new Message();
                                msg.what = 0;
                                handler.sendMessage(msg);
                            } else {
                                transfers.setStatus("-1");
                                transfers.update();

                                Message msg = new Message();
                                msg.what = 1;
                                handler.sendMessage(msg);
                            }
                        } else {
                            transfers.setStatus("-1");
                            transfers.update();

                            Message msg = new Message();
                            msg.what = 1;
                            handler.sendMessage(msg);
                        }


                    }
                }).subscribe();


    }


    /**
     * 初始化ONT sdk
     *
     * @return
     */
    public OntSdk getOntSdk() {
        //String ip = "http://polaris1.ont.io"; //测试网
        String ip = "http://dappnode1.ont.io"; //主网
        String rpcUrl = ip + ":" + "20336";
        OntSdk ontSdk = OntSdk.getInstance();
        try {
            ontSdk.setRpc(rpcUrl);
            ontSdk.setDefaultConnect(ontSdk.getRpc());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return ontSdk;
    }


    @Override
    public void onLoadMore(@android.support.annotation.NonNull RefreshLayout refreshLayout) {
        refreshLayout.finishLoadMore(2000);
        pageNo++;
        //getTransferAllStatus(pageNo);
        getAllTransacionInfo(pageNo);
    }

    @Override
    public void onRefresh(@android.support.annotation.NonNull RefreshLayout refreshLayout) {
        refreshLayout.finishRefresh(2000);
        pageNo = 1;
        resultBeans.clear();
        //getTransferAllStatus(pageNo);
        getAllTransacionInfo(pageNo);
        findBalances();

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocalManageUtil.setLocal(newBase));
    }

}
