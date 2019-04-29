package com.raistone.wallet.sealwallet.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chenenyu.router.Router;
import com.example.zhouwei.library.CustomPopWindow;
import com.raistone.wallet.sealwallet.R;
import com.raistone.wallet.sealwallet.WalletApplication;
import com.raistone.wallet.sealwallet.adapter.AssetsAdapter;
import com.raistone.wallet.sealwallet.adapter.WalletAdapter;
import com.raistone.wallet.sealwallet.daoutils.AssetsDetailDaoUtils;
import com.raistone.wallet.sealwallet.daoutils.ChainAddressDaoUtils;
import com.raistone.wallet.sealwallet.daoutils.ChainDaoUtils;
import com.raistone.wallet.sealwallet.datavases.AssetsDaoUtils;
import com.raistone.wallet.sealwallet.datavases.ChainInfoDaoUtils;
import com.raistone.wallet.sealwallet.datavases.WalletInfoDaoUtils;
import com.raistone.wallet.sealwallet.entity.AssetsInfo;
import com.raistone.wallet.sealwallet.entity.ChainInfo;
import com.raistone.wallet.sealwallet.entity.MessageEvent;
import com.raistone.wallet.sealwallet.entity.MultiChainInfo;
import com.raistone.wallet.sealwallet.entity.NeoBalanceInfo;
import com.raistone.wallet.sealwallet.entity.OntBalanceInfo;
import com.raistone.wallet.sealwallet.entity.PriceInfo;
import com.raistone.wallet.sealwallet.entity.WalletInfo;
import com.raistone.wallet.sealwallet.factory.AssetsDeatilInfo;
import com.raistone.wallet.sealwallet.factory.AssetsInfoData;
import com.raistone.wallet.sealwallet.factory.ChainAddressInfo;
import com.raistone.wallet.sealwallet.factory.ChainDataInfo;
import com.raistone.wallet.sealwallet.factory.HdWallet;
import com.raistone.wallet.sealwallet.factory.WalletManager;
import com.raistone.wallet.sealwallet.httputils.HttpUtils;
import com.raistone.wallet.sealwallet.ultraviewpager.UltraViewPager;
import com.raistone.wallet.sealwallet.ultraviewpager.transformer.UltraDepthScaleTransformer;
import com.raistone.wallet.sealwallet.utils.BigDecimalUtils;
import com.raistone.wallet.sealwallet.utils.ChainAddressCreateManager;
import com.raistone.wallet.sealwallet.utils.CommonUtils;
import com.raistone.wallet.sealwallet.utils.GsonUtils;
import com.raistone.wallet.sealwallet.utils.SharePreUtil;
import com.raistone.wallet.sealwallet.utils.ToastHelper;
import com.raistone.wallet.sealwallet.widget.AssetsFitPopupUtil;
import com.raistone.wallet.sealwallet.widget.DensityUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.unistrong.yang.zb_permission.Permission;
import com.unistrong.yang.zb_permission.ZbPermission;
import com.yanzhenjie.recyclerview.swipe.SwipeMenu;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuBridge;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;
import com.zhouyou.http.EasyHttp;
import com.zhouyou.http.callback.SimpleCallBack;
import com.zhouyou.http.exception.ApiException;
import com.zhouyou.http.subsciber.BaseSubscriber;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Function;
import okhttp3.RequestBody;

public class WalletFragment extends BaseFragment implements OnRefreshListener, ViewPager.OnPageChangeListener, BaseQuickAdapter.OnItemClickListener, BaseQuickAdapter.OnItemChildClickListener, TextWatcher {

    @BindView(R.id.qr_code_address)
    ImageView qrCodeAddress;
    @BindView(R.id.wallet_name)
    TextView walletName;
    @BindView(R.id.wallet_ll)
    LinearLayout walletLl;
    @BindView(R.id.scaner_code_iv)
    ImageView scanerCodeIv;
    @BindView(R.id.smartrefreshlayout)
    SmartRefreshLayout smartrefreshlayout;
    Unbinder unbinder;
    @BindView(R.id.viewpager)
    UltraViewPager viewpager;
    @BindView(R.id.assets_tv)
    TextView assetsTv;
    @BindView(R.id.three_icon)
    ImageView threeIcon;
    @BindView(R.id.search_ed)
    EditText searchEd;
    @BindView(R.id.plus_iv)
    ImageView plusIv;
    @BindView(R.id.recyclerview)
    SwipeMenuRecyclerView recyclerview;
    @BindView(R.id.assets_filter_ll)
    LinearLayout assetsFilterLl;
    @BindView(R.id.assets_ll)
    RelativeLayout assetsLl;
    @BindView(R.id.plus_ll)
    LinearLayout plusLl;

    private Context context;
    private LayoutInflater mInflater;
    private Boolean unitFlag;
    private List<WalletInfo> allWallet;
    private WalletInfo walletInfo;
    private String accountId;
    private List<ChainInfo> chainInfos;


    public static BigDecimal cnyPrice = new BigDecimal(0);
    public static BigDecimal usdPrice = new BigDecimal(0);

    private WalletAdapter walletAdapter;

    private AssetsAdapter assetsAdapter;


    private List<AssetsInfo.DataBean> infoData = new ArrayList<>();

    private String chainAddress;

    private static int selectIndex = 0;

    private int changIndex = 0;


    private List<String> parms = new ArrayList<>();

    public List<String> tokenSynbolsByLocal = new ArrayList<>();
    public List<String> tokenSynbols = new ArrayList<>();

    private String walletAddress;


    private final int REQUEST_CAMERA = 200;


    private CustomPopWindow mCustomPopWindow;

    private int findType = 0;
    private int findTypeNeo = 0;
    private int findTypeOnt = 0;

    private boolean hideAss = false;

    private List<ChainAddressInfo> chainAddressInfos = new ArrayList<>();

    List<AssetsDeatilInfo> assetsInfoDataList;

    private List<ChainDataInfo> walletSupportChains;

    HdWallet wallet;

