package com.raistone.wallet.sealwallet.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chenenyu.router.Router;
import com.chenenyu.router.annotation.Route;
import com.example.zhouwei.library.CustomPopWindow;
import com.github.ontio.network.rest.UrlConsts;
import com.mylhyl.zxing.scanner.encode.QREncode;
import com.raistone.wallet.sealwallet.R;
import com.raistone.wallet.sealwallet.WalletApplication;
import com.raistone.wallet.sealwallet.adapter.AddressManagerAdapterNews;
import com.raistone.wallet.sealwallet.daoutils.ChainAddressDaoUtils;
import com.raistone.wallet.sealwallet.daoutils.HdWalletDaoUtils;
import com.raistone.wallet.sealwallet.factory.ChainAddressInfo;
import com.raistone.wallet.sealwallet.factory.ChainDataInfo;
import com.raistone.wallet.sealwallet.factory.HdWallet;
import com.raistone.wallet.sealwallet.greendao.DaoSession;
import com.raistone.wallet.sealwallet.utils.ChainAddressCreateManager;
import com.raistone.wallet.sealwallet.utils.CommonUtils;
import com.raistone.wallet.sealwallet.utils.Constant;
import com.raistone.wallet.sealwallet.utils.Md5Utils;
import com.raistone.wallet.sealwallet.utils.StatusBarUtil;
import com.raistone.wallet.sealwallet.utils.ToastHelper;
import com.raistone.wallet.sealwallet.widget.AddressManagerPopup;
import org.greenrobot.eventbus.EventBus;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@Route(value = "AddressManageActivity")
public class AddressManageActivity extends BaseActivity implements BaseQuickAdapter.OnItemClickListener, BaseQuickAdapter.OnItemChildClickListener {

