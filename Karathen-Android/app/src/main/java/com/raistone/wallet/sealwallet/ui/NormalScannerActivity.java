package com.raistone.wallet.sealwallet.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.chenenyu.router.annotation.Route;
import com.google.zxing.Result;
import com.google.zxing.client.result.ParsedResult;
import com.mylhyl.zxing.scanner.OnScannerCompletionListener;
import com.mylhyl.zxing.scanner.ScannerOptions;
import com.mylhyl.zxing.scanner.ScannerView;
import com.raistone.wallet.sealwallet.R;
import com.raistone.wallet.sealwallet.datavases.MultiChainInfoDaoUtils;
import com.raistone.wallet.sealwallet.entity.MultiChainInfo;
import com.raistone.wallet.sealwallet.utils.LocalManageUtil;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@Route(value = "NormalScannerActivity")
public class NormalScannerActivity extends AppCompatActivity {

    @BindView(R.id.scanner_view)
    ScannerView scannerView;
    @BindView(R.id.ic_back)
    ImageView icBack;
    @BindView(R.id.back_ll)
    RelativeLayout backLl;
    @BindView(R.id.tv_title)
    TextView tvTitle;

    boolean isHome = true;

    MultiChainInfo current;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_scanner);
        ButterKnife.bind(this);

        ActivityManager.getInstance().pushActivity(this);

        current = MultiChainInfoDaoUtils.getCurrent();

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

                Intent intent = new Intent();
                intent.putExtra("message", text);
                setResult(RESULT_OK, intent);
                finish();

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


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocalManageUtil.setLocal(newBase));
    }

}
