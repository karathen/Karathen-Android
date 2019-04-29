package com.raistone.wallet.sealwallet.ui;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.chenenyu.router.annotation.Route;
import com.raistone.wallet.sealwallet.R;
import com.raistone.wallet.sealwallet.datavases.AssetsDaoUtils;
import com.raistone.wallet.sealwallet.datavases.LocalDataUtils;
import com.raistone.wallet.sealwallet.datavases.MultiChainInfoDaoUtils;
import com.raistone.wallet.sealwallet.datavases.WalletInfoDaoUtils;
import com.raistone.wallet.sealwallet.entity.AssetsInfo;
import com.raistone.wallet.sealwallet.entity.MessageEvent;
import com.raistone.wallet.sealwallet.entity.MultiChainInfo;
import com.raistone.wallet.sealwallet.utils.IconCreateUtils;
import com.raistone.wallet.sealwallet.utils.Md5Utils;
import com.raistone.wallet.sealwallet.utils.MultiChainCreateManager;
import com.raistone.wallet.sealwallet.utils.StatusBarUtil;
import com.raistone.wallet.sealwallet.utils.ToastHelper;
import com.raistone.wallet.sealwallet.widget.TitleBar;
import org.greenrobot.eventbus.EventBus;
import java.util.Arrays;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


@Route(value = "NewAddressActivityBack")
public class NewAddressActivityBack extends BaseActivity {

    @BindView(R.id.titleBar)
    TitleBar titleBar;
    @BindView(R.id.wallet_ec)
    EditText walletEc;
    @BindView(R.id.create_btn)
    Button createBtn;

    private MultiChainInfo ethWallet;
    private Context mContext;
    private String pinValue;
    private String pwd;
    private long lastId;
    private String coinType;
    private String defName;
    private String accountId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_address);
        ButterKnife.bind(this);
        //StatusBarUtil.setTransparent(this);

        mContext = this;

        ethWallet = (MultiChainInfo) getIntent().getSerializableExtra("wallet");

        accountId = getIntent().getStringExtra("accountId");

        coinType = ethWallet.getCoinType();

        pwd = WalletInfoDaoUtils.getWalletPwd();

        lastId = MultiChainInfoDaoUtils.getCountByType(coinType);

        lastId++;

        defName = coinType +"-"+ lastId;
        walletEc.setText(defName);

        setTitle(titleBar, getResources().getString(R.string.new_string) + ethWallet.getCoinType() + getResources().getString(R.string.address_string),true);

    }

    @OnClick(R.id.create_btn)
    public void onViewClicked() {
        showDialog();
    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        LayoutInflater inflater = LayoutInflater.from(mContext);
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
                pinValue = editText.getText().toString();
                if (!TextUtils.isEmpty(pinValue)) {
                    String value = Md5Utils.md5(pinValue);
                    if (TextUtils.equals(value, pwd)) {


                        MultiChainInfo chainByCoinType = createChainByCoinType(coinType);
                        EventBus.getDefault().post(new MessageEvent(chainByCoinType.getAddress()));
                        dialog.dismiss();
                        finish();

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


    public MultiChainInfo createChainByCoinType(String coinType){
        MultiChainInfo ew = null;


        if(coinType.equals(MultiChainCreateManager.ETH_COIN_TYPE)){
            String mnemonic = ethWallet.getMnemonic().trim();
            String[] split = mnemonic.split(" ");

            List<String> list = Arrays.asList(split);

            String lastType = MultiChainInfoDaoUtils.getLastType(coinType);

            String name = walletEc.getText().toString();

            if (TextUtils.isEmpty(name)) {
                ew = MultiChainCreateManager.generateMnemonic(lastType, defName, pinValue, list);

            } else {
                ew = MultiChainCreateManager.generateMnemonic(lastType, name, pinValue, list);
            }
            ew.setType_flag(lastType);

            ew.setCoinType(coinType);

            ew.setImagePath(IconCreateUtils.getIcon());

            ew.setAccountId(accountId);
            ew.setImport(false);
            ew.insert();

            LocalDataUtils.setAddresByEthAssets(ew.getAddress(),this);

        }

        if(coinType.equals(MultiChainCreateManager.NEO_COIN_TYPE)){
            String mnemonic = ethWallet.getMnemonic().trim();

            String lastType = MultiChainInfoDaoUtils.getLastType(coinType);

            String name = walletEc.getText().toString();

            if (TextUtils.isEmpty(name)) {
               ew= MultiChainCreateManager.generateMnemonicByNeoOrOnt(defName,mnemonic,coinType,lastType);
            } else {
               ew= MultiChainCreateManager.generateMnemonicByNeoOrOnt(name,mnemonic,coinType,lastType);
            }

            ew.setType_flag(lastType);

            ew.setCoinType(coinType);

            ew.setImagePath(IconCreateUtils.getIcon());

            ew.setCurrent(false);

            ew.setAccountId(accountId);
            ew.setPassword(Md5Utils.md5(pwd));
            ew.setImport(false);
            ew.insert();

            LocalDataUtils.setAddresByNeoAssets(ew.getAddress(),this);

        }


        if(coinType.equals(MultiChainCreateManager.ONT_COIN_TYPE)){

            String mnemonic = ethWallet.getMnemonic().trim();

            String lastType = MultiChainInfoDaoUtils.getLastType(coinType);

            String name = walletEc.getText().toString();

            if (TextUtils.isEmpty(name)) {
                ew= MultiChainCreateManager.generateMnemonicByNeoOrOnt(defName,mnemonic,coinType,lastType);
            } else {
                ew= MultiChainCreateManager.generateMnemonicByNeoOrOnt(name,mnemonic,coinType,lastType);
            }

            ew.setType_flag(lastType);

            ew.setCoinType(coinType);

            ew.setImagePath(IconCreateUtils.getIcon());

            ew.setCurrent(false);
            ew.setPassword(Md5Utils.md5(pwd));
            ew.setImport(false);
            ew.setAccountId(accountId);
            ew.insert();

            LocalDataUtils.setAddresByOntAssets(ew.getAddress(),this);


        }
        return ew;
    }


   public void setAddresByAssets(String coinType,String address){

       List<AssetsInfo.DataBean> allAssets = AssetsDaoUtils.findAllAssets();
       for (int i=0;i<allAssets.size();i++){
           AssetsInfo.DataBean dataBean = allAssets.get(i);
           if(coinType.equals(dataBean.getCoinType())) {
               dataBean.setWalletAddress(address);
               dataBean.update();
               continue;
           }
       }
   }

}
