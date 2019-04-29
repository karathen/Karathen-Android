package com.raistone.wallet.sealwallet.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.chenenyu.router.Router;
import com.chenenyu.router.annotation.Route;
import com.github.ontio.network.rest.UrlConsts;
import com.mylhyl.zxing.scanner.encode.QREncode;
import com.raistone.wallet.sealwallet.R;
import com.raistone.wallet.sealwallet.daoutils.ChainAddressDaoUtils;
import com.raistone.wallet.sealwallet.daoutils.HdWalletDaoUtils;
import com.raistone.wallet.sealwallet.factory.ChainAddressInfo;
import com.raistone.wallet.sealwallet.utils.CommonUtils;
import com.raistone.wallet.sealwallet.utils.Constant;
import com.raistone.wallet.sealwallet.utils.Md5Utils;
import com.raistone.wallet.sealwallet.utils.MultiChainCreateManager;
import com.raistone.wallet.sealwallet.utils.StatusBarUtil;
import com.raistone.wallet.sealwallet.utils.ToastHelper;
import com.raistone.wallet.sealwallet.widget.TitleBar;
import org.greenrobot.eventbus.EventBus;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;



@Route(value = "WalletAddressManageActivity")
public class WalletAddressManageActivity extends BaseActivity {

    @BindView(R.id.titleBar)
    TitleBar titleBar;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_address)
    TextView tvAddress;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_wallet_name)
    TextView tvWalletName;
    @BindView(R.id.update_wallet_name_rl)
    RelativeLayout updateWalletNameRl;
    @BindView(R.id.export_mnemonic_rl)
    RelativeLayout exportMnemonicRl;
    @BindView(R.id.export_private_key_rl)
    RelativeLayout exportPrivateKeyRl;
    @BindView(R.id.browser_query_rl)
    RelativeLayout browserQueryRl;


    private ChainAddressInfo ethWallet;
    private Context context;
    private String pinValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_address_manage);
        ButterKnife.bind(this);
        setTitle(titleBar, getResources().getString(R.string.address_manager_string), true);

        ActivityManager.getInstance().pushActivity(this);

        ethWallet = (ChainAddressInfo) getIntent().getSerializableExtra("ethWallet");

        if (ethWallet.getIsImport() && ethWallet.getImportType()!=4) {
            exportMnemonicRl.setVisibility(View.VISIBLE);
        } else {
            exportMnemonicRl.setVisibility(View.GONE);
        }
        tvAddress.setText(ethWallet.getAddress());
        tvWalletName.setText(ethWallet.getName());
        context = this;

        titleBar.setIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("ewaller", "data");
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    @OnClick({R.id.update_wallet_name_rl, R.id.export_mnemonic_rl, R.id.export_private_key_rl, R.id.browser_query_rl})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.update_wallet_name_rl:
                showUpdateDialog();
                break;
            case R.id.export_mnemonic_rl:
                showDialogByType(1);

                break;
            case R.id.export_private_key_rl:
                showDialogByType(0);
                break;
            case R.id.browser_query_rl:

                String coinType = ethWallet.getCoinType();
                String url = "";
                if (coinType.equals(MultiChainCreateManager.ETH_COIN_TYPE)) {
                    url = UrlConsts.URL_ETH_SCAN;
                }

                if (coinType.equals(MultiChainCreateManager.NEO_COIN_TYPE)) {
                    url = UrlConsts.URL_NEO_SCAN;
                }
                if (coinType.equals(MultiChainCreateManager.ONT_COIN_TYPE)) {
                    url = UrlConsts.URL_ONT_SCAN;
                }

                Intent intent = new Intent(this, WebViewActivity.class);
                intent.putExtra(Constant.IntentKey.WEB_URL, url + ethWallet.getAddress());
                intent.putExtra(Constant.IntentKey.WEB_TITLE, "");
                startActivity(intent);
                break;
        }
    }


    private void showUpdateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.update_dialog_layout, null);
        final Dialog dialog = builder.create();
        final EditText editText = v.findViewById(R.id.wallet_name_ed);
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
                String name = editText.getText().toString();
                if (TextUtils.isEmpty(name)) {
                    ToastHelper.showToast(getResources().getString(R.string.name_not_null_string));
                    return;
                }
                ChainAddressInfo addressInfo = ChainAddressDaoUtils.updateChainName(ethWallet.getId(), name);

                tvWalletName.setText(addressInfo.getName());


                dialog.dismiss();

                EventBus.getDefault().post(addressInfo);
            }
        });
        dialog.setCancelable(false);
        dialog.show();
        dialog.getWindow().setContentView(v);

        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);


    }


    private void showDialogByType(final int type) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
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
                    String pwd = HdWalletDaoUtils.findWalletBySelect().getWalletPwd();
                    if (TextUtils.equals(value, pwd)) {

                        switch (type) {
                            case 0:

                                String privateScrect = ethWallet.getPrivateScrect();
                                showPrivateQrCodeDialog(privateScrect,0);
                                dialog.dismiss();
                                break;
                            case 1:
                                Router.build("BackupMnemonicActivity").with("chainAddressInfo", ethWallet).with("isBack", true).go(WalletAddressManageActivity.this);
                                dialog.dismiss();
                                break;
                        }


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


    private void showPrivateQrCodeDialog(String privateKey, int type) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.export_dialog_two_layout, null);
        final Dialog dialog = builder.create();

        ImageView iv_delete = v.findViewById(R.id.delete_iv);

        ImageView qrcode_iv = v.findViewById(R.id.qrcode_iv);

        TextView title_tv_tips = v.findViewById(R.id.title_tv_tips);

        TextView warning_tv = v.findViewById(R.id.warning_tv);

        if (type == 0) {
            title_tv_tips.setText(getResources().getString(R.string.export_private_key_string));
            warning_tv.setText(getResources().getString(R.string.safety_warning_string));
        }
        if (type == 1) {
            title_tv_tips.setText(getResources().getString(R.string.export_wif_string));
            warning_tv.setText(getResources().getString(R.string.safety_warning_wif_string));
        }

        Bitmap bitmapShare = new QREncode.Builder(this)
                .setColor(getResources().getColor(R.color.text_main_color))
                .setContents(privateKey)//二维码内容
                .setMargin(0)
                .setSize(500)
                .build().encodeAsBitmap();
        qrcode_iv.setImageBitmap(bitmapShare);


        iv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        final TextView private_value_tv = v.findViewById(R.id.private_value_tv);

        private_value_tv.setText("0x" + privateKey);

        Button button = v.findViewById(R.id.copy_btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CommonUtils.copyString(private_value_tv);
            }
        });
        dialog.setCancelable(false);
        dialog.show();
        dialog.getWindow().setContentView(v);

        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);


    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent();
            intent.putExtra("ewaller", "data");
            setResult(RESULT_OK, intent);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }


}
