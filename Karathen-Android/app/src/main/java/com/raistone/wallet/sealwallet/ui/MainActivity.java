package com.raistone.wallet.sealwallet.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.widget.FrameLayout;
import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.chenenyu.router.annotation.Route;
import com.raistone.wallet.sealwallet.R;
import com.raistone.wallet.sealwallet.ui.fragment.DiscoveryFragment;
import com.raistone.wallet.sealwallet.ui.fragment.UserCenterFragment;
import com.raistone.wallet.sealwallet.ui.fragment.WalletFragmentNews;
import com.raistone.wallet.sealwallet.utils.SharePreUtil;
import com.raistone.wallet.sealwallet.utils.ToastHelper;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;


@Route(value = "MainActivity")
public class MainActivity extends BaseActivity implements BottomNavigationBar.OnTabSelectedListener {

    @BindView(R.id.fl_content)
    FrameLayout flContent;
    @BindView(R.id.bnve)
    BottomNavigationBar bnve;

    private Fragment mCurFragment = new Fragment();
    private WalletFragmentNews mWalletFragment = new WalletFragmentNews();
    private DiscoveryFragment mDiscoveryFragment = new DiscoveryFragment();
    private UserCenterFragment mUserCenterFragment = new UserCenterFragment();
    private long exitTime = 0;

    public static MainActivity instance = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        instance = this;

        SharePreUtil.saveBoolean(getApplicationContext(), "isFirst", false);

        ActivityManager.getInstance().finishAllActivity();

        bnve.addItem(new BottomNavigationItem(R.drawable.wallet_select_icon, getResources().getString(R.string.wallet_string)).setInactiveIconResource(R.drawable.wallet_unselect_icon).setInActiveColor("#CFCFCF").setActiveColor("#6478FF"))
                .addItem(new BottomNavigationItem(R.drawable.discovery_select_icon, getResources().getString(R.string.discovery_string)).setInactiveIconResource(R.drawable.discovery_unselect_icon).setInActiveColor("#CFCFCF").setActiveColor("#6478FF"))
                .addItem(new BottomNavigationItem(R.drawable.user_center_select_icon, getResources().getString(R.string.user_center_string)).setInactiveIconResource(R.drawable.user_center_unselect_icon).setInActiveColor("#CFCFCF").setActiveColor("#6478FF"))
                .setMode(BottomNavigationBar.MODE_FIXED)
                .setFirstSelectedPosition(0)
                .initialise();

        bnve.setTabSelectedListener(this);

        bnve.selectTab(0);


    }


    private void toTargetFragment(int position) {
        switch (position) {
            case 0: {
                switchFragment(mWalletFragment);
                break;
            }
            case 1: {
                switchFragment(mDiscoveryFragment);
                break;
            }
            case 2: {
                switchFragment(mUserCenterFragment);
                break;
            }
            default: {
                switchFragment(mWalletFragment);
                break;
            }
        }
    }


    private void switchFragment(Fragment targetFragment) {
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction();
        if (!targetFragment.isAdded()) {
            transaction
                    .hide(mCurFragment)
                    .add(R.id.fl_content, targetFragment, targetFragment.getClass().getName())
                    .commit();
        } else {
            transaction
                    .hide(mCurFragment)
                    .show(targetFragment)
                    .commit();
        }
        mCurFragment = targetFragment;
    }

    @Override
    public void onTabSelected(int position) {
        toTargetFragment(position);
    }

    @Override
    public void onTabUnselected(int position) {

    }

    @Override
    public void onTabReselected(int position) {

    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (event.getAction() == KeyEvent.ACTION_DOWN
                    && event.getRepeatCount() == 0) {
                this.exitApp();
            }
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    private void exitApp() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            ToastHelper.showToast(getResources().getString(R.string.exit_tips));
            exitTime = System.currentTimeMillis();
        } else {

            ActivityManager.getInstance().finishAllActivity();
            System.exit(0);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        List<Fragment> fragments = getSupportFragmentManager().getFragments();

        for (Fragment fragment : fragments) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }

    }


    @Override
    protected void onDestroy() {
        //AssetsDaoUtils.deleteAllAssets();
        super.onDestroy();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    public static void reStart(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