    private ChainDataInfo chainDataInfo;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        unbinder = ButterKnife.bind(this, view);
        context = getActivity();
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        smartrefreshlayout.setOnRefreshListener(this);
        unitFlag = SharePreUtil.getBoolean(context, "CurrencyUnit", true);
        initDataTwo();
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    public void initDataTwo() {
        viewpager.setInfiniteLoop(true);

        viewpager.setMultiScreen(0.8f);
        //viewPager.setItemRatio(1.0f);
        viewpager.setRatio(2.5f);
        viewpager.setPageTransformer(false, new UltraDepthScaleTransformer());

        viewpager.setOffscreenPageLimit(5);

        LinearLayoutManager layoutManager = new LinearLayoutManager(context);

        recyclerview.setLayoutManager(layoutManager);

        recyclerview.setSwipeMenuCreator(swipeMenuCreator);
        recyclerview.setSwipeMenuItemClickListener(mMenuItemClickListener);


        accountId = WalletManager.getInstance().getWalletChains(1).getAccountId();

        walletSupportChains = ChainDaoUtils.findAllChainByAccount(accountId);


        findAddressInfo();


        if (chainAddressInfos != null && chainAddressInfos.size() > 0) {

            walletAdapter = new WalletAdapter(context, chainAddressInfos, unitFlag);

            viewpager.setAdapter(walletAdapter);
        }

        walletName.setText(walletSupportChains.get(selectIndex).getChainName());


        chainDataInfo = walletSupportChains.get(selectIndex);

        chainAddress = chainAddressInfos.get(selectIndex).getAddress();


        assetsInfoDataList = chainAddressInfos.get(selectIndex).getAssetsInfoDataList();


        viewpager.setOnPageChangeListener(this);


        if (infoData != null) {
            assetsAdapter = new AssetsAdapter(assetsInfoDataList);
            assetsAdapter.setOnItemClickListener(this);
            assetsAdapter.setOnItemChildClickListener(this);
            recyclerview.setAdapter(assetsAdapter);
        }


        selectChainAddressInfo = chainAddressInfos.get(selectIndex);

        requestDataTwo(chainAddressInfos.get(selectIndex), chainAddressInfos.get(selectIndex).getAddress());

        searchEd.addTextChangedListener(this);

    }

