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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
import org.greenrobot.eventbus.EventBus;
import java.util.Arrays;
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

public class ImportKeystoreFragment extends BaseFragment implements View.OnTouchListener {


    Unbinder unbinder;
    @BindView(R.id.import_mnemonic_tv)
    TextView importMnemonicTv;
    @BindView(R.id.import_keystore_tv)
    TextView importKeystoreTv;
    @BindView(R.id.import_private_tv)
    TextView importPrivateTv;
    @BindView(R.id.import_wif_tv)
    TextView importWifTv;
    @BindView(R.id.keystroe_ed)
    EditText keystroeEd;
    @BindView(R.id.wallet_name_ed)
    EditText walletNameEd;
    @BindView(R.id.keystore_pwd_ed)
    EditText keystorePwdEd;
    @BindView(R.id.pwd_confirm_ed)
    EditText pwdConfirmEd;
    @BindView(R.id.pwd_tips_ed)
    EditText pwdTipsEd;

    @BindView(R.id.import_keystort_wallet_btn)
    Button importKeystortWalletBtn;
    @BindView(R.id.create_tv)
    TextView createTv;

    private Context context;

    private boolean isshowpass = false;

    private ProgressDialog progressDialog;

    private String walletName, keystoreValue, mPassWord;
    private boolean mCheckStatus=true, isAdd;

    private HdWallet walletInfo;

