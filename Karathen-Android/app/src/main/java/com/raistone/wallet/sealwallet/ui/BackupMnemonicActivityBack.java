package com.raistone.wallet.sealwallet.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import com.chenenyu.router.Router;
import com.chenenyu.router.annotation.Route;
import com.github.ontio.crypto.MnemonicCode;
import com.raistone.wallet.sealwallet.R;
import com.raistone.wallet.sealwallet.adapter.MnemonicAdapter;
import com.raistone.wallet.sealwallet.datavases.AssetsDaoUtils;
import com.raistone.wallet.sealwallet.datavases.ChainInfoDaoUtils;
import com.raistone.wallet.sealwallet.datavases.LocalDataUtils;
import com.raistone.wallet.sealwallet.entity.AssetsInfo;
import com.raistone.wallet.sealwallet.entity.ChainInfo;
import com.raistone.wallet.sealwallet.entity.MultiChainInfo;
import com.raistone.wallet.sealwallet.entity.WalletInfo;
import com.raistone.wallet.sealwallet.utils.ETHWalletUtils;
import com.raistone.wallet.sealwallet.utils.Md5Utils;
import com.raistone.wallet.sealwallet.utils.MultiChainCreateManager;
import com.raistone.wallet.sealwallet.utils.StatusBarUtil;
import com.raistone.wallet.sealwallet.widget.TitleBar;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;

@Route(value = "BackupMnemonicActivityBack")
public class BackupMnemonicActivityBack extends BaseActivity {

    @BindView(R.id.titleBar)
    TitleBar titleBar;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    @BindView(R.id.next_btn)
    Button nextBtn;

    private WalletInfo ethWalletBack;

    private List<String> datas;

    private MnemonicAdapter mnemonicAdapter;

    private String mPassWord;
    private boolean isBack, userCenter;

    private WalletInfo walletInfo;

    private List<String> chainDatas = Arrays.asList("ETH", "NEO", "ONT");

    private String codesStr;

    private MultiChainInfo multiChainInfo;

    private List<ChainInfo> chainInfos;

    private List<String> words;

    private Context context;