    public void initData() {

        viewpager.setInfiniteLoop(true);

        viewpager.setMultiScreen(0.8f);
        //viewPager.setItemRatio(1.0f);
        viewpager.setRatio(2.5f);
        viewpager.setPageTransformer(false, new UltraDepthScaleTransformer());

        viewpager.setOffscreenPageLimit(5);


        List<ChainDataInfo> walletSupportChains = WalletManager.getInstance().getWalletSupportChains();


        chainDataInfo = walletSupportChains.get(0);


        LinearLayoutManager layoutManager = new LinearLayoutManager(context);

        recyclerview.setLayoutManager(layoutManager);

        recyclerview.setSwipeMenuCreator(swipeMenuCreator);
        recyclerview.setSwipeMenuItemClickListener(mMenuItemClickListener);


        chainAddressInfos.clear();

        if (walletSupportChains != null && walletSupportChains.size() > 0) {


            viewpager.setAdapter(walletAdapter);
        }

        walletName.setText(walletSupportChains.get(selectIndex).getChainName());

        viewpager.setOnPageChangeListener(this);


        chainAddress = chainAddressInfos.get(0).getAddress();


        infoData = AssetsDaoUtils.findAllAssetsByWalletAddress(chainAddress);


        searchEd.addTextChangedListener(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(final ChainAddressInfo event) {
        final String address = event.getAddress();

        event.resetAssetsInfoDataList();

        chainAddressInfos.set(selectIndex, event);

        viewpager.refresh();


        parms.clear();


        assetsInfoDataList = event.getAssetsInfoDataList();

        assetsAdapter.setNewData(assetsInfoDataList);

        assetsAdapter.notifyDataSetChanged();

        Observable
                .empty()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete(new Action() {
                    @Override
                    public void run() throws Exception {
                        requestDataTwo(event, address);
                    }
                }).subscribe();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(final MultiChainInfo event) {
        final String address = event.getAddress();


        viewpager.refresh();


        parms.clear();


        infoData.clear();

        infoData = AssetsDaoUtils.findAllAssetsByWalletAddress(address);

        assetsAdapter.notifyDataSetChanged();
        Observable
                .empty()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete(new Action() {
                    @Override
                    public void run() throws Exception {
                        //requestData(event, address);
                    }
                }).subscribe();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {

        ChainAddressInfo addressInfo = chainAddressInfos.get(selectIndex);

        WalletApplication.getsInstance().getDaoSession().clear();

        addressInfo.resetAssetsInfoDataList();

        assetsInfoDataList = addressInfo.getAssetsInfoDataList();

        assetsAdapter.setNewData(assetsInfoDataList);
        assetsAdapter.notifyDataSetChanged();


    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onResetMsg(String msg) {


        walletSupportChains.clear();
        selectIndex = 0;
        wallet = WalletManager.getInstance().getWalletChains(1L);

        accountId = wallet.getAccountId();

        walletSupportChains = ChainDaoUtils.findAllChainByAccount(accountId);


        findAddressInfo();
        if (walletSupportChains != null && walletSupportChains.size() > 0) {

            walletAdapter = new WalletAdapter(context, chainAddressInfos, unitFlag);

            viewpager.setAdapter(walletAdapter);

            viewpager.setCurrentItem(0);

            if (chainInfos != null && chainInfos.size() > 0) {
                String chaName = chainInfos.get(selectIndex).getChaName();

                if (!TextUtils.isEmpty(chaName)) {
                    walletName.setText(chaName);
                }
            }
        }

        viewpager.setOffscreenPageLimit(5);
        viewpager.setOnPageChangeListener(this);


        chainAddress = chainAddressInfos.get(0).getAddress();


    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        if (!hidden) {

            Boolean unit = SharePreUtil.getBoolean(context, "CurrencyUnit", true);
            unitFlag = unit;

            HdWallet wallet = WalletManager.getInstance().getWalletChains(1l);

            walletSupportChains = ChainDaoUtils.findAllChainByAccount(wallet.getAccountId());

            chainDataInfo = walletSupportChains.get(selectIndex);

            ChainAddressInfo chainAddressInfo = chainAddressInfos.get(selectIndex);

            assetsInfoDataList = chainAddressInfo.getAssetsInfoDataList();

            assetsAdapter.setNewData(assetsInfoDataList);
            assetsAdapter.notifyDataSetChanged();


            walletAdapter.changUnit(unit);

            walletAdapter.notifyDataSetChanged();

            viewpager.refresh();

            viewpager.getAdapter().notifyDataSetChanged();
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
        unbinder.unbind();
    }

    @OnClick({R.id.qr_code_address, R.id.wallet_ll, R.id.scaner_code_iv, R.id.plus_ll, R.id.assets_filter_ll})
    public void onViewClicked(View view) {
        switch (view.getId()) {

            case R.id.qr_code_address:


                if (chainAddressInfos != null && chainAddressInfos.size() > 0) {
                    ChainAddressInfo chainAddressInfo = chainAddressInfos.get(selectIndex);
                    //Router.build("ReceiptActivity").with("ethWallet", wallet).go(this);

                    String contractAddress = "";
                    if (chainAddressInfo.getCoinType().equals(ChainAddressCreateManager.ETH_COIN_TYPE)) {
                        contractAddress = "0x0000000000000000000000000000000000000000";
                    }
                    if (chainAddressInfo.getCoinType().equals(ChainAddressCreateManager.NEO_COIN_TYPE)) {
                        contractAddress = "ox";
                    }
                    if (chainAddressInfo.getCoinType().equals(ChainAddressCreateManager.ETH_COIN_TYPE)) {
                        contractAddress = "00";
                    }
                    Router.build("ReceiptActivity").with("chainAddressInfo", chainAddressInfo).go(this);
                }


                break;
            case R.id.wallet_ll:


                chainDataInfo = walletSupportChains.get(selectIndex);

                if (chainDataInfo != null) {
                    Router.build("AddressManageActivity").with("chainDataInfo", chainDataInfo).with("accountId", accountId).with("selectIndex", selectIndex).requestCode(1).go(this);
                }


                break;

            case R.id.scaner_code_iv:
                ZbPermission.needPermission(getActivity(), REQUEST_CAMERA, Permission.CAMERA, new ZbPermission.ZbPermissionCallback() {
                    @Override
                    public void permissionSuccess(int i) {
                        Router.build("ScannerActivity").with("coinType", chainDataInfo.getChainType()).with("accountId", chainDataInfo.getAccountId()).requestCode(0).go(context);
                    }

                    @Override
                    public void permissionFail(int i) {
                        ToastHelper.showToast(getResources().getString(R.string.photo_permission));
                    }
                });

                break;

            case R.id.plus_ll:

                ChainAddressInfo chainAddressInfo = chainAddressInfos.get(selectIndex);

                Router.build("AddAssetsActivity").with("ethWallet", chainAddressInfo).requestCode(200).go(context);
                break;

            case R.id.assets_filter_ll:

                String chainType = chainDataInfo.getChainType();

                if (chainType.equals(ChainAddressCreateManager.ETH_COIN_TYPE)) {

                    View contentView = LayoutInflater.from(context).inflate(R.layout.assets_filter_layout, null);
                    handleLogic(contentView);

                    mCustomPopWindow = new CustomPopWindow.PopupWindowBuilder(context)
                            .setView(contentView)
                            .enableBackgroundDark(true)
                            .setClippingEnable(true)
                            .setBgDarkAlpha(0.7f)
                            .create()
                            .showAsDropDown(assetsFilterLl, DensityUtils.dp2px(10), 0);
                }

                if (chainType.equals(ChainAddressCreateManager.NEO_COIN_TYPE)) {

                    View contentView = LayoutInflater.from(context).inflate(R.layout.assets_filter_neo_layout, null);
                    handleLogicByNeo(contentView);

                    mCustomPopWindow = new CustomPopWindow.PopupWindowBuilder(context)
                            .setView(contentView)
                            .enableBackgroundDark(true)
                            .setClippingEnable(true)
                            .setBgDarkAlpha(0.7f)
                            .create()
                            .showAsDropDown(assetsFilterLl, DensityUtils.dp2px(10), 0);
                }

                if (chainType.equals(ChainAddressCreateManager.ONT_COIN_TYPE)) {

                    View contentView = LayoutInflater.from(context).inflate(R.layout.assets_filter_ont_layout, null);
                    handleLogicByOnt(contentView);

                    mCustomPopWindow = new CustomPopWindow.PopupWindowBuilder(context)
                            .setView(contentView)
                            .enableBackgroundDark(true)
                            .setClippingEnable(true)
                            .setBgDarkAlpha(0.7f)
                            .create()
                            .showAsDropDown(assetsFilterLl, DensityUtils.dp2px(10), 0);
                }


                break;

        }
    }

    private void handleLogic(View contentView) {
        LinearLayout all_ll = contentView.findViewById(R.id.all_ll);
        LinearLayout erc_20_ll = contentView.findViewById(R.id.erc_20_ll);
        LinearLayout erc_721_ll = contentView.findViewById(R.id.erc_721_ll);
        LinearLayout hide_zero_assets_ll = contentView.findViewById(R.id.hide_zero_assets_ll);

        final TextView all_tv = contentView.findViewById(R.id.all_tv);
        final TextView erc_20_tv = contentView.findViewById(R.id.erc_20_tv);
        final TextView erc_721_tv = contentView.findViewById(R.id.erc_721_tv);

        if (findType == 0) {
            all_tv.setTextColor(getResources().getColor(R.color.main_color));
            erc_20_tv.setTextColor(getResources().getColor(R.color.three_color));
            erc_721_tv.setTextColor(getResources().getColor(R.color.three_color));
        }

        if (findType == 1) {
            all_tv.setTextColor(getResources().getColor(R.color.three_color));
            erc_20_tv.setTextColor(getResources().getColor(R.color.main_color));
            erc_721_tv.setTextColor(getResources().getColor(R.color.three_color));
        }
        if (findType == 2) {
            all_tv.setTextColor(getResources().getColor(R.color.three_color));
            erc_20_tv.setTextColor(getResources().getColor(R.color.three_color));
            erc_721_tv.setTextColor(getResources().getColor(R.color.main_color));
        }


        final ImageButton imbutton = contentView.findViewById(R.id.hide_zero_assets_imbutton);

        if (hideAss) {
            imbutton.setBackgroundResource(R.drawable.open_icon);
        } else {
            imbutton.setBackgroundResource(R.drawable.close_icon);
        }


        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {

                    case R.id.all_ll:


                        if (mCustomPopWindow != null) {
                            mCustomPopWindow.dissmiss();
                        }
                        findType = 0;


                        assetsInfoDataList = AssetsDetailDaoUtils.filterData(findType, hideAss, walletAddress);

                        assetsAdapter.setNewData(assetsInfoDataList);

                        assetsAdapter.notifyDataSetChanged();

                        break;
                    case R.id.erc_20_ll:
                        if (mCustomPopWindow != null) {
                            mCustomPopWindow.dissmiss();
                        }
                        findType = 1;
                        if (hideAss) {
                            assetsInfoDataList = AssetsDetailDaoUtils.filterData(findType, true, walletAddress);
                        } else {
                            assetsInfoDataList = AssetsDetailDaoUtils.filterData(findType, false, walletAddress);
                        }

                        assetsAdapter.setNewData(assetsInfoDataList);
                        assetsAdapter.notifyDataSetChanged();

                        break;
                    case R.id.erc_721_ll:
                        findType = 2;
                        if (mCustomPopWindow != null) {
                            mCustomPopWindow.dissmiss();
                        }
                        if (hideAss) {
                            assetsInfoDataList = AssetsDetailDaoUtils.filterData(findType, true, walletAddress);
                        } else {
                            assetsInfoDataList = AssetsDetailDaoUtils.filterData(findType, false, walletAddress);
                        }

                        assetsAdapter.setNewData(assetsInfoDataList);
                        assetsAdapter.notifyDataSetChanged();
                        break;
                    case R.id.hide_zero_assets_ll:

                        if (mCustomPopWindow != null) {
                            mCustomPopWindow.dissmiss();
                        }

                        if (hideAss) {
                            hideAss = false;
                            imbutton.setBackgroundResource(R.drawable.close_icon);
                        } else {
                            hideAss = true;
                            imbutton.setBackgroundResource(R.drawable.open_icon);
                        }
                        assetsInfoDataList = AssetsDetailDaoUtils.filterData(findType, hideAss, walletAddress);
                        assetsAdapter.setNewData(assetsInfoDataList);
                        assetsAdapter.notifyDataSetChanged();
                        break;
                }


            }
        };
        all_ll.setOnClickListener(listener);
        erc_20_ll.setOnClickListener(listener);
        erc_721_ll.setOnClickListener(listener);
        hide_zero_assets_ll.setOnClickListener(listener);
    }


    private void handleLogicByNeo(View contentView) {
        LinearLayout all_ll = contentView.findViewById(R.id.all_ll);
        LinearLayout neo_ll = contentView.findViewById(R.id.neo_ll);
        LinearLayout gas_ll = contentView.findViewById(R.id.gas_ll);
        LinearLayout nep5_ll = contentView.findViewById(R.id.nep5_ll);
        LinearLayout hide_zero_assets_ll = contentView.findViewById(R.id.hide_zero_assets_ll);

        final TextView all_tv = contentView.findViewById(R.id.all_tv);
        final TextView neo_tv = contentView.findViewById(R.id.neo_tv);
        final TextView gas_tv = contentView.findViewById(R.id.gas_tv);
        final TextView nep5_tv = contentView.findViewById(R.id.nep5_tv);

        if (findTypeNeo == 0) {
            all_tv.setTextColor(getResources().getColor(R.color.main_color));
            neo_tv.setTextColor(getResources().getColor(R.color.three_color));
            gas_tv.setTextColor(getResources().getColor(R.color.three_color));
            nep5_tv.setTextColor(getResources().getColor(R.color.three_color));
        }

        if (findTypeNeo == 1) {
            all_tv.setTextColor(getResources().getColor(R.color.three_color));
            neo_tv.setTextColor(getResources().getColor(R.color.main_color));
            gas_tv.setTextColor(getResources().getColor(R.color.three_color));
            nep5_tv.setTextColor(getResources().getColor(R.color.three_color));
        }
        if (findTypeNeo == 2) {
            all_tv.setTextColor(getResources().getColor(R.color.three_color));
            neo_tv.setTextColor(getResources().getColor(R.color.three_color));
            gas_tv.setTextColor(getResources().getColor(R.color.main_color));
            nep5_tv.setTextColor(getResources().getColor(R.color.three_color));
        }

        if (findTypeNeo == 3) {
            all_tv.setTextColor(getResources().getColor(R.color.three_color));
            neo_tv.setTextColor(getResources().getColor(R.color.three_color));
            gas_tv.setTextColor(getResources().getColor(R.color.three_color));
            nep5_tv.setTextColor(getResources().getColor(R.color.main_color));
        }

        final ImageButton imbutton = contentView.findViewById(R.id.hide_zero_assets_imbutton);

        if (hideAss) {
            imbutton.setBackgroundResource(R.drawable.open_icon);
        } else {
            imbutton.setBackgroundResource(R.drawable.close_icon);
        }


        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {

                    case R.id.all_ll:


                        if (mCustomPopWindow != null) {
                            mCustomPopWindow.dissmiss();
                        }
                        findTypeNeo = 0;

                        assetsInfoDataList = AssetsDetailDaoUtils.filterDataNeo(findTypeNeo, hideAss, walletAddress);

                        assetsAdapter.setNewData(assetsInfoDataList);
                        assetsAdapter.notifyDataSetChanged();

                        break;
                    case R.id.neo_ll:
                        if (mCustomPopWindow != null) {
                            mCustomPopWindow.dissmiss();
                        }
                        findTypeNeo = 1;
                        if (hideAss) {
                            assetsInfoDataList = AssetsDetailDaoUtils.filterDataNeo(findTypeNeo, true, walletAddress);
                        } else {
                            assetsInfoDataList = AssetsDetailDaoUtils.filterDataNeo(findTypeNeo, false, walletAddress);
                        }

                        assetsAdapter.setNewData(assetsInfoDataList);
                        assetsAdapter.notifyDataSetChanged();

                        break;
                    case R.id.gas_ll:
                        findTypeNeo = 2;
                        if (mCustomPopWindow != null) {
                            mCustomPopWindow.dissmiss();
                        }
                        if (hideAss) {
                            assetsInfoDataList = AssetsDetailDaoUtils.filterDataNeo(findTypeNeo, true, walletAddress);
                        } else {
                            assetsInfoDataList = AssetsDetailDaoUtils.filterDataNeo(findTypeNeo, false, walletAddress);
                        }

                        assetsAdapter.setNewData(assetsInfoDataList);
                        assetsAdapter.notifyDataSetChanged();
                        break;
                    case R.id.nep5_ll:
                        findTypeNeo = 3;
                        if (mCustomPopWindow != null) {
                            mCustomPopWindow.dissmiss();
                        }
                        if (hideAss) {
                            assetsInfoDataList = AssetsDetailDaoUtils.filterDataNeo(findTypeNeo, true, walletAddress);
                        } else {
                            assetsInfoDataList = AssetsDetailDaoUtils.filterDataNeo(findTypeNeo, false, walletAddress);
                        }

                        assetsAdapter.setNewData(assetsInfoDataList);
                        assetsAdapter.notifyDataSetChanged();
                        break;
                    case R.id.hide_zero_assets_ll:

                        if (mCustomPopWindow != null) {
                            mCustomPopWindow.dissmiss();
                        }

                        if (hideAss) {
                            hideAss = false;
                            imbutton.setBackgroundResource(R.drawable.close_icon);
                        } else {
                            hideAss = true;
                            imbutton.setBackgroundResource(R.drawable.open_icon);
                        }
                        assetsInfoDataList = AssetsDetailDaoUtils.filterDataNeo(findTypeNeo, hideAss, walletAddress);
                        assetsAdapter.setNewData(assetsInfoDataList);
                        assetsAdapter.notifyDataSetChanged();
                        break;
                }


            }
        };
        all_ll.setOnClickListener(listener);
        neo_ll.setOnClickListener(listener);
        gas_ll.setOnClickListener(listener);
        nep5_ll.setOnClickListener(listener);
        hide_zero_assets_ll.setOnClickListener(listener);
    }


    private void handleLogicByOnt(View contentView) {
        LinearLayout all_ll = contentView.findViewById(R.id.all_ll);
        LinearLayout ont_ll = contentView.findViewById(R.id.ont_ll);
        LinearLayout ong_ll = contentView.findViewById(R.id.ong_ll);
        LinearLayout hide_zero_assets_ll = contentView.findViewById(R.id.hide_zero_assets_ll);

        final TextView all_tv = contentView.findViewById(R.id.all_tv);
        final TextView ont_tv = contentView.findViewById(R.id.ont_tv);
        final TextView ong_tv = contentView.findViewById(R.id.ong_tv);

        if (findTypeOnt == 0) {
            all_tv.setTextColor(getResources().getColor(R.color.main_color));
            ont_tv.setTextColor(getResources().getColor(R.color.three_color));
            ong_tv.setTextColor(getResources().getColor(R.color.three_color));
        }

        if (findTypeOnt == 1) {
            all_tv.setTextColor(getResources().getColor(R.color.three_color));
            ont_tv.setTextColor(getResources().getColor(R.color.main_color));
            ong_tv.setTextColor(getResources().getColor(R.color.three_color));
        }

        if (findTypeOnt == 2) {
            all_tv.setTextColor(getResources().getColor(R.color.three_color));
            ont_tv.setTextColor(getResources().getColor(R.color.three_color));
            ong_tv.setTextColor(getResources().getColor(R.color.main_color));
        }


        final ImageButton imbutton = contentView.findViewById(R.id.hide_zero_assets_imbutton);

        if (hideAss) {
            imbutton.setBackgroundResource(R.drawable.open_icon);
        } else {
            imbutton.setBackgroundResource(R.drawable.close_icon);
        }


        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {

                    case R.id.all_ll:


                        if (mCustomPopWindow != null) {
                            mCustomPopWindow.dissmiss();
                        }
                        findTypeOnt = 0;

                        assetsInfoDataList = AssetsDetailDaoUtils.filterDataOnt(findTypeOnt, hideAss, walletAddress);

                        assetsAdapter.setNewData(assetsInfoDataList);
                        assetsAdapter.notifyDataSetChanged();

                        //filterSetData(findType);
                        break;
                    case R.id.ont_ll:
                        if (mCustomPopWindow != null) {
                            mCustomPopWindow.dissmiss();
                        }
                        findTypeOnt = 1;
                        if (hideAss) {
                            assetsInfoDataList = AssetsDetailDaoUtils.filterDataOnt(findTypeOnt, true, walletAddress);
                        } else {
                            assetsInfoDataList = AssetsDetailDaoUtils.filterDataOnt(findTypeOnt, false, walletAddress);
                        }

                        assetsAdapter.setNewData(assetsInfoDataList);
                        assetsAdapter.notifyDataSetChanged();

                        break;
                    case R.id.ong_ll:
                        findTypeOnt = 2;
                        if (mCustomPopWindow != null) {
                            mCustomPopWindow.dissmiss();
                        }
                        if (hideAss) {
                            assetsInfoDataList = AssetsDetailDaoUtils.filterDataOnt(findTypeOnt, true, walletAddress);
                        } else {
                            assetsInfoDataList = AssetsDetailDaoUtils.filterDataOnt(findTypeOnt, false, walletAddress);
                        }

                        assetsAdapter.setNewData(assetsInfoDataList);
                        assetsAdapter.notifyDataSetChanged();
                        break;
                    case R.id.hide_zero_assets_ll:

                        if (mCustomPopWindow != null) {
                            mCustomPopWindow.dissmiss();
                        }

                        if (hideAss) {
                            hideAss = false;
                            imbutton.setBackgroundResource(R.drawable.close_icon);
                        } else {
                            hideAss = true;
                            imbutton.setBackgroundResource(R.drawable.open_icon);
                        }
                        assetsInfoDataList = AssetsDetailDaoUtils.filterDataOnt(findTypeOnt, hideAss, walletAddress);
                        assetsAdapter.setNewData(assetsInfoDataList);
                        assetsAdapter.notifyDataSetChanged();
                        break;
                }


            }
        };
        all_ll.setOnClickListener(listener);
        ont_ll.setOnClickListener(listener);
        ong_ll.setOnClickListener(listener);
        hide_zero_assets_ll.setOnClickListener(listener);
    }

