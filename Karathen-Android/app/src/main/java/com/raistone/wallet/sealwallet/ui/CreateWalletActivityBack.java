package com.raistone.wallet.sealwallet.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.chenenyu.router.Router;
import com.chenenyu.router.annotation.Route;
import com.raistone.wallet.sealwallet.R;
import com.raistone.wallet.sealwallet.utils.Constant;
import com.raistone.wallet.sealwallet.utils.StatusBarUtil;
import com.raistone.wallet.sealwallet.utils.ToastHelper;
import com.raistone.wallet.sealwallet.widget.SmoothCheckBox;
import com.raistone.wallet.sealwallet.widget.TitleBar;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * 创建钱包
 */
@Route(value = "CreateWalletActivityBack")
public class CreateWalletActivityBack extends BaseActivity implements SmoothCheckBox.OnCheckedChangeListener {

    @BindView(R.id.titleBar)
    TitleBar titleBar;
    @BindView(R.id.pin_ed)
    EditText pinEd;
    @BindView(R.id.pin_confirm_ed)
    EditText pinConfirmEd;
    @BindView(R.id.checkbox)
    SmoothCheckBox checkbox;
    @BindView(R.id.create_wallet_btn)
    Button createWalletBtn;
    @BindView(R.id.import_tv)
    TextView importTv;
    @BindView(R.id.service_tv)
    TextView serviceTv;

    boolean mCheckStatus;

    private String mPassWord, mConfirmPw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_wallet);
        ButterKnife.bind(this);
        //StatusBarUtil.setTransparent(this);
        setTitle(titleBar, getResources().getString(R.string.create_wallet_string), true);
        createWalletBtn.setClickable(false);
        checkbox.setOnCheckedChangeListener(this);
        ActivityManager.getInstance().pushActivity(this);
        pinEd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String s = charSequence.toString();

                if (s.length() > 0) {
                    mPassWord = s;
                    updateCreateBtnStatus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        pinConfirmEd.addTextChangedListener(new ConfirmWatcher());
    }

    @OnClick({R.id.create_wallet_btn, R.id.import_tv, R.id.service_tv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            //创建钱包
            case R.id.create_wallet_btn:
                //startProgressDialog();
                if (!mPassWord.equals(mConfirmPw)) {
                    //stopProgressDialog();
                    ToastHelper.showToast(getResources().getString(R.string.pin_input_error));
                    return;
                }
                createWalletBtn.setClickable(false);

                Router.build("BackupMnemonicActivity").with("mPassWord", mPassWord).go(this);

                break;
            //导入钱包
            case R.id.import_tv:
                importTv.setClickable(false);
                Router.build("ImportWalletActivity").with("isFormCreate", true).go(this);
                break;
            //服务条款
            case R.id.service_tv:
                Intent intent = new Intent(this, WebViewActivity.class);
                intent.putExtra(Constant.IntentKey.WEB_URL, Constant.SealWebUrl.SERVICE_AGREEMENT_URL);
                intent.putExtra(Constant.IntentKey.WEB_TITLE, this.getResources().getString(R.string.service_and_agreement_string));
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onCheckedChanged(SmoothCheckBox checkBox, boolean isChecked) {
        mCheckStatus = isChecked;
        updateCreateBtnStatus();
    }

    class ConfirmWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String s = charSequence.toString().trim();
            if (s.length() > 0) {
                mConfirmPw = s;
                updateCreateBtnStatus();

            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    }

    private void updateCreateBtnStatus() {
        if (checkValue(mPassWord, mConfirmPw, mCheckStatus)) {
            createWalletBtn.setBackground(getResources().getDrawable(R.drawable.btn_bg));
            createWalletBtn.setClickable(true);
        } else {
            createWalletBtn.setBackground(getResources().getDrawable(R.drawable.btn_gray_bg));
            createWalletBtn.setClickable(false);
        }
    }

    private boolean checkValue(String pw, String confirmPw, boolean agreement) {
        if (TextUtils.isEmpty(pw)) {
            return false;
        }
        if (pw.length()<6) {
            return false;
        }

        if (TextUtils.isEmpty(confirmPw)) {
            return false;
        }


        if (confirmPw.length()<6) {
            return false;
        }

        if (!pw.equals(confirmPw)) {
            return false;
        }

        if (!agreement) {
            return false;
        }
        return true;

    }


}
