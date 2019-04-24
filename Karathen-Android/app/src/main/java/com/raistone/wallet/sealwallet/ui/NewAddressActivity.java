package com.raistone.wallet.sealwallet.ui;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.raistone.wallet.sealwallet.daoutils.ChainAddressDaoUtils;
import com.raistone.wallet.sealwallet.daoutils.HdWalletDaoUtils;
import com.raistone.wallet.sealwallet.datavases.LocalDataUtils;
import com.raistone.wallet.sealwallet.factory.ChainAddressInfo;
import com.raistone.wallet.sealwallet.factory.ChainDataInfo;
import com.raistone.wallet.sealwallet.factory.HdWallet;
import com.raistone.wallet.sealwallet.utils.ChainAddressCreateManager;
import com.raistone.wallet.sealwallet.utils.IconCreateUtils;
import com.raistone.wallet.sealwallet.utils.Md5Utils;
import com.raistone.wallet.sealwallet.utils.MultiChainCreateManager;
import com.raistone.wallet.sealwallet.utils.StatusBarUtil;
import com.raistone.wallet.sealwallet.utils.ToastHelper;
import com.raistone.wallet.sealwallet.widget.TitleBar;
import java.util.Arrays;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 新建地址
 */

@Route(value = "NewAddressActivity")
public class NewAddressActivity extends BaseActivity {

    @BindView(R.id.titleBar)
    TitleBar titleBar;
    @BindView(R.id.wallet_ec)
    EditText walletEc;
    @BindView(R.id.create_btn)
    Button createBtn;

    private Context mContext;
    private String pinValue;
    private String pwd;
    private long lastId;
    private String coinType;
    private String defName;

    private ChainDataInfo selectChain;

    private HdWallet wallet;

    private ProgressDialog progressDialog;

