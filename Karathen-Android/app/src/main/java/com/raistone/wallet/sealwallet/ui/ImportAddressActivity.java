package com.raistone.wallet.sealwallet.ui;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.raistone.wallet.sealwallet.utils.ChainAddressCreateManager;
import com.raistone.wallet.sealwallet.utils.IconCreateUtils;
import com.raistone.wallet.sealwallet.utils.Md5Utils;
import com.raistone.wallet.sealwallet.utils.StatusBarUtil;
import com.raistone.wallet.sealwallet.utils.ToastHelper;
import com.raistone.wallet.sealwallet.widget.TitleBar;
import com.unistrong.yang.zb_permission.Permission;
import com.unistrong.yang.zb_permission.ZbPermission;
import java.util.Arrays;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 导入地址
 */

@Route(value = "ImportAddressActivity")
public class ImportAddressActivity extends BaseActivity {

    @BindView(R.id.titleBar)
    TitleBar titleBar;
    @BindView(R.id.mnemonic_tv)
    TextView mnemonicTv;
    @BindView(R.id.private_tv)
    TextView privateTv;
    @BindView(R.id.mnemonic_or_privatekey_ed)
    EditText mnemonicOrPrivatekeyEd;
    @BindView(R.id.wallet_ec)
    EditText walletEc;
    @BindView(R.id.create_btn)
    Button createBtn;
    @BindView(R.id.wif_tv)
    TextView wifTv;
    @BindView(R.id.scaner_code_iv)
    ImageView scanerCodeIv;
    @BindView(R.id.scaner_code_ll)
    LinearLayout scanerCodeLl;

    private ChainAddressInfo ethWallet;
    private long lastId;
    private String coinType;
    private String defName;
    private String pwd;
    private int defMnemonic = 0;//0 助记词 1 私钥导入 2 WIF 导入 默认为助记词导入
    private Context mContext;
    private String pinValue;
    private int privateKeyLength;
    private String privateKey;
    private final int REQUEST_CAMERA = 200;
    private String ethWalletWif, mnemonicOrKey;

    private HdWallet hdWallet;

    private ChainDataInfo selectChain;

