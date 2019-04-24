package com.raistone.wallet.sealwallet.ui.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.allen.library.SuperTextView;
import com.chenenyu.router.Router;
import com.raistone.wallet.sealwallet.R;
import com.raistone.wallet.sealwallet.WalletApplication;
import com.raistone.wallet.sealwallet.daoutils.AssetsDetailDaoUtils;
import com.raistone.wallet.sealwallet.daoutils.ChainAddressDaoUtils;
import com.raistone.wallet.sealwallet.daoutils.ChainDaoUtils;
import com.raistone.wallet.sealwallet.daoutils.HdWalletDaoUtils;
import com.raistone.wallet.sealwallet.datavases.AppDataBase;
import com.raistone.wallet.sealwallet.datavases.AssetsDaoUtils;
import com.raistone.wallet.sealwallet.datavases.ChainInfoDaoUtils;
import com.raistone.wallet.sealwallet.datavases.MultiChainInfoDaoUtils;
import com.raistone.wallet.sealwallet.datavases.TransferDaoUtils;
import com.raistone.wallet.sealwallet.datavases.WalletDaoUtils;
import com.raistone.wallet.sealwallet.datavases.WalletInfoDaoUtils;
import com.raistone.wallet.sealwallet.entity.ETHWallet;
import com.raistone.wallet.sealwallet.entity.MultiChainInfo;
import com.raistone.wallet.sealwallet.entity.WalletInfo;
import com.raistone.wallet.sealwallet.factory.HdWallet;
import com.raistone.wallet.sealwallet.factory.WalletManager;
import com.raistone.wallet.sealwallet.greendao.DaoMaster;
import com.raistone.wallet.sealwallet.greendao.DaoSession;
import com.raistone.wallet.sealwallet.ui.MultiChainManagerActivity;
import com.raistone.wallet.sealwallet.utils.Md5Utils;
import com.raistone.wallet.sealwallet.utils.SharePreUtil;
import com.raistone.wallet.sealwallet.utils.ToastHelper;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.greenrobot.greendao.database.Database;

import java.math.BigDecimal;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * 个人中心
 */
public class UserCenterFragment extends BaseFragment implements OnRefreshListener {


    @BindView(R.id.dlgl_super_tv)
    SuperTextView dlglSuperTv;
    @BindView(R.id.wallet_backup_super_tv)
    SuperTextView walletBackupSuperTv;
    @BindView(R.id.update_pin_super_tv)
    SuperTextView updatePinSuperTv;
    @BindView(R.id.currency_super_tv)
    SuperTextView currencySuperTv;
    @BindView(R.id.language_setting_super_tv)
    SuperTextView languageSettingSuperTv;
    @BindView(R.id.technical_support_super_tv)
    SuperTextView technicalSupportSuperTv;
    @BindView(R.id.about_me_super_tv)
    SuperTextView aboutMeSuperTv;
    @BindView(R.id.login_out_btn)
    Button loginOutBtn;
    @BindView(R.id.smartrefreshlayout)
    SmartRefreshLayout smartrefreshlayout;
    Unbinder unbinder;
    @BindView(R.id.back_tv)
    TextView backTv;

    //private MultiChainInfo current;
    private String pinValue;
    private Context mContext;

    //List<WalletInfo> wallet;

    //WalletInfo walletInfo;

    HdWallet wallet;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_center, container, false);
        unbinder = ButterKnife.bind(this, view);
        smartrefreshlayout.setOnRefreshListener(this);
        mContext = getActivity();

        return view;
    }


    @Override
    public void onResume() {
        super.onResume();

        //wallet= WalletManager.getInstance().getWalletChains(1l);

       /* if(wallet!=null) {
            updateStatus();
        }*/
    }

    private void updateStatus() {
        if (wallet.getIsBackup()) {
            backTv.setBackground(getResources().getDrawable(R.drawable.tag_right_top_bg));
            backTv.setText(getResources().getString(R.string.have_backup_string));
        }else {
            backTv.setBackground(getResources().getDrawable(R.drawable.unback_bg));
            backTv.setText(getResources().getString(R.string.not_backup_string));
        }
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        /*if (!hidden) {
           updateStatus();
        }*/
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.dlgl_super_tv, R.id.wallet_backup_super_tv, R.id.update_pin_super_tv, R.id.currency_super_tv, R.id.language_setting_super_tv, R.id.technical_support_super_tv, R.id.about_me_super_tv, R.id.login_out_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {

            case R.id.dlgl_super_tv:
                Router.build("MultiChainManagerActivity").go(mContext);
                break;
            case R.id.wallet_backup_super_tv:
                showDialog(0);
                break;

            case R.id.update_pin_super_tv:
                Router.build("ResetPinActivity").go(mContext);
                break;

            case R.id.currency_super_tv:
                Router.build("CurrencySettingActivity").go(mContext);
                break;

            case R.id.language_setting_super_tv:
                Router.build("LanguageSettingActivity").go(mContext);
                break;
            case R.id.technical_support_super_tv:
                Router.build("TechnicalSupportActivity").go(mContext);
                break;
            case R.id.about_me_super_tv:
                //AboutUsActivity
                Router.build("AboutUsActivity").go(mContext);
                break;
            case R.id.login_out_btn:
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle(R.string.exit_current_wallet_title);
                builder.setMessage(R.string.exit_wallet_warning_string);
                builder.setPositiveButton(R.string.confirm_string, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        showDialog(1);
                    }
                });
                builder.setNegativeButton(R.string.cancel_string, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
                break;
        }
    }


    /**
     * @param tpye 0 代表备份钱包，1 代表退出钱包
     */
    private void showDialog(final int tpye) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(R.layout.coustom_dialog_layout, null);
        //builer.setView(v);//这里如果使用builer.setView(v)，自定义布局只会覆盖title和button之间的那部分
        final Dialog dialog = builder.create();
        final EditText editText = v.findViewById(R.id.pin_ed);
        TextView textView = v.findViewById(R.id.cancel_tv);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        TextView textView1 = v.findViewById(R.id.confirm_tv);
        textView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pinValue = editText.getText().toString();
                if (!TextUtils.isEmpty(pinValue)) {
                    String value = Md5Utils.md5(pinValue);
                    String pwd = wallet.getWalletPwd();
                    if (TextUtils.equals(value, pwd)) {
                        if (tpye == 0) {
                            dialog.dismiss();

                            Router.build("BackupMnemonicActivity").with("userCenter",true).with("mnemonic", wallet).go(getActivity());
                        } else if (tpye == 1) {
                            SharePreUtil.saveBoolean(mContext, "isFirst", true);

                            FlowManager.getDatabase(AppDataBase.class).reset();


                            Database database = WalletApplication.getsInstance().getDaoSession().getDatabase();

                            DaoMaster.dropAllTables(database,true);

                            DaoMaster.createAllTables(database,true);


                            WalletFragment.usdPrice=new BigDecimal("0");
                            WalletFragment.cnyPrice=new BigDecimal("0");

                            dialog.dismiss();

                            Router.build("GuideActivityNews").go(mContext);
                            getActivity().finish();
                        }
                    } else {
                        ToastHelper.showToast(getString(R.string.pin_error));
                    }
                } else {
                    ToastHelper.showToast(getString(R.string.pin_error));
                }
            }
        });
        dialog.setCancelable(false);
        dialog.show();
        dialog.getWindow().setContentView(v);

        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);


    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        refreshLayout.finishRefresh(2000);
    }
}
