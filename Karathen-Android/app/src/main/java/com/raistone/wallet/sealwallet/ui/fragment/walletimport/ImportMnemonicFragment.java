package com.raistone.wallet.sealwallet.ui.fragment.walletimport;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chenenyu.router.Router;
import com.raistone.wallet.sealwallet.R;
import com.raistone.wallet.sealwallet.daoutils.ChainAddressDaoUtils;
import com.raistone.wallet.sealwallet.daoutils.HdWalletDaoUtils;
import com.raistone.wallet.sealwallet.datavases.LocalDataUtils;
import com.raistone.wallet.sealwallet.datavases.WalletDaoUtils;
import com.raistone.wallet.sealwallet.factory.ChainAddressInfo;
import com.raistone.wallet.sealwallet.factory.ChainDataInfo;
import com.raistone.wallet.sealwallet.factory.HdWallet;
import com.raistone.wallet.sealwallet.factory.WalletFactoryManager;
import com.raistone.wallet.sealwallet.ui.ActivityManager;
import com.raistone.wallet.sealwallet.ui.WebViewActivity;
import com.raistone.wallet.sealwallet.ui.fragment.BaseFragment;
import com.raistone.wallet.sealwallet.utils.ChainAddressCreateManager;
import com.raistone.wallet.sealwallet.utils.Constant;
import com.raistone.wallet.sealwallet.utils.ETHWalletUtils;
import com.raistone.wallet.sealwallet.utils.IconCreateUtils;
import com.raistone.wallet.sealwallet.utils.LocalManageUtil;
import com.raistone.wallet.sealwallet.utils.Md5Utils;
import com.raistone.wallet.sealwallet.utils.MultiChainCreateManager;
import com.raistone.wallet.sealwallet.utils.SPUtil;
import com.raistone.wallet.sealwallet.utils.ToastHelper;
import com.raistone.wallet.sealwallet.widget.SmoothCheckBox;

import org.bitcoinj.crypto.MnemonicCode;
import org.greenrobot.eventbus.EventBus;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static android.app.Activity.RESULT_OK;


/**
 * 导入助记词
 */
public class ImportMnemonicFragment extends BaseFragment {


    @BindView(R.id.import_mnemonic_tv)
    TextView importMnemonicTv;
    @BindView(R.id.import_keystore_tv)
    TextView importKeystoreTv;
    @BindView(R.id.import_private_tv)
    TextView importPrivateTv;
    @BindView(R.id.import_wif_tv)
    TextView importWifTv;
    @BindView(R.id.mnemonic_or_privatekey_ed)
    EditText mnemonicOrPrivatekeyEd;
    @BindView(R.id.wallet_name_ed)
    EditText walletNameEd;
    @BindView(R.id.pwd_ed)
    EditText pwdEd;
    @BindView(R.id.pwd_confirm_ed)
    EditText pwdConfirmEd;
    @BindView(R.id.pwd_tips_ed)
    EditText pwdTipsEd;

    @BindView(R.id.import_wallet_btn)
    Button importWalletBtn;
    @BindView(R.id.create_tv)
    TextView createTv;
    Unbinder unbinder;

    private Context context;

    private String walletName,mnemonics,mPassWord,mConfirmPw;
    private boolean mCheckStatus=true;
    private boolean isshowpass=false;

    private ProgressDialog progressDialog;

    private String[] words;

    private HdWallet walletInfo;

    private List<String> chainDatas = Arrays.asList("ETH", "NEO", "ONT");

    private boolean isAdd;



    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
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


                                    String[] words = mnemonics.split(" ");

                                    List<String> strings = Arrays.asList(words);

                                    ChainAddressInfo eth = null;

                                    try {
                                        eth = ChainAddressCreateManager.importMnemonic("ETH-1", strings, mPassWord);
                                    } catch (Exception e) {
                                        progressDialog.dismiss();
                                        if (TextUtils.equals("mnemonicMsg", e.getMessage())) {
                                            importWalletBtn.setClickable(true);
                                            Looper.prepare();
                                            ToastHelper.showToast(getResources().getString(R.string.please_enter_a_valid_mnemonic));
                                            Looper.loop();
                                        } else {
                                            importWalletBtn.setClickable(true);
                                            Looper.prepare();
                                            ToastHelper.showToast(getResources().getString(R.string.import_failed));
                                            Looper.loop();
                                        }
                                        progressDialog.dismiss();
                                    }

