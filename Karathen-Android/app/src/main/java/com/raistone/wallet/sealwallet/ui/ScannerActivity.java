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

    boolean isHome = true;//是否从首页回来

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

        //current = MultiChainInfoDaoUtils.getCurrent();

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

                //取出前缀
                String[] strs = text.split(":");

                String str = strs[0];

                if (text.contains("?") && text.contains("ethereum") || text.contains("neo") || text.contains("ontology")) {

                    //判断是哪条链


                    String type = chainAddressInfo.getCoinType();//获取进来是哪条链

                    //str是扫出来的链类型

                    //ETH
                    if (str.equals("ethereum")) {
                        str = "ETH";
                    }


                    //NEO
                    if (str.equals("neo")) {
                        str = "NEO";
                    }

                    //ONT
                    if (str.equals("ontology")) {
                        str = "ONT";
                    }


                    if (type.equals(str)) {

                        //问号截取字符串
                        String[] split = text.split("\\?");

                        String s = split[0];

                        if (s.contains(":")) {

                            //通过冒号截取
                            String[] strings = s.split(":");

                            //获取到最终的地址信息
                            String scanAddress = strings[1];

                            //ETH链
                            if (str.equals("ETH")) {

                                //判断地址是否有效
                                boolean validAddress = isETHValidAddress(scanAddress);

                                if (validAddress) {
                                    //ToastHelper.showToast("ETH地址有效");
                                    scannerView.restartPreviewAfterDelay(1000);
                                    gotoTransfer(text);
                                } else {
                                    //ToastHelper.showToast("ETH地址无效 跳转到详情页");
                                    scannerView.restartPreviewAfterDelay(1000);
                                    Router.build("ScannerResultActivity").with("result", text).go(context);
                                }

                            }

                            //NEO链
                            if (str.equals("NEO")) {
                                //判断地址是否有效
                                boolean validAddress = Neoutils.validateNEOAddress(scanAddress);

                                if (validAddress) {
                                    //ToastHelper.showToast("NEO地址有效");
                                    scannerView.restartPreviewAfterDelay(1000);
                                    gotoTransfer(text);
                                } else {
                                    //ToastHelper.showToast("NEO地址无效 跳转到详情页");
                                    scannerView.restartPreviewAfterDelay(1000);
                                    Router.build("ScannerResultActivity").with("result", text).go(context);
                                }
                            }

                            //ONT链
                            if (coinType.equals("ONT")) {
                                //判断地址是否有效
                                boolean validAddress = Neoutils.validateNEOAddress(scanAddress);

                                if (validAddress) {
                                    //ToastHelper.showToast("ONT地址有效");
                                    scannerView.restartPreviewAfterDelay(1000);
                                    gotoTransfer(text);
                                } else {
                                    //ToastHelper.showToast("ONT地址无效 跳转到详情页");
                                    scannerView.restartPreviewAfterDelay(1000);
                                    Router.build("ScannerResultActivity").with("result", text).go(context);
                                }
                            }
                        } else {
                            //ToastHelper.showToast("地址无效 直接跳转到详情页");
                            scannerView.restartPreviewAfterDelay(1000);
                            Router.build("ScannerResultActivity").with("result", text).go(context);
                        }


                    } else {

                        scannerView.restartPreviewAfterDelay(1000);
                        ToastHelper.showToast(getString(R.string.cannot_transaction));
                    }


                } else {
                    //纯文本 判断是否是有效地址

                    String coinType = chainAddressInfo.getCoinType();

                    boolean checkAddress;

                    if (coinType.equals("ETH")) {
                        checkAddress = isETHValidAddress(text);
                    } else {
                        checkAddress = Neoutils.validateNEOAddress(text);
                    }

                    if (checkAddress) {
                        //ToastHelper.showToast("checkAddress 是有效地址");
                        scannerView.restartPreviewAfterDelay(1000);
                        gotoTransfer(text);
                    } else {
                        //ToastHelper.showToast("checkAddress 无效地址");
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
            //ethereum:0x0dF63Ff925a5E0e9632a109E5115D9936A33036b?contractAddress=0x1234567461d3f8db7496581774bd869c83d51c93;
            int i = text.lastIndexOf("=");
            String tokenAddress = text.substring(i + 1);

            //AssetsInfo.DataBean byTokenAddress = AssetsDaoUtils.findAssterByTokenAddress(tokenAddress);

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
            //从转账页面过来
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