    private SwipeMenuCreator swipeMenuCreator = new SwipeMenuCreator() {
        @Override
        public void onCreateMenu(SwipeMenu swipeLeftMenu, SwipeMenu swipeRightMenu, int viewType) {
            int width = getResources().getDimensionPixelSize(R.dimen.common_dp_70);

            int height = ViewGroup.LayoutParams.MATCH_PARENT;


            {
                SwipeMenuItem deleteItem = new SwipeMenuItem(context)
                        .setBackground(R.drawable.selector_red)
                        .setImage(R.mipmap.ic_action_delete)
                        .setText(getResources().getString(R.string.delete_string))
                        .setTextColor(Color.WHITE)
                        .setWidth(width)
                        .setHeight(height);
                swipeRightMenu.addMenuItem(deleteItem);
            }
        }
    };

    private SwipeMenuItemClickListener mMenuItemClickListener = new SwipeMenuItemClickListener() {
        @Override
        public void onItemClick(SwipeMenuBridge menuBridge) {
            menuBridge.closeMenu();

            int direction = menuBridge.getDirection();
            int adapterPosition = menuBridge.getAdapterPosition();
            int menuPosition = menuBridge.getPosition();

            AssetsDeatilInfo deatilInfo = assetsInfoDataList.get(adapterPosition);

            if (deatilInfo.getTokenSynbol().equals(ChainAddressCreateManager.ETH_COIN_TYPE) || deatilInfo.getTokenSynbol().equals("ONT")
                    || deatilInfo.getTokenSynbol().equals("ONG") || deatilInfo.getTokenSynbol().equals("NEO") || deatilInfo.getTokenSynbol().equals("GAS")
                    ) {
                ToastHelper.showToast(getResources().getString(R.string.cant_delete));
                return;
            }

            BigDecimal price = new BigDecimal(deatilInfo.getPrice());

            BigDecimal priceUSD = new BigDecimal(deatilInfo.getPriceUSD());


            cnyPrice = BigDecimalUtils.sub(cnyPrice + "", price + "");

            usdPrice = BigDecimalUtils.sub(usdPrice + "", priceUSD + "");

            assetsInfoDataList.remove(deatilInfo);

            AssetsDetailDaoUtils.deleteAssetsByAddress(deatilInfo);

            assetsAdapter.notifyDataSetChanged();

            setWalletMoney();

        }
    };

