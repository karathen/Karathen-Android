package com.raistone.wallet.sealwallet.ui;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chenenyu.router.Router;
import com.chenenyu.router.annotation.Route;
import com.raistone.wallet.sealwallet.R;
import com.raistone.wallet.sealwallet.adapter.MnemonicAdapter;
import com.raistone.wallet.sealwallet.adapter.MnemonicVerifyAdapter;
import com.raistone.wallet.sealwallet.daoutils.ChainAddressDaoUtils;
import com.raistone.wallet.sealwallet.daoutils.HdWalletDaoUtils;
import com.raistone.wallet.sealwallet.datavases.LocalDataUtils;
import com.raistone.wallet.sealwallet.entity.WordsBean;
import com.raistone.wallet.sealwallet.factory.ChainAddressInfo;
import com.raistone.wallet.sealwallet.factory.ChainDataInfo;
import com.raistone.wallet.sealwallet.factory.HdWallet;
import com.raistone.wallet.sealwallet.factory.WalletFactoryManager;
import com.raistone.wallet.sealwallet.utils.ChainAddressCreateManager;
import com.raistone.wallet.sealwallet.utils.ETHWalletUtils;
import com.raistone.wallet.sealwallet.utils.Md5Utils;
import com.raistone.wallet.sealwallet.utils.MultiChainCreateManager;
import com.raistone.wallet.sealwallet.utils.StatusBarUtil;
import com.raistone.wallet.sealwallet.utils.ToastHelper;
import com.raistone.wallet.sealwallet.widget.TitleBar;
import org.greenrobot.eventbus.EventBus;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * 助记词验证
 */

@Route(value = "MnemonicVerifyActivity")
public class MnemonicVerifyActivity extends BaseActivity {

    @BindView(R.id.titleBar)
    TitleBar titleBar;
    @BindView(R.id.jump_tv)
    TextView jumpTv;
    @BindView(R.id.newRecyclerview)
    RecyclerView newRecyclerview;
    @BindView(R.id.oldRecyclerView)
    RecyclerView oldRecyclerView;
    @BindView(R.id.complete_btn)
    Button completeBtn;

    private String words;

    private MnemonicVerifyAdapter mnemonicVerifyAdapter;

    private MnemonicAdapter mnemonicAdapter;

    private List<WordsBean> datas = new ArrayList<>();

    private List<String> newsDatas = new ArrayList<>();
    private List<String> flagDatas = new ArrayList<>();
    private List<String> codes;

    private HdWallet ethWallet;

    private String mPassWord;


    private List<String> chainDatas = Arrays.asList("ETH", "NEO", "ONT");

    private String codesStr,walletTips,walletName;

    private Context context;

    private String accountId;

    private ProgressDialog progressDialog;

    private boolean isBack,isAdd;

    private HdWallet walletInfo;

