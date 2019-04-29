package com.raistone.wallet.sealwallet.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.chenenyu.router.Router;
import com.chenenyu.router.annotation.Route;
import com.google.zxing.Result;
import com.google.zxing.client.result.ParsedResult;
import com.mylhyl.zxing.scanner.OnScannerCompletionListener;
import com.mylhyl.zxing.scanner.ScannerOptions;
import com.mylhyl.zxing.scanner.ScannerView;
import com.raistone.wallet.sealwallet.R;
import com.raistone.wallet.sealwallet.daoutils.AssetsDetailDaoUtils;
import com.raistone.wallet.sealwallet.daoutils.ChainAddressDaoUtils;
import com.raistone.wallet.sealwallet.factory.AssetsDeatilInfo;
import com.raistone.wallet.sealwallet.factory.ChainAddressInfo;
import com.raistone.wallet.sealwallet.utils.LocalManageUtil;
import com.raistone.wallet.sealwallet.utils.ToastHelper;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@Route(value = "ScannerActivityBack")
public class ScannerActivityBack extends AppCompatActivity {

    @BindView(R.id.scanner_view)
    ScannerView scannerView;
    @BindView(R.id.ic_back)
    ImageView icBack;
    @BindView(R.id.back_ll)
    RelativeLayout backLl;
    @BindView(R.id.tv_title)
    TextView tvTitle;

    boolean isHome = true;

    ChainAddressInfo chainAddressInfo;

    private Context context;

    private String coinType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_scanner);
        ButterKnife.bind(this);

        coinType=getIntent().getStringExtra("coinType");

        chainAddressInfo = ChainAddressDaoUtils.getCurrentByCoinType(coinType,"11");

        isHome=getIntent().getBooleanExtra("isHome",true);

        context = this;

        ScannerOptions.Builder options = new ScannerOptions.Builder();
        options.setFrameCornerColor(getResources().getColor(R.color.main_color)).setLaserLineColor(getResources().getColor(R.color.main_color));

        options.setTipText(getResources().getString(R.string.scanner_tips_string));
        scannerView.setScannerOptions(options.build());

        scannerView.setOnScannerCompletionListener(new OnScannerCompletionListener() {
            @Override
            public void onScannerCompletion(Result rawResult, ParsedResult parsedResult, Bitmap barcode) {
                String text = rawResult.getText();

                Intent intent = new Intent();
                if (!TextUtils.isEmpty(text)) {
                    if (!text.contains("toAddress:")) {
                        scannerView.restartPreviewAfterDelay(1000);
                        ToastHelper.showToast(getString(R.string.unrecognized_qr_code_string));
                        return;
                    }

                    String str = subStr(text);

                    if (!TextUtils.isEmpty(str)) {
                        if (!chainAddressInfo.getCoinType().equals(str)) {
                            scannerView.restartPreviewAfterDelay(1000);
                            ToastHelper.showToast(getString(R.string.cannot_transaction));
                            return;
                        } else {

                            if(isHome) {
                                int i = text.lastIndexOf("=");
                                String tokenAddress = text.substring(i + 1);

                                AssetsDeatilInfo byTokenAddress = AssetsDetailDaoUtils.findAssterByTokenAddressByAccount(tokenAddress,chainAddressInfo.getAddress(),chainAddressInfo.getAccountId());

                                int start = text.indexOf(":");

                                int end=text.indexOf("?");

                                String formAddress = text.substring(start + 1, end);

                                Router.build("TransferActivity")
                                        .with("address", chainAddressInfo.getAddress())
                                        .with("coinName", byTokenAddress.getTokenSynbol())
                                        .with("tokenSynbol", byTokenAddress.getTokenSynbol())
                                        .with("tokenType", byTokenAddress.getTokenType())
                                        .with("tokenAddress", byTokenAddress.getTokenAddress())
                                        .with("tokenDecimal", byTokenAddress.getTokenDecimal())
                                        .with("formAddress",formAddress)
                                        .with("coinType",byTokenAddress.getCoinType())
                                        .with("balance", byTokenAddress.getBalance().toString())
                                        .with("dataBean", byTokenAddress)
                                        .go(context);
                            }else {
                                int start = text.indexOf(":");

                                int end=text.indexOf("?");
                                String formAddress = text.substring(start + 1, end);
                                intent.putExtra("message", formAddress);
                                setResult(RESULT_OK, intent);
                                finish();
                            }
                        }
                    }

                } else {
                    //intent.putExtra("",)
                    scannerView.restartPreviewAfterDelay(1000);
                    ToastHelper.showToast(getResources().getString(R.string.unrecognized_qr_code_string));
                }
            }
        });
    }

    @Override
    protected void onResume() {
        scannerView.onResume();
        super.onResume();
    }

    @Override
    protected void onPause() {
        scannerView.onPause();
        super.onPause();
    }

    @OnClick(R.id.back_ll)
    public void onViewClicked() {
        finish();
    }

    public static String subStr(String args) {
        String[] split = args.split("\\?");

        String s1 = split[1];

        String[] split1 = s1.split("&");

        String s2 = split1[0];

        String[] split2 = s2.split("=");

        return split2[1];
    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocalManageUtil.setLocal(newBase));
    }


}
