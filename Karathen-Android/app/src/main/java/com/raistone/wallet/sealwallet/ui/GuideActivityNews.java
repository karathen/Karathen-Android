package com.raistone.wallet.sealwallet.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import com.chenenyu.router.annotation.Route;
import com.flyco.tablayout.SlidingTabLayout;
import com.raistone.wallet.sealwallet.R;
import com.raistone.wallet.sealwallet.ui.fragment.CloudWalletFragment;
import com.raistone.wallet.sealwallet.ui.fragment.HDWalletFragment;
import com.raistone.wallet.sealwallet.ui.fragment.HardwareWalletFragment;
import com.raistone.wallet.sealwallet.utils.LocalManageUtil;
import com.raistone.wallet.sealwallet.widget.TitleBar;
import com.unistrong.yang.zb_permission.Permission;
import com.unistrong.yang.zb_permission.ZbPermission;
import com.unistrong.yang.zb_permission.ZbPermissionFail;
import com.unistrong.yang.zb_permission.ZbPermissionSuccess;
import java.util.ArrayList;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@Route(value = "GuideActivityNews")
public class GuideActivityNews extends BaseActivity {


    private final int REQUEST_STORAGE = 100;

    boolean permission;

    @BindView(R.id.viewPager)
    ViewPager viewPager;
    @BindView(R.id.titleBar)
    TitleBar titleBar;
    @BindView(R.id.slidingTabLayout)
    SlidingTabLayout slidingTabLayout;
    @BindView(R.id.close_ll)
    LinearLayout closeLl;

    private ArrayList<Fragment> fragmentList = new ArrayList<>();
    private String[] mTitles;

    private Fragment cloudFragment, hdFragment, hardwareFragment;

    boolean isExit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide_news);
        ButterKnife.bind(this);

        boolean isAdd = getIntent().getBooleanExtra("isAdd", false);
        isExit = getIntent().getBooleanExtra("isExit", false);

        setTitle(titleBar, getResources().getString(R.string.app_name));
        ActivityManager.getInstance().pushActivity(this);

        initView();

        ZbPermission.needPermission(this, REQUEST_STORAGE, Permission.STORAGE);

        if(isAdd){
            closeLl.setVisibility(View.VISIBLE);
        }else {
            closeLl.setVisibility(View.GONE);
        }


    }

    public void initData() {
        mTitles = new String[]{getResources().getString(R.string.hd_wallet_string), getResources().getString(R.string.hardware_wallet_string)};
        cloudFragment = new CloudWalletFragment();

        hdFragment = new HDWalletFragment();

        hardwareFragment = new HardwareWalletFragment();

        fragmentList.add(hdFragment);
        fragmentList.add(hardwareFragment);
    }

    public void initView() {
        initData();

        slidingTabLayout.setViewPager(viewPager, mTitles, this, fragmentList);
    }

    @OnClick(R.id.close_ll)
    public void onViewClicked() {
        if(isExit){
            MainActivity.instance.finish();
            ActivityManager.getInstance().finishAllActivity();
            System.exit(0);

        }else {
            finish();
        }
    }


    @ZbPermissionSuccess(requestCode = REQUEST_STORAGE)
    public void permissionSuccess() {
        permission = true;
    }

    @ZbPermissionFail(requestCode = REQUEST_STORAGE)
    public void permissionFail() {
        permission = false;
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


    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (event.getAction() == KeyEvent.ACTION_DOWN && event.getRepeatCount() == 0) {
                if(isExit){
                    MainActivity.instance.finish();
                    ActivityManager.getInstance().finishAllActivity();
                    System.exit(0);
                }else {
                    finish();
                }
            }
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

}
