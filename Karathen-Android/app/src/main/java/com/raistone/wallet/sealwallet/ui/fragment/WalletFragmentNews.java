package com.raistone.wallet.sealwallet.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
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
import com.google.gson.JsonSyntaxException;
import com.raistone.wallet.sealwallet.R;
import com.raistone.wallet.sealwallet.adapter.AssetsAdapter;
import com.raistone.wallet.sealwallet.adapter.WalletAdapterNews;
import com.raistone.wallet.sealwallet.daoutils.AssetsDetailDaoUtils;
import com.raistone.wallet.sealwallet.daoutils.ChainAddressDaoUtils;
import com.raistone.wallet.sealwallet.daoutils.ChainDaoUtils;
import com.raistone.wallet.sealwallet.daoutils.HdWalletDaoUtils;
import com.raistone.wallet.sealwallet.entity.ChainInfo;
import com.raistone.wallet.sealwallet.entity.MessageEvent;
import com.raistone.wallet.sealwallet.entity.NetStatus;
import com.raistone.wallet.sealwallet.entity.OntBalanceInfo;
import com.raistone.wallet.sealwallet.entity.PriceInfo;
import com.raistone.wallet.sealwallet.entity.UpateWalletInfo;
import com.raistone.wallet.sealwallet.entity.WalletInfo;
import com.raistone.wallet.sealwallet.factory.AssetsDeatilInfo;
import com.raistone.wallet.sealwallet.factory.AssetsInfoData;
import com.raistone.wallet.sealwallet.factory.ChainAddressInfo;
import com.raistone.wallet.sealwallet.factory.ChainDataInfo;
import com.raistone.wallet.sealwallet.factory.HdWallet;
import com.raistone.wallet.sealwallet.httputils.HttpUtils;
import com.raistone.wallet.sealwallet.transforms.ZoomInTransformer;
import com.raistone.wallet.sealwallet.utils.BigDecimalUtils;
import com.raistone.wallet.sealwallet.utils.ChainAddressCreateManager;
import com.raistone.wallet.sealwallet.utils.CommonUtils;
import com.raistone.wallet.sealwallet.utils.Constant;
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
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.disposables.Disposable;
import okhttp3.RequestBody;

public class WalletFragmentNews extends BaseFragment implements OnRefreshListener, ViewPager.OnPageChangeListener, BaseQuickAdapter.OnItemClickListener, BaseQuickAdapter.OnItemChildClickListener, TextWatcher {

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
    ViewPager viewpager;
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
    @BindView(R.id.left_iv)
    ImageView leftIv;
    @BindView(R.id.right_iv)
    ImageView rightIv;

    private Context context;
    private Boolean unitFlag;
    private List<WalletInfo> allWallet;//钱包集合
    private WalletInfo walletInfo;//钱包对象
    private String accountId;//账户ID
    private List<ChainInfo> chainInfos; //多链信息


    public static BigDecimal cnyPrice = new BigDecimal(0);
    public static BigDecimal usdPrice = new BigDecimal(0);

    private WalletAdapterNews walletAdapter; //多链展示

    private AssetsAdapter assetsAdapter;//资产 adapter


    private String chainAddress; //链地址

    private static int selectIndex = 0;


    private List<String> parms = new ArrayList<>();//币类型 数组


    //Set<String> parms = new LinkedHashSet<>();


    public List<String> tokenSynbolsByLocal = new ArrayList<>();
    public List<String> tokenSynbols = new ArrayList<>();

    private String walletAddress;//钱包地址


    private final int REQUEST_CAMERA = 200;


    private CustomPopWindow mCustomPopWindow;

    private int findType = 0;
    private int findTypeNeo = 0;
    private int findTypeOnt = 0;

    private boolean hideAss = false;

    private List<ChainAddressInfo> chainAddressInfos = new ArrayList<>();

    private List<AssetsDeatilInfo> assetsInfoDataList;

    private List<ChainDataInfo> walletSupportChains;

    private HdWallet wallet;

