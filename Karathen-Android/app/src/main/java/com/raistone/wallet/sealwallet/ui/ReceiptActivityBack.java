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
import com.raistone.wallet.sealwallet.utils.CommonUtils;
import com.raistone.wallet.sealwallet.utils.StatusBarUtil;
import com.raistone.wallet.sealwallet.widget.TitleBar;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


@Route(value = "ReceiptActivityBack")
public class ReceiptActivityBack extends BaseActivity {

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

   // private ETHWallet wallet;
    private String address;
    private String coinName;
    private String fromName;
    private String contractAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt);
        ButterKnife.bind(this);
        //StatusBarUtil.setTransparent(this);
        setTitle(titleBar,getResources().getString(R.string.receipt_string),true);
        //wallet= (ETHWallet) getIntent().getSerializableExtra("ethWallet");
        address  =  getIntent().getStringExtra("address");
        coinName =  getIntent().getStringExtra("coinName");
        fromName =  getIntent().getStringExtra("fromName");
        contractAddress =  getIntent().getStringExtra("contractAddress");

        createCode();
    }

    public void createCode(){
        if(!TextUtils.isEmpty(address) && !TextUtils.isEmpty(coinName)) {


                Bitmap bitmapShare = new QREncode.Builder(this)
                        .setColor(getResources().getColor(R.color.text_main_color))//二维码颜色
                        //.setParsedResultType(ParsedResultType.TEXT)//默认是TEXT类型
                        .setContents("toAddress:"+address+"?coinType="+coinName+"&contractAddress="+contractAddress)//二维码内容
                        .setMargin(0)
                        .setSize(500)
                        //.setLogoBitmap(logoBitmap)//二维码中间logo
                        .build().encodeAsBitmap();
                qrCodeIv.setImageBitmap(bitmapShare);
            }

            receiptAddress.setText(address);
            receiptNameTv.setText(fromName+"-"+coinName);

    }


    @OnClick(R.id.copy_btn)
    public void onViewClicked() {
        CommonUtils.copyString(receiptAddress);
    }
}
