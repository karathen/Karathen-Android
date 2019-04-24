package com.raistone.wallet.sealwallet.ui;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.chenenyu.router.Router;
import com.chenenyu.router.annotation.Route;
import com.mylhyl.zxing.scanner.encode.QREncode;
import com.raistone.wallet.sealwallet.R;
import com.raistone.wallet.sealwallet.adapter.MnemonicAdapter;
import com.raistone.wallet.sealwallet.factory.ChainAddressInfo;
import com.raistone.wallet.sealwallet.factory.HdWallet;
import com.raistone.wallet.sealwallet.utils.CommonUtils;
import com.raistone.wallet.sealwallet.utils.StatusBarUtil;
import com.raistone.wallet.sealwallet.widget.TitleBar;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@Route(value = "BackupMnemonicActivity")
public class BackupMnemonicActivity extends BaseActivity {

    @BindView(R.id.titleBar)
    TitleBar titleBar;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    @BindView(R.id.next_btn)
    Button nextBtn;
    @BindView(R.id.qr_code_address_ll)
    LinearLayout qrCodeAddressLl;

    private HdWallet ethWalletBack;

    private List<String> datas;

    private MnemonicAdapter mnemonicAdapter;

    private boolean isBack, userCenter, isAdd;

    private String codesStr, mPassWord, walletTips, walletName;

    private ChainAddressInfo chainAddressInfo;

    private List<String> words;

    private Context context;

    private int walletType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup_mnemonic);
        ButterKnife.bind(this);
        setTitle(titleBar, getResources().getString(R.string.backup_mnemonic), true);

        context = this;

        //StatusBarUtil.setTransparent(this);

        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);

        recyclerview.setLayoutManager(layoutManager);

        ethWalletBack = (HdWallet) getIntent().getSerializableExtra("mnemonic");

        codesStr = getIntent().getStringExtra("codesStr");

        walletName = getIntent().getStringExtra("walletName");

        mPassWord = getIntent().getStringExtra("mPassWord");

        walletTips = getIntent().getStringExtra("walletTips");

        walletType = getIntent().getIntExtra("walletType", 0);

        chainAddressInfo = (ChainAddressInfo) getIntent().getSerializableExtra("chainAddressInfo");

        isBack = getIntent().getBooleanExtra("isBack", false);
        userCenter = getIntent().getBooleanExtra("userCenter", false);
        isAdd = getIntent().getBooleanExtra("isAdd", false);

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


            if (!TextUtils.isEmpty(codesStr)) {
                String[] mnemon = codesStr.split(" ");

                words = Arrays.asList(mnemon);


                if (words != null) {
                    mnemonicAdapter = new MnemonicAdapter(words);
                    recyclerview.setAdapter(mnemonicAdapter);
                }
            }

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

            if (chainAddressInfo != null) {
                String mnemonic = chainAddressInfo.getMnemonic();

                if (!TextUtils.isEmpty(mnemonic)) {

                    String[] split = mnemonic.split(" ");

                    datas = Arrays.asList(split);
                }
            }
            if (datas != null && datas.size() > 0) {
                mnemonicAdapter = new MnemonicAdapter(datas);
                recyclerview.setAdapter(mnemonicAdapter);
            }

        }


    }

    @OnClick({R.id.qr_code_address_ll, R.id.next_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            //生成二维码
            case R.id.qr_code_address_ll:

                if (!isBack && !userCenter) {
                    showMnemonicQrCodeDialog(codesStr);
                } else {
                    if (ethWalletBack != null) {
                        showMnemonicQrCodeDialog(ethWalletBack.getMnemonic());
                    }
                }
                break;

            //下一步
            case R.id.next_btn:

                nextBtn.setClickable(false);


                //是否是创建进入
                if (!isBack && !userCenter) {
                    //EventBus.getDefault().post(new MessageEvent(ethWallet.getAddress()));
                    Router.build("MnemonicVerifyActivity").with("words", codesStr).with("mPassWord", mPassWord)
                            .with("codesStr", codesStr).with("walletType", walletType)
                            .with("walletName", walletName)
                            .with("isAdd", isAdd)
                            .with("walletTips", walletTips).go(this);
                    return;
                }
                if (ethWalletBack != null && userCenter) {
                    //EventBus.getDefault().post(new MessageEvent(ethWalletBack.getAddress()));
                    Router.build("MnemonicVerifyActivity").with("ethWallet", ethWalletBack).with("words", ethWalletBack.getMnemonic()).with("isBack", true).go(this);
                }

                break;
        }
    }


    /**
     * @param mnemonic
     */
    private void showMnemonicQrCodeDialog(String mnemonic) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.export_dialog_two_layout, null);
        //builer.setView(v);//这里如果使用builer.setView(v)，自定义布局只会覆盖title和button之间的那部分
        final Dialog dialog = builder.create();

        ImageView iv_delete = v.findViewById(R.id.delete_iv);

        ImageView qrcode_iv = v.findViewById(R.id.qrcode_iv);

        TextView title_tv_tips = v.findViewById(R.id.title_tv_tips);

        TextView warning_tv = v.findViewById(R.id.warning_tv);


        title_tv_tips.setText(getResources().getString(R.string.export_mnemonic_string));
        warning_tv.setText(getResources().getString(R.string.safety_warning_mnemonic_string));


        Bitmap bitmapShare = new QREncode.Builder(this)
                .setColor(getResources().getColor(R.color.text_main_color))//二维码颜色
                //.setParsedResultType(ParsedResultType.TEXT)//默认是TEXT类型
                .setContents(mnemonic)//二维码内容
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
        private_value_tv.setText(mnemonic);

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


}
