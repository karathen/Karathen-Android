package com.raistone.wallet.sealwallet.ui;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.chenenyu.router.annotation.Route;
import com.mylhyl.zxing.scanner.encode.QREncode;
import com.raistone.wallet.sealwallet.R;
import com.raistone.wallet.sealwallet.factory.ChainAddressInfo;
import com.raistone.wallet.sealwallet.utils.ChainAddressCreateManager;
import com.raistone.wallet.sealwallet.utils.CommonUtils;
import com.raistone.wallet.sealwallet.utils.StatusBarUtil;
import com.raistone.wallet.sealwallet.widget.TitleBar;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * 收款
 */
@Route(value = "ReceiptActivity")
public class ReceiptActivity extends BaseActivity {

    @BindView(R.id.titleBar)
    TitleBar titleBar;
    @BindView(R.id.receipt_name_tv)
    TextView receiptNameTv;
    @BindView(R.id.qr_code_iv)
    ImageView qrCodeIv;
    @BindView(R.id.receipt_address)
    TextView receiptAddress;
    @BindView(R.id.copy_btn)
    Button copyBtn;

    private ChainAddressInfo chainAddressInfo;
    private String address;
    private String coinName;
    private String fromName;
    private String contractAddress;
    private String coinType;

    private boolean isHome;//默认是首页进入

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt);
        ButterKnife.bind(this);
        //StatusBarUtil.setTransparent(this);

        ActivityManager.getInstance().pushActivity(this);
        setTitle(titleBar, getResources().getString(R.string.receipt_string), true);

        isHome = getIntent().getBooleanExtra("isHome", true);
        coinName = getIntent().getStringExtra("coinName");
        coinType = getIntent().getStringExtra("coinType");

        //fromName =  getIntent().getStringExtra("fromName");
        contractAddress = getIntent().getStringExtra("contractAddress");

        chainAddressInfo = (ChainAddressInfo) getIntent().getSerializableExtra("chainAddressInfo");

        String content = "";

        if (isHome) {
            content = chainAddressInfo.getAddress();
        } else {
            String coinType = chainAddressInfo.getCoinType();

            if (coinType.equals(ChainAddressCreateManager.ETH_COIN_TYPE)) {
                content = "ethereum:" + chainAddressInfo.getAddress() + "?contractAddress=" + contractAddress;
            }

            if (coinType.equals(ChainAddressCreateManager.NEO_COIN_TYPE)) {
                content = "neo:" + chainAddressInfo.getAddress() + "?contractAddress=" + contractAddress;
            }
            if (coinType.equals(ChainAddressCreateManager.ONT_COIN_TYPE)) {
                content = "ontology:" + chainAddressInfo.getAddress() + "?contractAddress=" + contractAddress;
            }
        }


        createCode(content);

    }

    public void createCode() {
        if (!TextUtils.isEmpty(address) && !TextUtils.isEmpty(coinName)) {


            Bitmap bitmapShare = new QREncode.Builder(this)
                    .setColor(getResources().getColor(R.color.text_main_color))//二维码颜色
                    //.setParsedResultType(ParsedResultType.TEXT)//默认是TEXT类型
                    .setContents("toAddress:" + address + "?coinType=" + coinName + "&contractAddress=" + contractAddress)//二维码内容
                    .setMargin(0)
                    .setSize(500)
                    //.setLogoBitmap(logoBitmap)//二维码中间logo
                    .build().encodeAsBitmap();
            qrCodeIv.setImageBitmap(bitmapShare);
        }

        receiptAddress.setText(address);
        receiptNameTv.setText(fromName + "-" + coinName);

    }


    public void createCode(String content) {
        if (!TextUtils.isEmpty(content)) {


            Bitmap bitmapShare = new QREncode.Builder(this)
                    .setColor(getResources().getColor(R.color.text_main_color))//二维码颜色
                    //.setParsedResultType(ParsedResultType.TEXT)//默认是TEXT类型
                    .setContents(content)//二维码内容
                    .setMargin(0)
                    .setSize(500)
                    //.setLogoBitmap(logoBitmap)//二维码中间logo
                    .build().encodeAsBitmap();
            qrCodeIv.setImageBitmap(bitmapShare);
        }

        receiptAddress.setText(chainAddressInfo.getAddress());
        if (isHome) {
            receiptNameTv.setText(chainAddressInfo.getName());
        }else {
            receiptNameTv.setText(coinType+" - "+coinName);
        }

    }


    @OnClick(R.id.copy_btn)
    public void onViewClicked() {
        CommonUtils.copyString(receiptAddress);
    }


}