    private int walletType;

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(final Message msg) {
            super.handleMessage(msg);
            final int what = msg.what;
            switch (what) {
                case 0:

                    new Thread() {
                        @Override
                        public void run() {
                            super.run();

                            List<ChainDataInfo> chainDataInfos = walletInfo.getChainDataInfos();

                            for (int i = 0; i < chainDataInfos.size(); i++) {

                                ChainDataInfo dataInfo = chainDataInfos.get(i);

                                if (dataInfo.getChainType().equals(ChainAddressCreateManager.ETH_COIN_TYPE)) {

                                    ChainAddressInfo eth = ChainAddressCreateManager.importMnemonic(codes, mPassWord);

                                    eth.setCoinType(MultiChainCreateManager.ETH_COIN_TYPE);
                                    eth.setIsImport(false);
                                    eth.setName("ETH-1");
                                    eth.setIsHDWallet(true);
                                    eth.setSelectStatus(true);
                                    eth.setCnyTotalPrice("0");
                                    eth.setUsdtTotalPrice("0");
                                    eth.setIsCurrent(true);
                                    eth.setType_flag(ETHWalletUtils.ETH_JAXX_TYPE);
                                    eth.setWalletType(1);
                                    eth.setChainId(dataInfo.getId());
                                    eth.setAccountId(Md5Utils.md5(codesStr));
                                    ChainAddressDaoUtils.insertNewAddress(eth);

                                    LocalDataUtils.setAddresByAssets(Md5Utils.md5(codesStr),"assets.json",eth.getId(),eth.getAddress() ,context);

                                    continue;
                                }

                                if (dataInfo.getChainType().equals(MultiChainCreateManager.NEO_COIN_TYPE)) {


                                    ChainAddressInfo neo = ChainAddressCreateManager.generateMnemonicByNeoOrOnt("NEO-1", codesStr, MultiChainCreateManager.NEO_COIN_TYPE, MultiChainCreateManager.NEO_JAXX_TYPE);
                                    neo.setWalletType(1);
                                    neo.setAccountId(accountId);
                                    neo.setPassword(Md5Utils.md5(mPassWord));
                                    neo.setIsCurrent(true);
                                    neo.setCnyTotalPrice("0");
                                    neo.setUsdtTotalPrice("0");
                                    neo.setChainId(dataInfo.getId());
                                    neo.setSelectStatus(false);
                                    ChainAddressDaoUtils.insertNewAddress(neo);

                                    //LocalDataUtils.setAddresByNeoAssets(neo.getAddress(), context);

                                    LocalDataUtils.setAddresByAssets(Md5Utils.md5(codesStr),"neo_assets.json",neo.getId(),neo.getAddress() ,context);

                                    continue;
                                }

                                if (dataInfo.getChainType().equals(MultiChainCreateManager.ONT_COIN_TYPE)) {

                                    ChainAddressInfo ont = ChainAddressCreateManager.generateMnemonicByNeoOrOnt("ONT-1", codesStr, MultiChainCreateManager.ONT_COIN_TYPE, MultiChainCreateManager.ONT_JAXX_TYPE);
                                    ont.setWalletType(1);
                                    ont.setAccountId(Md5Utils.md5(codesStr));
                                    ont.setPassword(Md5Utils.md5(mPassWord));
                                    ont.setIsCurrent(true);
                                    ont.setCnyTotalPrice("0");
                                    ont.setUsdtTotalPrice("0");
                                    ont.setChainId(dataInfo.getId());
                                    ont.setSelectStatus(false);
                                    ChainAddressDaoUtils.insertNewAddress(ont);

                                    LocalDataUtils.setAddresByAssets(Md5Utils.md5(codesStr),"ont_assets.json",ont.getId(),ont.getAddress() ,context);

                                    continue;
                                }
                            }


                            progressDialog.dismiss();

                            if(!isAdd) {
                                Router.build("MainActivity").go(context);
                            }else {
                                EventBus.getDefault().post(walletInfo);
                                ActivityManager.getInstance().finishAllActivity();
                            }
                        }
                    }.start();


                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mnemonic_verify);
        ButterKnife.bind(this);
        //StatusBarUtil.setTransparent(this);
        ActivityManager.getInstance().pushActivity(this);
        setTitle(titleBar, getResources().getString(R.string.verification_mnemonic), true);

        context = this;

        words = getIntent().getStringExtra("words");

        mPassWord = getIntent().getStringExtra("mPassWord");

        codesStr = getIntent().getStringExtra("codesStr");

        walletTips = getIntent().getStringExtra("walletTips");

        walletName = getIntent().getStringExtra("walletName");

        walletType = getIntent().getIntExtra("walletType",0);

        ethWallet = (HdWallet) getIntent().getSerializableExtra("ethWallet");

        isBack=getIntent().getBooleanExtra("isBack",false);//是否是备份
        isAdd=getIntent().getBooleanExtra("isAdd",false);//是否是备份


        initData();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getResources().getString(R.string.creating_wallet));
        progressDialog.setCancelable(false);
    }

    public void initData() {

        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        GridLayoutManager oldlayoutManager = new GridLayoutManager(this, 3);

        mnemonicAdapter = new MnemonicAdapter(newsDatas);

        mnemonicAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

               /* String s = newsDatas.get(position);

                for (int i = 0; i < datas.size(); i++) {
                    if (datas.get(i).getWord().equals(s)) {
                        datas.get(i).setSelect(false);
                    }
                }
                mnemonicVerifyAdapter.notifyDataSetChanged();
                newsDatas.remove(newsDatas.get(position));
                flagDatas.remove(flagDatas.get(position));
                mnemonicAdapter.notifyDataSetChanged();*/

                String s = flagDatas.get(position);

                for (int i = 0; i < datas.size(); i++) {
                    if (datas.get(i).getFlag().equals(s)) {
                        datas.get(i).setSelect(false);
                    }
                }
                mnemonicVerifyAdapter.notifyDataSetChanged();
                newsDatas.remove(newsDatas.get(position));
                flagDatas.remove(flagDatas.get(position));
                mnemonicAdapter.notifyDataSetChanged();

            }
        });

        newRecyclerview.setLayoutManager(layoutManager);

        newRecyclerview.setAdapter(mnemonicAdapter);

        oldRecyclerView.setLayoutManager(oldlayoutManager);


        if (!TextUtils.isEmpty(words)) {
            String[] split = words.split(" ");

            for (int i = 0; i < split.length; i++) {
                WordsBean wordsBean = new WordsBean(split[i], false,i,split[i]+i);
                datas.add(wordsBean);
            }

            //datas = Arrays.asList(split);
            Collections.shuffle(datas);

            mnemonicVerifyAdapter = new MnemonicVerifyAdapter(datas);

            oldRecyclerView.setAdapter(mnemonicVerifyAdapter);

            mnemonicVerifyAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

                    datas.get(position).setSelect(true);

                    mnemonicVerifyAdapter.notifyDataSetChanged();

                    if (!flagDatas.contains(datas.get(position).getFlag())) {
                        newsDatas.add(datas.get(position).getWord());
                        flagDatas.add(datas.get(position).getFlag());
                        mnemonicAdapter.setNewData(newsDatas);
                    } else {
                        newsDatas.remove(datas.get(position).getWord());
                        flagDatas.remove(datas.get(position).getFlag());
                        mnemonicAdapter.setNewData(newsDatas);

                        datas.get(position).setSelect(false);
                        mnemonicVerifyAdapter.notifyDataSetChanged();
                    }

                }
            });
        }
    }


    @OnClick({R.id.jump_tv, R.id.complete_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            /**
             * 跳过
             */
            case R.id.jump_tv:

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(getResources().getString(R.string.jump_over));
                builder.setMessage(getResources().getString(R.string.jumo_over_tips));
                builder.setPositiveButton(getResources().getString(R.string.determine_string), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        if(!isBack) {
                            createChain(false);
                        }else{
                            ethWallet.setIsBackup(ethWallet.getIsBackup());
                            HdWalletDaoUtils.updateWallet(ethWallet);
                            ActivityManager.getInstance().finishAllActivity();
                        }
                    }
                });
                builder.setNegativeButton(R.string.cancel_string, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();

                break;
            /**
             * 完成
             */
            case R.id.complete_btn:

                String newWords = getNewWords(newsDatas);

                String oldWords = getOldWords(words);

                if (!TextUtils.isEmpty(newWords) && !TextUtils.isEmpty(oldWords)) {

                    if (TextUtils.equals(oldWords, newWords)) {
                        /*ethWallet.setBackup(true);
                        ethWallet.update();*/
                        if(!isBack) {
                            createChain(true);
                        }else {
                            ethWallet.setIsBackup(true);
                            HdWalletDaoUtils.updateWallet(ethWallet);
                            ActivityManager.getInstance().finishAllActivity();
                        }
                    } else {
                        ToastHelper.showToast(getResources().getString(R.string.please_check_your_mnemonic));
                    }
                }else {
                    ToastHelper.showToast(getResources().getString(R.string.please_check_your_mnemonic));
                }


                break;
        }
    }

    private void createChain(boolean isBack) {
        progressDialog.show();

    /*    walletInfo = new WalletInfo();
        walletInfo.setBackup(isBack);*/

        walletInfo = (HdWallet) WalletFactoryManager.getInstance().createWallet(walletType);

        walletInfo.setIsBackup(isBack);

        accountId = Md5Utils.md5(codesStr);

        walletInfo.setAccountId(accountId);

        walletInfo.setMnemonic(codesStr);

        walletInfo.setPwdTips(walletTips);

        walletInfo.setWalletName(walletName);

        walletInfo.setWalletPwd(Md5Utils.md5(mPassWord));

        HdWalletDaoUtils.updateWalletPwd(walletInfo);


        HdWalletDaoUtils.updateCurrent(walletInfo.getWalletId());

        walletInfo.createChain(walletInfo.getWalletId(),chainDatas);


        String[] mnemon = codesStr.split(" ");

        codes = Arrays.asList(mnemon);

        Message msg = new Message();
        msg.what = 0;

        handler.sendMessage(msg);
    }

    public String getOldWords(String words) {
        String[] split = words.split(" ");

        List<String> datas = Arrays.asList(split);

        if (words != null && datas.size() == 12) {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < datas.size(); i++) {
                sb.append(datas.get(i) + " ");
            }
            return sb.toString().trim();
        }
        return null;
    }

    public String getNewWords(List<String> words) {
        if (words != null && words.size() == 12) {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < words.size(); i++) {
                sb.append(words.get(i) + " ");
            }
            return sb.toString().trim();
        }
        return null;
    }
}
