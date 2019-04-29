package com.raistone.wallet.sealwallet;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.chenenyu.router.annotation.Route;
import com.raistone.wallet.sealwallet.ui.ActivityManager;
import com.raistone.wallet.sealwallet.ui.BaseActivity;
import com.raistone.wallet.sealwallet.utils.ToastHelper;
import com.raistone.wallet.sealwallet.widget.TitleBar;
import com.unistrong.yang.zb_permission.Permission;
import com.unistrong.yang.zb_permission.ZbPermission;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;




@Route(value = "TechnicalSupportActivity")
public class TechnicalSupportActivity extends BaseActivity implements View.OnTouchListener {

    @BindView(R.id.titleBar)
    TitleBar titleBar;
    @BindView(R.id.em_tv)
    TextView emTv;
    @BindView(R.id.email_ed)
    EditText emailEd;
    @BindView(R.id.title_tv)
    TextView titleTv;
    @BindView(R.id.title_ed)
    EditText titleEd;
    @BindView(R.id.phone_tv)
    TextView phoneTv;
    @BindView(R.id.phone_ed)
    EditText phoneEd;
    @BindView(R.id.depict_tv)
    TextView depictTv;
    @BindView(R.id.depict_ed)
    EditText depictEd;
    @BindView(R.id.commit_btn)
    Button commitBtn;
    @BindView(R.id.select_iv)
    ImageView selectIv;


    private String emailValue;

    private String telephoneValue;

    private String titleValue;

    private String contentValue;


    private final int REQUEST_PHON = 500;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_technical_support);
        ButterKnife.bind(this);

        ActivityManager.getInstance().pushActivity(this);

        setTitle(titleBar, getString(R.string.technical_support_string), true);
        depictEd.setOnTouchListener(this);

        context = this;


    }


    @OnClick(R.id.commit_btn)
    public void onViewClicked() {
        emailValue=emailEd.getText().toString();

        telephoneValue=phoneEd.getText().toString();

        titleValue=titleEd.getText().toString();

        contentValue=depictEd.getText().toString();

        if(TextUtils.isEmpty(emailValue)){
            ToastHelper.showToast(getResources().getString(R.string.input_email_address));
            return;
        }

        if(TextUtils.isEmpty(telephoneValue)){
            ToastHelper.showToast(getResources().getString(R.string.input_phone_number));
            return;
        }

        if(TextUtils.isEmpty(titleValue)){
            ToastHelper.showToast(getResources().getString(R.string.input_title_number));
            return;
        }

        if(TextUtils.isEmpty(contentValue)){
            ToastHelper.showToast(getResources().getString(R.string.input_depict_string));
            return;
        }

        ZbPermission.needPermission(this, REQUEST_PHON, Permission.PHONE, new ZbPermission.ZbPermissionCallback() {
            @Override
            public void permissionSuccess(int i) {
                ToastHelper.showToast(getResources().getString(R.string.submitted_string));
            }

            @Override
            public void permissionFail(int i) {
                ToastHelper.showToast(getResources().getString(R.string.please_open_permissions));
            }
        });


    }


    private boolean canVerticalScroll(EditText editText) {
        int scrollY = editText.getScrollY();
        int scrollRange = editText.getLayout().getHeight();
        int scrollExtent = editText.getHeight() - editText.getCompoundPaddingTop() - editText.getCompoundPaddingBottom();
        int scrollDifference = scrollRange - scrollExtent;

        if (scrollDifference == 0) {
            return false;
        }

        return (scrollY > 0) || (scrollY < scrollDifference - 1);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if ((view.getId() == R.id.depict_ed && canVerticalScroll(depictEd))) {
            view.getParent().requestDisallowInterceptTouchEvent(true);
            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                view.getParent().requestDisallowInterceptTouchEvent(false);
            }
        }
        return false;
    }


}
