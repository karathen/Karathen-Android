package com.raistone.wallet.sealwallet.ui;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.chenenyu.router.Router;
import com.chenenyu.router.annotation.Route;
import com.raistone.wallet.sealwallet.R;
import com.raistone.wallet.sealwallet.daoutils.ChainAddressDaoUtils;
import com.raistone.wallet.sealwallet.daoutils.HdWalletDaoUtils;
import com.raistone.wallet.sealwallet.datavases.LocalDataUtils;
import com.raistone.wallet.sealwallet.factory.ChainAddressInfo;
import com.raistone.wallet.sealwallet.factory.ChainDataInfo;
import com.raistone.wallet.sealwallet.factory.HdWallet;
import com.raistone.wallet.sealwallet.factory.WalletFactoryManager;
import com.raistone.wallet.sealwallet.utils.ChainAddressCreateManager;
import com.raistone.wallet.sealwallet.utils.IconCreateUtils;
import com.raistone.wallet.sealwallet.utils.Md5Utils;
import com.raistone.wallet.sealwallet.utils.MultiChainCreateManager;
import com.raistone.wallet.sealwallet.utils.SPUtil;
import com.raistone.wallet.sealwallet.utils.ToastHelper;
import com.raistone.wallet.sealwallet.widget.TitleBar;
import org.bitcoinj.crypto.MnemonicCode;
import java.util.Arrays;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@Route(value = "ImportWalletActivity")
public class ImportWalletActivity extends BaseActivity {

    @BindView(R.id.titleBar)
    TitleBar titleBar;
    @BindView(R.id.et_words)
    EditText etWords;
    @BindView(R.id.et_set_pin)
    EditText etSetPin;
    @BindView(R.id.et_confirm_pin)
    EditText etConfirmPin;
    @BindView(R.id.service_tv)
    TextView serviceTv;
    @BindView(R.id.btn_import)
    Button btnImport;
    @BindView(R.id.tv_create)
    TextView tvCreate;

    boolean mCheckStatus=true, isFormCreate;

    private String mnemonics, mPassWord, mConfirmPw;

    private HdWallet walletInfo;

    private List<String> chainDatas = Arrays.asList("ETH", "NEO", "ONT");

    private ProgressDialog progressDialog;

    private String[] words;

    private Context context;

    boolean isshowpass=false;

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
                                            btnImport.setClickable(true);
                                            Looper.prepare();
                                            ToastHelper.showToast(getResources().getString(R.string.please_enter_a_valid_mnemonic));
                                            Looper.loop();
                                        } else {
                                            btnImport.setClickable(true);
                                            Looper.prepare();
                                            ToastHelper.showToast(getResources().getString(R.string.import_failed));
                                            Looper.loop();
                                        }
                                        progressDialog.dismiss();
                                    }

                                    if (eth != null) {
                                        btnImport.setClickable(false);
                                        eth.setType_flag(ChainAddressCreateManager.ETH_JAXX_TYPE);
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

                            Router.build("MainActivity").go(context);

                        }
                    }.start();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_wallet);
        ButterKnife.bind(this);


        ActivityManager.getInstance().pushActivity(this);

        setTitle(titleBar, getString(R.string.import_wallet), true);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getResources().getString(R.string.importing_wallet));

        context = this;

        isFormCreate = getIntent().getBooleanExtra("isFormCreate", false);

        etSetPin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String s = charSequence.toString();

                if (s.length() > 0) {
                    mPassWord = s;
                    updateCreateBtnStatus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        etConfirmPin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String s = charSequence.toString().trim();
                if (s.length() > 0) {
                    mConfirmPw = s;
                    updateCreateBtnStatus();

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        etWords.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String s = charSequence.toString().trim();
                if (s.length() > 0) {
                    mnemonics = s;
                    updateCreateBtnStatus();

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });



    }

    @Override
    protected void onResume() {
        super.onResume();
        btnImport.setClickable(true);
    }

    @OnClick({R.id.service_tv, R.id.btn_import, R.id.tv_create})
    public void onViewClicked(View view) {
        int savedLanguageType = SPUtil.getInstance(this).getSelectLanguage();
        switch (view.getId()) {

            case R.id.btn_import:

                btnImport.setClickable(false);
                if (!TextUtils.isEmpty(mnemonics)) {
                    mnemonics = mnemonics.trim();
                    words = mnemonics.split(" ");

                    if (words.length < 12) {
                        btnImport.setClickable(true);
                        ToastHelper.showToast(getResources().getString(R.string.please_enter_a_valid_mnemonic));
                        return;
                    }

                    try {
                        byte[] mnemonicSeedBytes = MnemonicCode.INSTANCE.toEntropy(Arrays.asList(words));

                        progressDialog.setCancelable(false);

                        progressDialog.show();

                        walletInfo = (HdWallet) WalletFactoryManager.getInstance().createWallet(WalletFactoryManager.HEWALLET_TYPE);

                        walletInfo.setIsBackup(false);

                        walletInfo.setAccountId(Md5Utils.md5(mnemonics));

                        walletInfo.setMnemonic(mnemonics);

                        walletInfo.setWalletName("wallet-01");

                        walletInfo.setWalletPwd(Md5Utils.md5(mPassWord));

                        HdWalletDaoUtils.updateWalletPwd(walletInfo);

                        walletInfo.createChain(walletInfo.getWalletId(), chainDatas);


                        Message msg = new Message();
                        msg.what = 0;
                        handler.sendMessage(msg);
                    } catch (Exception e) {
                        ToastHelper.showToast(getResources().getString(R.string.please_enter_a_valid_mnemonic));
                    }

                }

                break;

            case R.id.tv_create:
                if (isFormCreate) {
                    finish();
                } else {
                    Router.build("CreateWalletActivity").go(this);
                }
                break;

        }
    }


    private void updateCreateBtnStatus() {
        if (checkPassWord(mnemonics, mPassWord, mConfirmPw, mCheckStatus)) {
            btnImport.setBackground(getResources().getDrawable(R.drawable.btn_bg));
            btnImport.setClickable(true);
        } else {
            btnImport.setBackground(getResources().getDrawable(R.drawable.btn_gray_bg));
            btnImport.setClickable(false);
        }
    }

    private boolean checkPassWord(String mnemonics, String pw, String confirmPw, boolean agreement) {
        if (TextUtils.isEmpty(mnemonics)) {
            return false;
        }

        if (TextUtils.isEmpty(pw) || TextUtils.isEmpty(confirmPw)) {
            return false;
        }

        if (pw.trim().length() != 6 || confirmPw.trim().toString().trim().length() != 6) {
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
