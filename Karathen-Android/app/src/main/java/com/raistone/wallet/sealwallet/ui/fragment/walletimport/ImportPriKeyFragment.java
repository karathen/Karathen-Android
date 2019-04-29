package com.raistone.wallet.sealwallet.ui.fragment.walletimport;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.chenenyu.router.Router;
import com.raistone.wallet.sealwallet.R;
import com.raistone.wallet.sealwallet.daoutils.ChainAddressDaoUtils;
import com.raistone.wallet.sealwallet.daoutils.HdWalletDaoUtils;
import com.raistone.wallet.sealwallet.datavases.LocalDataUtils;
import com.raistone.wallet.sealwallet.factory.ChainAddressInfo;
import com.raistone.wallet.sealwallet.factory.ChainDataInfo;
import com.raistone.wallet.sealwallet.factory.HdWallet;
import com.raistone.wallet.sealwallet.factory.WalletFactoryManager;
import com.raistone.wallet.sealwallet.ui.ActivityManager;
import com.raistone.wallet.sealwallet.ui.fragment.BaseFragment;
import com.raistone.wallet.sealwallet.utils.ChainAddressCreateManager;
import com.raistone.wallet.sealwallet.utils.IconCreateUtils;
import com.raistone.wallet.sealwallet.utils.Md5Utils;
import com.raistone.wallet.sealwallet.utils.SPUtil;
import com.raistone.wallet.sealwallet.utils.ToastHelper;
import com.raistone.wallet.sealwallet.widget.BottomMenuPopupWin;
import com.raistone.wallet.sealwallet.widget.WalletManagerPopup;
import org.greenrobot.eventbus.EventBus;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static android.app.Activity.RESULT_OK;


public class ImportPriKeyFragment extends BaseFragment {


    @BindView(R.id.privatekey_ed)
    EditText privatekeyEd;
    @BindView(R.id.coin_na_tv)
    TextView coinNaTv;
    @BindView(R.id.coin_type_ll)
    RelativeLayout coinTypeLl;
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

    private boolean isshowpass = false;

    private boolean mCheckStatus=true, isAdd;
    private ProgressDialog progressDialog;

    private String walletName, priKey, mPassWord, mConfirmPw;

    private HdWallet walletInfo;


    private List<String> chainDatas = new ArrayList<>();

