package com.raistone.wallet.sealwallet.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.chenenyu.router.Router;
import com.chenenyu.router.annotation.Route;
import com.raistone.wallet.sealwallet.R;
import com.raistone.wallet.sealwallet.adapter.AssetsAdapter;
import com.raistone.wallet.sealwallet.entity.GasInfo;
import com.raistone.wallet.sealwallet.factory.AssetsDeatilInfo;
import com.raistone.wallet.sealwallet.utils.BigDecimalUtils;
import com.raistone.wallet.sealwallet.utils.ChainAddressCreateManager;
import com.raistone.wallet.sealwallet.utils.Constant;
import com.raistone.wallet.sealwallet.utils.GsonUtils;
import com.raistone.wallet.sealwallet.utils.StatusBarUtil;
import com.raistone.wallet.sealwallet.utils.ToastHelper;
import com.raistone.wallet.sealwallet.widget.TitleBar;
import com.unistrong.yang.zb_permission.Permission;
import com.unistrong.yang.zb_permission.ZbPermission;
import com.zhouyou.http.EasyHttp;
import com.zhouyou.http.callback.SimpleCallBack;
import com.zhouyou.http.exception.ApiException;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.disposables.Disposable;
import neoutils.Neoutils;
import retrofit2.converter.gson.GsonConverterFactory;


