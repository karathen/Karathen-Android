package com.raistone.wallet.sealwallet.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chenenyu.router.Router;
import com.chenenyu.router.annotation.Route;
import com.raistone.wallet.sealwallet.R;
import com.raistone.wallet.sealwallet.adapter.WalletManagerAdapter;
import com.raistone.wallet.sealwallet.daoutils.HdWalletDaoUtils;
import com.raistone.wallet.sealwallet.entity.UpateWalletInfo;
import com.raistone.wallet.sealwallet.factory.HdWallet;
import com.raistone.wallet.sealwallet.utils.Md5Utils;
import com.raistone.wallet.sealwallet.utils.SharePreUtil;
import com.raistone.wallet.sealwallet.utils.StatusBarUtil;
import com.raistone.wallet.sealwallet.utils.ToastHelper;
import com.raistone.wallet.sealwallet.widget.TitleBar;
import com.raistone.wallet.sealwallet.widget.WalletManagerPopup;
import org.greenrobot.eventbus.EventBus;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@Route(value = "WalletManagerActivity")
public class WalletManagerActivity extends BaseActivity implements BaseQuickAdapter.OnItemClickListener, BaseQuickAdapter.OnItemChildClickListener {

    @BindView(R.id.titleBar)
    TitleBar titleBar;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    @BindView(R.id.add_btn)
    Button addBtn;

    private WalletManagerAdapter walletManagerAdapter;

    private Context context;

    private List<HdWallet> allWallet;

    private HdWallet selectWallet;

    private WalletManagerPopup addressManagerPopup;