    @SuppressLint("HandlerLeak")
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            new Thread(){
                @Override
                public void run() {
                    super.run();

                    ChainAddressInfo chainByCoinType = createChainByCoinType(coinType);
                }
            }.start();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_address);
        ButterKnife.bind(this);
        //StatusBarUtil.setTransparent(this);

        ActivityManager.getInstance().pushActivity(this);
        mContext = this;

        selectChain = (ChainDataInfo) getIntent().getSerializableExtra("selectChain");


        coinType = selectChain.getChainType();

        //wd = WalletInfoDaoUtils.getWalletPwd();
        wallet= HdWalletDaoUtils.findWalletBySelect();

        pwd= wallet.getWalletPwd();

        //lastId = MultiChainInfoDaoUtils.getCountByType(coinType);

        lastId = ChainAddressDaoUtils.getCountByType(coinType,wallet.getAccountId());

        lastId++;

        defName = coinType +"-"+ lastId;
        walletEc.setText(defName);

        setTitle(titleBar, getResources().getString(R.string.new_string) + selectChain.getChainType() + getResources().getString(R.string.address_string),true);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getResources().getString(R.string.creating_new_address));
        progressDialog.setCancelable(false);

    }

    @OnClick(R.id.create_btn)
    public void onViewClicked() {
        showDialog();
    }

    private void showDialog() {
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
                    if (TextUtils.equals(value, pwd)) {


                        progressDialog.show();

                        Message msg=new Message();
                        msg.what=0;
                        handler.sendMessage(msg);
                        dialog.dismiss();
                        //finish();

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


    /**
     * 根据 coinTyoe 创建多链
     * @param coinType
     */
    public ChainAddressInfo createChainByCoinType(String coinType){


        ChainAddressInfo chainAddressInfo=null;


        if (coinType.equals(ChainAddressCreateManager.ETH_COIN_TYPE)) {


            String mnemonic = wallet.getMnemonic().trim();
            String[] split = mnemonic.split(" ");

            List<String> list = Arrays.asList(split);

            String lastType = ChainAddressDaoUtils.getLastType(coinType);

            String name = walletEc.getText().toString();

            if (TextUtils.isEmpty(name)) {
                chainAddressInfo = ChainAddressCreateManager.generateMnemonic(lastType, defName, pinValue, list);

            } else {
                chainAddressInfo = ChainAddressCreateManager.generateMnemonic(lastType, name, pinValue, list);
            }
            chainAddressInfo.setType_flag(lastType);

            chainAddressInfo.setCoinType(coinType);

            chainAddressInfo.setImagePath(IconCreateUtils.getIcon());

            chainAddressInfo.setAccountId(selectChain.getAccountId());

            chainAddressInfo.setIsImport(false);

            chainAddressInfo.setUsdtTotalPrice("0");

            chainAddressInfo.setCnyTotalPrice("0");

            chainAddressInfo.setChainId(selectChain.getId());

            ChainAddressDaoUtils.insertNewAddress(chainAddressInfo);

            selectChain.resetChainAddressInfos();

            LocalDataUtils.setAddresByAssets(selectChain.getAccountId(),"assets.json",chainAddressInfo.getId(),chainAddressInfo.getAddress() ,mContext);

            chainAddressInfo.resetAssetsInfoDataList();

            progressDialog.dismiss();

            finish();

        }

        /**
         * 创建NEO 链
         */
        if(coinType.equals(MultiChainCreateManager.NEO_COIN_TYPE)){
            //String mnemonic = ethWallet.getMnemonic().trim();

            String lastType = ChainAddressDaoUtils.getLastType(coinType);

            String name = walletEc.getText().toString();

            if (TextUtils.isEmpty(name)) {
                chainAddressInfo= ChainAddressCreateManager.generateMnemonicByNeoOrOnt(defName,wallet.getMnemonic(),coinType,lastType);
            } else {
                chainAddressInfo= ChainAddressCreateManager.generateMnemonicByNeoOrOnt(name,wallet.getMnemonic(),coinType,lastType);
            }

            chainAddressInfo.setType_flag(lastType);

            chainAddressInfo.setCoinType(coinType);

            chainAddressInfo.setImagePath(IconCreateUtils.getIcon());

            chainAddressInfo.setIsCurrent(false);

            chainAddressInfo.setAccountId(selectChain.getAccountId());
            chainAddressInfo.setPassword(Md5Utils.md5(pwd));
            chainAddressInfo.setIsImport(false);
            chainAddressInfo.setUsdtTotalPrice("0");
            chainAddressInfo.setCnyTotalPrice("0");
            chainAddressInfo.setChainId(selectChain.getId());

            ChainAddressDaoUtils.insertNewAddress(chainAddressInfo);

            selectChain.resetChainAddressInfos();

            LocalDataUtils.setAddresByAssets(selectChain.getAccountId(),"neo_assets.json",chainAddressInfo.getId(),chainAddressInfo.getAddress() ,mContext);

            chainAddressInfo.resetAssetsInfoDataList();

            progressDialog.dismiss();

            finish();

        }

        if(coinType.equals(MultiChainCreateManager.ONT_COIN_TYPE)){

            String mnemonic = wallet.getMnemonic().trim();

            String lastType = ChainAddressDaoUtils.getLastType(coinType);

            String name = walletEc.getText().toString();

            if (TextUtils.isEmpty(name)) {
                chainAddressInfo= ChainAddressCreateManager.generateMnemonicByNeoOrOnt(defName,mnemonic,coinType,lastType);
            } else {
                chainAddressInfo= ChainAddressCreateManager.generateMnemonicByNeoOrOnt(name,mnemonic,coinType,lastType);
            }

            chainAddressInfo.setType_flag(lastType);

            chainAddressInfo.setCoinType(coinType);

            chainAddressInfo.setImagePath(IconCreateUtils.getIcon());

            chainAddressInfo.setIsCurrent(false);

            chainAddressInfo.setPassword(Md5Utils.md5(pwd));

            chainAddressInfo.setIsImport(false);

            chainAddressInfo.setUsdtTotalPrice("0");

            chainAddressInfo.setCnyTotalPrice("0");
            chainAddressInfo.setAccountId(selectChain.getAccountId());

            chainAddressInfo.setChainId(selectChain.getId());

            ChainAddressDaoUtils.insertNewAddress(chainAddressInfo);


            selectChain.resetChainAddressInfos();

            LocalDataUtils.setAddresByAssets(selectChain.getAccountId(),"ont_assets.json",chainAddressInfo.getId(),chainAddressInfo.getAddress() ,mContext);

            chainAddressInfo.resetAssetsInfoDataList();

            progressDialog.dismiss();

            finish();


        }
        return chainAddressInfo;


    }

}