    public void requestDataTwo(final ChainAddressInfo chainInfo, final String multAddress) {

        walletAddress = chainInfo.getAddress();

        String coinType = chainInfo.getCoinType();

        parms.clear();
        tokenSynbols.clear();
        tokenSynbolsByLocal.clear();

        chainInfo.resetAssetsInfoDataList();
        if (coinType.equals(ChainAddressCreateManager.ETH_COIN_TYPE)) {
            walletAddress = chainInfo.getAddress();

            getTokenHolding(chainInfo, multAddress);


        } else if (coinType.equals(ChainAddressCreateManager.NEO_COIN_TYPE)) {
            walletAddress = chainInfo.getAddress();

            assetsAdapter.notifyDataSetChanged();


            getNeoAssetsByAddress(chainInfo, walletAddress);

        } else if (coinType.equals(ChainAddressCreateManager.ONT_COIN_TYPE)) {
            walletAddress = chainInfo.getAddress();

            assetsInfoDataList = chainInfo.getAssetsInfoDataList();

            assetsAdapter.notifyDataSetChanged();

            for (int i = 0; i < assetsInfoDataList.size(); i++) {
                AssetsDeatilInfo bean = assetsInfoDataList.get(i);

                parms.add(bean.getTokenSynbol());
            }
            getOntBalance(chainInfo.getAddress());
            getPrice_2(chainInfo, multAddress, parms);
        }

    }