    @BindView(R.id.ic_back)
    ImageView icBack;
    @BindView(R.id.back_ll)
    RelativeLayout backLl;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.add_iv)
    ImageView addIv;
    @BindView(R.id.import_iv)
    ImageView importIv;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    @BindView(R.id.add_ll)
    LinearLayout addLl;
    @BindView(R.id.import_ll)
    LinearLayout importLl;
    @BindView(R.id.news_add_tips)
    TextView newsAddTips;

    private AddressManagerAdapterNews managerAdapter;

    private Context context;

    private ChainAddressInfo walletSelect;

    private String pinValue;

    private CustomPopWindow popWindow;


    private List<ChainAddressInfo> chainAddressInfos;

    private ChainDataInfo chainDataInfo;

    private HdWallet wallet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_manage);
        ButterKnife.bind(this);
        //StatusBarUtil.setTransparent(this);

        chainDataInfo = (ChainDataInfo) getIntent().getSerializableExtra("chainDataInfo");

        ActivityManager.getInstance().pushActivity(this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        recyclerview.setLayoutManager(layoutManager);

        context = this;

        wallet = HdWalletDaoUtils.findWalletBySelect();

        boolean isImport = wallet.getIsImport();//是否是导入
        if (isImport) {
            int importType = wallet.getImportType();
            //助记词导入
            if (importType == 0) {
                addLl.setVisibility(View.VISIBLE);
                newsAddTips.setVisibility(View.VISIBLE);
            } else {
                addLl.setVisibility(View.GONE);
                newsAddTips.setVisibility(View.GONE);
            }
        } else {
            addLl.setVisibility(View.VISIBLE);
            newsAddTips.setVisibility(View.VISIBLE);
        }


        if (chainAddressInfos != null && chainAddressInfos.size() > 0) {
            managerAdapter = new AddressManagerAdapterNews(chainAddressInfos);
            recyclerview.setAdapter(managerAdapter);

            managerAdapter.setOnItemClickListener(this);
            managerAdapter.setOnItemChildClickListener(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        chainDataInfo.resetChainAddressInfos();

        DaoSession daoSession = WalletApplication.getsInstance().getDaoSession();

        chainDataInfo.__setDaoSession(daoSession);

        chainAddressInfos = chainDataInfo.getChainAddressInfos();

        if (chainAddressInfos != null && chainAddressInfos.size() > 0) {
            managerAdapter = new AddressManagerAdapterNews(chainAddressInfos);
            recyclerview.setAdapter(managerAdapter);
            managerAdapter.setOnItemClickListener(this);
            managerAdapter.setOnItemChildClickListener(this);
        }

    }

    @OnClick({R.id.back_ll, R.id.add_ll, R.id.import_ll})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            /**
             * 关闭
             */
            case R.id.back_ll:


                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();


                break;
            /**
             * 添加
             */
            case R.id.add_ll:

                Router.build("NewAddressActivity").with("chainId", chainDataInfo.getId()).with("selectChain", chainDataInfo).go(this);
                break;
            /**
             * 导入
             */
            case R.id.import_ll:
                Router.build("ImportAddressActivity").with("selectChain", chainDataInfo).with("wallet", chainAddressInfos.get(0)).go(this);

                break;
        }
    }


    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

        EventBus.getDefault().post(chainAddressInfos.get(position));
        ChainAddressDaoUtils.updateCurrent(chainAddressInfos.get(position).getId(), chainDataInfo.getChainType(), chainDataInfo.getAccountId());

        managerAdapter.notifyDataSetChanged();

        walletSelect = chainAddressInfos.get(position);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {

        walletSelect = chainAddressInfos.get(position);

        View view1 = view.findViewById(R.id.more_iv);

        int id = view.getId();
        switch (id) {
            /**
             * 复制
             */
            case R.id.copyLl:
                //TextView textView = view.findViewById(R.id.address_tv);

                TextView textView = (TextView) adapter.getViewByPosition(recyclerview, position, R.id.address_tv);

                CommonUtils.copyString(textView, chainAddressInfos.get(position).getAddress());

                break;
            /**
             * 更多
             */
            case R.id.more_ll:


                final AddressManagerPopup addressManagerPopup = new AddressManagerPopup(context, walletSelect);


                addressManagerPopup.setOnCommentPopupClickListener(new AddressManagerPopup.OnCommentPopupClickListener() {
                    @Override
                    public void onCommentClick(View v) {

                        switch (v.getId()) {

                            case R.id.export_private_ll:
                                addressManagerPopup.dismiss();
                                showDialogByType(0);
                                break;

                            case R.id.export_mnemonic_ll:
                                addressManagerPopup.dismiss();

                                break;
                            case R.id.export_keystore_ll:
                                addressManagerPopup.dismiss();

                                showDialogByType(5);
                                break;

                            case R.id.export_wif_ll:
                                addressManagerPopup.dismiss();
                                showDialogByType(3);
                                break;

                            case R.id.update_name_ll:
                                addressManagerPopup.dismiss();
                                showUpdateDialog();
                                break;
                            case R.id.browser_query_ll:
                                addressManagerPopup.dismiss();
                                String coinType = walletSelect.getCoinType();
                                String url = "";
                                if (coinType.equals(ChainAddressCreateManager.ETH_COIN_TYPE)) {
                                    url = UrlConsts.URL_ETH_SCAN;
                                }

                                if (coinType.equals(ChainAddressCreateManager.NEO_COIN_TYPE)) {
                                    url = UrlConsts.URL_NEO_SCAN;
                                }
                                if (coinType.equals(ChainAddressCreateManager.ONT_COIN_TYPE)) {
                                    url = UrlConsts.URL_ONT_SCAN;
                                }

                                Intent intent = new Intent(context, WebViewActivity.class);
                                intent.putExtra(Constant.IntentKey.WEB_URL, url + walletSelect.getAddress());
                                intent.putExtra(Constant.IntentKey.WEB_TITLE, "");
                                startActivity(intent);
                                break;
                            case R.id.delete:
                                addressManagerPopup.dismiss();
                                //ToastHelper.showToast("删除地址");
                                break;
                            case R.id.claim_ll:
                                addressManagerPopup.dismiss();
                                Router.build("ClaimActivity").with("ethWallet", walletSelect).requestCode(1).go(context);
                                break;
                        }

                    }
                });

                int alpha = (int) (255 * (float) 15 / 100);
                int color = Color.argb(alpha, Color.red(Color.parseColor("#BBBDBF")), Color.green(Color.parseColor("#BBBDBF")), Color.blue(Color.parseColor("#BBBDBF")));
                addressManagerPopup.setBackgroundColor(color);
                addressManagerPopup.showPopupWindow(view1);

                break;
        }
    }


    private void showUpdateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.update_dialog_layout, null);
        //builer.setView(v);//这里如果使用builer.setView(v)，自定义布局只会覆盖title和button之间的那部分
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

                ChainAddressInfo addressInfo = ChainAddressDaoUtils.updateChainName(walletSelect.getId(), name);

                if (addressInfo != null) {
                    //byType = MultiChainInfoDaoUtils.loadAllByType(coinType);
                    managerAdapter.setNewData(chainAddressInfos);
                    managerAdapter.notifyDataSetChanged();

                    if (addressInfo.getIsCurrent()) {
                        EventBus.getDefault().post(addressInfo);
                    }
                }

                dialog.dismiss();
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
        final TextView textView = v.findViewById(R.id.cancel_tv);
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

                hideKeyboard(textView);

                pinValue = editText.getText().toString();
                if (!TextUtils.isEmpty(pinValue)) {
                    String value = Md5Utils.md5(pinValue);
                    String pwd = wallet.getWalletPwd();
                    if (TextUtils.equals(value, pwd)) {

                        switch (type) {
                            //导出私钥
                            case 0:
                                String privateScrect = walletSelect.getPrivateScrect();
                                showPrivateQrCodeDialog(privateScrect, 0);
                                dialog.dismiss();
                                break;
                            //导出助记词
                            case 1:
                                //Router.build("BackupMnemonicActivity").with("mnemonic", walletSelect).with("isBack", true).go(context);
                                Router.build("BackupMnemonicActivity").with("chainAddressInfo", walletSelect).with("isBack", true).go(context);
                                dialog.dismiss();
                                break;
                            //删除导入地址
                            case 2:

                                String address = walletSelect.getAddress();

                                //


                                ChainAddressDaoUtils.deleteAddressById(walletSelect.getId());

                                chainDataInfo.resetChainAddressInfos();


                                chainAddressInfos = chainDataInfo.getChainAddressInfos();

                                managerAdapter.setNewData(chainAddressInfos);

                                recyclerview.setAdapter(managerAdapter);
                                managerAdapter.notifyDataSetChanged();
                                dialog.dismiss();

                                EventBus.getDefault().post(chainAddressInfos.get(0));
                                break;
                            //导出wif
                            case 3:
                                String wif = walletSelect.getWif();
                                showPrivateQrCodeDialog(wif, 1);
                                dialog.dismiss();
                                break;
                            //提取gas
                            case 4:
                                Router.build("ClaimActivity").with("ethWallet", walletSelect).requestCode(1).go(context);
                                break;
                            //导出keystore
                            case 5:

                                startProgressDialog(getResources().getString(R.string.exporting_string));
                                String keystore = ChainAddressCreateManager.deriveKeystore(walletSelect.getId(), pinValue);

                                showPrivateQrCodeDialog(keystore, 2);
                                //ToastHelper.showToast("keystore "+keystore);
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


    /**
     * @param privateKey
     * @param type
     */
    private void showPrivateQrCodeDialog(String privateKey, int type) {
        stopProgressDialog();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.export_dialog_two_layout, null);
        //builer.setView(v);//这里如果使用builer.setView(v)，自定义布局只会覆盖title和button之间的那部分
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

        if (type == 2) {
            title_tv_tips.setText(getResources().getString(R.string.export_keystore_string));
            warning_tv.setText(getResources().getString(R.string.safety_warning_keystore_string));
        }

        Bitmap bitmapShare = new QREncode.Builder(this)
                .setColor(getResources().getColor(R.color.text_main_color))//二维码颜色
                //.setParsedResultType(ParsedResultType.TEXT)//默认是TEXT类型
                .setContents(privateKey)//二维码内容
                .setMargin(0)
                .setSize(500)
                //.setLogoBitmap(logoBitmap)//二维码中间logo
                .build().encodeAsBitmap();
        qrcode_iv.setImageBitmap(bitmapShare);


        iv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        final TextView private_value_tv = v.findViewById(R.id.private_value_tv);

        if (type == 0) {
            private_value_tv.setText("0x" + privateKey);
        }
        if (type == 1) {
            private_value_tv.setText(privateKey);
        }

        if (type == 2) {
            private_value_tv.setText(privateKey);
        }
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

        hideKeyboard(v);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            Intent intent = new Intent();
            // intent.putExtra("address", wallet.getAddress());
            setResult(RESULT_OK, intent);
            finish();

        }
        return super.onKeyDown(keyCode, event);
    }
}