    private ChainDataInfo chainDataInfo;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_news, container, false);
        unbinder = ButterKnife.bind(this, view);
        context = getActivity();
        smartrefreshlayout.setOnRefreshListener(this);
        unitFlag = SharePreUtil.getBoolean(context, "CurrencyUnit", true);

        HdWallet hdWallet = HdWalletDaoUtils.findWalletBySelect();

        initViews();
        initDataTwo(hdWallet);
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    public void initViews() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);

        recyclerview.setLayoutManager(layoutManager);

        recyclerview.setSwipeMenuCreator(swipeMenuCreator);
        recyclerview.setSwipeMenuItemClickListener(mMenuItemClickListener);

        viewpager.setPageTransformer(true, new ZoomInTransformer());

        viewpager.setOffscreenPageLimit(5);

        viewpager.addOnPageChangeListener(this);

    }

    public void initDataTwo(HdWallet hdWallet) {

        try {
            selectIndex = 0;


            if (hdWallet != null) {
                accountId = hdWallet.getAccountId();
            }

            walletSupportChains = ChainDaoUtils.findAllChainByAccount(accountId);


            findAddressInfo();


            if (chainAddressInfos != null && chainAddressInfos.size() > 0) {

                walletAdapter = new WalletAdapterNews(context, walletSupportChains, chainAddressInfos, unitFlag);

                viewpager.setAdapter(walletAdapter);

                chainAddress = chainAddressInfos.get(selectIndex).getAddress();

                assetsInfoDataList = getAssetsInfoDataListByStatus(chainAddressInfos.get(selectIndex));
            }


            //viewpager.setCurrentItem(150, false);

            if (walletSupportChains != null && walletSupportChains.size() > 0) {

                if (walletSupportChains.size() == 1) {

                    viewpager.setCurrentItem(50, false);
                }
                if (walletSupportChains.size() == 2) {

                    viewpager.setCurrentItem(100, false);
                }

                if (walletSupportChains.size() == 3) {

                    viewpager.setCurrentItem(150, false);
                }
            }

            walletName.setText(hdWallet.getWalletName());


            chainDataInfo = walletSupportChains.get(selectIndex);


            if (assetsInfoDataList != null) {
                assetsAdapter = new AssetsAdapter(assetsInfoDataList);
                assetsAdapter.setOnItemClickListener(this);
                assetsAdapter.setOnItemChildClickListener(this);
                recyclerview.setAdapter(assetsAdapter);
            }


            selectChainAddressInfo = chainAddressInfos.get(selectIndex);

            requestDataTwo(chainAddressInfos.get(selectIndex), chainAddressInfos.get(selectIndex).getAddress());

            searchEd.addTextChangedListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ChainAddressInfo event) {
        try {
            String address = event.getAddress();

            event.resetAssetsInfoDataList();

            chainAddressInfos.set(selectIndex, event);

            viewpager.getAdapter().notifyDataSetChanged();

            parms.clear();

            assetsInfoDataList = getAssetsInfoDataListByStatus(event);

            assetsAdapter.setNewData(assetsInfoDataList);

            assetsAdapter.notifyDataSetChanged();


            requestDataTwo(event, address);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(HdWallet hdWallet) {
        try {
            parms.clear();
            initDataTwo(hdWallet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(UpateWalletInfo walletInfo) {
        try {
            walletName.setText(walletInfo.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {

        try {

            requestDataTwo(chainAddressInfos.get(selectIndex), event.getMessage());


        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onResetMsg(String msg) {


        try {
            walletSupportChains.clear();
            selectIndex = 0;
            wallet = HdWalletDaoUtils.findWalletBySelect();
            accountId = wallet.getAccountId();

            walletSupportChains = ChainDaoUtils.findAllChainByAccount(accountId);


            findAddressInfo();
            if (walletSupportChains != null && walletSupportChains.size() > 0) {

                walletAdapter = new WalletAdapterNews(context, walletSupportChains, chainAddressInfos, unitFlag);

                viewpager.setAdapter(walletAdapter);

                if (walletSupportChains.size() == 1) {

                    viewpager.setCurrentItem(50, false);
                }
                if (walletSupportChains.size() == 2) {

                    viewpager.setCurrentItem(100, false);
                }

                if (walletSupportChains.size() == 3) {

                    viewpager.setCurrentItem(150, false);
                }

            }

            viewpager.setOffscreenPageLimit(5);
            viewpager.addOnPageChangeListener(this);


            chainAddress = chainAddressInfos.get(0).getAddress();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(NetStatus status) {
        boolean netStatus = status.isNetStatus();
        if (netStatus) {
            walletAdapter.netStatusChang(true);
        } else {
            walletAdapter.netStatusChang(false);
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        try {
            if (!hidden) {

                Boolean unit = SharePreUtil.getBoolean(context, "CurrencyUnit", true);
                unitFlag = unit;

                HdWallet wallet = HdWalletDaoUtils.findWalletBySelect();

                walletSupportChains = ChainDaoUtils.findAllChainByAccount(wallet.getAccountId());

                chainDataInfo = walletSupportChains.get(selectIndex);

                ChainAddressInfo chainAddressInfo = chainAddressInfos.get(selectIndex);

                assetsInfoDataList = getAssetsInfoDataListByStatus(chainAddressInfo);

                assetsAdapter.setNewData(assetsInfoDataList);
                assetsAdapter.notifyDataSetChanged();

                walletAdapter.changUnit(unit);

                walletAdapter.notifyDataSetChanged();

                viewpager.getAdapter().notifyDataSetChanged();
            }
        } catch (Exception e) {
            e.printStackTrace();
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

                    Router.build("ReceiptActivity").with("chainAddressInfo", chainAddressInfo).go(this);
                }


                break;
            case R.id.wallet_ll:

                Router.build("WalletManagerActivity").with("chainDataInfo", chainDataInfo).with("accountId", accountId).with("selectIndex", selectIndex).requestCode(1).go(this);

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

                if (chainAddressInfos != null && chainAddressInfos.size() > 0) {
                    ChainAddressInfo chainAddressInfo = chainAddressInfos.get(selectIndex);

                    Router.build("AddAssetsActivity").with("ethWallet", chainAddressInfo).requestCode(200).go(context);
                }
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

                        //filterSetData(findType);
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


                        assetsInfoDataList = getAssetsInfoDataListByStatus(chainAddressInfos.get(selectIndex));

                        assetsAdapter.setNewData(assetsInfoDataList);
                        assetsAdapter.notifyDataSetChanged();

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

                            assetsInfoDataList = getAssetsInfoDataListByStatus(chainAddressInfos.get(selectIndex));

                            assetsAdapter.setNewData(assetsInfoDataList);
                            assetsAdapter.notifyDataSetChanged();
                            return;
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


            boolean isDefault = deatilInfo.getIsDefault();

            if (isDefault) {
                ToastHelper.showToast(getResources().getString(R.string.cant_delete));
                return;
            }


            BigDecimal price = new BigDecimal(deatilInfo.getPrice());

            BigDecimal priceUSD = new BigDecimal(deatilInfo.getPriceUSD());


            cnyPrice = BigDecimalUtils.sub(cnyPrice + "", price + "");

            usdPrice = BigDecimalUtils.sub(usdPrice + "", priceUSD + "");

            assetsInfoDataList.remove(deatilInfo);

            requestDataTwo(chainAddressInfos.get(selectIndex), chainAddressInfos.get(selectIndex).getAddress());


            deatilInfo.setStatus(1);
            AssetsDetailDaoUtils.updatePrice(deatilInfo);

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


            assetsInfoDataList = getAssetsInfoDataListByStatus(chainInfo);

            assetsAdapter.notifyDataSetChanged();

            for (int i = 0; i < assetsInfoDataList.size(); i++) {
                AssetsDeatilInfo bean = assetsInfoDataList.get(i);

                parms.add(bean.getTokenSynbol());
            }
            getOntBalance(chainInfo.getAddress());
            getPrice_2(chainInfo, multAddress, parms);
        }

    }


    private Disposable ethGetTokenHolding;
    private Disposable neoGetTokenHolding;
    private Disposable ontGetTokenHolding;

    public void getTokenHolding(final ChainAddressInfo chainInfo, final String address) {

        try {
            final String[] params = new String[1];
            params[0] = address;
            //测试地址

            RequestBody body = HttpUtils.toRequestBody("getTokenHolding", params);

            ethGetTokenHolding = EasyHttp.getInstance().setBaseUrl(Constant.ETHParams.BASE_MAIN_URL).post("/")
                    .requestBody(body)
                    .execute(new SimpleCallBack<String>() {
                        @Override
                        public void onError(ApiException e) {
                            //showToast(e.getMessage());
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
                                } else {
                                    for (AssetsDeatilInfo b : assetsInfoDataList) {
                                        parms.add(b.getTokenSynbol());
                                    }
                                    getPrice_2(chainInfo, address, parms);
                                }
                            }
                        }
                    });
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }

    }

    public void getNeoAssetsByAddress(final ChainAddressInfo chainAddressInfo, final String address) {
        try {
            List<String> params = new ArrayList();
            params.add(address);

            RequestBody body = HttpUtils.toRequestBody("getTokenHolding", params);

            neoGetTokenHolding = EasyHttp.getInstance().setBaseUrl(Constant.NEOParams.NEO_BASE_URL).post("/")
                    .requestBody(body)
                    .execute(new SimpleCallBack<String>() {
                        @Override
                        public void onError(ApiException e) {
                        }

                        @Override
                        public void onSuccess(String response) {

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

                                } else {
                                    for (AssetsDeatilInfo b : assetsInfoDataList) {
                                        parms.add(b.getTokenSynbol());
                                    }
                                    getPrice_2(chainAddressInfo, address, parms);
                                }
                            }

                        }
                    });
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
    }

    public void getOntBalance(final String multiAddress) {

        try {
            final ChainAddressInfo addressInfo = chainAddressInfos.get(selectIndex);

            List<String> params = new ArrayList();
            params.add(multiAddress);

            RequestBody body = HttpUtils.toRequestBody("getbalance", params);

            ontGetTokenHolding = EasyHttp.getInstance().setBaseUrl(Constant.ONTParams.ONT_BASE_URL).post("/")
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

                                List<AssetsDeatilInfo> deatilInfos = getAssetsInfoDataListByStatus(addressInfo);

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
                                    equstListDataTwo(addressInfo, deatilInfos, walletAddress, "ONT");
                                }
                            }

                        }
                    });
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }

    }

    public void getPrice_2(final ChainAddressInfo chainAddressInfo, final String address, List<String> tokenSynbos) {

        try {
            String[] params = tokenSynbos.toArray(new String[tokenSynbos.size()]);
            String[][] newStrings = new String[1][];
            newStrings[0] = params;

            final RequestBody body = HttpUtils.toRequestBody("getPrice", newStrings);

            EasyHttp.getInstance().setBaseUrl(Constant.HttpServiceUrl.BASE_MAIN_URL).post("/")
                    .requestBody(body)
                    .execute(new SimpleCallBack<String>() {
                        @Override
                        public void onError(ApiException e) {

                        }

                        @Override
                        public void onSuccess(String skinTestResult) {

                            PriceInfo priceInfo = GsonUtils.decodeJSON(skinTestResult, PriceInfo.class);
                            if (priceInfo != null) {
                                updatePriceTwo(chainAddressInfo, priceInfo, address);
                            }
                        }
                    });
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }


    }

    public void equstListDataTwo(ChainAddressInfo chainInfo, List<AssetsDeatilInfo> dataInfos, String walletAddes, String coinType) {

        try {
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
                    if (dataBean.getTokenAddress().toLowerCase().equals(synbol.toLowerCase())) {
                        flag = true;
                        break;
                    } else {
                        flag = false;
                    }
                }
                if (flag) {
                    String decimal = bean.getTokenDecimal();

                    String balance = bean.getBalance();
                    if (!TextUtils.isEmpty(decimal) && !TextUtils.isEmpty(balance)) {
                        BigDecimal bigDecimal = BigDecimalUtils.div(bean.getBalance(), String.valueOf(Math.pow(10, Double.parseDouble(bean.getTokenDecimal()))), 8);
                        dataBean.setBalance(bigDecimal.toPlainString());
                    } else {
                        if (!TextUtils.isEmpty(balance)) {
                            dataBean.setBalance(bean.getBalance());
                        }
                    }
                    dataBean.setAsset_name(chainInfo.getAccountId());
                    dataBean.setCoinType(coinType);
                    AssetsDetailDaoUtils.updatePrice(dataBean);
                } else {
                    if (chainInfo != null) {
                        bean.setAsset_name(chainInfo.getAccountId());
                        bean.setChainAddressId(chainInfo.getId());
                        bean.setWalletAddress(walletAddes);
                        AssetsDetailDaoUtils.insertNewAssets(bean);
                        chainInfo.resetAssetsInfoDataList();
                    }
                }
                tokenSynbolsByLocal.add(bean.getTokenSynbol());
            }


            parms.clear();
            for (AssetsDeatilInfo info : chainInfo.getAssetsInfoDataList()) {
                parms.add(info.getTokenName());
            }


            getPrice_2(chainInfo, walletAddes, parms);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

    }

    public void updatePriceTwo(ChainAddressInfo chainAddressInfo, PriceInfo info, String walletAddress) {


        try {
            List<PriceInfo.ResultBean> result = info.getResult();

            assetsInfoDataList = getAssetsInfoDataListByStatus(chainAddressInfo);

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

                        dataBean.setPriceFlag(price);

                        dataBean.setPriceUsdFlag(priceUSD);

                        BigDecimal priceValue = BigDecimalUtils.mul(value1 + "", price.toString());

                        BigDecimal usdPriceValue = BigDecimalUtils.mul(value1 + "", priceUSD.toString());

                        dataBean.setPrice(BigDecimalUtils.intercept(priceValue + "", 2).toPlainString());

                        dataBean.setPriceUSD(BigDecimalUtils.intercept(usdPriceValue + "", 2).toPlainString());


                        AssetsDetailDaoUtils.updatePrice(dataBean);

                        cnyPrice = cnyPrice.add(BigDecimalUtils.intercept(priceValue + "", 2));

                        usdPrice = usdPrice.add(BigDecimalUtils.intercept(usdPriceValue + "", 2));

                        chainAddressInfo.setCnyTotalPrice(cnyPrice.toPlainString());
                        chainAddressInfo.setUsdtTotalPrice(usdPrice.toPlainString());
                        chainAddressInfo.update();

                        AssetsDetailDaoUtils.updatePrice(dataBean);
                    }

                }

            }


            chainAddressInfo.resetAssetsInfoDataList();


            if (selectChainAddressInfo != null) {

                if (selectChainAddressInfo.getCoinType().equals(chainAddressInfo.getCoinType())) {

                    assetsInfoDataList = getAssetsInfoDataListByStatus(chainAddressInfo);
                    assetsAdapter.setNewData(assetsInfoDataList);
                    assetsAdapter.notifyDataSetChanged();

                    setWalletMoney();
                } else {
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void setWalletMoney() {
        if (viewpager != null) {

            PagerAdapter adapter = viewpager.getAdapter();

            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }

        }

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
                if (!srcData.get(i).getBalance().equals("0")) {
                    newsDatas.add(srcData.get(i));
                }
            }
        } else {
            newsDatas.addAll(srcData);
        }

        return newsDatas;
    }


    @Override
    public void onPageScrolled(int arg0, float arg1, int i1) {

    }

    ChainAddressInfo addressInfo;

    @Override
    public void onPageSelected(int i) {


        try {
            searchEd.setText("");

            i = i % walletSupportChains.size();

            findType = 0;
            findTypeNeo = 0;
            findTypeOnt = 0;
            selectIndex = i;

            chainDataInfo = walletSupportChains.get(selectIndex);

            if (chainDataInfo != null) {

                if (chainAddressInfos != null && chainAddressInfos.size() > 0) {

                    addressInfo = chainAddressInfos.get(selectIndex);

                    assetsInfoDataList = getAssetsInfoDataListByStatus(chainAddressInfos.get(selectIndex));

                    if (addressInfo != null) {

                        walletAddress = addressInfo.getAddress();


                        if (assetsInfoDataList != null) {


                            assetsAdapter.setNewData(assetsInfoDataList);
                            assetsAdapter.notifyDataSetChanged();


                            selectChainAddressInfo = chainAddressInfos.get(selectIndex);

                            if (addressInfo == selectChainAddressInfo) {
                                if (!TextUtils.isEmpty(addressInfo.getAddress())) {
                                    requestDataTwo(addressInfo, addressInfo.getAddress());
                                }
                            }


                        }

                    }
                }
            }

            changBgColor(selectIndex);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    List<AssetsDeatilInfo> newsDatas=new ArrayList<>();
    private List<AssetsDeatilInfo> getAssetsInfoDataListByStatus(ChainAddressInfo addressInfo){
        newsDatas.clear();
        if(addressInfo!=null) {
            List<AssetsDeatilInfo> infoDataList = addressInfo.getAssetsInfoDataList();
            if(infoDataList!=null && infoDataList.size()>0){
                for (AssetsDeatilInfo info:infoDataList) {
                    if(info.getStatus()!=1) {
                        newsDatas.add(info);
                    }
                }
            }
        }

        return newsDatas;
    }

    private void changBgColor(int index) {

        if(index==0) {
            GradientDrawable myGrad = (GradientDrawable) leftIv.getBackground();
            myGrad.setColor(Color.parseColor("#27B3E7"));

            GradientDrawable myGrad1 = (GradientDrawable) rightIv.getBackground();
            myGrad1.setColor(Color.parseColor("#3F8EC8"));
        }

        if(index==1) {
            GradientDrawable myGrad = (GradientDrawable) leftIv.getBackground();
            myGrad.setColor(Color.parseColor("#7082FE"));

            GradientDrawable myGrad1 = (GradientDrawable) rightIv.getBackground();
            myGrad1.setColor(Color.parseColor("#27B3E7"));
        }

        if(index==2) {
            GradientDrawable myGrad = (GradientDrawable) leftIv.getBackground();
            myGrad.setColor(Color.parseColor("#3F8EC8"));

            GradientDrawable myGrad1 = (GradientDrawable) rightIv.getBackground();
            myGrad1.setColor(Color.parseColor("#7082FE"));
        }
    }


    private ChainAddressInfo selectChainAddressInfo;

    @Override
    public void onPageScrollStateChanged(int state) {
        long currentTime = Calendar.getInstance().getTimeInMillis();
        switch (state) {
            case ViewPager.SCROLL_STATE_DRAGGING:


                if (ethGetTokenHolding != null && !ethGetTokenHolding.isDisposed()) {
                    ethGetTokenHolding.dispose();

                    EasyHttp.cancelSubscription(ethGetTokenHolding);
                }
                if (neoGetTokenHolding != null && !neoGetTokenHolding.isDisposed()) {
                    neoGetTokenHolding.dispose();
                    EasyHttp.cancelSubscription(neoGetTokenHolding);
                }
                if (ontGetTokenHolding != null && !ontGetTokenHolding.isDisposed()) {
                    ontGetTokenHolding.dispose();
                    EasyHttp.cancelSubscription(ontGetTokenHolding);
                }

                break;

            case ViewPager.SCROLL_STATE_SETTLING:
                break;

            case ViewPager.SCROLL_STATE_IDLE:


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

            assetsInfoDataList = filterData;
            assetsAdapter.setNewData(assetsInfoDataList);
            recyclerview.setAdapter(assetsAdapter);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }


    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        //gotoDetail(position);

        ChainAddressInfo wallet = chainAddressInfos.get(selectIndex);

        AssetsDeatilInfo deatilInfo = assetsInfoDataList.get(position);

        Router.build("WalletItemDetailActivity").with("ethWallet", wallet).with("assetsInfo", deatilInfo).go(context);
    }


    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        if (assetsInfoDataList != null && assetsInfoDataList.size() > 0) {
            showPopWindows(assetsInfoDataList, view, position);
        }
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
        if (chainAddressInfos != null && chainAddressInfos.size() > 0) {
            requestDataTwo(chainAddressInfos.get(selectIndex), chainAddressInfos.get(selectIndex).getAddress());
        }
    }


}
