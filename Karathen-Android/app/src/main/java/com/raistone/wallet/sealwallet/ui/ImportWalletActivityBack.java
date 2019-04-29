package com.raistone.wallet.sealwallet.ui;

import android.os.Bundle;
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
import com.raistone.wallet.sealwallet.datavases.AssetsDaoUtils;
import com.raistone.wallet.sealwallet.datavases.ChainInfoDaoUtils;
import com.raistone.wallet.sealwallet.datavases.LocalDataUtils;
import com.raistone.wallet.sealwallet.datavases.WalletInfoDaoUtils;
import com.raistone.wallet.sealwallet.entity.AssetsInfo;
import com.raistone.wallet.sealwallet.entity.ChainInfo;
import com.raistone.wallet.sealwallet.entity.MultiChainInfo;
import com.raistone.wallet.sealwallet.entity.WalletInfo;
import com.raistone.wallet.sealwallet.utils.ChainAddressCreateManager;
import com.raistone.wallet.sealwallet.utils.CommonUtils;
import com.raistone.wallet.sealwallet.utils.GsonUtils;
import com.raistone.wallet.sealwallet.utils.IconCreateUtils;
import com.raistone.wallet.sealwallet.utils.Md5Utils;
import com.raistone.wallet.sealwallet.utils.MultiChainCreateManager;
import com.raistone.wallet.sealwallet.utils.ToastHelper;
import com.raistone.wallet.sealwallet.widget.TitleBar;
import java.util.Arrays;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@Route(value = "ImportWalletActivityBack")
public class ImportWalletActivityBack extends BaseActivity {

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

    private WalletInfo walletInfo;

    private List<String> chainDatas = Arrays.asList("ETH", "NEO", "ONT");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_wallet);
        ButterKnife.bind(this);

        //StatusBarUtil.setTransparent(this);

        ActivityManager.getInstance().pushActivity(this);

        setTitle(titleBar, getString(R.string.import_wallet), true);

        String json = CommonUtils.getJson("assets.json", this);

        AssetsInfo assetsInfo = GsonUtils.decodeJSON(json, AssetsInfo.class);

        List<AssetsInfo.DataBean> result = assetsInfo.getResult();
        for (AssetsInfo.DataBean bean : result) {
            bean.insert();
        }

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
        switch (view.getId()) {

            case R.id.btn_import:

                btnImport.setClickable(false);
                mnemonics = mnemonics.trim();
                String[] words = mnemonics.split(" ");

                if (words.length < 12) {
                    btnImport.setClickable(true);
                    ToastHelper.showToast(getResources().getString(R.string.please_enter_a_valid_mnemonic));
                    return;
                }

                String accountId = Md5Utils.md5(mnemonics);

                for (int i = 0; i < chainDatas.size(); i++) {
                    ChainInfo chainInfo = new ChainInfo();
                    chainInfo.setAccountId(accountId);
                    chainInfo.setChaName(chainDatas.get(i));
                    chainInfo.setChaType(chainDatas.get(i));
                    chainInfo.setShow(true);
                    chainInfo.setOrderInfo(i);
                    chainInfo.insert();
                }

                List<ChainInfo> chainInfos = ChainInfoDaoUtils.findAllChains();

                for (int i = 0; i < chainInfos.size(); i++) {
                    ChainInfo chainInfo = chainInfos.get(i);

                    if (chainInfo.getChaType().equals(MultiChainCreateManager.ETH_COIN_TYPE)) {

                        List<String> strings = Arrays.asList(words);

                        walletInfo = new WalletInfo();

                        walletInfo.setWalletPwd(Md5Utils.md5(mPassWord));

                        walletInfo.setMnemonic(mnemonics);

                        walletInfo.setAccountId(Md5Utils.md5(mnemonics));

                        walletInfo.insert();

                        MultiChainInfo eth = null;

                        try {
                            eth = MultiChainCreateManager.importMnemonic("ETH-1", strings, mPassWord);
                        } catch (Exception e) {
                            if (TextUtils.equals("mnemonicMsg", e.getMessage())) {
                                btnImport.setClickable(true);
                                ToastHelper.showToast(getResources().getString(R.string.please_enter_a_valid_mnemonic));
                            } else {
                                btnImport.setClickable(true);
                                ToastHelper.showToast(getResources().getString(R.string.import_failed));
                            }
                        }


                        if (eth != null) {
                            btnImport.setClickable(false);
                            eth.setType_flag(ChainAddressCreateManager.ETH_JAXX_TYPE);
                            eth.setImport(false);
                            eth.setCurrent(true);
                            eth.setAccount(true);
                            eth.setCoinType("ETH");
                            eth.setImagePath(IconCreateUtils.getIcon());
                            eth.setAccountId(accountId);
                            eth.insert();
                            LocalDataUtils.setAddresByEthAssets(eth.getAddress(), this);
                        }
                        continue;
                    }

                    if (chainInfo.getChaType().equals(MultiChainCreateManager.NEO_COIN_TYPE)) {
                        MultiChainInfo neo = MultiChainCreateManager.importMnemonicByNeoOrOnt("NEO-1", MultiChainCreateManager.NEO_JAXX_TYPE, mnemonics, MultiChainCreateManager.NEO_COIN_TYPE);

                        if (neo != null) {
                            neo.setImport(false);
                            neo.setCoinType(MultiChainCreateManager.NEO_COIN_TYPE);
                            neo.setCurrent(true);
                            neo.setImagePath(IconCreateUtils.getIcon());
                            neo.setAccountId(WalletInfoDaoUtils.getAccountId());
                            neo.setAccountId(accountId);
                            neo.insert();
                            LocalDataUtils.setAddresByNeoAssets(neo.getAddress(), this);

                        }
                        continue;
                    }

                    if (chainInfo.getChaType().equals(MultiChainCreateManager.ONT_COIN_TYPE)) {
                        MultiChainInfo ont = MultiChainCreateManager.importMnemonicByNeoOrOnt("ONT-1", MultiChainCreateManager.ONT_JAXX_TYPE, mnemonics, MultiChainCreateManager.ONT_COIN_TYPE);
                        if (ont != null) {
                            ont.setImport(false);
                            ont.setCoinType(MultiChainCreateManager.ONT_COIN_TYPE);
                            ont.setCurrent(true);
                            ont.setImagePath(IconCreateUtils.getIcon());
                            ont.setAccountId(WalletInfoDaoUtils.getAccountId());
                            ont.setAccountId(accountId);
                            ont.insert();
                            LocalDataUtils.setAddresByOntAssets(ont.getAddress(), this);

                        }
                        continue;
                    }
                }

                Router.build("MainActivity").go(this);

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

    public void setAddresByAssets(String address) {

        List<AssetsInfo.DataBean> allAssets = AssetsDaoUtils.findAllAssets();
        for (int i = 0; i < allAssets.size(); i++) {
            AssetsInfo.DataBean dataBean = allAssets.get(i);
            dataBean.setWalletAddress(address);
            dataBean.update();
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
