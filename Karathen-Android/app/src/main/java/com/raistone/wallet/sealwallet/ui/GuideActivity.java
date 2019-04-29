package com.raistone.wallet.sealwallet.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.chenenyu.router.Router;
import com.chenenyu.router.annotation.Route;
import com.raistone.wallet.sealwallet.R;
import com.raistone.wallet.sealwallet.adapter.GuideAdapter;
import com.raistone.wallet.sealwallet.utils.LocalManageUtil;
import com.raistone.wallet.sealwallet.utils.StatusBarUtil;
import com.raistone.wallet.sealwallet.utils.ToastHelper;
import com.unistrong.yang.zb_permission.Permission;
import com.unistrong.yang.zb_permission.ZbPermission;
import com.unistrong.yang.zb_permission.ZbPermissionFail;
import com.unistrong.yang.zb_permission.ZbPermissionSuccess;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@Route(value ="GuideActivity")
public class GuideActivity extends AppCompatActivity {

    @BindView(R.id.viewpager)
    ViewPager viewpager;
    @BindView(R.id.create_btn)
    Button createBtn;
    @BindView(R.id.import_wallet_tv)
    TextView importWalletTv;

    private GuideAdapter guideAdapter;

    private static final int[] layouts = {R.layout.guide_layout1, R.layout.guide_layout2, R.layout.guide_layout3,
            R.layout.guide_layout4};


    private final int REQUEST_STORAGE = 100;

    boolean permission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        ButterKnife.bind(this);

        ActivityManager.getInstance().pushActivity(this);

        guideAdapter = new GuideAdapter(this, layouts);

        viewpager.setAdapter(guideAdapter);

        ZbPermission.needPermission(this, REQUEST_STORAGE, Permission.STORAGE);


    }

    @OnClick({R.id.create_btn, R.id.import_wallet_tv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.create_btn:
                if(permission) {
                    Router.build("CreateWalletActivity").go(this);
                }else {
                    ToastHelper.showToast(getResources().getString(R.string.please_open_permissions));
                }
                break;
            case R.id.import_wallet_tv:
                if(permission) {
                    Router.build("ImportWalletActivity").go(this);
                }else {
                    ToastHelper.showToast(getResources().getString(R.string.please_open_permissions));
                }
                break;
        }
    }

    @ZbPermissionSuccess(requestCode = REQUEST_STORAGE)
    public void permissionSuccess() {
        permission=true;
    }

    @ZbPermissionFail(requestCode = REQUEST_STORAGE)
    public void permissionFail() {
        permission=false;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        ZbPermission.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocalManageUtil.setLocal(newBase));
    }

}
