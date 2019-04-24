package com.raistone.wallet.sealwallet.ui;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.chenenyu.router.annotation.Route;
import com.raistone.wallet.sealwallet.R;
import com.raistone.wallet.sealwallet.utils.CommonUtils;
import com.raistone.wallet.sealwallet.utils.StatusBarUtil;
import com.raistone.wallet.sealwallet.widget.TitleBar;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@Route(value = "ScannerResultActivity")
public class ScannerResultActivity extends BaseActivity {

    @BindView(R.id.titleBar)
    TitleBar titleBar;
    @BindView(R.id.result_tv)
    TextView resultTv;
    @BindView(R.id.copy_lv)
    ImageView copyLv;
    @BindView(R.id.copy_rl)
    RelativeLayout copyRl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner_result);
        ButterKnife.bind(this);

        setTitle(titleBar,getResources().getString(R.string.scanner_result_string),true);

        String result=getIntent().getStringExtra("result");

        ActivityManager.getInstance().pushActivity(this);

        resultTv.setText(result);
    }

    @OnClick(R.id.copy_rl)
    public void onViewClicked() {
        CommonUtils.copyString(resultTv);
    }
}