    public void getTokenHolding(final ChainAddressInfo chainInfo, final String address) {

        final String[] params = new String[1];
        params[0] = address;
        RequestBody body = HttpUtils.toRequestBody("getTokenHolding", params);

        EasyHttp.getInstance().setBaseUrl("").post("/")
                .requestBody(body)
                .execute(new SimpleCallBack<String>() {
                    @Override
                    public void onError(ApiException e) {
                    }

                    @Override
                    public void onSuccess(String response) {
                        //showToast(response);

                        AssetsInfoData assetsInfo = GsonUtils.decodeJSON(response, AssetsInfoData.class);


                        if (assetsInfo != null) {
                            List<AssetsDeatilInfo> result = assetsInfo.getResult();

                            if (result != null && result.size() > 0) {

                                for (AssetsDeatilInfo b : result) {
                                    parms.add(b.getTokenSynbol());
                                }

                                if (result != null && result.size() > 0) {
                                    equstListDataTwo(chainInfo, result, address, ChainAddressCreateManager.ETH_COIN_TYPE);
                                }
                                getPrice_2(chainInfo, address, parms);
                            } else {
                                for (AssetsInfo.DataBean b : infoData) {
                                    parms.add(b.getTokenSynbol());
                                }
                                getPrice_2(chainInfo, address, parms);
                            }
                        }
                    }
                });

    }

    public void getNeoAssetsByAddress(final ChainAddressInfo chainAddressInfo, final String address) {
        List<String> params = new ArrayList();
        params.add(address);

        RequestBody body = HttpUtils.toRequestBody("getTokenHolding", params);

        EasyHttp.getInstance().setBaseUrl("").post("/")
                .requestBody(body)
                .execute(new SimpleCallBack<String>() {
                    @Override
                    public void onError(ApiException e) {
                    }

                    @Override
                    public void onSuccess(String response) {
                        parms.clear();

                        AssetsInfoData assetsInfo = GsonUtils.decodeJSON(response, AssetsInfoData.class);


                        if (assetsInfo != null) {
                            List<AssetsDeatilInfo> result = assetsInfo.getResult();

                            if (result != null && result.size() > 0) {

                                for (AssetsDeatilInfo b : result) {
                                    parms.add(b.getTokenSynbol());
                                }

                                if (result != null && result.size() > 0) {
                                    equstListDataTwo(chainAddressInfo, result, address, ChainAddressCreateManager.NEO_COIN_TYPE);
                                }
                                getPrice_2(chainAddressInfo, address, parms);
                            } else {
                                for (AssetsDeatilInfo b : assetsInfoDataList) {
                                    parms.add(b.getTokenSynbol());
                                }
                                getPrice_2(chainAddressInfo, address, parms);
                            }
                        }

                    }
                });
    }

