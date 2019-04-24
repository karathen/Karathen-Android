package com.raistone.wallet.sealwallet.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import com.chenenyu.router.annotation.Route;
import com.raistone.wallet.sealwallet.R;
import com.raistone.wallet.sealwallet.daoutils.HdWalletDaoUtils;
import com.raistone.wallet.sealwallet.factory.HdWallet;
import com.raistone.wallet.sealwallet.utils.Md5Utils;
import com.raistone.wallet.sealwallet.utils.StatusBarUtil;
import com.raistone.wallet.sealwallet.utils.ToastHelper;
import com.raistone.wallet.sealwallet.widget.TitleBar;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 重置Pin码
 */

@Route(value = "ResetPinActivity")
public class ResetPinActivity extends BaseActivity {

    @BindView(R.id.titleBar)
    TitleBar titleBar;
    @BindView(R.id.old_pin_ed)
    EditText oldPinEd;
    @BindView(R.id.new_pin_ed)
    EditText newPinEd;
    @BindView(R.id.pin_confirm_ed)
    EditText pinConfirmEd;
    @BindView(R.id.confirm_btn)
    Button confirmBtn;

    private HdWallet ethWallet;

    private String pwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pin);
        ButterKnife.bind(this);
        setTitle(titleBar,getString(R.string.reset_pin_string),true);
        ActivityManager.getInstance().pushActivity(this);

        ethWallet= (HdWallet) getIntent().getSerializableExtra("hdwallet");

        pwd=ethWallet.getWalletPwd();
    }

    @OnClick(R.id.confirm_btn)
    public void onViewClicked() {
        String oldPwd = oldPinEd.getText().toString();
        String newPwd = newPinEd.getText().toString();
        String confirPwd = pinConfirmEd.getText().toString();

        if(TextUtils.isEmpty(oldPwd)){
            ToastHelper.showToast(getResources().getString(R.string.original_pin_code));

            return;
        }

        if(!pwd.equals(Md5Utils.md5(oldPwd))){
            ToastHelper.showToast(getResources().getString(R.string.original_pin_error));
            return;
        }

        if(TextUtils.isEmpty(newPwd)){
            ToastHelper.showToast(getResources().getString(R.string.please_enter_a_new_pin));
            return;
        }

        if(TextUtils.isEmpty(confirPwd)){
            ToastHelper.showToast(getResources().getString(R.string.please_enter_a_confirmation_pin));
            return;
        }
        if(!newPwd.equals(confirPwd)){
            ToastHelper.showToast(getResources().getString(R.string.two_pin_codes_are_inconsistent));
            return;
        }
        String s = Md5Utils.md5(newPwd);


        ethWallet.setWalletPwd(s);

        HdWalletDaoUtils.updateWalletPwd(ethWallet);

        ToastHelper.showToast(getResources().getString(R.string.successfully_modified));
        finish();
    }
}
