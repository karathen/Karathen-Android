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

/**
 * 钱包
 */
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
    private List<WalletInfo> allWallet;//钱包集合
    private WalletInfo walletInfo;//钱包对象
    private String accountId;//账户ID
    private List<ChainInfo> chainInfos; //多链信息


    public static BigDecimal cnyPrice = new BigDecimal(0);
    public static BigDecimal usdPrice = new BigDecimal(0);

    private WalletAdapter walletAdapter; //多链展示

    private AssetsAdapter assetsAdapter;//资产 adapter

    //private List<MultiChainInfo> multiChainInfos = new ArrayList<>(); //地址集合

    private List<AssetsInfo.DataBean> infoData = new ArrayList<>(); //资产集合

    private String chainAddress; //链地址

    private static int selectIndex = 0;

    private int changIndex = 0;


    private List<String> parms = new ArrayList<>();//币类型 数组

    public List<String> tokenSynbolsByLocal = new ArrayList<>();//本地资产列表 这里和服务器返回的资产进行筛选过后的返回的资产列表
    public List<String> tokenSynbols = new ArrayList<>();//本地资产列表 这里和服务器返回的资产进行筛选过后的返回的资产列表

    private String walletAddress;//钱包地址


    private final int REQUEST_CAMERA = 200;


    private CustomPopWindow mCustomPopWindow;

    private int findType = 0;// 显示类型,0 全部 1 erc20 2 erc721 3 隐藏为 o 资产
    private int findTypeNeo = 0;// 显示类型,0 全部 1 erc20 2 erc721 3 隐藏为 o 资产
    private int findTypeOnt = 0;// 显示类型,0 全部 1 erc20 2 erc721 3 隐藏为 o 资产

    private boolean hideAss = false;

    private List<ChainAddressInfo> chainAddressInfos = new ArrayList<>();

    List<AssetsDeatilInfo> assetsInfoDataList;

    private List<ChainDataInfo> walletSupportChains;

    HdWallet wallet;

    private ChainDataInfo chainDataInfo;
   /* @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case 0:

                    MultiChainInfo chainInfo = (MultiChainInfo) msg.obj;

                    infoData.clear();

                    infoData = AssetsDaoUtils.findAllAssetsByCoinType(chainInfo.getCoinType(), chainInfo.getAddress());
                    //infoData = AssetsDaoUtils.findAllAssetsByAddress(chainInfo.getAddress());


                    Observable
                            .empty()
                            .observeOn(AndroidSchedulers.mainThread())
                            .doOnComplete(new Action() {
                                @Override
                                public void run() throws Exception {

                                    //assetsAdapter.setNewData(infoData);

                                    assetsAdapter.notifyDataSetChanged();
                                }
                            }).subscribe();


                    break;
            }
        }
    };*/


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


        //获取钱包信息

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

        // requestData(multiChainInfos.get(selectIndex), multiChainInfos.get(selectIndex).getAddress());

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
        //获取钱包信息


        List<ChainDataInfo> walletSupportChains = WalletManager.getInstance().getWalletSupportChains();


        chainDataInfo = walletSupportChains.get(0);


        LinearLayoutManager layoutManager = new LinearLayoutManager(context);

        recyclerview.setLayoutManager(layoutManager);

        recyclerview.setSwipeMenuCreator(swipeMenuCreator);
        recyclerview.setSwipeMenuItemClickListener(mMenuItemClickListener);


        chainAddressInfos.clear();
        /*for (int i = 0; i < walletSupportChains.size(); i++) {
            ChainDataInfo chainDataInfo = walletSupportChains.get(i);


            MultiChainInfo multiChainInfo = MultiChainInfoDaoUtils.loadByTypeInAccount(chainInfo.getChaType(), chainInfo.getAccountId());
            multiChainInfos.add(multiChainInfo);
        }

        walletAdapter = new WalletAdapter(context, multiChainInfos, unitFlag);

        viewpager.setAdapter(walletAdapter);
*/


        if (walletSupportChains != null && walletSupportChains.size() > 0) {

            //walletAdapter = new WalletAdapter(context, multiChainInfos, unitFlag);

            viewpager.setAdapter(walletAdapter);
        }

        walletName.setText(walletSupportChains.get(selectIndex).getChainName());

        viewpager.setOnPageChangeListener(this);


        //默认为第一条数据的资产
        chainAddress = chainAddressInfos.get(0).getAddress();


        //根据地址查询本地资产余额
        infoData = AssetsDaoUtils.findAllAssetsByWalletAddress(chainAddress);


     /*   if (infoData != null) {
            assetsAdapter = new AssetsAdapter(infoData);
            assetsAdapter.setOnItemClickListener(this);
            assetsAdapter.setOnItemChildClickListener(this);
            recyclerview.setAdapter(assetsAdapter);
        }
*/

        //requestData(multiChainInfos.get(selectIndex), multiChainInfos.get(selectIndex).getAddress());

        searchEd.addTextChangedListener(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(final ChainAddressInfo event) {
        final String address = event.getAddress();

        event.resetAssetsInfoDataList();

        chainAddressInfos.set(selectIndex, event);

        viewpager.refresh();


        parms.clear();//地址清空

        //assetsInfoDataList.clear();

        assetsInfoDataList = event.getAssetsInfoDataList();

        assetsAdapter.setNewData(assetsInfoDataList);

        assetsAdapter.notifyDataSetChanged();

        /**
         * 开启线程请求最新数据
         */
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


        //findAddressInfoTwo();


        //chainAddressInfos.set(selectIndex, event);

        viewpager.refresh();

        /*walletAdapter = new WalletAdapter(context, this.multiChainInfos, unitFlag);

        viewpager.setAdapter(walletAdapter);

        viewpager.setCurrentItem(selectIndex);*/

        parms.clear();//地址清空

        //setWalletMoney();//更新钱包余额

        infoData.clear(); //原始数据清空

        //加载本地数据库信息
        infoData = AssetsDaoUtils.findAllAssetsByWalletAddress(address);

        //设置信息
        //assetsAdapter.setNewData(infoData);

        //更新
        assetsAdapter.notifyDataSetChanged();

        /**
         * 开启线程请求最新数据
         */
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


    /**
     * 更新添加后的数据
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {


       /* infoData.clear(); //原始数据清空

        //加载本地数据库信息
        infoData = AssetsDaoUtils.findAllAssetsByWalletAddress(event.getMessage());

        //更新
        assetsAdapter.notifyDataSetChanged();*/
        ChainAddressInfo addressInfo = chainAddressInfos.get(selectIndex);

        WalletApplication.getsInstance().getDaoSession().clear();

        addressInfo.resetAssetsInfoDataList();

        assetsInfoDataList = addressInfo.getAssetsInfoDataList();

        assetsAdapter.setNewData(assetsInfoDataList);
        assetsAdapter.notifyDataSetChanged();


    }

    /**
     * 更新添加后的数据
     *
     * @param msg
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onResetMsg(String msg) {


        walletSupportChains.clear();
        selectIndex = 0;
        //获取钱包信息
        wallet = WalletManager.getInstance().getWalletChains(1L);

        //获取钱包
        //获取账户ID
        accountId = wallet.getAccountId();

        //根据账户Id获取多链信息
        walletSupportChains = ChainDaoUtils.findAllChainByAccount(accountId);


        findAddressInfo();
        if (walletSupportChains != null && walletSupportChains.size() > 0) {

            walletAdapter = new WalletAdapter(context, chainAddressInfos, unitFlag);

            viewpager.setAdapter(walletAdapter);

            viewpager.setCurrentItem(0);

            if(chainInfos!=null && chainInfos.size()>0) {
                String chaName = chainInfos.get(selectIndex).getChaName();

                if(!TextUtils.isEmpty(chaName)) {
                    walletName.setText(chaName);
                }
            }
        }

        viewpager.setOffscreenPageLimit(5);
        viewpager.setOnPageChangeListener(this);


        //默认为第一条数据的资产
        chainAddress = chainAddressInfos.get(0).getAddress();


        //requestData(multiChainInfos.get(selectIndex), multiChainInfos.get(selectIndex).getAddress());


    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        if (!hidden) {

            //selectIndex=0;
            Boolean unit = SharePreUtil.getBoolean(context, "CurrencyUnit", true);
            unitFlag = unit;

            HdWallet wallet = WalletManager.getInstance().getWalletChains(1l);

            //根据账户Id获取多链信息
            walletSupportChains = ChainDaoUtils.findAllChainByAccount(wallet.getAccountId());

            chainDataInfo = walletSupportChains.get(selectIndex);

            ChainAddressInfo chainAddressInfo = chainAddressInfos.get(selectIndex);

            assetsInfoDataList = chainAddressInfo.getAssetsInfoDataList();

            assetsAdapter.setNewData(assetsInfoDataList);
            assetsAdapter.notifyDataSetChanged();

            //chainAddressInfos.set(0, chainAddressInfo);

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
            /**
             * 二维码地址
             */
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

                   /* Router.build("ReceiptActivity")
                            .with("address", chainAddressInfo.getAddress())
                            .with("fromName", chainAddressInfo.getCoinType())
                            .with("contractAddress", contractAddress)
                            .with("coinName", chainAddressInfo.getCoinType()).go(this);*/
                    Router.build("ReceiptActivity").with("chainAddressInfo", chainAddressInfo).go(this);
                }


                break;
            /**
             * 钱包管理
             */
            case R.id.wallet_ll:


                chainDataInfo = walletSupportChains.get(selectIndex);

                if (chainDataInfo != null) {
                    Router.build("AddressManageActivity").with("chainDataInfo", chainDataInfo).with("accountId", accountId).with("selectIndex", selectIndex).requestCode(1).go(this);
                }


                break;
            /**
             * 扫描二维码
             */
            case R.id.scaner_code_iv:
                ZbPermission.needPermission(getActivity(), REQUEST_CAMERA, Permission.CAMERA, new ZbPermission.ZbPermissionCallback() {
                    @Override
                    public void permissionSuccess(int i) {
                        Router.build("ScannerActivity").with("coinType", chainDataInfo.getChainType()).with("accountId",chainDataInfo.getAccountId()).requestCode(0).go(context);
                    }

                    @Override
                    public void permissionFail(int i) {
                        ToastHelper.showToast(getResources().getString(R.string.photo_permission));
                    }
                });

                break;
            /**
             * 资产添加
             */
            case R.id.plus_ll:

                ChainAddressInfo chainAddressInfo = chainAddressInfos.get(selectIndex);

                Router.build("AddAssetsActivity").with("ethWallet", chainAddressInfo).requestCode(200).go(context);
                break;
            /**
             * 筛选
             */
            case R.id.assets_filter_ll:

                //ETH 筛选
                String chainType = chainDataInfo.getChainType();

                if (chainType.equals(ChainAddressCreateManager.ETH_COIN_TYPE)) {

                    View contentView = LayoutInflater.from(context).inflate(R.layout.assets_filter_layout, null);
                    //处理popWindow 显示内容
                    handleLogic(contentView);

                    mCustomPopWindow = new CustomPopWindow.PopupWindowBuilder(context)
                            .setView(contentView)
                            .enableBackgroundDark(true) //弹出popWindow时，背景是否变暗
                            .setClippingEnable(true)
                            .setBgDarkAlpha(0.7f) // 控制亮度
                            .create()
                            .showAsDropDown(assetsFilterLl, DensityUtils.dp2px(10), 0);
                }

                //NEO 筛选
                if (chainType.equals(ChainAddressCreateManager.NEO_COIN_TYPE)) {

                    View contentView = LayoutInflater.from(context).inflate(R.layout.assets_filter_neo_layout, null);
                    //处理popWindow 显示内容
                    handleLogicByNeo(contentView);

                    mCustomPopWindow = new CustomPopWindow.PopupWindowBuilder(context)
                            .setView(contentView)
                            .enableBackgroundDark(true) //弹出popWindow时，背景是否变暗
                            .setClippingEnable(true)
                            .setBgDarkAlpha(0.7f) // 控制亮度
                            .create()
                            .showAsDropDown(assetsFilterLl, DensityUtils.dp2px(10), 0);
                }

                // ONT 筛选
                if (chainType.equals(ChainAddressCreateManager.ONT_COIN_TYPE)) {

                    View contentView = LayoutInflater.from(context).inflate(R.layout.assets_filter_ont_layout, null);
                    //处理popWindow 显示内容
                    handleLogicByOnt(contentView);

                    mCustomPopWindow = new CustomPopWindow.PopupWindowBuilder(context)
                            .setView(contentView)
                            .enableBackgroundDark(true) //弹出popWindow时，背景是否变暗
                            .setClippingEnable(true)
                            .setBgDarkAlpha(0.7f) // 控制亮度
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

    /**
     * 菜单创建器，在Item要创建菜单的时候调用。
     */
    private SwipeMenuCreator swipeMenuCreator = new SwipeMenuCreator() {
        @Override
        public void onCreateMenu(SwipeMenu swipeLeftMenu, SwipeMenu swipeRightMenu, int viewType) {
            int width = getResources().getDimensionPixelSize(R.dimen.common_dp_70);

            // 1. MATCH_PARENT 自适应高度，保持和Item一样高;
            // 2. 指定具体的高，比如80;
            // 3. WRAP_CONTENT，自身高度，不推荐;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;


            // 添加右侧的，如果不添加，则右侧不会出现菜单。
            {
                SwipeMenuItem deleteItem = new SwipeMenuItem(context)
                        .setBackground(R.drawable.selector_red)
                        .setImage(R.mipmap.ic_action_delete)
                        .setText(getResources().getString(R.string.delete_string))
                        .setTextColor(Color.WHITE)
                        .setWidth(width)
                        .setHeight(height);
                swipeRightMenu.addMenuItem(deleteItem);// 添加菜单到右侧。
            }
        }
    };

    /**
     * RecyclerView的Item的Menu点击监听。
     */
    private SwipeMenuItemClickListener mMenuItemClickListener = new SwipeMenuItemClickListener() {
        @Override
        public void onItemClick(SwipeMenuBridge menuBridge) {
            menuBridge.closeMenu();

            int direction = menuBridge.getDirection(); // 左侧还是右侧菜单。
            int adapterPosition = menuBridge.getAdapterPosition(); // RecyclerView的Item的position。
            int menuPosition = menuBridge.getPosition(); // 菜单在RecyclerView的Item中的Position。


            //AssetsInfo.DataBean bean = infoData.get(adapterPosition);

            AssetsDeatilInfo deatilInfo = assetsInfoDataList.get(adapterPosition);

            if (deatilInfo.getTokenSynbol().equals(ChainAddressCreateManager.ETH_COIN_TYPE) || deatilInfo.getTokenSynbol().equals("ONT")
                    || deatilInfo.getTokenSynbol().equals("ONG")|| deatilInfo.getTokenSynbol().equals("NEO")|| deatilInfo.getTokenSynbol().equals("GAS")
                    ) {
                ToastHelper.showToast(getResources().getString(R.string.cant_delete));
                return;
            }

            BigDecimal price = new BigDecimal(deatilInfo.getPrice());//人民币

            BigDecimal priceUSD = new BigDecimal(deatilInfo.getPriceUSD());//美元


            cnyPrice = BigDecimalUtils.sub(cnyPrice + "", price + "");

            usdPrice = BigDecimalUtils.sub(usdPrice + "", priceUSD + "");

            assetsInfoDataList.remove(deatilInfo);

            //AssetsDaoUtils.deleteAssetsByAddress(walletAddress, deatilInfo.getTokenAddress());

            //assetsAdapter.setNewData(infoData);
            AssetsDetailDaoUtils.deleteAssetsByAddress(deatilInfo);

            assetsAdapter.notifyDataSetChanged();

            setWalletMoney();

        }
    };


    /**
     * 根据钱包地址获取 钱包资产
     */
    public void requestDataTwo(final ChainAddressInfo chainInfo, final String multAddress) {

        walletAddress = chainInfo.getAddress();

        String coinType = chainInfo.getCoinType();

        parms.clear();
        tokenSynbols.clear();
        tokenSynbolsByLocal.clear();

        //assetsInfoDataList.clear();

        chainInfo.resetAssetsInfoDataList();
        //代表是ETH链
        if (coinType.equals(ChainAddressCreateManager.ETH_COIN_TYPE)) {
            walletAddress = chainInfo.getAddress();

            getTokenHolding(chainInfo, multAddress);


            //assetsInfoDataList=chainInfo.getAssetsInfoDataList();
            // assetsAdapter.notifyDataSetChanged();

           /* for (int i = 0; i < assetsInfoDataList.size(); i++) {
                AssetsDeatilInfo bean = assetsInfoDataList.get(i);

                parms.add(bean.getTokenSynbol());
            }*/

            /*new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    getPrice_2(chainInfo,multAddress, parms);
                }
            }, 1000);*/


            //代表是NEO链
        } else if (coinType.equals(ChainAddressCreateManager.NEO_COIN_TYPE)) {
            walletAddress = chainInfo.getAddress();
            //infoData = AssetsDaoUtils.findAllAssetsByCoinType(coinType, multAddress);
            //infoData = AssetsDaoUtils.findAllAssetsByAddress(multAddress);
            //assetsAdapter.setNewData(infoData);
            assetsAdapter.notifyDataSetChanged();


            getNeoAssetsByAddress(chainInfo, walletAddress);

            /*for (int i = 0; i < assetsInfoDataList.size(); i++) {
                AssetsDeatilInfo bean = assetsInfoDataList.get(i);

                parms.add(bean.getTokenSynbol());
            }*/


            //代表ONT链
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


    /**
     * 根据钱包地址获取 钱包资产
     */
    public void requestData(MultiChainInfo chainInfo, final String multAddress) {
        String coinType = chainInfo.getCoinType();

        parms.clear();
        tokenSynbols.clear();
        tokenSynbolsByLocal.clear();
        infoData.clear();

        //代表是ETH链
        if (coinType.equals(ChainAddressCreateManager.ETH_COIN_TYPE)) {
            walletAddress = chainInfo.getAddress();

            //getTokenHolding(chainInfo,multAddress);

            //infoData = AssetsDaoUtils.findAllAssetsByAddress(coinType, multAddress);
            infoData = AssetsDaoUtils.findAllAssetsByAddress(multAddress);

            //assetsAdapter.setNewData(infoData);
            assetsAdapter.notifyDataSetChanged();

            for (int i = 0; i < infoData.size(); i++) {
                AssetsInfo.DataBean bean = infoData.get(i);

                parms.add(bean.getTokenSynbol());
            }

            /*new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    //getPrice_2(multAddress, parms);
                }
            }, 2000);*/


            //代表是NEO链
        } else if (coinType.equals(ChainAddressCreateManager.NEO_COIN_TYPE)) {
            walletAddress = chainInfo.getAddress();
            //infoData = AssetsDaoUtils.findAllAssetsByCoinType(coinType, multAddress);
            infoData = AssetsDaoUtils.findAllAssetsByAddress(multAddress);
            //assetsAdapter.setNewData(infoData);
            assetsAdapter.notifyDataSetChanged();

            /*for (int i = 0; i < infoData.size(); i++) {
                AssetsInfo.DataBean bean = infoData.get(i);

                parms.add(bean.getTokenSynbol());

                getNeoBalance(multAddress, bean.getTokenAddress());
            }*/

            // getNeoAssetsByAddress(walletAddress);

            for (int i = 0; i < infoData.size(); i++) {
                AssetsInfo.DataBean bean = infoData.get(i);

                parms.add(bean.getTokenSynbol());
            }
            //getPrice_2(multAddress, parms);

            //代表ONT链
        } else if (coinType.equals(ChainAddressCreateManager.ONT_COIN_TYPE)) {
            walletAddress = chainInfo.getAddress();

            // infoData = AssetsDaoUtils.findAllAssetsByCoinType(coinType, multAddress);
            infoData = AssetsDaoUtils.findAllAssetsByAddress(multAddress);
            //assetsAdapter.setNewData(infoData);
            assetsAdapter.notifyDataSetChanged();

            for (int i = 0; i < infoData.size(); i++) {
                AssetsInfo.DataBean bean = infoData.get(i);

                parms.add(bean.getTokenSynbol());
            }
            getOntBalance(chainAddressInfos.get(selectIndex).getAddress());
            //getPrice_2(multAddress, parms);
        }

    }


    /**
     * 查询资产列表
     */
    public void getTokenHolding(final ChainAddressInfo chainInfo, final String address) {

        final String[] params = new String[1];
        params[0] = address;
        //测试地址
        //params[0] = "0x0dF63Ff925a5E0e9632a109E5115D9936A33036b";

        RequestBody body = HttpUtils.toRequestBody("getTokenHolding", params);

        EasyHttp.getInstance().setBaseUrl("https://ethapi.trinity.ink/").post("/")
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
                                //getPrice_2(address, parms);
                                getPrice_2(chainInfo, address, parms);
                            } else {
                                for (AssetsInfo.DataBean b : infoData) {
                                    parms.add(b.getTokenSynbol());
                                }
                                //getPrice_2(address, parms);

                                getPrice_2(chainInfo, address, parms);
                            }
                        }
                    }
                });

    }


    /**
     * 获取 NEO 资产余额
     *
     * @param multiAddress 链地址
     * @param address      合约地址
     */
    public void getNeoBalance(final String multiAddress, final String address) {

        List<String> params = new ArrayList();
        params.add(multiAddress);
        params.add(address);

        RequestBody body = HttpUtils.toRequestBody("getBalance", params);

        EasyHttp.getInstance().setBaseUrl("https://neoapi.trinity.ink/").post("/")
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

                            AssetsInfo.DataBean signAssets = AssetsDaoUtils.findSignAssets(address, multiAddress);

                            signAssets.setBalance(new BigDecimal(result));

                            signAssets.update();
                        }

                    }
                });

    }

    /**
     * 根据链地址查询 NEO 对应地址的资产
     *
     * @param address 链地址
     */
    public void getNeoAssetsByAddress(final ChainAddressInfo chainAddressInfo, final String address) {
        List<String> params = new ArrayList();
        params.add(address);

        RequestBody body = HttpUtils.toRequestBody("getTokenHolding", params);

        EasyHttp.getInstance().setBaseUrl("https://neoapi.trinity.ink/").post("/")
                .requestBody(body)
                .execute(new SimpleCallBack<String>() {
                    @Override
                    public void onError(ApiException e) {
                        //showToast(e.getMessage());
                    }

                    @Override
                    public void onSuccess(String response) {
                        //showToast(response);
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
                                //getPrice_2(address, parms);
                                getPrice_2(chainAddressInfo, address, parms);
                            } else {
                                for (AssetsDeatilInfo b : assetsInfoDataList) {
                                    parms.add(b.getTokenSynbol());
                                }
                                //getPrice_2(address, parms);

                                getPrice_2(chainAddressInfo, address, parms);
                            }
                        }

                    }
                });
    }


    /**
     * 获取 ONT 资产余额
     *
     * @param multiAddress 链地址
     */
    public void getOntBalance(final String multiAddress) {

        final ChainAddressInfo addressInfo = chainAddressInfos.get(selectIndex);

        List<String> params = new ArrayList();
        params.add(multiAddress);

        RequestBody body = HttpUtils.toRequestBody("getbalance", params);

        EasyHttp.getInstance().setBaseUrl("http://dappnode1.ont.io:20336/").post("/")
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


    /**
     * 获取代币价格
     */
    public void getPrice_2(final ChainAddressInfo chainAddressInfo, final String address, List<String> tokenSynbos) {

        String[] params = tokenSynbos.toArray(new String[tokenSynbos.size()]);
        String[][] newStrings = new String[1][];
        newStrings[0] = params;

        final RequestBody body = HttpUtils.toRequestBody("getPrice_2", newStrings);

        Observable.timer(0, TimeUnit.SECONDS).flatMap(new Function<Long, ObservableSource<String>>() {
            @Override
            public ObservableSource<String> apply(@io.reactivex.annotations.NonNull Long aLong) throws Exception {
                return EasyHttp.getInstance().setBaseUrl("https://appserver.trinity.ink/").post("/").requestBody(body)
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
                /**
                 * 切换线程 处理数据 更新数据
                 */
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

    /**
     * 判断网上获取到的资产是否和本地的资产有重复
     * 如果有，则替换，如果没有，则新加
     *
     * @param dataInfos   实时数据
     * @param walletAddes 钱包地址
     */
    public void equstListDataTwo(ChainAddressInfo chainInfo, List<AssetsDeatilInfo> dataInfos, String walletAddes, String coinType) {

        tokenSynbolsByLocal.clear();
        tokenSynbols.clear();
        //本地资产
        //assetsInfoDataList.clear();
        assetsInfoDataList = chainInfo.getAssetsInfoDataList();

        //循环获取到的资产
        for (int i = 0; i < dataInfos.size(); i++) {

            //获取一条记录
            AssetsDeatilInfo bean = dataInfos.get(i);
            bean.setCoinType(coinType);

            //得到    tokenAddress
            String synbol = bean.getTokenAddress();


            boolean flag = false;//默认没有这条数据
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
            //就代表本地有这条数据
            if (flag) {
                //AssetsDaoUtils.updateBalance(bean, walletAddes);
                dataBean.setBalance(bean.getBalance());
                dataBean.setCoinType(coinType);
                //dataBean.update();
                AssetsDetailDaoUtils.updatePrice(dataBean);
            } else {
                //AssetsDaoUtils.insertNewAssets(bean, walletAddes);
                bean.setChainAddressId(chainInfo.getId());
                bean.setWalletAddress(walletAddes);
                AssetsDetailDaoUtils.insertNewAssets(bean);
                chainInfo.resetAssetsInfoDataList();
            }
            tokenSynbolsByLocal.add(bean.getTokenSynbol());
        }

    }

    /**
     * 判断网上获取到的资产是否和本地的资产有重复
     * 如果有，则替换，如果没有，则新加
     *
     * @param dataInfos   实时数据
     * @param walletAddes 钱包地址
     */
    public void equstListData(List<AssetsInfo.DataBean> dataInfos, String walletAddes, String coinType) {

        tokenSynbolsByLocal.clear();
        tokenSynbols.clear();
        //本地资产
        infoData = AssetsDaoUtils.findAllAssetsByWalletAddress(walletAddes);

        //循环获取到的资产
        for (int i = 0; i < dataInfos.size(); i++) {

            //获取一条记录
            AssetsInfo.DataBean bean = dataInfos.get(i);
            bean.setCoinType(coinType);

            //得到    tokenSynbol
            String synbol = bean.getTokenSynbol();


            boolean flag = false;//默认没有这条数据
            AssetsInfo.DataBean dataBean = null;
            for (int j = 0; j < infoData.size(); j++) {
                dataBean = infoData.get(j);
                if (dataBean.getTokenSynbol().equals(synbol)) {
                    flag = true;
                    break;
                } else {
                    flag = false;
                }
            }
            //就代表本地有这条数据
            if (flag) {
                //AssetsDaoUtils.updateBalance(bean, walletAddes);
                dataBean.setBalance(bean.getBalance());
                dataBean.setCoinType(coinType);
                dataBean.update();
            } else {
                AssetsDaoUtils.insertNewAssets(bean, walletAddes);
            }
            tokenSynbolsByLocal.add(bean.getTokenSynbol());
        }

    }


    /**
     * 更新对应币种的价格
     *
     * @param info 获取到的价格
     */
    public void updatePriceTwo(ChainAddressInfo chainAddressInfo, PriceInfo info, String walletAddress) {

      /*  if(selectChainAddressInfo!=null) {

            String coinType = selectChainAddressInfo.getCoinType();

            ToastHelper.showToast("滑动最终停留" + coinType);
        }*/


        //ToastHelper.showToast("要设置的"+chainAddressInfo.getCoinType());

        List<PriceInfo.ResultBean> result = info.getResult();

        assetsInfoDataList = chainAddressInfo.getAssetsInfoDataList();

        cnyPrice = BigDecimal.ZERO;
        usdPrice = BigDecimal.ZERO;

        for (int i = 0; i < result.size(); i++) {
            PriceInfo.ResultBean resultBean = result.get(i);//实时的数据

            String synbol = resultBean.getSymbol();

            boolean flag = false;

            AssetsDeatilInfo dataBean = null;//本地数据

            for (int j = 0; j < assetsInfoDataList.size(); j++) {
                dataBean = assetsInfoDataList.get(j);
                if (dataBean.getTokenSynbol().equals(synbol)) {
                    flag = true;
                    break;
                } else {
                    flag = false;
                }

            }

            String balance = dataBean.getBalance();//市场价格

            String price = resultBean.getCny_price().toPlainString();//人民币

            String priceUSD = resultBean.getUsd_price().toPlainString();//美元

            dataBean.setPriceFlag("0");
            dataBean.setPriceUsdFlag("0");
            //dataBean.update();

            AssetsDetailDaoUtils.updatePrice(dataBean);

            if (flag) {

                String decimal1 = dataBean.getTokenDecimal();

                if (!TextUtils.isEmpty(decimal1)) {

                    BigDecimal value1 = new BigDecimal(balance.toString());

                    double value2 = Math.pow(10, Double.parseDouble(decimal1));//币种精度

                    dataBean.setPriceFlag(price);

                    dataBean.setPriceUsdFlag(priceUSD);

                    BigDecimal decimal = BigDecimalUtils.div(value1.toString(), value2 + "");


                    BigDecimal priceValue = BigDecimalUtils.mul(decimal + "", price.toString());

                    BigDecimal usdPriceValue = BigDecimalUtils.mul(decimal + "", priceUSD.toString());

                    //设置人民币价格
                    dataBean.setPrice(BigDecimalUtils.intercept(priceValue + "", 2).toPlainString());

                    //设置美元价格
                    dataBean.setPriceUSD(BigDecimalUtils.intercept(usdPriceValue + "", 2).toPlainString());

                    //dataBean.update();


                    AssetsDetailDaoUtils.updatePrice(dataBean);

                    cnyPrice = cnyPrice.add(BigDecimalUtils.intercept(priceValue + "", 2));

                    usdPrice = usdPrice.add(BigDecimalUtils.intercept(usdPriceValue + "", 2));

                    //AssetsDaoUtils.updatePrice(dataBean, walletAddress);

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

                //ToastHelper.showToast("相等可以设置");


                assetsInfoDataList = chainAddressInfo.getAssetsInfoDataList();
                assetsAdapter.setNewData(assetsInfoDataList);
                assetsAdapter.notifyDataSetChanged();

                setWalletMoney();
            } else {
                //ToastHelper.showToast("不相等不设置");
            }
        }
    }


    /**
     * 更新对应币种的价格
     *
     * @param info 获取到的价格
     */
    public void updatePrice(PriceInfo info, String walletAddress) {

        List<PriceInfo.ResultBean> result = info.getResult();

        infoData = AssetsDaoUtils.findAllAssetsByWalletAddress(walletAddress);

        cnyPrice = BigDecimal.ZERO;
        usdPrice = BigDecimal.ZERO;

        for (int i = 0; i < result.size(); i++) {
            PriceInfo.ResultBean resultBean = result.get(i);//实时的数据

            String synbol = resultBean.getSymbol();

            boolean flag = false;

            AssetsInfo.DataBean dataBean = null;//本地数据

            for (int j = 0; j < infoData.size(); j++) {
                dataBean = infoData.get(j);
                if (dataBean.getTokenSynbol().equals(synbol)) {
                    flag = true;
                    break;
                } else {
                    flag = false;
                }

            }

            BigDecimal balance = dataBean.getBalance();//市场价格

            BigDecimal price = resultBean.getCny_price();//人民币

            BigDecimal priceUSD = resultBean.getUsd_price();//美元

            dataBean.setPriceFlag(new BigDecimal("0"));
            dataBean.setPriceUsdFlag(new BigDecimal("0"));
            dataBean.update();

            if (flag) {

                String decimal1 = dataBean.getTokenDecimal();

                if (!TextUtils.isEmpty(decimal1)) {

                    BigDecimal value1 = new BigDecimal(balance.toString());

                    double value2 = Math.pow(10, Double.parseDouble(decimal1));//币种精度

                    dataBean.setPriceFlag(price);

                    dataBean.setPriceUsdFlag(priceUSD);

                    BigDecimal decimal = BigDecimalUtils.div(value1.toString(), value2 + "");


                    BigDecimal priceValue = BigDecimalUtils.mul(decimal + "", price.toString());

                    BigDecimal usdPriceValue = BigDecimalUtils.mul(decimal + "", priceUSD.toString());

                    //设置人民币价格
                    dataBean.setPrice(BigDecimalUtils.intercept(priceValue + "", 2));

                    //设置美元价格
                    dataBean.setPriceUSD(BigDecimalUtils.intercept(usdPriceValue + "", 2));

                    dataBean.update();


                    cnyPrice = cnyPrice.add(BigDecimalUtils.intercept(priceValue + "", 2));

                    usdPrice = usdPrice.add(BigDecimalUtils.intercept(usdPriceValue + "", 2));

                    AssetsDaoUtils.updatePrice(dataBean, walletAddress);
                }

            }

        }

      /*  MultiChainInfo chainInfo = multiChainInfos.get(selectIndex);
        chainInfo.setCnyTotalPrice(cnyPrice);
        chainInfo.setUsdtTotalPrice(usdPrice);
        chainInfo.update();*/

        infoData = AssetsDaoUtils.findAllAssetsByWalletAddress(walletAddress);
        //assetsAdapter.setNewData(infoData);
        assetsAdapter.notifyDataSetChanged();

        setWalletMoney();
    }


    private void setWalletMoney() {


        viewpager.refresh();
        //viewpager.setCurrentItem(selectIndex);
    }


    /**
     * 根据账户ID 查询地址信息
     */
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


    /**
     * 根据账户ID 查询地址信息
     */
    public void findAddressInfoTwo() {


        selectIndex = 0;
        //获取钱包信息
        allWallet = WalletInfoDaoUtils.findAllWallet();

        //获取钱包
        walletInfo = allWallet.get(0);
        //获取账户ID
        accountId = walletInfo.getAccountId();

        //根据账户Id获取多链信息
        chainInfos = ChainInfoDaoUtils.findAllChainByAccountIsSelect(accountId);


        int ethValue = SharePreUtil.getIntValue(context, ChainAddressCreateManager.ETH_COIN_TYPE, 0);
        int neoValue = SharePreUtil.getIntValue(context, ChainAddressCreateManager.NEO_COIN_TYPE, 0);
        int ontValue = SharePreUtil.getIntValue(context, ChainAddressCreateManager.ONT_COIN_TYPE, 0);

        /*multiChainInfos.clear();
        List<MultiChainInfo> infoList = MultiChainInfoDaoUtils.loadAll();
        for (int i = 0; i < chainInfos.size(); i++) {
            if (chainInfos.get(i).getChaType().equals(ChainAddressCreateManager.ETH_COIN_TYPE)) {
                multiChainInfos.add(infoList.get(ethValue));
                continue;
            }
            if (chainInfos.get(i).getChaType().equals(ChainAddressCreateManager.NEO_COIN_TYPE)) {
                multiChainInfos.add(infoList.get(neoValue));
                continue;
            }

            if (chainInfos.get(i).getChaType().equals(ChainAddressCreateManager.ONT_COIN_TYPE)) {
                multiChainInfos.add(infoList.get(ontValue));
                continue;
            }
        }
*/

        //findAddressInfo();
        if (chainInfos != null && chainInfos.size() > 0) {

            // walletAdapter = new WalletAdapter(context, this.multiChainInfos, unitFlag);


            viewpager.setAdapter(walletAdapter);

            viewpager.setCurrentItem(0);

            walletName.setText(chainInfos.get(selectIndex).getChaName());
        }

        viewpager.setOffscreenPageLimit(5);
        viewpager.setOnPageChangeListener(this);


        //默认为第一条数据的资产
        chainAddress = chainAddressInfos.get(0).getAddress();


        //根据地址查询本地资产余额
        infoData = AssetsDaoUtils.findAllAssetsByWalletAddress(chainAddress);


        if (infoData != null) {
            //assetsAdapter = new AssetsAdapter(infoData);
            assetsAdapter.setOnItemClickListener(this);
            assetsAdapter.setOnItemChildClickListener(this);
            recyclerview.setAdapter(assetsAdapter);
        }

    }

    /**
     * 获取筛选数据
     *
     * @param srcData
     * @param isHide
     * @param coninType
     * @return
     */
    public List<AssetsDeatilInfo> getFilterData(List<AssetsDeatilInfo> srcData, boolean isHide, String coninType) {

        List<AssetsDeatilInfo> newsDatas = new ArrayList<>();

        //隐藏为0的资产
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

        //ToastHelper.showToast("onPageSelected");

        //long currentTime = Calendar.getInstance().getTimeInMillis();

        //exitTime = System.currentTimeMillis();

        i = i % chainAddressInfos.size();

        if (selectIndex != i) {

            findType = 0;
            findTypeNeo = 0;
            findTypeOnt = 0;
            selectIndex = i;

            chainDataInfo = walletSupportChains.get(selectIndex);

            if(chainDataInfo!=null) {

                if(chainAddressInfos!=null && chainAddressInfos.size()>0) {
                    addressInfo = chainAddressInfos.get(i);

                    if(addressInfo!=null) {

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

                currentTime = currentTime - lastClickTime;

                //ToastHelper.showToast("正在被用户拖拽滑动状态");

                break;

            case ViewPager.SCROLL_STATE_SETTLING:
                //ToastHelper.showToast("【注】被用户拖动并松手后，ViewPager自动滑动，即将归于停止的状态");
                break;

            case ViewPager.SCROLL_STATE_IDLE:
                // ToastHelper.showToast("【注】被用户滑动后的最终静止状态"+(currentTime - lastClickTime) );

                selectChainAddressInfo = chainAddressInfos.get(selectIndex);

               /* if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
                    lastClickTime = currentTime;
                    if (addressInfo != null) {*/
               if(addressInfo!=null) {
                   if(!TextUtils.isEmpty(addressInfo.getAddress())) {
                       requestDataTwo(addressInfo, addressInfo.getAddress());
                   }
               }
                /*    }
                } else {
                    currentTime = Calendar.getInstance().getTimeInMillis();
                }*/


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
                    //gotoDetail(filterData, position);
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
            /**
             * 更多
             */
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