@Route(value = "TransferActivity")
public class TransferActivity extends BaseActivity {
    @BindView(R.id.titleBar)
    TitleBar titleBar;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_balance)
    TextView tvBalance;
    @BindView(R.id.input_money_ed)
    EditText inputMoneyEd;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.receiptInput)
    EditText receiptInput;
    @BindView(R.id.tv_receipt)
    TextView tvReceipt;
    @BindView(R.id.tv_receipt_address)
    TextView tvReceiptAddress;
    @BindView(R.id.tv_remark)
    TextView tvRemark;
    @BindView(R.id.remark_ed)
    EditText remarkEd;
    @BindView(R.id.tv_transfer)
    TextView tvTransfer;
    @BindView(R.id.scaner_code_iv)
    ImageView scanerCodeIv;

    private String address, coinName, balance, tokenSynbol, tokenType, tokenAddress, tokenDecimal, formAddress,coinType,accountId;

    private Context context;

    private final int REQUEST_CAMERA = 200;

    private AssetsDeatilInfo dataBean;

    private Disposable polldisposable;


    public  int convertsToInt=5;
    /**
     * 转账
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer);
        ButterKnife.bind(this);
        //StatusBarUtil.setTransparent(this);
        setTitle(titleBar, getResources().getString(R.string.transfer_string), true);

        context = this;

        ActivityManager.getInstance().pushActivity(this);
        address = getIntent().getStringExtra("address");
        coinName = getIntent().getStringExtra("coinName");
        balance = getIntent().getStringExtra("balance");
        tokenSynbol = getIntent().getStringExtra("tokenSynbol");
        tokenType = getIntent().getStringExtra("tokenType");
        tokenAddress = getIntent().getStringExtra("tokenAddress");
        tokenDecimal = getIntent().getStringExtra("tokenDecimal");
        formAddress = getIntent().getStringExtra("formAddress");
        coinType = getIntent().getStringExtra("coinType");
        accountId = getIntent().getStringExtra("accountId");

        dataBean = (AssetsDeatilInfo) getIntent().getSerializableExtra("dataBean");

        tvName.setText(dataBean.getTokenSynbol());
        tvReceiptAddress.setText(address);

        if (!TextUtils.isEmpty(formAddress)) {
            receiptInput.setText(formAddress);
        }


        if(tokenSynbol.equals("NEO") || tokenSynbol.equals("ONT")){
            inputMoneyEd.setInputType(InputType.TYPE_CLASS_NUMBER);
        }




        if (!TextUtils.isEmpty(dataBean.getBalance())) {

            String decimal = dataBean.getTokenDecimal();

            if (!TextUtils.isEmpty(decimal) && !dataBean.getBalance().equals("0")) {

                tvBalance.setText(AssetsAdapter.subZeroAndDot(dataBean.getBalance()+"")+ coinName);
            }else {
                tvBalance.setText(AssetsAdapter.subZeroAndDot(dataBean.getBalance()+"") + coinName);
            }
        } else {
            tvBalance.setText(AssetsAdapter.subZeroAndDot(dataBean.getBalance()+"") + coinName);
        }

        getGasPrice();

    }

    @OnClick({R.id.scaner_code_iv, R.id.tv_transfer})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.scaner_code_iv:
                ZbPermission.needPermission(this, REQUEST_CAMERA, Permission.CAMERA, new ZbPermission.ZbPermissionCallback() {
                    @Override
                    public void permissionSuccess(int i) {
                        Router.build("ScannerActivity").with("isHome",false).with("coinType",coinType).with("tokenSynbol",tokenSynbol).with("accountId",accountId).requestCode(0).go(context);
                    }

                    @Override
                    public void permissionFail(int i) {
                        ToastHelper.showToast(getResources().getString(R.string.photo_permission));
                    }
                });
                break;
            /**
             * 转账
             */
            case R.id.tv_transfer:
                //fromAddress 收款地址 toAddress 付款地址
                String s = inputMoneyEd.getText().toString();

                if(s.startsWith(".")){
                    ToastHelper.showToast(getResources().getString(R.string.please_enter_the_correct_amount));
                    return;
                }

                if (TextUtils.isEmpty(s)) {
                    ToastHelper.showToast(getString(R.string.please_enter_the_amount));
                    return;
                }

                double aDouble = Double.parseDouble(s);
                if (aDouble<=0) {
                    ToastHelper.showToast(getString(R.string.tansfer_amount_cannot_string));
                    return;
                }

                double value1 = Double.parseDouble(dataBean.getBalance());

                if(aDouble>value1){
                    ToastHelper.showToast(getString(R.string.insufficient_balance_string));
                    return;
                }

                String receiptAddress = receiptInput.getText().toString();

                if(TextUtils.isEmpty(receiptAddress)){
                    ToastHelper.showToast(getString(R.string.collection_address_cannot_be_empty));
                    return;
                }

                //判断地址是否合法
                if(!TextUtils.isEmpty(coinType)){
                    if(coinType.equals(ChainAddressCreateManager.ETH_COIN_TYPE)){
                        boolean validAddress = ScannerActivity.isETHValidAddress(receiptAddress);
                        if(!validAddress){
                            ToastHelper.showToast(getResources().getString(R.string.invalid_address_string));
                            return;
                        }
                    }

                    if(coinType.equals(ChainAddressCreateManager.ONT_COIN_TYPE)  || coinType.equals(ChainAddressCreateManager.NEO_COIN_TYPE)){
                        boolean validAddress = Neoutils.validateNEOAddress(receiptAddress);
                        if(!validAddress){
                            ToastHelper.showToast(getResources().getString(R.string.invalid_address_string));
                            return;
                        }
                    }
                }

                Router.build("TransferConfirmActivity")
                        .with("fromAddress", address)
                        .with("money", inputMoneyEd.getText().toString())
                        .with("tokenSynbol", tokenSynbol)
                        .with("tokenType", tokenType)
                        .with("remark", remarkEd.getText().toString())
                        .with("tokenAddress", tokenAddress)
                        .with("tokenDecimal", tokenDecimal)
                        .with("convertsToInt",convertsToInt)
                        .with("coinType",coinType)
                        .with("toAddress", receiptInput.getText().toString())
                        .with("accountId",accountId)
                        .with("coinName", coinName).go(this);

                ActivityManager.getInstance().pushActivity(this);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == RESULT_OK) {
            if (data != null) {
                String code = data.getStringExtra("message");
                //chamberWalletAddressEd.setText(code);
                receiptInput.setText(code);
            }
        }
    }

    /**
     * 获取gasPrice
     */
    public void getGasPrice() {

        EasyHttp.getInstance().get("api")
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(Constant.HttpServiceUrl.MAIN_URL)
                .params("module", "proxy")
                .params("action", "eth_gasPrice")
                .execute(new SimpleCallBack<String>() {
                    @Override
                    public void onError(ApiException e) {
                    }

                    @Override
                    public void onSuccess(String content) {

                        GasInfo gasInfo = GsonUtils.decodeJSON(content, GasInfo.class);


                        double pow = Math.pow(10, -9);

                        String substring = gasInfo.getResult().substring(2);

                        //转换后的gasPrice
                        long parseLong = Long.parseLong(substring, 16);


                        double result = parseLong * pow;

                        convertsToInt = BigDecimalUtils.convertsToInt(result);

                    }
                });
    }
}