    private String pwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_manager);
        ButterKnife.bind(this);

        setTitle(titleBar, getResources().getString(R.string.wallet_manager_string), true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        recyclerview.setLayoutManager(layoutManager);

        context = this;


    }

    @Override
    protected void onResume() {
        super.onResume();
        allWallet = HdWalletDaoUtils.findAllWallet();

        if (allWallet != null && allWallet.size() > 0) {
            walletManagerAdapter = new WalletManagerAdapter(allWallet);
            recyclerview.setAdapter(walletManagerAdapter);

            walletManagerAdapter.setOnItemClickListener(this);
            walletManagerAdapter.setOnItemChildClickListener(this);

            selectWallet = HdWalletDaoUtils.findWalletBySelect();

        }
    }

    @OnClick(R.id.add_btn)
    public void onViewClicked() {
        Router.build("GuideActivityNews").with("isAdd", true).go(context);
    }

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {


        selectWallet = allWallet.get(position);
        addressManagerPopup = new WalletManagerPopup(context, selectWallet);

        View view1 = view.findViewById(R.id.more_iv);

        addressManagerPopup.setOnCommentPopupClickListener(new WalletManagerPopup.OnCommentPopupClickListener() {
            @Override
            public void onCommentClick(View v) {
                switch (v.getId()) {

                    case R.id.multi_management_rl:

                        addressManagerPopup.dismiss();
                        Router.build("MultiChainManagerActivity").with("hdwallet", selectWallet).go(context);

                        break;
                    case R.id.wallet_update_name_rl:
                        addressManagerPopup.dismiss();
                        showUpdateWalletNameDialog();

                        break;
                    case R.id.reset_pwd_rl:
                        addressManagerPopup.dismiss();
                        Router.build("ResetPinActivity").with("hdwallet", selectWallet).go(context);
                        break;
                    case R.id.pwd_tips_rl:
                        addressManagerPopup.dismiss();
                        String pwdTips = selectWallet.getPwdTips();

                        if (TextUtils.isEmpty(pwdTips)) {
                            pwdTips = getResources().getString(R.string.none_string);
                        }

                        TextView msg = new TextView(context);
                        msg.setText(pwdTips);
                        msg.setPadding(10, 30, 10, 10);
                        msg.setGravity(Gravity.CENTER);
                        msg.setTextSize(18);


                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setPositiveButton(R.string.confirm_string, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                        builder.setView(msg);
                        Dialog dialog = builder.create();


                        dialog.setCancelable(true);
                        dialog.show();

                        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
                        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

                        break;
                    case R.id.back_rl:
                        addressManagerPopup.dismiss();
                        showDialogByType(0);
                        break;
                    case R.id.delete_rl:
                        addressManagerPopup.dismiss();
                        showDialogByType(1);
                        break;
                }

            }
        });

        int alpha = (int) (255 * (float) 15 / 100);
        int color = Color.argb(alpha, Color.red(Color.parseColor("#BBBDBF")), Color.green(Color.parseColor("#BBBDBF")), Color.blue(Color.parseColor("#BBBDBF")));
        addressManagerPopup.setBackgroundColor(color);
        addressManagerPopup.showPopupWindow(view1);
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

        if (allWallet != null && allWallet.size() > 0) {
            HdWalletDaoUtils.updateCurrent(allWallet.get(position).getWalletId());

            walletManagerAdapter.notifyDataSetChanged();

            selectWallet = allWallet.get(position);

            EventBus.getDefault().post(selectWallet);
        }
    }

    private void showUpdateWalletNameDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.update_dialog_layout, null);

        TextView title_tv = v.findViewById(R.id.tv_title);
        title_tv.setText(getResources().getString(R.string.update_wallet_name));
        final Dialog dialog = builder.create();
        final EditText editText = v.findViewById(R.id.wallet_name_ed);
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
                String name = editText.getText().toString();
                if (TextUtils.isEmpty(name)) {
                    ToastHelper.showToast(getResources().getString(R.string.wallet_name_not_null_string));
                    return;
                }

                selectWallet.setWalletName(name);

                EventBus.getDefault().post(new UpateWalletInfo(name));

                HdWalletDaoUtils.updateWallet(selectWallet);


                walletManagerAdapter.setNewData(allWallet);
                walletManagerAdapter.notifyDataSetChanged();

                dialog.dismiss();
            }
        });
        dialog.setCancelable(false);
        dialog.show();
        dialog.getWindow().setContentView(v);

        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);


    }


    private void showDialogByType(final int type) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.coustom_dialog_layout, null);
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
                pwd = editText.getText().toString();
                if (!TextUtils.isEmpty(pwd)) {
                    String value = Md5Utils.md5(pwd);
                    String pwd = selectWallet.getWalletPwd();
                    if (TextUtils.equals(value, pwd)) {

                        switch (type) {
                            case 0:
                                dialog.dismiss();
                                Router.build("BackupMnemonicActivity").with("userCenter",true).with("mnemonic", selectWallet).go(context);
                                break;
                            case 1:
                                if (selectWallet != null) {
                                    if(allWallet!=null && allWallet.size()>0) {
                                        allWallet.remove(selectWallet);
                                        allWallet=HdWalletDaoUtils.deleteWalletById(selectWallet.getWalletId());

                                        walletManagerAdapter.setNewData(allWallet);
                                        walletManagerAdapter.notifyDataSetChanged();

                                        if(allWallet.size()>0) {
                                            if(selectWallet.getIsCurrent()) {
                                                EventBus.getDefault().post(allWallet.get(0));
                                            }else {
                                                HdWallet bySelect = HdWalletDaoUtils.findWalletBySelect();
                                                EventBus.getDefault().post(bySelect);
                                            }
                                        }

                                        if(allWallet==null || allWallet.size()<=0) {
                                            //finish();
                                            ActivityManager.getInstance().pushActivity(WalletManagerActivity.this);
                                            SharePreUtil.saveBoolean(getApplicationContext(), "isFirst", true);
                                            Router.build("GuideActivityNews").with("isExit",true).go(context);
                                        }
                                    }else {
                                        ActivityManager.getInstance().pushActivity(WalletManagerActivity.this);
                                        SharePreUtil.saveBoolean(getApplicationContext(), "isFirst", true);
                                        Router.build("GuideActivityNews").with("isExit",true).go(context);
                                    }
                                }
                                dialog.dismiss();
                                break;
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
}