    private int importType = 0;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_import_prikey, container, false);
        unbinder = ButterKnife.bind(this, view);
        context = getActivity();

        isAdd = getActivity().getIntent().getBooleanExtra("isAdd", false);

        initViews();
        return view;
    }


    public void initViews() {

        //keystorePwdEd.setOnTouchListener(this);

        chainDatas.add("ETH");

        privatekeyEd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                String s = charSequence.toString();

                if (s.length() > 0) {
                    priKey = s;
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0 && resultCode == RESULT_OK) {
            if (data != null) {
                String code = data.getStringExtra("message");
                privatekeyEd.setText(code);
            }
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.coin_type_ll, R.id.import_wallet_btn, R.id.create_tv})
    public void onViewClicked(View view) {
        int savedLanguageType = SPUtil.getInstance(context).getSelectLanguage();
        switch (view.getId()) {

            case R.id.coin_type_ll:

                final BottomMenuPopupWin bottomMenuPopupWin = new BottomMenuPopupWin(context, false);

                bottomMenuPopupWin.setOnCommentPopupClickListener(new WalletManagerPopup.OnCommentPopupClickListener() {
                    @Override
                    public void onCommentClick(View v) {
                        switch (v.getId()) {
                            case R.id.eth_tv:
                                bottomMenuPopupWin.dismiss();
                                coinNaTv.setText("ETH");
                                chainDatas.clear();
                                chainDatas.add("ETH");
                                importType = 0;

                                break;
                            case R.id.neo_tv:
                                bottomMenuPopupWin.dismiss();
                                coinNaTv.setText("NEO");
                                chainDatas.clear();
                                chainDatas.add("NEO");
                                importType = 1;
                                break;
                            case R.id.ont_tv:
                                bottomMenuPopupWin.dismiss();
                                coinNaTv.setText("ONT");
                                chainDatas.clear();
                                chainDatas.add("ONT");
                                importType = 2;
                                break;
                        }
                    }
                });
                bottomMenuPopupWin.showPopupWindow();

                hideKeyboard(coinTypeLl);

                break;

            case R.id.import_wallet_btn:

                String tips = pwdTipsEd.getText().toString().trim() + "";


                try {
                    progressDialog = new ProgressDialog(context);
                    progressDialog.setMessage(getResources().getString(R.string.importing_wallet));

                    progressDialog.setCancelable(false);

                    progressDialog.show();

                    walletInfo = (HdWallet) WalletFactoryManager.getInstance().createWallet(0);

                    walletInfo.setIsBackup(true);

                    walletInfo.setAccountId(Md5Utils.md5(priKey + coinNaTv.getText().toString()));

                    walletInfo.setPrivateScrect(priKey);

                    walletInfo.setWalletName(walletName);

                    walletInfo.setIsImport(true);


                    walletInfo.setIsHDWallet(true);

                    walletInfo.setImportType(2);

                    walletInfo.setPwdTips(tips);

                    walletInfo.setWalletPwd(Md5Utils.md5(mPassWord));

                    HdWalletDaoUtils.updateWalletPwd(walletInfo);

                    HdWalletDaoUtils.updateCurrent(walletInfo.getWalletId());

                    walletInfo.createChain(walletInfo.getWalletId(), chainDatas);

                    importWalletByPrice(importType, priKey, walletName, mPassWord, tips);

                } catch (Exception e) {
                    ToastHelper.showToast(getResources().getString(R.string.please_enter_a_valid_mnemonic));
                }

                break;

            case R.id.create_tv:
                getActivity().finish();
                break;
        }
    }


    public void importWalletByPrice(final int coinType, final String privateKye, final String waName, final String pwd, final String tips) {
        Observable.create(new ObservableOnSubscribe<ChainAddressInfo>() {
            @Override
            public void subscribe(ObservableEmitter<ChainAddressInfo> e) throws Exception {

                List<ChainDataInfo> dataInfos = walletInfo.getChainDataInfos();
                ChainDataInfo dataInfo = dataInfos.get(0);

                String newPrivateKey = "";

                if (privateKye.trim().startsWith("0x")) {
                    newPrivateKey = privateKye.substring(2).trim();
                } else {
                    newPrivateKey = privateKye;
                }

                if (newPrivateKey.trim().length() < 60) {
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                    }
                    HdWalletDaoUtils.deleteWalletById(walletInfo.getWalletId());
                    Looper.prepare();
                    ToastHelper.showToast(getResources().getString(R.string.input_private_key_string));
                    Looper.loop();
                    return;
                }

                try {

                    if (coinType == 0) {


                        ChainAddressInfo wallet = ChainAddressCreateManager.loadWalletByPrivateKey("ETH-1", newPrivateKey, pwd);


                        if (wallet != null) {
                            boolean b = ChainAddressDaoUtils.checkAddressEqAccountiD("ETH",walletInfo.getAccountId(),wallet.getAddress());

                            if (b) {
                                if (progressDialog != null) {
                                    progressDialog.dismiss();
                                }
                                HdWalletDaoUtils.deleteWalletById(walletInfo.getWalletId());
                                Looper.prepare();
                                ToastHelper.showToast(R.string.existing_address);
                                Looper.loop();
                                return;
                            }

                            wallet.setType_flag(ChainAddressCreateManager.ETH_JAXX_TYPE);
                            wallet.setIsImport(false);
                            wallet.setIsCurrent(true);
                            wallet.setAccount(true);//是否是主账号
                            wallet.setCoinType("ETH");
                            wallet.setPrivateScrect(privateKye);
                            wallet.setImportType(1);
                            wallet.setImagePath(IconCreateUtils.getIcon());
                            wallet.setAccountId(walletInfo.getAccountId());
                            wallet.setUsdtTotalPrice("0");
                            wallet.setCnyTotalPrice("0");
                            wallet.setChainId(dataInfo.getId());
                            wallet.setSelectStatus(false);

                            dataInfo.resetChainAddressInfos();

                            ChainAddressDaoUtils.insertNewAddress(wallet);

                            LocalDataUtils.setAddresByAssets(walletInfo.getAccountId(),"assets.json", wallet.getId(), wallet.getAddress(), context);
                            //EventBus.getDefault().post(wallet);
                            if (progressDialog != null) {
                                if (progressDialog != null) {
                                    progressDialog.dismiss();
                                }
                            }
                            e.onNext(wallet);

                        } else {
                            progressDialog.dismiss();
                            HdWalletDaoUtils.deleteWalletById(walletInfo.getWalletId());
                            Looper.prepare();
                            ToastHelper.showToast(getResources().getString(R.string.import_failed));
                            Looper.loop();
                        }

                    }

                    if (coinType == 1) {

                        ChainAddressInfo wallet = ChainAddressCreateManager.generateByPrivateKeyNeoOrOnt(newPrivateKey, "NEO-1", "NEO");

                        if (wallet != null) {

                            boolean b = ChainAddressDaoUtils.checkAddressEqAccountiD("NEO",walletInfo.getAccountId(),wallet.getAddress());

                            if (b) {
                                if (progressDialog != null) {
                                    progressDialog.dismiss();
                                }
                                HdWalletDaoUtils.deleteWalletById(walletInfo.getWalletId());
                                Looper.prepare();
                                ToastHelper.showToast(R.string.existing_address);
                                Looper.loop();
                                return;
                            }

                            wallet.setIsImport(true);
                            wallet.setIsCurrent(true);
                            wallet.setImportType(1);
                            wallet.setPassword(Md5Utils.md5(pwd));
                            wallet.setImagePath(IconCreateUtils.getIcon());
                            wallet.setAccountId(dataInfo.getAccountId());
                            wallet.setCnyTotalPrice("0");
                            wallet.setPrivateScrect(newPrivateKey);
                            wallet.setUsdtTotalPrice("0");
                            wallet.setChainId(dataInfo.getId());

                            ChainAddressDaoUtils.insertNewAddress(wallet);

                            dataInfo.resetChainAddressInfos();

                            LocalDataUtils.setAddresByAssets(walletInfo.getAccountId(),"neo_assets.json", wallet.getId(), wallet.getAddress(), context);


                            if (progressDialog != null) {
                                progressDialog.dismiss();
                            }

                            e.onNext(wallet);
                        } else {
                            if (progressDialog != null) {
                                progressDialog.dismiss();
                            }
                            HdWalletDaoUtils.deleteWalletById(walletInfo.getWalletId());
                            Looper.prepare();
                            ToastHelper.showToast(getResources().getString(R.string.input_private_key_string));
                            Looper.loop();
                        }

                    }


                    if (coinType == 2) {
                        ChainAddressInfo wallet = ChainAddressCreateManager.generateByPrivateKeyNeoOrOnt(newPrivateKey, "ONT-1", "ONT");

                        if (wallet != null) {

                            boolean b = ChainAddressDaoUtils.checkAddressEqAccountiD("ONT",walletInfo.getAccountId(),wallet.getAddress());
                            if (b) {
                                if (progressDialog != null) {
                                    progressDialog.dismiss();
                                }
                                Looper.prepare();
                                ToastHelper.showToast(R.string.existing_address);
                                Looper.loop();
                                HdWalletDaoUtils.deleteWalletById(walletInfo.getWalletId());
                                return;
                            }

                            wallet.setIsImport(true);
                            wallet.setIsCurrent(true);
                            wallet.setImportType(1);
                            wallet.setPassword(Md5Utils.md5(pwd));
                            wallet.setImagePath(IconCreateUtils.getIcon());
                            wallet.setAccountId(dataInfo.getAccountId());
                            wallet.setCnyTotalPrice("0");
                            wallet.setPrivateScrect(newPrivateKey);
                            wallet.setUsdtTotalPrice("0");
                            wallet.setChainId(dataInfo.getId());

                            ChainAddressDaoUtils.insertNewAddress(wallet);

                            dataInfo.resetChainAddressInfos();

                            LocalDataUtils.setAddresByAssets(walletInfo.getAccountId(),"ont_assets.json", wallet.getId(), wallet.getAddress(), context);


                            if (progressDialog != null) {
                                progressDialog.dismiss();
                            }
                            e.onNext(wallet);
                        } else {
                            if (progressDialog != null) {
                                progressDialog.dismiss();
                            }
                            HdWalletDaoUtils.deleteWalletById(walletInfo.getWalletId());
                            Looper.prepare();
                            ToastHelper.showToast(getResources().getString(R.string.input_private_key_string));
                            Looper.loop();
                        }
                    }

                } catch (Exception ex) {
                    progressDialog.dismiss();

                }

//                e.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ChainAddressInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ChainAddressInfo wallet) {
                        //mView.loadSuccess(wallet);
                        progressDialog.dismiss();
                        ToastHelper.showToast(getResources().getString(R.string.import_succ_string));

                        if (!isAdd) {
                            Router.build("MainActivity").go(context);
                        } else {
                            EventBus.getDefault().post(walletInfo);
                            ActivityManager.getInstance().finishAllActivity();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        HdWalletDaoUtils.deleteWalletById(walletInfo.getWalletId());
                        ToastHelper.showToast(getResources().getString(R.string.import_failed));

                        progressDialog.dismiss();
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }


    private void updateCreateBtnStatus() {
        if (checkPassWord(walletName, priKey, mPassWord, mConfirmPw, mCheckStatus)) {
            importWalletBtn.setBackground(getResources().getDrawable(R.drawable.btn_bg));
            importWalletBtn.setClickable(true);
        } else {
            importWalletBtn.setBackground(getResources().getDrawable(R.drawable.btn_gray_bg));
            importWalletBtn.setClickable(false);
        }
    }

    private boolean checkPassWord(String walletName, String mnemonics, String pw, String confirmPw, boolean agreement) {

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