    private List<String> chainDatas = Arrays.asList("ETH");


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_import_keystore, container, false);
        unbinder = ButterKnife.bind(this, view);
        context = getActivity();
        isAdd = getActivity().getIntent().getBooleanExtra("isAdd", false);
        initViews();
        return view;
    }


    public void initViews() {

        keystorePwdEd.setOnTouchListener(this);


        keystroeEd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                String s = charSequence.toString();

                if (s.length() > 0) {
                    keystoreValue = s;
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

        keystorePwdEd.addTextChangedListener(new TextWatcher() {
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

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0 && resultCode == RESULT_OK) {
            if (data != null) {
                String code = data.getStringExtra("message");
                keystroeEd.setText(code);
            }
        }

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.import_keystort_wallet_btn, R.id.create_tv})
    public void onViewClicked(View view) {
        int savedLanguageType = SPUtil.getInstance(context).getSelectLanguage();
        switch (view.getId()) {

            case R.id.import_keystort_wallet_btn:

                String tips = pwdTipsEd.getText().toString().trim() + "";


                try {

                    progressDialog = new ProgressDialog(context);
                    progressDialog.setMessage(getResources().getString(R.string.importing_wallet));

                    progressDialog.setCancelable(false);

                    progressDialog.show();

                    walletInfo = (HdWallet) WalletFactoryManager.getInstance().createWallet(0);

                    walletInfo.setIsBackup(true);

                    walletInfo.setAccountId(Md5Utils.md5(keystoreValue + "ETH"));

                    walletInfo.setKeystore(keystoreValue);

                    walletInfo.setWalletName(walletName);

                    walletInfo.setIsImport(true);


                    walletInfo.setIsHDWallet(true);

                    walletInfo.setImportType(1);

                    walletInfo.setPwdTips(tips);

                    walletInfo.setWalletPwd(Md5Utils.md5(mPassWord));

                    HdWalletDaoUtils.updateWalletPwd(walletInfo);

                    HdWalletDaoUtils.updateCurrent(walletInfo.getWalletId());

                    walletInfo.createChain(walletInfo.getWalletId(), chainDatas);


                    loadWalletByKeystore("ETH-1", keystoreValue, mPassWord);

                } catch (Exception e) {
                    ToastHelper.showToast(getResources().getString(R.string.please_enter_a_valid_mnemonic));
                }


                break;
            case R.id.create_tv:
                getActivity().finish();
                break;
        }
    }

    public void loadWalletByKeystore(final String walName, final String keystore, final String pwd) {
        Observable.create(new ObservableOnSubscribe<ChainAddressInfo>() {
            @Override
            public void subscribe(ObservableEmitter<ChainAddressInfo> e) throws Exception {
                try {

                    List<ChainDataInfo> dataInfos = walletInfo.getChainDataInfos();
                    ChainDataInfo dataInfo = dataInfos.get(0);
                    ChainAddressInfo eth = ChainAddressCreateManager.loadWalletByKeystore("ETH-1", keystore, pwd);

                    if (eth != null) {

                        boolean b = ChainAddressDaoUtils.checkAddressEqAccountiD("ETH",walletInfo.getAccountId(),eth.getAddress());

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


                        importKeystortWalletBtn.setClickable(false);
                        eth.setType_flag(ChainAddressCreateManager.ETH_JAXX_TYPE);
                        eth.setIsImport(false);
                        eth.setIsCurrent(true);
                        eth.setAccount(true);
                        eth.setCoinType("ETH");
                        eth.setImportType(1);
                        eth.setImagePath(IconCreateUtils.getIcon());
                        eth.setAccountId(walletInfo.getAccountId());
                        eth.setUsdtTotalPrice("0");
                        eth.setCnyTotalPrice("0");
                        eth.setChainId(dataInfo.getId());
                        eth.setSelectStatus(false);

                        ChainAddressDaoUtils.insertNewAddress(eth);

                        LocalDataUtils.setAddresByAssets(walletInfo.getAccountId(),"assets.json", eth.getId(), eth.getAddress(), context);

                        e.onNext(eth);
                    } else {
                        progressDialog.dismiss();
                        HdWalletDaoUtils.deleteWalletById(walletInfo.getWalletId());
                        Looper.prepare();
                        ToastHelper.showToast(getResources().getString(R.string.import_failed));
                        Looper.loop();
                    }
                } catch (Exception ex) {
                    progressDialog.dismiss();
                    HdWalletDaoUtils.deleteWalletById(walletInfo.getWalletId());
                    Looper.prepare();
                    ToastHelper.showToast(getResources().getString(R.string.import_failed));
                    Looper.loop();

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


    @Override
    public boolean onTouch(View view, MotionEvent event) {
        if ((view.getId() == R.id.keystroe_ed && canVerticalScroll(keystroeEd))) {
            view.getParent().requestDisallowInterceptTouchEvent(true);
            if (event.getAction() == MotionEvent.ACTION_UP) {
                view.getParent().requestDisallowInterceptTouchEvent(false);
            }
        }
        return false;
    }


    private boolean canVerticalScroll(EditText editText) {
        int scrollY = editText.getScrollY();
        int scrollRange = editText.getLayout().getHeight();
        int scrollExtent = editText.getHeight() - editText.getCompoundPaddingTop() - editText.getCompoundPaddingBottom();
        int scrollDifference = scrollRange - scrollExtent;

        if (scrollDifference == 0) {
            return false;
        }

        return (scrollY > 0) || (scrollY < scrollDifference - 1);
    }

    private void updateCreateBtnStatus() {
        if (checkPassWord(walletName, keystoreValue, mPassWord, mCheckStatus)) {
            importKeystortWalletBtn.setBackground(getResources().getDrawable(R.drawable.btn_bg));
            importKeystortWalletBtn.setClickable(true);
        } else {
            importKeystortWalletBtn.setBackground(getResources().getDrawable(R.drawable.btn_gray_bg));
            importKeystortWalletBtn.setClickable(false);
        }
    }

    private boolean checkPassWord(String walletName, String mnemonics, String pw, boolean agreement) {

        if (TextUtils.isEmpty(walletName)) {
            return false;
        }

        if (TextUtils.isEmpty(mnemonics)) {
            return false;
        }

        if (TextUtils.isEmpty(pw)) {
            return false;
        }

        if (!agreement) {
            return false;
        }
        return true;
    }
}