    private ProgressDialog progressDialog;

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    importWaallet(coinType, defMnemonic, pinValue, mnemonicOrKey);
                }
            }.start();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_address);
        ButterKnife.bind(this);

        ActivityManager.getInstance().pushActivity(this);

        mContext = this;

        ethWallet = (ChainAddressInfo) getIntent().getSerializableExtra("wallet");

        selectChain = (ChainDataInfo) getIntent().getSerializableExtra("selectChain");

        hdWallet = HdWalletDaoUtils.findWalletBySelect();

        coinType = ethWallet.getCoinType();

        pwd = hdWallet.getWalletPwd();

        lastId = ChainAddressDaoUtils.getCountByType(coinType,hdWallet.getAccountId());

        lastId++;

        defName = coinType + "-" + lastId;

        walletEc.setText(defName);

        privateKeyLength = ethWallet.getPrivateScrect().length();

        ethWalletWif = ethWallet.getWif();

        privateKey = ethWallet.getPrivateScrect();

        setTitle(titleBar, getResources().getString(R.string.import_string) + " " + ethWallet.getCoinType() + " " + getResources().getString(R.string.address_string), true);

        if (!coinType.equals(ChainAddressCreateManager.ETH_COIN_TYPE)) {
            wifTv.setVisibility(View.VISIBLE);
        } else {
            wifTv.setVisibility(View.GONE);
        }


        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getResources().getString(R.string.importing_address));
        progressDialog.setCancelable(false);
    }

    @OnClick({R.id.mnemonic_tv, R.id.private_tv, R.id.create_btn, R.id.wif_tv, R.id.scaner_code_iv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            /**
             * 助记词
             */
            case R.id.mnemonic_tv:
                mnemonicTv.setBackground(getResources().getDrawable(R.drawable.tag_right_top_bg));
                privateTv.setBackground(getResources().getDrawable(R.drawable.tag_unselect_bg));
                wifTv.setBackground(getResources().getDrawable(R.drawable.tag_unselect_bg));
                defMnemonic = 0;
                mnemonicOrPrivatekeyEd.setHint(R.string.input_mnemonic_string);
                break;
            /**
             * 私钥导入
             */
            case R.id.private_tv:
                privateTv.setBackground(getResources().getDrawable(R.drawable.tag_right_top_bg));
                mnemonicTv.setBackground(getResources().getDrawable(R.drawable.tag_unselect_bg));
                wifTv.setBackground(getResources().getDrawable(R.drawable.tag_unselect_bg));
                defMnemonic = 1;
                mnemonicOrPrivatekeyEd.setHint(R.string.mingwen_string);
                break;
            /**
             * wif导入
             */
            case R.id.wif_tv:
                wifTv.setBackground(getResources().getDrawable(R.drawable.tag_right_top_bg));
                privateTv.setBackground(getResources().getDrawable(R.drawable.tag_unselect_bg));
                mnemonicTv.setBackground(getResources().getDrawable(R.drawable.tag_unselect_bg));
                defMnemonic = 2;
                mnemonicOrPrivatekeyEd.setHint("WIF");
                break;
            /**
             * 导入
             */
            case R.id.create_btn:
                mnemonicOrKey = mnemonicOrPrivatekeyEd.getText().toString().trim();
                if (!TextUtils.isEmpty(mnemonicOrKey)) {
                    showDialog(coinType, mnemonicOrKey);
                } else {
                    switch (defMnemonic) {
                        case 0:
                            ToastHelper.showToast(getResources().getString(R.string.mnemonic_cannot_be_empty));
                            break;
                        case 1:
                            ToastHelper.showToast(getResources().getString(R.string.private_cannot_be_empty));
                            break;
                        case 2:
                            ToastHelper.showToast(getResources().getString(R.string.wif_cannot_be_empty));
                            break;
                    }

                }
                break;
            /**
             * 扫码二维码
             */
            case R.id.scaner_code_iv:
                ZbPermission.needPermission(this, REQUEST_CAMERA, Permission.CAMERA, new ZbPermission.ZbPermissionCallback() {
                    @Override
                    public void permissionSuccess(int i) {
                        Router.build("NormalScannerActivity").requestCode(0).go(mContext);
                    }

                    @Override
                    public void permissionFail(int i) {
                        ToastHelper.showToast(getResources().getString(R.string.photo_permission));
                    }
                });

                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0 && resultCode == RESULT_OK) {
            if (data != null) {
                String code = data.getStringExtra("message");
                mnemonicOrPrivatekeyEd.setText(code);
            }
        }
        if (requestCode == 1 && resultCode == RESULT_OK) {


        }
    }

    private void showDialog(final String coinType, final String mnemonicOrKey) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(R.layout.coustom_dialog_layout, null);
        //builer.setView(v);//这里如果使用builer.setView(v)，自定义布局只会覆盖title和button之间的那部分
        final Dialog dialog = builder.create();
        final EditText editText = v.findViewById(R.id.pin_ed);
        TextView textView = v.findViewById(R.id.cancel_tv);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        TextView textView1 = v.findViewById(R.id.confirm_tv);
        textView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pinValue = editText.getText().toString();
                if (!TextUtils.isEmpty(pinValue)) {
                    String value = Md5Utils.md5(pinValue);
                    if (TextUtils.equals(value, pwd)) {

                        dialog.dismiss();

                        progressDialog.show();

                        Message msg = new Message();
                        msg.what = 0;
                        handler.sendMessage(msg);
                        //importWaallet(coinType, defMnemonic, pinValue, mnemonicOrKey);
                    } else {
                        ToastHelper.showToast(getString(R.string.pin_error));
                    }
                } else {
                    ToastHelper.showToast(getString(R.string.pin_error));
                }
            }
        });
        dialog.setCancelable(false);
        dialog.show();
        dialog.getWindow().setContentView(v);

        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);


    }

    /**
     * 导入地址
     *
     * @param coinType 链类型
     * @param type     导入类型
     * @param pwd      密码
     * @param word     助记词
     */
    public void importWaallet(String coinType, int type, String pwd, String word) {

        String name = walletEc.getText().toString();

        if (coinType.equals(ChainAddressCreateManager.ETH_COIN_TYPE)) {

            switch (type) {
                /**
                 * 助记词导入
                 */
                case 0:
                    if (word.split(" ").length == 12) {

                        boolean menmonic = ChainAddressDaoUtils.checkRepeatByMenmonic(word, coinType);

                        if (menmonic) {
                            if (progressDialog != null) {
                                progressDialog.dismiss();
                            }
                            Looper.prepare();
                            ToastHelper.showToast(getResources().getString(R.string.imoprt_tips_one_string));
                            Looper.loop();
                            return;
                        }

                        word.split(" ");

                        List<String> list = Arrays.asList(word.split(" "));

                        ChainAddressInfo chainAddressInfo;
                        try {
                            if (!TextUtils.isEmpty(name)) {

                                chainAddressInfo = ChainAddressCreateManager.importMnemonic(ChainAddressCreateManager.ETH_JAXX_TYPE, name, list, pwd);

                            } else {
                                //chainAddressInfo = ChainAddressCreateManager.importMnemonicByNeoOrOnt(defName, ChainAddressCreateManager.NEO_JAXX_TYPE, word, pwd);
                                chainAddressInfo = ChainAddressCreateManager.importMnemonic(ChainAddressCreateManager.ETH_JAXX_TYPE, defName, list, pwd);
                            }


                        } catch (Exception e) {
                            if (progressDialog != null) {
                                progressDialog.dismiss();
                            }
                            if (TextUtils.equals("mnemonicMsg", e.getMessage())) {
                                Looper.prepare();
                                ToastHelper.showToast(getResources().getString(R.string.imoprt_tips_two_string));
                                Looper.loop();
                                return;
                            } else {

                                Looper.prepare();
                                ToastHelper.showToast(getResources().getString(R.string.import_failed_string));
                                Looper.loop();
                                return;
                            }
                        }


                        if (chainAddressInfo != null) {


                            boolean b = ChainAddressDaoUtils.checkAddressEq(chainAddressInfo.getAddress());
                            if (b) {
                                if (progressDialog != null) {
                                    progressDialog.dismiss();
                                }
                                Looper.prepare();
                                ToastHelper.showToast(R.string.existing_address);
                                Looper.loop();
                                return;
                            }

                            chainAddressInfo.setIsImport(true);
                            chainAddressInfo.setCoinType(coinType);
                            chainAddressInfo.setImportType(0);
                            chainAddressInfo.setImagePath(IconCreateUtils.getIcon());
                            chainAddressInfo.setAccountId(hdWallet.getAccountId());
                            chainAddressInfo.setCnyTotalPrice("0");
                            chainAddressInfo.setUsdtTotalPrice("0");
                            chainAddressInfo.setMnemonic(word);

                            chainAddressInfo.setChainId(selectChain.getId());

                            ChainAddressDaoUtils.insertNewAddress(chainAddressInfo);

                            selectChain.resetChainAddressInfos();

                            LocalDataUtils.setAddresByAssets(hdWallet.getAccountId(),"assets.json", chainAddressInfo.getId(), chainAddressInfo.getAddress(), this);

                            //EventBus.getDefault().post(ethWallet);

                            if (progressDialog != null) {
                                progressDialog.dismiss();
                            }
                            finish();
                        }
                    } else {
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }
                        Looper.prepare();
                        ToastHelper.showToast(getResources().getString(R.string.imoprt_tips_two_string));
                        Looper.loop();
                    }
                    break;
                /**
                 * 私钥
                 */
                case 1:

                    if (word.trim().startsWith("0x")) {
                        word = word.substring(2).trim();
                    }

                    if (word.trim().length() < 60) {

                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }
                        Looper.prepare();
                        ToastHelper.showToast(getResources().getString(R.string.input_private_key_string));
                        Looper.loop();
                        return;
                    }
                    if (word.equals(privateKey)) {
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }
                        Looper.prepare();
                        ToastHelper.showToast(getResources().getString(R.string.repeat_recovery_string));
                        Looper.loop();
                    }
                    ChainAddressInfo wallet = null;
                    if (!TextUtils.isEmpty(name)) {
                        wallet = ChainAddressCreateManager.loadWalletByPrivateKey(name, word, pwd);
                    } else {
                        wallet = ChainAddressCreateManager.loadWalletByPrivateKey(defName, word, pwd);
                    }

                    if (wallet != null) {
                        boolean b = ChainAddressDaoUtils.checkAddressEq(wallet.getAddress());

                        if (b) {
                            if (progressDialog != null) {
                                progressDialog.dismiss();
                            }
                            Looper.prepare();
                            ToastHelper.showToast(R.string.existing_address);
                            Looper.loop();
                            return;
                        }

                        wallet.setCoinType(coinType);
                        wallet.setIsImport(true);
                        wallet.setImportType(1);
                        wallet.setImagePath(IconCreateUtils.getIcon());
                        wallet.setAccountId(hdWallet.getAccountId());
                        wallet.setCnyTotalPrice("0");
                        wallet.setPrivateScrect(word);
                        wallet.setUsdtTotalPrice("0");
                        wallet.setChainId(selectChain.getId());
                        ChainAddressDaoUtils.insertNewAddress(wallet);
                        selectChain.resetChainAddressInfos();
                        LocalDataUtils.setAddresByAssets(hdWallet.getAccountId(),"assets.json", wallet.getId(), wallet.getAddress(), this);
                        //EventBus.getDefault().post(wallet);
                        if (progressDialog != null) {
                            if (progressDialog != null) {
                                progressDialog.dismiss();
                            }
                        }
                        finish();
                    } else {
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }
                        Looper.prepare();
                        ToastHelper.showToast(getResources().getString(R.string.input_private_key_string));
                        Looper.loop();
                    }
                    break;
                /**
                 * wif 导入
                 */
                case 2:
                    break;
            }
        }

        /**
         * NEO 链导入 ONT 链导入
         */
        if (coinType.equals(ChainAddressCreateManager.NEO_COIN_TYPE) || coinType.equals(ChainAddressCreateManager.ONT_COIN_TYPE)) {

            switch (type) {

                case 0:
                    if (word.split(" ").length == 12) {

                        boolean menmonic = ChainAddressDaoUtils.checkRepeatByMenmonic(word, coinType);

                        if (menmonic) {
                            if (progressDialog != null) {
                                progressDialog.dismiss();
                            }
                            Looper.prepare();
                            ToastHelper.showToast(getResources().getString(R.string.imoprt_tips_one_string));
                            Looper.loop();
                            return;
                        }


                        ChainAddressInfo neoChainAddress = null;
                        try {

                            if (!TextUtils.isEmpty(name)) {

                                if (coinType.equals(ChainAddressCreateManager.NEO_COIN_TYPE)) {
                                    neoChainAddress = ChainAddressCreateManager.importMnemonicByNeoOrOnt(name, ChainAddressCreateManager.NEO_JAXX_TYPE, word, coinType);
                                } else {
                                    neoChainAddress = ChainAddressCreateManager.importMnemonicByNeoOrOnt(name, ChainAddressCreateManager.ONT_JAXX_TYPE, word, coinType);
                                }

                            } else {
                                if (coinType.equals(ChainAddressCreateManager.NEO_COIN_TYPE)) {
                                    neoChainAddress = ChainAddressCreateManager.importMnemonicByNeoOrOnt(defName, ChainAddressCreateManager.NEO_JAXX_TYPE, word, coinType);
                                } else {
                                    neoChainAddress = ChainAddressCreateManager.importMnemonicByNeoOrOnt(defName, ChainAddressCreateManager.ONT_JAXX_TYPE, word, coinType);
                                }
                            }


                        } catch (Exception e) {
                            if (progressDialog != null) {
                                progressDialog.dismiss();
                            }
                            if (TextUtils.equals("mnemonicMsg", e.getMessage())) {
                                Looper.prepare();
                                ToastHelper.showToast(getResources().getString(R.string.imoprt_tips_two_string));
                                Looper.loop();
                                return;
                            } else {
                                Looper.prepare();
                                ToastHelper.showToast(getResources().getString(R.string.import_failed_string));
                                Looper.loop();
                                return;
                            }
                        }


                        if (neoChainAddress != null) {


                            boolean b = ChainAddressDaoUtils.checkAddressEq(neoChainAddress.getAddress());

                            if (b) {
                                if (progressDialog != null) {
                                    progressDialog.dismiss();
                                }
                                Looper.prepare();
                                ToastHelper.showToast(R.string.existing_address);
                                Looper.loop();

                                return;
                            }

                            neoChainAddress.setIsImport(true);
                            neoChainAddress.setCoinType(coinType);
                            neoChainAddress.setImportType(0);
                            neoChainAddress.setPassword(Md5Utils.md5(pwd));
                            neoChainAddress.setImagePath(IconCreateUtils.getIcon());
                            neoChainAddress.setCnyTotalPrice("0");
                            neoChainAddress.setMnemonic(word);
                            neoChainAddress.setUsdtTotalPrice("0");
                            neoChainAddress.setAccountId(hdWallet.getAccountId());

                            neoChainAddress.setChainId(selectChain.getId());


                            boolean exist = ChainAddressDaoUtils.chainAddressIsExist(coinType, neoChainAddress.getAddress());

                            if (!exist) {
                                ChainAddressDaoUtils.insertNewAddress(neoChainAddress);
                            } else {
                                if (progressDialog != null) {
                                    progressDialog.dismiss();
                                }
                                Looper.prepare();
                                ToastHelper.showToast(getResources().getString(R.string.existing_address));
                                Looper.loop();
                            }



                            //ChainAddressDaoUtils.insertNewAddress(neoChainAddress);

                            selectChain.resetChainAddressInfos();

                            if (coinType.equals(ChainAddressCreateManager.NEO_COIN_TYPE)) {


                                LocalDataUtils.setAddresByAssets(hdWallet.getAccountId(),"neo_assets.json", neoChainAddress.getId(), neoChainAddress.getAddress(), this);
                            } else {
                                LocalDataUtils.setAddresByAssets(hdWallet.getAccountId(),"ont_assets.json", neoChainAddress.getId(), neoChainAddress.getAddress(), this);
                            }
                            //EventBus.getDefault().post(neoChainAddress);

                            if (progressDialog != null) {
                                progressDialog.dismiss();
                            }
                            finish();
                        }
                    } else {
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }
                        Looper.prepare();
                        ToastHelper.showToast(getResources().getString(R.string.imoprt_tips_two_string));
                        Looper.loop();
                    }
                    break;
                /**
                 * 私钥
                 */
                case 1:
                    if (word.trim().startsWith("0x")) {
                        word = word.substring(2).trim();
                    }
                    if (word.trim().length() != privateKeyLength) {

                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }
                        Looper.prepare();
                        ToastHelper.showToast(getResources().getString(R.string.input_private_key_string));
                        Looper.loop();
                        return;
                    }
                    if (word.equals(privateKey)) {
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }
                        Looper.prepare();
                        ToastHelper.showToast(getResources().getString(R.string.repeat_recovery_string));
                        Looper.loop();
                        return;
                    }

                    ChainAddressInfo wallet = null;

                    if (!TextUtils.isEmpty(name)) {
                        wallet = ChainAddressCreateManager.generateByPrivateKeyNeoOrOnt(word, name, coinType);
                    } else {

                        wallet = ChainAddressCreateManager.generateByPrivateKeyNeoOrOnt(word, defName, coinType);
                    }
                    if (wallet != null) {

                        boolean b = ChainAddressDaoUtils.checkAddressEq(wallet.getCoinType(),wallet.getAddress());

                        if (b) {
                            if (progressDialog != null) {
                                progressDialog.dismiss();
                            }
                            Looper.prepare();
                            ToastHelper.showToast(R.string.existing_address);
                            Looper.loop();
                            return;
                        }

                        wallet.setCoinType(coinType);
                        wallet.setIsImport(true);
                        wallet.setImportType(1);
                        wallet.setPassword(Md5Utils.md5(pwd));
                        wallet.setImagePath(IconCreateUtils.getIcon());
                        wallet.setAccountId(hdWallet.getAccountId());
                        wallet.setCnyTotalPrice("0");
                        wallet.setPrivateScrect(word);
                        wallet.setUsdtTotalPrice("0");
                        wallet.setChainId(selectChain.getId());

                        ChainAddressDaoUtils.insertNewAddress(wallet);

                        selectChain.resetChainAddressInfos();

                        if (coinType.equals(ChainAddressCreateManager.NEO_COIN_TYPE)) {
                            LocalDataUtils.setAddresByAssets(hdWallet.getAccountId(),"neo_assets.json", wallet.getId(), wallet.getAddress(), this);
                        } else {
                            LocalDataUtils.setAddresByAssets(hdWallet.getAccountId(),"ont_assets.json", wallet.getId(), wallet.getAddress(), this);
                        }

                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }
                        //EventBus.getDefault().post(wallet);
                        finish();
                    } else {
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }
                        Looper.prepare();
                        ToastHelper.showToast(getResources().getString(R.string.input_private_key_string));
                        Looper.loop();
                    }
                    break;
                /**
                 * wif 导入
                 */
                case 2:

                    boolean byWif = ChainAddressDaoUtils.checkRepeatByWif(word, coinType);

                    if (word.trim().length() != ethWalletWif.length()) {
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }
                        Looper.prepare();
                        ToastHelper.showToast(getResources().getString(R.string.input_wif_key_string));
                        Looper.loop();
                        return;
                    }
                    if (byWif) {
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }
                        Looper.prepare();
                        ToastHelper.showToast(getResources().getString(R.string.repeat_wif_recovery_string));
                        Looper.loop();
                        return;
                    }

                    ChainAddressInfo chainInfo = null;

                    if (!TextUtils.isEmpty(name)) {
                        chainInfo = ChainAddressCreateManager.importWifByWalletByNeoOrOnt(name, word, coinType);
                    } else {

                        chainInfo = ChainAddressCreateManager.importWifByWalletByNeoOrOnt(word, defName, coinType);
                    }
                    if (chainInfo != null) {


                        boolean addressEq = ChainAddressDaoUtils.checkAddressEq(chainInfo.getCoinType(),chainInfo.getAddress());

                        if (addressEq) {
                            if (progressDialog != null) {
                                progressDialog.dismiss();
                            }
                            Looper.prepare();
                            ToastHelper.showToast(R.string.existing_address);
                            Looper.loop();
                            return;
                        }

                        if (chainInfo != null) {
                            chainInfo.setCoinType(coinType);
                            chainInfo.setIsImport(true);
                            chainInfo.setImportType(1);
                            chainInfo.setPassword(Md5Utils.md5(pwd));
                            chainInfo.setImagePath(IconCreateUtils.getIcon());
                            chainInfo.setAccountId(hdWallet.getAccountId());
                            chainInfo.setWif(word);
                            chainInfo.setCnyTotalPrice("0");
                            chainInfo.setUsdtTotalPrice("0");
                            chainInfo.setChainId(selectChain.getId());


                            boolean exist = ChainAddressDaoUtils.chainAddressIsExist(coinType, chainInfo.getAddress());

                            if (!exist) {
                                ChainAddressDaoUtils.insertNewAddress(chainInfo);
                            } else {
                                if (progressDialog != null) {
                                    progressDialog.dismiss();
                                }
                                Looper.prepare();
                                ToastHelper.showToast(getResources().getString(R.string.existing_address));
                                Looper.loop();
                            }


                            //ChainAddressDaoUtils.insertNewAddress(chainInfo);

                            selectChain.resetChainAddressInfos();

                            if (coinType.equals(ChainAddressCreateManager.NEO_COIN_TYPE)) {
                                LocalDataUtils.setAddresByAssets(hdWallet.getAccountId(),"neo_assets.json", chainInfo.getId(), chainInfo.getAddress(), this);
                            } else {
                                LocalDataUtils.setAddresByAssets(hdWallet.getAccountId(),"ont_assets.json", chainInfo.getId(), chainInfo.getAddress(), this);
                            }
                        } else {
                            if (progressDialog != null) {
                                progressDialog.dismiss();
                            }
                            Looper.prepare();
                            ToastHelper.showToast(getResources().getString(R.string.import_failed));
                            Looper.loop();
                        }
                        //EventBus.getDefault().post(chainInfo);
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }
                        finish();
                    } else {
                        Looper.prepare();
                        ToastHelper.showToast(getResources().getString(R.string.input_wif_key_string));
                        Looper.loop();
                    }
                    break;
            }
        }

    }

}
