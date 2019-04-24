package com.raistone.wallet.sealwallet.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.chenenyu.router.Router;
import com.chenenyu.router.annotation.Route;
import com.flyco.tablayout.SlidingTabLayout;
import com.raistone.wallet.sealwallet.R;
import com.raistone.wallet.sealwallet.ui.fragment.walletimport.ImportKeystoreFragment;
import com.raistone.wallet.sealwallet.ui.fragment.walletimport.ImportMnemonicFragment;
import com.raistone.wallet.sealwallet.ui.fragment.walletimport.ImportPriKeyFragment;
import com.raistone.wallet.sealwallet.ui.fragment.walletimport.ImportWifFragment;
import com.raistone.wallet.sealwallet.utils.StatusBarUtil;
import com.raistone.wallet.sealwallet.utils.ToastHelper;
import com.raistone.wallet.sealwallet.widget.TitleBar;
import com.unistrong.yang.zb_permission.Permission;
import com.unistrong.yang.zb_permission.ZbPermission;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 导入钱包
 */

@Route(value = "ImportWalletNewsActivity")
public class ImportWalletNewsActivity extends BaseActivity {


    @BindView(R.id.titleBar)
    TitleBar titleBar;
    @BindView(R.id.scaner_code_iv)
    ImageView scanerCodeIv;
    @BindView(R.id.scaner_code_ll)
    LinearLayout scanerCodeLl;
    @BindView(R.id.slidingTabLayout)
    SlidingTabLayout slidingTabLayout;
    @BindView(R.id.viewPager)
    ViewPager viewPager;

    private String[] mTitles;

    private Fragment importMnemonic,importKeystort,importPrikey,importWif;


    private final int REQUEST_CAMERA = 200;

    private ArrayList<Fragment> fragmentList = new ArrayList<>();

    private Context context;

    boolean isAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        setContentView(R.layout.activity_import_wallet_news);
        ButterKnife.bind(this);

        //StatusBarUtil.setTransparent(this);

        ActivityManager.getInstance().pushActivity(this);

        isAdd = getIntent().getBooleanExtra("isAdd", false);
        context=this;
        setTitle(titleBar,getResources().getString(R.string.import_wallet),true);
        initData();


        slidingTabLayout.setViewPager(viewPager, mTitles, this, fragmentList);
    }


    public void initData() {
        mTitles = new String[]{getResources().getString(R.string.mnemonic_string), "Keystore",getResources().getString(R.string.private_key_string),"WIF"};

        importMnemonic = new ImportMnemonicFragment();

        importKeystort = new ImportKeystoreFragment();

        importPrikey = new ImportPriKeyFragment();

        importWif = new ImportWifFragment();


        fragmentList.add(importMnemonic);
        fragmentList.add(importKeystort);
        fragmentList.add(importPrikey);
        fragmentList.add(importWif);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        List<Fragment> fragments = getSupportFragmentManager().getFragments();

        for (Fragment fragment : fragments) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    @OnClick(R.id.scaner_code_ll)
    public void onViewClicked() {
        ZbPermission.needPermission(this, REQUEST_CAMERA, Permission.CAMERA, new ZbPermission.ZbPermissionCallback() {
            @Override
            public void permissionSuccess(int i) {
                Router.build("NormalScannerActivity").requestCode(0).go(context);
            }

            @Override
            public void permissionFail(int i) {
                ToastHelper.showToast(getResources().getString(R.string.photo_permission));
            }
        });

    }

}
