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
import org.web3j.crypto.WalletUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import neoutils.Neoutils;

@Route(value = "ScannerActivity")
public class ScannerActivity extends AppCompatActivity {

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

    private String coinType, tokenSynbol;

    private String accountId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_scanner);

        ActivityManager.getInstance().pushActivity(this);

        ButterKnife.bind(this);

        coinType = getIntent().getStringExtra("coinType");
        accountId = getIntent().getStringExtra("accountId");
        tokenSynbol = getIntent().getStringExtra("tokenSynbol");

        chainAddressInfo = ChainAddressDaoUtils.getCurrentByCoinType(coinType, accountId);

        isHome = getIntent().getBooleanExtra("isHome", true);

        context = this;

        ScannerOptions.Builder options = new ScannerOptions.Builder();
        options.setFrameCornerColor(getResources().getColor(R.color.main_color)).setLaserLineColor(getResources().getColor(R.color.main_color));

        options.setTipText(getResources().getString(R.string.scanner_tips_string));
        scannerView.setScannerOptions(options.build());

        scannerView.setOnScannerCompletionListener(new OnScannerCompletionListener() {
            @Override
            public void onScannerCompletion(Result rawResult, ParsedResult parsedResult, Bitmap barcode) {

                String text = rawResult.getText();

                String[] strs = text.split(":");

                String str = strs[0];

                if (text.contains("?") && text.contains("ethereum") || text.contains("neo") || text.contains("ontology")) {



                    String type = chainAddressInfo.getCoinType();


                    if (str.equals("ethereum")) {
                        str = "ETH";
                    }


                    if (str.equals("neo")) {
                        str = "NEO";
                    }

                    if (str.equals("ontology")) {
                        str = "ONT";
                    }


                    if (type.equals(str)) {

                        String[] split = text.split("\\?");

                        String s = split[0];

                        if (s.contains(":")) {

                            String[] strings = s.split(":");

                            String scanAddress = strings[1];

                            if (str.equals("ETH")) {

                                boolean validAddress = isETHValidAddress(scanAddress);

                                if (validAddress) {
                                    scannerView.restartPreviewAfterDelay(1000);
                                    gotoTransfer(text);
                                } else {
                                    scannerView.restartPreviewAfterDelay(1000);
                                    Router.build("ScannerResultActivity").with("result", text).go(context);
                                }

                            }

                            if (str.equals("NEO")) {
                                boolean validAddress = Neoutils.validateNEOAddress(scanAddress);

                                if (validAddress) {
                                    scannerView.restartPreviewAfterDelay(1000);
                                    gotoTransfer(text);
                                } else {
                                    scannerView.restartPreviewAfterDelay(1000);
                                    Router.build("ScannerResultActivity").with("result", text).go(context);
                                }
                            }

                            if (coinType.equals("ONT")) {
                                boolean validAddress = Neoutils.validateNEOAddress(scanAddress);

                                if (validAddress) {
                                    scannerView.restartPreviewAfterDelay(1000);
                                    gotoTransfer(text);
                                } else {
                                    scannerView.restartPreviewAfterDelay(1000);
                                    Router.build("ScannerResultActivity").with("result", text).go(context);
                                }
                            }
                        } else {
                            scannerView.restartPreviewAfterDelay(1000);
                            Router.build("ScannerResultActivity").with("result", text).go(context);
                        }


                    } else {

                        scannerView.restartPreviewAfterDelay(1000);
                        ToastHelper.showToast(getString(R.string.cannot_transaction));
                    }


                } else {

                    String coinType = chainAddressInfo.getCoinType();

                    boolean checkAddress;

                    if (coinType.equals("ETH")) {
                        checkAddress = isETHValidAddress(text);
                    } else {
                        checkAddress = Neoutils.validateNEOAddress(text);
                    }

                    if (checkAddress) {
                        scannerView.restartPreviewAfterDelay(1000);
                        gotoTransfer(text);
                    } else {
                        scannerView.restartPreviewAfterDelay(1000);
                        Router.build("ScannerResultActivity").with("result", text).go(context);
                    }

                }

            }
        });
    }

    public void gotoTransfer(String text) {
        Intent intent = new Intent();
        if (isHome) {
            int i = text.lastIndexOf("=");
            String tokenAddress = text.substring(i + 1);

            if (text.contains(":") && text.contains("?")) {

                AssetsDeatilInfo byTokenAddress = AssetsDetailDaoUtils.findAssterByTokenAddressByAccount(tokenAddress, chainAddressInfo.getAddress(), chainAddressInfo.getAccountId());

                if (byTokenAddress != null) {

                    int start = text.indexOf(":");

                    int end = text.indexOf("?");

                    String formAddress = text.substring(start + 1, end);

                    Router.build("TransferActivity")
                            .with("address", chainAddressInfo.getAddress())
                            .with("coinName", byTokenAddress.getTokenSynbol())
                            .with("tokenSynbol", byTokenAddress.getTokenSynbol())
                            .with("tokenType", byTokenAddress.getTokenType())
                            .with("tokenAddress", byTokenAddress.getTokenAddress())
                            .with("tokenDecimal", byTokenAddress.getTokenDecimal())
                            .with("formAddress", formAddress)
                            .with("coinType", byTokenAddress.getCoinType())
                            .with("balance", byTokenAddress.getBalance().toString())
                            .with("dataBean", byTokenAddress)
                            .go(context);
                } else {
                    scannerView.restartPreviewAfterDelay(1000);
                    Router.build("ScannerResultActivity").with("result", text).go(context);
                }
            } else {
                String coinType = chainAddressInfo.getCoinType();
                AssetsDeatilInfo deatilInfo = null;
                if (coinType.equals("ETH")) {
                    deatilInfo = AssetsDetailDaoUtils.findAssterByTokenAddressByAccount("0x0000000000000000000000000000000000000000", chainAddressInfo.getAddress(), chainAddressInfo.getAccountId());
                }
                if (coinType.equals("NEO")) {
                    deatilInfo = AssetsDetailDaoUtils.findAssterByTokenAddressByAccount("0xc56f33fc6ecfcd0c225c4ab356fee59390af8560be0e930faebe74a6daff7c9b", chainAddressInfo.getAddress(), chainAddressInfo.getAccountId());
                }
                if (coinType.equals("ONT")) {
                    deatilInfo = AssetsDetailDaoUtils.findAssterByTokenAddressByAccount("0000000000000000000000000000000000000001", chainAddressInfo.getAddress(), chainAddressInfo.getAccountId());
                }


                Router.build("TransferActivity")
                        .with("address", chainAddressInfo.getAddress())
                        .with("coinName", deatilInfo.getTokenSynbol())
                        .with("tokenSynbol", deatilInfo.getTokenSynbol())
                        .with("tokenType", deatilInfo.getTokenType())
                        .with("tokenAddress", deatilInfo.getTokenAddress())
                        .with("tokenDecimal", deatilInfo.getTokenDecimal())
                        .with("formAddress", text)
                        .with("coinType", deatilInfo.getCoinType())
                        .with("balance", deatilInfo.getBalance().toString())
                        .with("dataBean", deatilInfo)
                        .go(context);
            }
        } else {
            if (text.contains(":") && text.contains("?")) {


                int i = text.lastIndexOf("=");
                String tokenAddress = text.substring(i + 1);

                AssetsDeatilInfo deatilInfo = AssetsDetailDaoUtils.findAssterByTokenAddressByAccount(tokenAddress, chainAddressInfo.getAddress(), chainAddressInfo.getAccountId());
                if (deatilInfo != null) {
                    if (!tokenSynbol.equals(deatilInfo.getTokenSynbol())) {
                        ToastHelper.showToast(getResources().getString(R.string.please_check_qrcode_string));
                        scannerView.restartPreviewAfterDelay(1000);
                        return;
                    } else {
                        int start = text.indexOf(":");

                        int end = text.indexOf("?");
                        String formAddress = text.substring(start + 1, end);

                        intent.putExtra("message", formAddress);
                        setResult(RESULT_OK, intent);
                        finish();
                    }

                } else {

                    int start = text.indexOf(":");

                    int end = text.indexOf("?");
                    String formAddress = text.substring(start + 1, end);

                    intent.putExtra("message", formAddress);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            } else {
                intent.putExtra("message", text);
                setResult(RESULT_OK, intent);
                finish();
            }
        }
    }

    public static boolean isETHValidAddress(String input) {
        if (TextUtils.isEmpty(input) || !input.startsWith("0x"))
            return false;
        return WalletUtils.isValidAddress(input);
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