    private String accountId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup_mnemonic);
        ButterKnife.bind(this);
        setTitle(titleBar, getResources().getString(R.string.backup_mnemonic), true);

        context=this;
        //StatusBarUtil.setTransparent(this);

        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);

        recyclerview.setLayoutManager(layoutManager);

        ethWalletBack = (WalletInfo) getIntent().getSerializableExtra("mnemonic");

        walletInfo = (WalletInfo) getIntent().getSerializableExtra("walletInfo");

        multiChainInfo = (MultiChainInfo) getIntent().getSerializableExtra("multiChainInfo");

        mPassWord = getIntent().getStringExtra("mPassWord");

        isBack = getIntent().getBooleanExtra("isBack", false);
        userCenter = getIntent().getBooleanExtra("userCenter", false);

        if (isBack) {
            nextBtn.setVisibility(View.GONE);
        } else {
            nextBtn.setVisibility(View.VISIBLE);
        }

        ActivityManager.getInstance().pushActivity(this);

        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        nextBtn.setClickable(true);
    }

    public void initData() {


        //判断是否是由备份过来
        if (!isBack && !userCenter) {

            walletInfo = new WalletInfo();
            walletInfo.setWalletPwd(Md5Utils.md5(mPassWord));

            codesStr = MnemonicCode.generateMnemonicCodesStr();

            accountId = Md5Utils.md5(codesStr);

            //生成链表

            for (int i = 0; i < chainDatas.size(); i++) {
                ChainInfo chainInfo = new ChainInfo();
                chainInfo.setAccountId(accountId);
                chainInfo.setChaName(chainDatas.get(i));
                chainInfo.setChaType(chainDatas.get(i));
                chainInfo.setOrderInfo(i);
                chainInfo.setShow(true);
                chainInfo.insert();
            }

            //根据链创建地址
            chainInfos = ChainInfoDaoUtils.findAllChains();

            String[] mnemon = codesStr.split(" ");

            words = Arrays.asList(mnemon);

            Observable
                    .empty()
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnComplete(new Action() {
                        @Override
                        public void run() throws Exception {
                            for (int i = 0; i < chainInfos.size(); i++) {
                                ChainInfo chainInfo = chainInfos.get(i);

                                if (chainInfo.getChaType().equals(MultiChainCreateManager.ETH_COIN_TYPE)) {

                                    MultiChainInfo eth = MultiChainCreateManager.importMnemonic(words, mPassWord);
                                    eth.setCoinType(MultiChainCreateManager.ETH_COIN_TYPE);
                                    eth.setImport(false);
                                    eth.setName("ETH-1");
                                    eth.setHDWallet(true);
                                    eth.setCurrent(true);
                                    eth.setType_flag(ETHWalletUtils.ETH_JAXX_TYPE);
                                    eth.setWalletType(1);
                                    eth.setAccountId(Md5Utils.md5(codesStr));
                                    LocalDataUtils.setAddresByEthAssets(eth.getAddress(), context);
                                    setAddresByAssets(eth.getCoinType(), eth.getAddress());
                                    eth.insert();
                                    continue;
                                }

                                if (chainInfo.getChaType().equals(MultiChainCreateManager.NEO_COIN_TYPE)) {
                                    MultiChainInfo neo = MultiChainCreateManager.generateMnemonicByNeoOrOnt("NEO-1", codesStr, MultiChainCreateManager.NEO_COIN_TYPE, MultiChainCreateManager.NEO_JAXX_TYPE);
                                    neo.setWalletType(1);
                                    neo.setAccountId(accountId);
                                    neo.setPassword(Md5Utils.md5(mPassWord));
                                    neo.setCurrent(true);
                                    LocalDataUtils.setAddresByNeoAssets(neo.getAddress(), context);
                                    neo.insert();

                                    continue;
                                }

                                if (chainInfo.getChaType().equals(MultiChainCreateManager.ONT_COIN_TYPE)) {
                                    MultiChainInfo ont = MultiChainCreateManager.generateMnemonicByNeoOrOnt("ONT-1", codesStr, MultiChainCreateManager.ONT_COIN_TYPE, MultiChainCreateManager.ONT_JAXX_TYPE);
                                    ont.setWalletType(1);
                                    ont.setAccountId(Md5Utils.md5(codesStr));
                                    ont.setPassword(Md5Utils.md5(mPassWord));
                                    ont.setCurrent(true);
                                    LocalDataUtils.setAddresByOntAssets(ont.getAddress(), context);
                                    ont.insert();
                                    continue;
                                }
                            }
                        }
                    }).subscribe();

            if (words != null) {
                mnemonicAdapter = new MnemonicAdapter(words);
                recyclerview.setAdapter(mnemonicAdapter);
            }

            walletInfo.setMnemonic(codesStr);
            walletInfo.setAccountId(Md5Utils.md5(codesStr));

            walletInfo.insert();
        } else {

            datas = new ArrayList<>();
            if (ethWalletBack != null) {
                String mnemonic = ethWalletBack.getMnemonic();

                String[] split = mnemonic.split(" ");

                datas = Arrays.asList(split);
            }
            if (datas != null) {
                mnemonicAdapter = new MnemonicAdapter(datas);
                recyclerview.setAdapter(mnemonicAdapter);
            }

            if (multiChainInfo != null) {
                String mnemonic = multiChainInfo.getMnemonic();

                String[] split = mnemonic.split(" ");

                datas = Arrays.asList(split);
            }
            if (datas != null) {
                mnemonicAdapter = new MnemonicAdapter(datas);
                recyclerview.setAdapter(mnemonicAdapter);
            }

        }


    }

    /**
     * 判断创建类型
     *
     * @param coinType
     * @param address
     */

    public void setAddresByAssets(String coinType, String address) {

        List<AssetsInfo.DataBean> allAssets = AssetsDaoUtils.findAllAssets();
        for (int i = 0; i < allAssets.size(); i++) {
            AssetsInfo.DataBean dataBean = allAssets.get(i);
            if (coinType.equals(dataBean.getCoinType())) {
                dataBean.setWalletAddress(address);
                dataBean.update();
                continue;
            }
        }
    }


    @OnClick(R.id.next_btn)
    public void onViewClicked() {

        nextBtn.setClickable(false);
        /**
         * 下一步
         */
        //是否是创建进入
        if (!isBack && !userCenter) {
            //EventBus.getDefault().post(new MessageEvent(ethWallet.getAddress()));
            Router.build("MnemonicVerifyActivity").with("words", codesStr).with("ethWallet",walletInfo).go(this);
            return;
        }
        if (ethWalletBack != null && userCenter) {
            //EventBus.getDefault().post(new MessageEvent(ethWalletBack.getAddress()));
            Router.build("MnemonicVerifyActivity").with("ethWallet", ethWalletBack).with("words", ethWalletBack.getMnemonic()).go(this);
        }
    }
}
