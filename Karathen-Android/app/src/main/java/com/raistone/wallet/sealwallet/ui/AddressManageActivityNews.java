package com.raistone.wallet.sealwallet.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chenenyu.router.Router;
import com.chenenyu.router.annotation.Route;
import com.example.zhouwei.library.CustomPopWindow;
import com.github.ontio.network.rest.UrlConsts;
import com.mylhyl.zxing.scanner.encode.QREncode;
import com.raistone.wallet.sealwallet.R;
import com.raistone.wallet.sealwallet.WalletApplication;
import com.raistone.wallet.sealwallet.adapter.AddressManagerAdapterNews;
import com.raistone.wallet.sealwallet.daoutils.ChainAddressDaoUtils;
import com.raistone.wallet.sealwallet.daoutils.HdWalletDaoUtils;
import com.raistone.wallet.sealwallet.factory.ChainAddressInfo;
import com.raistone.wallet.sealwallet.factory.ChainDataInfo;
import com.raistone.wallet.sealwallet.factory.HdWallet;
import com.raistone.wallet.sealwallet.factory.WalletManager;
import com.raistone.wallet.sealwallet.greendao.DaoSession;
import com.raistone.wallet.sealwallet.utils.ChainAddressCreateManager;
import com.raistone.wallet.sealwallet.utils.CommonUtils;
import com.raistone.wallet.sealwallet.utils.Constant;
import com.raistone.wallet.sealwallet.utils.Md5Utils;
import com.raistone.wallet.sealwallet.utils.StatusBarUtil;
import com.raistone.wallet.sealwallet.utils.ToastHelper;
import com.raistone.wallet.sealwallet.widget.AddressManagerPopup;
import org.greenrobot.eventbus.EventBus;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 地址管理
 */

@Route(value = "AddressManageActivityNews")
public class AddressManageActivityNews extends BaseActivity implements BaseQuickAdapter.OnItemClickListener, BaseQuickAdapter.OnItemChildClickListener {

    @BindView(R.id.ic_back)
    ImageView icBack;
    @BindView(R.id.back_ll)
    RelativeLayout backLl;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.add_iv)
    ImageView addIv;
    @BindView(R.id.import_iv)
    ImageView importIv;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    @BindView(R.id.add_ll)
    LinearLayout addLl;
    @BindView(R.id.import_ll)
    LinearLayout importLl;

    private AddressManagerAdapterNews managerAdapter;

    private Context context;

    private ChainAddressInfo walletSelect;

    private String pinValue;

    CustomPopWindow popWindow;


    List<ChainAddressInfo> chainAddressInfos;

    ChainDataInfo chainDataInfo;

    private HdWallet wallet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_manage);
        ButterKnife.bind(this);
        //StatusBarUtil.setTransparent(this);

        chainDataInfo = (ChainDataInfo) getIntent().getSerializableExtra("chainDataInfo");

        ActivityManager.getInstance().pushActivity(this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        recyclerview.setLayoutManager(layoutManager);

        context = this;

        wallet = HdWalletDaoUtils.findWalletBySelect();


        if (chainAddressInfos != null && chainAddressInfos.size() > 0) {
            managerAdapter = new AddressManagerAdapterNews(chainAddressInfos);
            recyclerview.setAdapter(managerAdapter);

            managerAdapter.setOnItemClickListener(this);
            managerAdapter.setOnItemChildClickListener(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        chainDataInfo.resetChainAddressInfos();

        DaoSession daoSession = WalletApplication.getsInstance().getDaoSession();

        chainDataInfo.__setDaoSession(daoSession);

        chainAddressInfos = chainDataInfo.getChainAddressInfos();

        if (chainAddressInfos != null && chainAddressInfos.size() > 0) {
            managerAdapter = new AddressManagerAdapterNews(chainAddressInfos);
            recyclerview.setAdapter(managerAdapter);
            managerAdapter.setOnItemClickListener(this);
            managerAdapter.setOnItemChildClickListener(this);
        }

    }

    @OnClick({R.id.back_ll, R.id.add_ll, R.id.import_ll})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            /**
             * 关闭
             */
            case R.id.back_ll:


                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();


                break;
            /**
             * 添加
             */
            case R.id.add_ll:

                Router.build("NewAddressActivity").with("chainId", chainDataInfo.getId()).with("selectChain", chainDataInfo).go(this);
                break;
            /**
             * 导入
             */
            case R.id.import_ll:
                Router.build("ImportAddressActivity").with("selectChain", chainDataInfo).with("wallet", chainAddressInfos.get(0)).go(this);

                break;
        }
    }


    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

        EventBus.getDefault().post(chainAddressInfos.get(position));
        ChainAddressDaoUtils.updateCurrent(chainAddressInfos.get(position).getId(), chainDataInfo.getChainType(),chainDataInfo.getAccountId());

        managerAdapter.notifyDataSetChanged();

        walletSelect = chainAddressInfos.get(position);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {


        int id = view.getId();
        switch (id) {
            /**
             * 复制
             */
            case R.id.copyLl:

                TextView textView = (TextView) adapter.getViewByPosition(recyclerview, position, R.id.address_tv);

                CommonUtils.copyString(textView, chainAddressInfos.get(position).getAddress());

                break;
            /**
             * 更多
             */
            case R.id.more_ll:

                AddressManagerPopup addressManagerPopup = new AddressManagerPopup(context);

                View view1 = view.findViewById(R.id.more_iv);

                walletSelect = chainAddressInfos.get(position);

                addressManagerPopup.setOnCommentPopupClickListener(new AddressManagerPopup.OnCommentPopupClickListener() {
                    @Override
                    public void onCommentClick(View v) {

                        switch (v.getId()) {
                            case R.id.export_private_ll:
                                ToastHelper.showToast("导出私钥");
                                break;
                            case R.id.export_mnemonic_ll:
                                ToastHelper.showToast("导出助记词");
                                break;
                            case R.id.export_wif_ll:
                                ToastHelper.showToast("导出wif");
                                break;
                            case R.id.update_name_ll:
                                ToastHelper.showToast("修改名称");
                                break;
                            case R.id.browser_query_ll:
                                ToastHelper.showToast("浏览器查询");
                                break;
                            case R.id.delete:
                                ToastHelper.showToast("删除地址");
                                break;
                            case R.id.claim_ll:
                                ToastHelper.showToast("提取GAS");
                                break;
                        }

                    }
                });

                int alpha = (int) (255 * (float) 15 / 100);
                int color = Color.argb(alpha, Color.red(Color.parseColor("#BBBDBF")), Color.green(Color.parseColor("#BBBDBF")), Color.blue(Color.parseColor("#BBBDBF")));
                addressManagerPopup.setBackgroundColor(color);
                addressManagerPopup.showPopupWindow(view1);

                break;
        }


    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            Intent intent = new Intent();
            // intent.putExtra("address", wallet.getAddress());
            setResult(RESULT_OK, intent);
            finish();

        }
        return super.onKeyDown(keyCode, event);
    }
}