                                    if (eth != null) {
                                        importWalletBtn.setClickable(false);
                                        eth.setType_flag(ETHWalletUtils.ETH_JAXX_TYPE);
                                        eth.setIsImport(false);
                                        eth.setIsCurrent(true);
                                        eth.setAccount(true);//是否是主账号
                                        eth.setCoinType("ETH");
                                        eth.setMnemonic(mnemonics);
                                        eth.setImportType(4);
                                        eth.setImagePath(IconCreateUtils.getIcon());
                                        eth.setAccountId(walletInfo.getAccountId());
                                        eth.setUsdtTotalPrice("0");
                                        eth.setCnyTotalPrice("0");
                                        eth.setChainId(dataInfo.getId());
                                        eth.setSelectStatus(false);

                                        ChainAddressDaoUtils.insertNewAddress(eth);

                                        //LocalDataUtils.setAddresByEthAssets(eth.getAddress(), context);

                                        LocalDataUtils.setAddresByAssets(walletInfo.getAccountId(),"assets.json", eth.getId(), eth.getAddress(), context);
                                    }
                                    continue;

                                }

                                if (dataInfo.getChainType().equals(MultiChainCreateManager.NEO_COIN_TYPE)) {


                                    ChainAddressInfo neo = ChainAddressCreateManager.generateMnemonicByNeoOrOnt("NEO-1", mnemonics, ChainAddressCreateManager.NEO_COIN_TYPE, ChainAddressCreateManager.NEO_JAXX_TYPE);
                                    neo.setWalletType(1);
                                    neo.setAccountId(walletInfo.getAccountId());
                                    neo.setPassword(Md5Utils.md5(mPassWord));
                                    neo.setIsCurrent(true);
                                    neo.setCnyTotalPrice("0");
                                    neo.setUsdtTotalPrice("0");
                                    neo.setChainId(dataInfo.getId());
                                    neo.setSelectStatus(false);
                                    neo.setMnemonic(mnemonics);
                                    neo.setImportType(4);
                                    neo.setIsImport(false);
                                    ChainAddressDaoUtils.insertNewAddress(neo);

                                    LocalDataUtils.setAddresByAssets(walletInfo.getAccountId(),"neo_assets.json", neo.getId(), neo.getAddress(), context);

                                    continue;
                                }

                                if (dataInfo.getChainType().equals(MultiChainCreateManager.ONT_COIN_TYPE)) {

                                    ChainAddressInfo ont = ChainAddressCreateManager.generateMnemonicByNeoOrOnt("ONT-1", mnemonics, ChainAddressCreateManager.ONT_COIN_TYPE, ChainAddressCreateManager.ONT_JAXX_TYPE);
                                    ont.setWalletType(1);
                                    ont.setAccountId(walletInfo.getAccountId());
                                    ont.setPassword(Md5Utils.md5(mPassWord));
                                    ont.setIsCurrent(true);
                                    ont.setCnyTotalPrice("0");
                                    ont.setUsdtTotalPrice("0");
                                    ont.setChainId(dataInfo.getId());
                                    ont.setImportType(4);
                                    ont.setIsImport(false);
                                    ont.setMnemonic(mnemonics);
                                    ont.setSelectStatus(false);
                                    ChainAddressDaoUtils.insertNewAddress(ont);

                                    LocalDataUtils.setAddresByAssets(walletInfo.getAccountId(),"ont_assets.json", ont.getId(), ont.getAddress(), context);

                                    continue;
                                }
                            }


                            progressDialog.dismiss();

                            walletInfo.setMnemonic(mnemonics);
                            walletInfo.update();
                            if(!isAdd) {
                                Router.build("MainActivity").go(context);
                            }else {
                                EventBus.getDefault().post(walletInfo);
                                ActivityManager.getInstance().finishAllActivity();
                            }

                        }
                    }.start();
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_import_mnemonic, container, false);
        unbinder = ButterKnife.bind(this, view);
        context=getActivity();

        isAdd=getActivity().getIntent().getBooleanExtra("isAdd",false);
        initViews();
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0 && resultCode == RESULT_OK) {
            if (data != null) {
                String code = data.getStringExtra("message");
                //mnemonicOrPrivatekeyEd.setText(code);
                mnemonicOrPrivatekeyEd.setText(code);
            }
        }

    }

    public void initViews(){


        mnemonicOrPrivatekeyEd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                String s = charSequence.toString();

                if (s.length() > 0) {
                    mnemonics = s;
                    updateCreateBtnStatus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        walletNameEd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                String s = charSequence.toString();

                if (s.length() > 0) {
                    walletName = s;
                    updateCreateBtnStatus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        pwdEd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                String s = charSequence.toString();

                if (s.length() > 0) {
                    mPassWord = s;
                    updateCreateBtnStatus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        pwdConfirmEd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                String s = charSequence.toString();

                if (s.length() > 0) {
                    mConfirmPw = s;
                    updateCreateBtnStatus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.service_tv, R.id.service_ll, R.id.import_wallet_btn, R.id.create_tv})
    public void onViewClicked(View view) {
        int savedLanguageType = SPUtil.getInstance(context).getSelectLanguage();
        switch (view.getId()) {

            case R.id.import_wallet_btn:
                /**
                 * 导入钱包
                 */

                String tips = pwdTipsEd.getText().toString().trim()+"";


                if (!TextUtils.isEmpty(mnemonics)) {
                    mnemonics = mnemonics.toLowerCase().trim();
                    words = mnemonics.split(" ");

                    if (words.length < 12) {
                        importWalletBtn.setClickable(true);
                        ToastHelper.showToast(getResources().getString(R.string.please_enter_a_valid_mnemonic));
                        return;
                    }

                    boolean menmonic = HdWalletDaoUtils.checkRepeatByMenmonic(mnemonics);

                    if (menmonic) {
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }
                        //HdWalletDaoUtils.deleteWalletById(walletInfo.getWalletId());
                        ToastHelper.showToast(getResources().getString(R.string.existing_address));
                        return;
                    }
                    //importWalletBtn.setClickable(false);

                    //效验助记词
                    try {
                        byte[] mnemonicSeedBytes = MnemonicCode.INSTANCE.toEntropy(Arrays.asList(words));

                        progressDialog = new ProgressDialog(context);
                        progressDialog.setMessage(getResources().getString(R.string.importing_wallet));

                        progressDialog.setCancelable(false);

                        progressDialog.show();

                        walletInfo = (HdWallet) WalletFactoryManager.getInstance().createWallet(0);

                        walletInfo.setIsBackup(true);

                        walletInfo.setAccountId(Md5Utils.md5(mnemonics));

                        walletInfo.setMnemonic(mnemonics);

                        walletInfo.setWalletName(walletName);

                        walletInfo.setIsImport(false);


                        walletInfo.setIsHDWallet(true);

                        walletInfo.setImportType(0);

                        walletInfo.setPwdTips(tips);

                        walletInfo.setWalletPwd(Md5Utils.md5(mPassWord));

                        HdWalletDaoUtils.updateWalletPwd(walletInfo);

                        HdWalletDaoUtils.updateCurrent(walletInfo.getWalletId());

                        walletInfo.createChain(walletInfo.getWalletId(), chainDatas);


                        Message msg = new Message();
                        msg.what = 0;
                        handler.sendMessage(msg);
                    } catch (Exception e) {
                        ToastHelper.showToast(getResources().getString(R.string.please_enter_a_valid_mnemonic));
                    }

                }


                break;
            case R.id.create_tv:
                /**
                 * 创建钱包
                 */
                getActivity().finish();
                break;
        }
    }
    private void updateCreateBtnStatus() {
        if (checkPassWord(walletName,mnemonics, mPassWord, mConfirmPw, mCheckStatus)) {
            importWalletBtn.setBackground(getResources().getDrawable(R.drawable.btn_bg));
            importWalletBtn.setClickable(true);
        } else {
            importWalletBtn.setBackground(getResources().getDrawable(R.drawable.btn_gray_bg));
            importWalletBtn.setClickable(false);
        }
    }

    private boolean checkPassWord(String walletName,String mnemonics, String pw, String confirmPw, boolean agreement) {

        if (TextUtils.isEmpty(walletName)) {
            return false;
        }

        if (TextUtils.isEmpty(mnemonics)) {
            return false;
        }

        if (TextUtils.isEmpty(pw) || TextUtils.isEmpty(confirmPw)) {
            return false;
        }

        if (pw.trim().length() < 6 || confirmPw.trim().toString().trim().length() < 6) {
            return false;
        }

        if (!TextUtils.equals(pw, confirmPw)) {
            return false;
        }
        if (!agreement) {
            return false;
        }
        return true;
    }
}