    public void getOntBalance(final String multiAddress) {

        final ChainAddressInfo addressInfo = chainAddressInfos.get(selectIndex);

        List<String> params = new ArrayList();
        params.add(multiAddress);

        RequestBody body = HttpUtils.toRequestBody("getbalance", params);

        EasyHttp.getInstance().setBaseUrl("").post("/")
                .requestBody(body)
                .execute(new SimpleCallBack<String>() {
                    @Override
                    public void onError(ApiException e) {
                    }

                    @Override
                    public void onSuccess(String response) {
                        //showToast(response);
                        OntBalanceInfo ontBalanceInfo = GsonUtils.decodeJSON(response, OntBalanceInfo.class);

                        OntBalanceInfo.ResultBean result = ontBalanceInfo.getResult();

                        if (result != null) {
                            String ont = result.getOnt();

                            String ong = result.getOng();

                            List<AssetsDeatilInfo> deatilInfos = addressInfo.getAssetsInfoDataList();

                            if (deatilInfos != null && deatilInfos.size() > 0) {
                                for (AssetsDeatilInfo info : deatilInfos) {
                                    if (info.getTokenSynbol().toLowerCase().equals("ont")) {
                                        info.setBalance(ont);
                                        AssetsDetailDaoUtils.updatePrice(info);
                                        continue;
                                    }
                                    if (info.getTokenSynbol().toLowerCase().equals("ong")) {
                                        info.setBalance(ong);
                                        AssetsDetailDaoUtils.updatePrice(info);
                                        continue;
                                    }
                                }
                            }
                        }

                    }
                });

    }

    public void getPrice_2(final ChainAddressInfo chainAddressInfo, final String address, List<String> tokenSynbos) {

        String[] params = tokenSynbos.toArray(new String[tokenSynbos.size()]);
        String[][] newStrings = new String[1][];
        newStrings[0] = params;

        final RequestBody body = HttpUtils.toRequestBody("getPrice_2", newStrings);

        Observable.timer(0, TimeUnit.SECONDS).flatMap(new Function<Long, ObservableSource<String>>() {
            @Override
            public ObservableSource<String> apply(@io.reactivex.annotations.NonNull Long aLong) throws Exception {
                return EasyHttp.getInstance().setBaseUrl("").post("/").requestBody(body)
                        .timeStamp(true)
                        .execute(String.class);
            }
        }).subscribe(new BaseSubscriber<String>() {
            @Override
            protected void onStart() {

            }

            @Override
            public void onError(ApiException e) {
                //showToast(e.getMessage());
            }

            @Override
            public void onNext(@io.reactivex.annotations.NonNull String skinTestResult) {

                final PriceInfo priceInfo = GsonUtils.decodeJSON(skinTestResult, PriceInfo.class);
                Observable
                        .empty()
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnComplete(new Action() {
                            @Override
                            public void run() throws Exception {
                                updatePriceTwo(chainAddressInfo, priceInfo, address);
                            }
                        }).subscribe();

            }
        });

    }

    public void equstListDataTwo(ChainAddressInfo chainInfo, List<AssetsDeatilInfo> dataInfos, String walletAddes, String coinType) {

        tokenSynbolsByLocal.clear();
        tokenSynbols.clear();
        assetsInfoDataList = chainInfo.getAssetsInfoDataList();

        for (int i = 0; i < dataInfos.size(); i++) {

            AssetsDeatilInfo bean = dataInfos.get(i);
            bean.setCoinType(coinType);

            String synbol = bean.getTokenAddress();


            boolean flag = false;
            AssetsDeatilInfo dataBean = null;
            for (int j = 0; j < assetsInfoDataList.size(); j++) {
                dataBean = assetsInfoDataList.get(j);
                if (dataBean.getTokenAddress().equals(synbol)) {
                    flag = true;
                    break;
                } else {
                    flag = false;
                }
            }
            if (flag) {
                dataBean.setBalance(bean.getBalance());
                dataBean.setCoinType(coinType);
                AssetsDetailDaoUtils.updatePrice(dataBean);
            } else {
                bean.setChainAddressId(chainInfo.getId());
                bean.setWalletAddress(walletAddes);
                AssetsDetailDaoUtils.insertNewAssets(bean);
                chainInfo.resetAssetsInfoDataList();
            }
            tokenSynbolsByLocal.add(bean.getTokenSynbol());
        }

    }

    public void updatePriceTwo(ChainAddressInfo chainAddressInfo, PriceInfo info, String walletAddress) {

        List<PriceInfo.ResultBean> result = info.getResult();

        assetsInfoDataList = chainAddressInfo.getAssetsInfoDataList();

        cnyPrice = BigDecimal.ZERO;
        usdPrice = BigDecimal.ZERO;

        for (int i = 0; i < result.size(); i++) {
            PriceInfo.ResultBean resultBean = result.get(i);

            String synbol = resultBean.getSymbol();

            boolean flag = false;

            AssetsDeatilInfo dataBean = null;

            for (int j = 0; j < assetsInfoDataList.size(); j++) {
                dataBean = assetsInfoDataList.get(j);
                if (dataBean.getTokenSynbol().equals(synbol)) {
                    flag = true;
                    break;
                } else {
                    flag = false;
                }

            }

            String balance = dataBean.getBalance();

            String price = resultBean.getCny_price().toPlainString();

            String priceUSD = resultBean.getUsd_price().toPlainString();

            dataBean.setPriceFlag("0");
            dataBean.setPriceUsdFlag("0");

            AssetsDetailDaoUtils.updatePrice(dataBean);

            if (flag) {

                String decimal1 = dataBean.getTokenDecimal();

                if (!TextUtils.isEmpty(decimal1)) {

                    BigDecimal value1 = new BigDecimal(balance.toString());

                    double value2 = Math.pow(10, Double.parseDouble(decimal1));

                    dataBean.setPriceFlag(price);

                    dataBean.setPriceUsdFlag(priceUSD);

                    BigDecimal decimal = BigDecimalUtils.div(value1.toString(), value2 + "");


                    BigDecimal priceValue = BigDecimalUtils.mul(decimal + "", price.toString());

                    BigDecimal usdPriceValue = BigDecimalUtils.mul(decimal + "", priceUSD.toString());

                    dataBean.setPrice(BigDecimalUtils.intercept(priceValue + "", 2).toPlainString());

                    dataBean.setPriceUSD(BigDecimalUtils.intercept(usdPriceValue + "", 2).toPlainString());


                    AssetsDetailDaoUtils.updatePrice(dataBean);

                    cnyPrice = cnyPrice.add(BigDecimalUtils.intercept(priceValue + "", 2));

                    usdPrice = usdPrice.add(BigDecimalUtils.intercept(usdPriceValue + "", 2));


                    AssetsDetailDaoUtils.updatePrice(dataBean);
                }

            }

        }

        chainAddressInfo.setCnyTotalPrice(cnyPrice.toPlainString());
        chainAddressInfo.setUsdtTotalPrice(usdPrice.toPlainString());
        chainAddressInfo.update();


        chainAddressInfo.resetAssetsInfoDataList();


        if (selectChainAddressInfo != null) {

            if (selectChainAddressInfo.getCoinType().equals(chainAddressInfo.getCoinType())) {


                assetsInfoDataList = chainAddressInfo.getAssetsInfoDataList();
                assetsAdapter.setNewData(assetsInfoDataList);
                assetsAdapter.notifyDataSetChanged();

                setWalletMoney();
            } else {
            }
        }
    }


