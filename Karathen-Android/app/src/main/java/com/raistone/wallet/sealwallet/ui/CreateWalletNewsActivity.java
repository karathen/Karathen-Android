package com.raistone.wallet.sealwallet.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.chenenyu.router.Router;
import com.chenenyu.router.annotation.Route;
import com.github.ontio.crypto.MnemonicCode;
import com.raistone.wallet.sealwallet.R;
import com.raistone.wallet.sealwallet.utils.Constant;
import com.raistone.wallet.sealwallet.utils.LocalManageUtil;
import com.raistone.wallet.sealwallet.utils.SPUtil;
import com.raistone.wallet.sealwallet.utils.StatusBarUtil;
import com.raistone.wallet.sealwallet.widget.SmoothCheckBox;
import com.raistone.wallet.sealwallet.widget.TitleBar;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


@Route(value = "CreateWalletNewsActivity")
public class CreateWalletNewsActivity extends BaseActivity  {

    @BindView(R.id.titleBar)
    TitleBar titleBar;
    @BindView(R.id.wallet_name_ed)
    EditText walletNameEd;
    @BindView(R.id.pwd_ed)
    EditText pwdEd;
    @BindView(R.id.pwd_confirm_ed)
    EditText pwdConfirmEd;
    @BindView(R.id.pwd_tips_ed)
    EditText pwdTipsEd;

    @BindView(R.id.create_wallet_btn)
    Button createWalletBtn;
    @BindView(R.id.import_tv)
    TextView importTv;
    private int type;

    private String walletName, walletPwd, walletConfirmPwd, pwdTips;

    boolean mCheckStatus=true,isAdd;
    boolean isshowpass = false;

    private Context context;

    private String mnemonicCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_wallet_news);
        ButterKnife.bind(this);
        //StatusBarUtil.setTransparent(this);

        ActivityManager.getInstance().pushActivity(this);
        context = this;
        createWalletBtn.setClickable(false);
        setTitle(titleBar, getResources().getString(R.string.create_wallet), true);
        type = getIntent().getIntExtra("walletType", 0);
        isAdd = getIntent().getBooleanExtra("isAdd", false);


        walletNameEd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String s = charSequence.toString();

                if (s.length() > 0) {
                    walletName = s;
                    updateCreateBtnStatus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        pwdEd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String s = charSequence.toString();

                if (s.length() > 0) {
                    walletPwd = s;
                    updateCreateBtnStatus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        pwdConfirmEd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                String s = charSequence.toString();

                if (s.length() > 0) {
                    walletConfirmPwd = s;
                    updateCreateBtnStatus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    @OnClick({ R.id.create_wallet_btn, R.id.import_tv})
    public void onViewClicked(View view) {
        int savedLanguageType = SPUtil.getInstance(this).getSelectLanguage();
        switch (view.getId()) {
            case R.id.create_wallet_btn:

                pwdTips=pwdTipsEd.getText().toString();

                mnemonicCode = MnemonicCode.generateMnemonicCodesStr();

                if (!TextUtils.isEmpty(mnemonicCode)) {
                    Router.build("BackupMnemonicActivity")
                            .with("codesStr", mnemonicCode)
                            .with("mPassWord", walletPwd)
                            .with("walletType",type)
                            .with("walletName",walletName)
                            .with("walletTips",pwdTips+"")
                            .with("isAdd",isAdd)
                            .go(context);
                }
                break;
            case R.id.import_tv:
                break;
        }
    }


    private void updateCreateBtnStatus() {
        if (checkValue(walletName, walletPwd, walletConfirmPwd, mCheckStatus)) {
            createWalletBtn.setBackground(getResources().getDrawable(R.drawable.btn_bg));
            createWalletBtn.setClickable(true);
        } else {
            createWalletBtn.setBackground(getResources().getDrawable(R.drawable.btn_gray_bg));
            createWalletBtn.setClickable(false);
        }
    }

    private boolean checkValue(String waName, String pw, String confirmPw, boolean agreement) {

        if (TextUtils.isEmpty(waName)) {
            return false;
        }

        if (TextUtils.isEmpty(pw)) {
            return false;
        }
        if (pw.length() < 6) {
            return false;
        }

        if (TextUtils.isEmpty(confirmPw)) {
            return false;
        }


        if (confirmPw.length() < 6) {
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