    private void setWalletMoney() {


        viewpager.refresh();
    }


    public void findAddressInfo() {

        chainAddressInfos.clear();

        for (int i = 0; i < walletSupportChains.size(); i++) {
            ChainDataInfo chainDataInfo = walletSupportChains.get(i);

            if (chainDataInfo.getIsShow()) {
                ChainAddressInfo chainAddressInfo = ChainAddressDaoUtils.loadByTypeInAccount(chainDataInfo.getChainType(), chainDataInfo.getAccountId());

                chainAddressInfos.add(chainAddressInfo);
            }

        }
        viewpager.setAdapter(walletAdapter);


    }

    public List<AssetsDeatilInfo> getFilterData(List<AssetsDeatilInfo> srcData, boolean isHide, String coninType) {

        List<AssetsDeatilInfo> newsDatas = new ArrayList<>();

        if (isHide) {
            for (int i = 0; i < srcData.size(); i++) {
                if (!srcData.get(i).getBalance().equals(BigDecimal.ZERO)) {
                    newsDatas.add(srcData.get(i));
                }
            }
        } else {
            newsDatas.addAll(srcData);
        }

        return newsDatas;
    }


    @Override
    public void onPageScrolled(int i, float v, int i1) {

    }
    //private long exitTime = 0;

    public static final int MIN_CLICK_DELAY_TIME = 3000;
    private long lastClickTime = 0;

    ChainAddressInfo addressInfo;

    //String address;
    @Override
    public void onPageSelected(int i) {


        i = i % chainAddressInfos.size();

        if (selectIndex != i) {

            findType = 0;
            findTypeNeo = 0;
            findTypeOnt = 0;
            selectIndex = i;

            chainDataInfo = walletSupportChains.get(selectIndex);

            if (chainDataInfo != null) {

                if (chainAddressInfos != null && chainAddressInfos.size() > 0) {
                    addressInfo = chainAddressInfos.get(i);

                    if (addressInfo != null) {

                        walletAddress = addressInfo.getAddress();

                        assetsInfoDataList = chainAddressInfos.get(selectIndex).getAssetsInfoDataList();
                        if (assetsInfoDataList != null) {


                            assetsAdapter.setNewData(assetsInfoDataList);
                            assetsAdapter.notifyDataSetChanged();

                        }

                        walletName.setText(addressInfo.getCoinType());
                    }
                }
            }
        }
    }


    private ChainAddressInfo selectChainAddressInfo;

    @Override
    public void onPageScrollStateChanged(int state) {
        long currentTime = Calendar.getInstance().getTimeInMillis();
        switch (state) {
            case ViewPager.SCROLL_STATE_DRAGGING:


                break;

            case ViewPager.SCROLL_STATE_SETTLING:
                break;

            case ViewPager.SCROLL_STATE_IDLE:
                selectChainAddressInfo = chainAddressInfos.get(selectIndex);

                if (addressInfo != null) {
                    if (!TextUtils.isEmpty(addressInfo.getAddress())) {
                        requestDataTwo(addressInfo, addressInfo.getAddress());
                    }
                }

                break;
        }


    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
        String like = charSequence.toString().trim();

        List<AssetsDeatilInfo> beans = AssetsDetailDaoUtils.likeFind(like, walletAddress);

        if (beans != null && beans.size() > 0) {

            final List<AssetsDeatilInfo> filterData = getFilterData(beans, hideAss, "All");

            assetsAdapter = new AssetsAdapter(filterData);

            assetsAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                }
            });
            assetsAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
                @Override
                public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                    showPopWindows(filterData, view, position);
                }
            });
            recyclerview.setAdapter(assetsAdapter);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }


    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        gotoDetail(assetsInfoDataList, position);
    }

    private void gotoDetail(List<AssetsDeatilInfo> infoDatas, int position) {
        AssetsDeatilInfo bean = infoDatas.get(position);

        ChainAddressInfo wallet = chainAddressInfos.get(selectIndex);

        Router.build("WalletItemDetailActivity").with("ethWallet", wallet).with("assetsInfo", bean).go(context);
    }

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        showPopWindows(assetsInfoDataList, view, position);
    }

    private void showPopWindows(List<AssetsDeatilInfo> deatilInfos, View view, int position) {
        AssetsDeatilInfo deatilInfo = deatilInfos.get(position);
        int id = view.getId();
        View view1 = view.findViewById(R.id.more_iv);
        switch (id) {

            case R.id.more_ll:

                AssetsFitPopupUtil fitPopupUtil = new AssetsFitPopupUtil((Activity) context, deatilInfo);
                fitPopupUtil.setOnClickListener(new AssetsFitPopupUtil.OnCommitClickListener() {
                    @Override
                    public void onClick(String reason) {
                        CommonUtils.copy(reason, context);
                    }
                });
                fitPopupUtil.showPopup(view1);
                break;
        }
    }


    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        refreshLayout.finishRefresh(2000);
    }


}
