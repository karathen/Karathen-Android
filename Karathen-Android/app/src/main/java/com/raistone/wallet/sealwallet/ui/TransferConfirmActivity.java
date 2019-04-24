package com.raistone.wallet.sealwallet.ui;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatSeekBar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import com.blankj.utilcode.util.NetworkUtils;
import com.chenenyu.router.Router;
import com.chenenyu.router.annotation.Route;
import com.github.ontio.OntSdk;
import com.github.ontio.account.Account;
import com.github.ontio.common.Helper;
import com.raistone.wallet.sealwallet.R;
import com.raistone.wallet.sealwallet.adapter.AssetsAdapter;
import com.raistone.wallet.sealwallet.daoutils.ChainAddressDaoUtils;
import com.raistone.wallet.sealwallet.daoutils.HdWalletDaoUtils;
import com.raistone.wallet.sealwallet.datavases.TransferDaoUtils;
import com.raistone.wallet.sealwallet.entity.NeoResultInfo;
import com.raistone.wallet.sealwallet.entity.NeoTransferInfo;
import com.raistone.wallet.sealwallet.entity.TransactionResultBean;
import com.raistone.wallet.sealwallet.entity.TransferInfo;
import com.raistone.wallet.sealwallet.factory.ChainAddressInfo;
import com.raistone.wallet.sealwallet.httputils.SealApi;
import com.raistone.wallet.sealwallet.utils.BigDecimalUtils;
import com.raistone.wallet.sealwallet.utils.CommonUtils;
import com.raistone.wallet.sealwallet.utils.Constant;
import com.raistone.wallet.sealwallet.utils.GsonUtils;
import com.raistone.wallet.sealwallet.utils.Md5Utils;
import com.raistone.wallet.sealwallet.utils.MultiChainCreateManager;
import com.raistone.wallet.sealwallet.utils.SPUtil;
import com.raistone.wallet.sealwallet.utils.StatusBarUtil;
import com.raistone.wallet.sealwallet.utils.ToastHelper;
import com.raistone.wallet.sealwallet.widget.TitleBar;
import com.zhouyou.http.EasyHttp;
import com.zhouyou.http.callback.SimpleCallBack;
import com.zhouyou.http.exception.ApiException;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.JsonRpc2_0Web3j;
import org.web3j.protocol.core.Response;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthEstimateGas;
import org.web3j.protocol.core.methods.response.EthGasPrice;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.ChainId;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;
import neoutils.Neoutils;
import okhttp3.RequestBody;
import retrofit2.converter.gson.GsonConverterFactory;


@Route(value = "TransferConfirmActivity")
public class TransferConfirmActivity extends BaseActivity implements SeekBar.OnSeekBarChangeListener {

    @BindView(R.id.titleBar)
    TitleBar titleBar;
    @BindView(R.id.money_tv)
    TextView moneyTv;
    @BindView(R.id.coin_name_tv)
    TextView coinNameTv;
    @BindView(R.id.collection_address)
    TextView collectionAddress;
    @BindView(R.id.paying_address_tv)
    TextView payingAddressTv;
    @BindView(R.id.miner_cost_tv)
    TextView minerCostTv;
    @BindView(R.id.right_icon)
    ImageView rightIcon;
    @BindView(R.id.miner_cost_ll)
    RelativeLayout minerCostLl;
    @BindView(R.id.confirm_btn)
    Button confirmBtn;
    @BindView(R.id.seekBar)
    AppCompatSeekBar seekBar;
    @BindView(R.id.gas_price_tv)
    TextView gasPriceTv;
    @BindView(R.id.remark_tv)
    TextView remarkTv;
    @BindView(R.id.remark_ll)
    LinearLayout remarkLl;
    @BindView(R.id.kgfy_ll)
    LinearLayout kgfyLl;

    private String fromAddress, toAddress, coinName, money, tokenSynbol, tokenType, remark, tokenAddress, tokenDecimal, accountId;

    private ChainAddressInfo wallet;


    public static Web3j web3j = new JsonRpc2_0Web3j(new HttpService("https://mainnet.infura.io/"));

    private BigInteger gaslimt;
    private BigInteger gasPrice;
    private int convertsToInt;

    private String minerCost;

    private Context context;

    private boolean hideLl = false;

    private String pinValue;
    private String coinType;

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    String data = (String) msg.obj;

                    stopProgressDialog();
                    TransferInfo transferInfo = new TransferInfo();
                    transferInfo.setFromAddress(fromAddress);
                    transferInfo.setToAddress(toAddress);
                    transferInfo.setBlockHeighe(new BigInteger("0"));
                    transferInfo.setBlockTime("");
                    transferInfo.setCoinType(coinType);
                    transferInfo.setData("0/12");
                    transferInfo.setGaslimit(gaslimt + "");
                    transferInfo.setGasPrice(gasPrice + "");
                    transferInfo.setRemark(remark);
                    transferInfo.setToAddress(toAddress + "");
                    transferInfo.setTokenSynbol(tokenSynbol);
                    transferInfo.setTxId(data);
                    transferInfo.setStatus("0");
                    transferInfo.setValue(money);
                    transferInfo.setWalletAddress(fromAddress);
                    transferInfo.setMinerCost(minerCost);
                    transferInfo.setTransferTime(CommonUtils.getCurrentTime());
                    transferInfo.setNonce(ethNonce.toString());
                    transferInfo.insert();
                    ActivityManager.getInstance().pushActivity(TransferConfirmActivity.this);

                    if (!TextUtils.isEmpty(remark)) {
                        addTxRemark(data, remark);
                    }
                    Router.build("TransactionDetailsActivity").with("txid", data).go(context);

                    break;
                case 1:
                    stopProgressDialog();
                    String data1 = (String) msg.obj;

                    Toast.makeText(TransferConfirmActivity.this, data1, Toast.LENGTH_LONG).show();


                case 2:
                    stopProgressDialog();
                    ToastHelper.showToast(getResources().getString(R.string.transaction_repeated));

                    ActivityManager.getInstance().finishAllActivity();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer_confirm);
        context = this;

        ActivityManager.getInstance().pushActivity(this);
        ButterKnife.bind(this);
        setTitle(titleBar, getResources().getString(R.string.transfer_confirm_string), true);
        //StatusBarUtil.setTransparent(this);
        fromAddress = getIntent().getStringExtra("fromAddress");
        toAddress = getIntent().getStringExtra("toAddress");
        coinName = getIntent().getStringExtra("coinName");
        tokenSynbol = getIntent().getStringExtra("tokenSynbol");
        tokenType = getIntent().getStringExtra("tokenType");
        remark = getIntent().getStringExtra("remark");
        tokenAddress = getIntent().getStringExtra("tokenAddress");
        tokenDecimal = getIntent().getStringExtra("tokenDecimal");
        coinType = getIntent().getStringExtra("coinType");
        accountId = getIntent().getStringExtra("accountId");

        convertsToInt = getIntent().getIntExtra("convertsToInt", 5);

        money = getIntent().getStringExtra("money");

        collectionAddress.setText(toAddress);

        payingAddressTv.setText(fromAddress);

        moneyTv.setText(money);
        coinNameTv.setText(coinName);

        wallet = ChainAddressDaoUtils.getCurrentByCoinType(coinType, accountId);

        gasPrice = new BigInteger(convertsToInt + "");


        initData();


        if (TextUtils.isEmpty(remark)) {
            remarkLl.setVisibility(View.GONE);
        } else {
            remarkLl.setVisibility(View.VISIBLE);
            remarkTv.setText(remark);
        }

        if (coinType.equals(MultiChainCreateManager.ETH_COIN_TYPE)) {
            minerCostLl.setVisibility(View.VISIBLE);
        } else {
            minerCostLl.setVisibility(View.GONE);
        }

    }


    public void initData() {

        if (tokenType.equals("ERC-721")) {
            gaslimt = new BigInteger("450000");
        } else {
            if (tokenSynbol.equals("ETH")) {
                gaslimt = new BigInteger("21000");
            } else {
                gaslimt = new BigInteger("60000");

            }
        }

        double powTo = Math.pow(10, 9);

        double powThree = Math.pow(10, -18);

        double parseDouble = Double.parseDouble(gaslimt + "");


        double res = convertsToInt * powTo * powThree * parseDouble;

        BigDecimal bb = new BigDecimal(res);

        BigDecimal bbc = BigDecimalUtils.intercept(bb.toString(), 8);

        minerCost = bbc.toString();

        gasPriceTv.setText(gasPrice + " Gwei");

        minerCostTv.setText(AssetsAdapter.subZeroAndDot(minerCost) + " ETH");

        seekBar.setProgress(convertsToInt);
        seekBar.setOnSeekBarChangeListener(this);


    }


    @OnClick({R.id.miner_cost_ll, R.id.confirm_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.miner_cost_ll:

                if (!hideLl) {
                    hideLl = true;
                    kgfyLl.setVisibility(View.VISIBLE);
                    rightIcon.animate().rotation(-90);
                } else {
                    hideLl = false;
                    kgfyLl.setVisibility(View.GONE);
                    rightIcon.animate().rotation(90);
                }
                break;
            case R.id.confirm_btn:

                boolean connected = NetworkUtils.isConnected();
                if (connected) {

                    String gasPricetv = gasPriceTv.getText().toString();

                    if (coinType.equals(MultiChainCreateManager.ETH_COIN_TYPE)) {

                        if (coinType.equals(MultiChainCreateManager.ETH_COIN_TYPE) && coinName.equals(MultiChainCreateManager.ETH_COIN_TYPE)) {

                            showTranDialog(0);

                        } else {
                            showTranDialog(1);
                        }
                    }

                    if (coinType.equals(MultiChainCreateManager.NEO_COIN_TYPE)) {

                        showTranDialog(2);
                    }

                    if (coinType.equals(MultiChainCreateManager.ONT_COIN_TYPE)) {

                        showTranDialog(3);
                    }
                } else {
                    ToastHelper.showToast(getResources().getString(R.string.check_network));
                }

                break;
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

        if (i > 0) {
            double powTo = Math.pow(10, 9);

            double powThree = Math.pow(10, -18);

            double parseDouble = Double.parseDouble(gaslimt + "");


            double res = i * powTo * powThree * parseDouble;

            BigDecimal bb = new BigDecimal(res);

            BigDecimal bbc = BigDecimalUtils.intercept(bb.toString(), 8);

            minerCost = bbc.toString();


            gasPriceTv.setText(i + " Gwei");

            minerCostTv.setText(AssetsAdapter.subZeroAndDot(minerCost) + " ETH");


            gasPrice = BigInteger.valueOf(i);


        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    public static String signTransaction(BigInteger nonce, BigInteger gasPrice, BigInteger gasLimit, String to,
                                         BigInteger value, String data, byte chainId, String privateKey) throws IOException {
        byte[] signedMessage;
        RawTransaction rawTransaction = RawTransaction.createTransaction(
                nonce,
                gasPrice,
                gasLimit,
                to,
                value,
                data);

        if (privateKey.startsWith("0x")) {
            privateKey = privateKey.substring(2);
        }
        ECKeyPair ecKeyPair = ECKeyPair.create(new BigInteger(privateKey, 16));
        Credentials credentials = Credentials.create(ecKeyPair);

        if (chainId > ChainId.NONE) {
            signedMessage = TransactionEncoder.signMessage(rawTransaction, chainId, credentials);
        } else {
            signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
        }

        String hexValue = Numeric.toHexString(signedMessage);
        return hexValue;
    }


    BigInteger ethNonce;

    public void ETHTransaction(Web3j web3j, String privateKey, String fmAddress, String tAddress, BigDecimal amount, String data, BigInteger gas, BigInteger gaslimit) {


        EthGetTransactionCount ethGetTransactionCount = null;
        try {
            ethGetTransactionCount = web3j.ethGetTransactionCount(fmAddress, DefaultBlockParameterName.LATEST).send();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (ethGetTransactionCount == null) return;
        ethNonce = ethGetTransactionCount.getTransactionCount();

        BigInteger value = Convert.toWei(BigDecimal.valueOf(amount.doubleValue()), Convert.Unit.ETHER).toBigInteger();
        byte chainId = ChainId.NONE;
        String signedData;
        try {
            //BigInteger gasPrice = null;
            BigInteger amountUsed = null;
            if (gas != null && gaslimit != null) {
                gasPrice = Convert.toWei(new BigDecimal(gas), Convert.Unit.GWEI).toBigInteger();
                amountUsed = gaslimit;

            } else {
                Transaction transaction = makeTransaction(fmAddress, tAddress, null, null, null, value);
                EthGasPrice ethGasPrice = web3j.ethGasPrice().send();
                EthEstimateGas ethEstimateGas = web3j.ethEstimateGas(transaction).send();
                gasPrice = ethGasPrice.getGasPrice().multiply(new BigInteger("2"));
                amountUsed = ethEstimateGas.getAmountUsed().multiply(new BigInteger("2"));
            }
            data = "0x" + encode(data);
            signedData = signTransaction(ethNonce, gasPrice, amountUsed, toAddress, value, data, chainId, privateKey);
            if (signedData != null) {
                EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(signedData).send();
                String hash = ethSendTransaction.getTransactionHash();

                TransferInfo transferInfo = TransferDaoUtils.selectTransfersByHash(hash);

                Message msg = new Message();
                if (transferInfo != null) {
                    msg.what = 2;
                    handler.sendMessage(msg);
                } else {
                    if (!TextUtils.isEmpty(hash)) {
                        msg.what = 0;
                        msg.obj = hash;
                        handler.sendMessage(msg);
                    } else {
                        //String error = ethGetTransactionCount.getError().getMessage();
                        msg.what = 1;
                        Response.Error error = ethSendTransaction.getError();

                        if (error != null) {
                            String message = error.getMessage();

                            msg.obj = message;
                            handler.sendMessage(msg);
                        }
                    }
                }


            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * @param tpye 0 代表备份钱包，1 代表退出钱包
     */
    public void showTranDialog(final int tpye) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
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

                    final BigDecimal decimal = new BigDecimal(money);

                    //String pwd = WalletManager.getInstance().getWalletChains(1l).getWalletPwd();
                    String pwd = HdWalletDaoUtils.findWalletBySelect().getWalletPwd();
                    if (TextUtils.equals(value, pwd)) {
                        dialog.dismiss();
                        startProgressDialog(getString(R.string.transfer_middle_string));

                        if (tpye == 0) {

                            io.reactivex.Observable
                                    .empty()
                                    .observeOn(Schedulers.io())
                                    .doOnComplete(new Action() {
                                        @Override
                                        public void run() throws Exception {
                                            ETHTransaction(web3j, wallet.getPrivateScrect(), fromAddress, toAddress, decimal, "", gasPrice, gaslimt);
                                        }
                                    }).subscribe();

                        }
                        if (tpye == 1) {
                            //startProgressDialog(getString(R.string.transfer_middle_string));

                            io.reactivex.Observable
                                    .empty()
                                    .observeOn(Schedulers.io())
                                    .doOnComplete(new Action() {
                                        @Override
                                        public void run() throws Exception {

                                            TransactionResultBean resultBean = tokenTransaction(web3j, wallet.getPrivateScrect(), fromAddress, tokenAddress, toAddress, decimal, Integer.parseInt(tokenDecimal), gasPrice, gaslimt);

                                            if (resultBean == null || TextUtils.isEmpty(resultBean.getTxHash())) {
                                                Message msg = new Message();
                                                msg.what = 1;
                                                msg.obj = resultBean.getErrorMsg();
                                                handler.sendMessage(msg);
                                            } else {
                                                TransferInfo transferInfo = new TransferInfo();
                                                transferInfo.setFromAddress(fromAddress);
                                                transferInfo.setToAddress(toAddress);
                                                transferInfo.setBlockHeighe(BigInteger.valueOf(0));
                                                transferInfo.setBlockTime("");
                                                transferInfo.setCoinType(coinType);
                                                transferInfo.setData("0/12");
                                                transferInfo.setGaslimit(gaslimt + "");
                                                transferInfo.setGasPrice(gasPrice + "");
                                                transferInfo.setRemark(remark);
                                                transferInfo.setToAddress(toAddress + "");
                                                transferInfo.setTokenSynbol(tokenSynbol);
                                                transferInfo.setTxId(resultBean.getTxHash());
                                                transferInfo.setStatus("0");
                                                transferInfo.setValue(money);
                                                transferInfo.setWalletAddress(fromAddress);
                                                transferInfo.setMinerCost(minerCost);
                                                transferInfo.setTransferTime(CommonUtils.getCurrentTime());
                                                ActivityManager.getInstance().pushActivity(TransferConfirmActivity.this);
                                                transferInfo.insert();
                                                stopProgressDialog();
                                                Router.build("TransactionDetailsActivity").with("txid", resultBean.getTxHash()).go(context);
                                            }
                                        }
                                    }).subscribe();


                        }

                        if (tpye == 2) {

                            io.reactivex.Observable
                                    .empty()
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .doOnComplete(new Action() {
                                        @Override
                                        public void run() throws Exception {

                                            constructTxByNeo(toAddress, fromAddress, money, tokenAddress, wallet.getPrivateScrect(), wallet.getPublicScrect());
                                        }
                                    }).subscribe();

                        }

                        if (tpye == 3) {

                            io.reactivex.Observable
                                    .empty()
                                    .observeOn(Schedulers.io())
                                    .doOnComplete(new Action() {
                                        @Override
                                        public void run() throws Exception {

                                            //ToastHelper.showToast("ONT 转账");

                                            ontTransfer(fromAddress, wallet.getWif(), tokenSynbol, toAddress, Float.parseFloat(money));
                                        }
                                    }).subscribe();

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


    /*
     * 16进制数字字符集
     */
    private static String hexString = "0123456789ABCDEF";

    /*
     * 将字符串编码成16进制数字,适用于所有字符（包括中文）
     */
    public static String encode(String str) {
        // 根据默认编码获取字节数组
        byte[] bytes = str.getBytes();
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        // 将字节数组中每个字节拆解成2位16进制整数
        for (int i = 0; i < bytes.length; i++) {
            sb.append(hexString.charAt((bytes[i] & 0xf0) >> 4));
            sb.append(hexString.charAt((bytes[i] & 0x0f) >> 0));
        }
        return sb.toString();
    }

    private static Transaction makeTransaction(String fromAddress, String toAddress,
                                               BigInteger nonce, BigInteger gasPrice,
                                               BigInteger gasLimit, BigInteger value) {
        Transaction transaction;
        transaction = Transaction.createEtherTransaction(fromAddress, nonce, gasPrice, gasLimit, toAddress, value);
        return transaction;
    }

    private static Transaction makeTokenTransaction(String fromAddress, String toAddress,
                                                    BigInteger nonce, BigInteger gasPrice,
                                                    BigInteger gasLimit, String data) {
        Transaction transaction;
        transaction = Transaction.createFunctionCallTransaction(fromAddress, nonce, gasPrice, gasLimit, toAddress, data);
        return transaction;
    }

    public TransactionResultBean tokenTransaction(Web3j web3j,
                                                  String privateKey,
                                                  String fromAddress,
                                                  String contractAddress,
                                                  String toAddress,
                                                  BigDecimal amount,
                                                  int decimals,
                                                  BigInteger gas,
                                                  BigInteger gaslimit) {
        BigInteger nonce;
        EthGetTransactionCount ethGetTransactionCount = null;
        try {
            ethGetTransactionCount = web3j.ethGetTransactionCount(fromAddress, DefaultBlockParameterName.PENDING).send();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (ethGetTransactionCount == null) return null;
        TransactionResultBean transactionResultBean = new TransactionResultBean();
        nonce = ethGetTransactionCount.getTransactionCount();

        System.out.println("nonce " + nonce);
        BigInteger value = BigInteger.ZERO;
        //token转账参数
        String methodName = "transfer";
        List<Type> inputParameters = new ArrayList<>();
        List<TypeReference<?>> outputParameters = new ArrayList<>();
        Address tAddress = new Address(toAddress);
        Uint256 tokenValue = new Uint256(BigDecimal.valueOf(amount.doubleValue()).multiply(BigDecimal.TEN.pow(decimals)).toBigInteger());
        inputParameters.add(tAddress);
        inputParameters.add(tokenValue);
        TypeReference<Bool> typeReference = new TypeReference<Bool>() {
        };
        outputParameters.add(typeReference);
        Function function = new Function(methodName, inputParameters, outputParameters);
        String data = FunctionEncoder.encode(function);

        byte chainId = ChainId.NONE;
        String signedData;
        try {
            BigInteger gasPrice = null;
            BigInteger amountUsed = null;
            if (gas != null && gaslimit != null) {
                gasPrice = Convert.toWei(new BigDecimal(gas), Convert.Unit.GWEI).toBigInteger();
                amountUsed = gaslimit;
            } else {
                Transaction transaction = makeTokenTransaction(fromAddress, contractAddress,
                        null, null, null, data);
                EthGasPrice ethGasPrice = web3j.ethGasPrice().send();
                EthEstimateGas ethEstimateGas = web3j.ethEstimateGas(transaction).send();
                gas = ethGasPrice.getGasPrice().multiply(new BigInteger("2"));
                amountUsed = ethEstimateGas.getAmountUsed().multiply(new BigInteger("2"));
            }
            signedData = signTransaction(nonce, gasPrice, amountUsed, contractAddress, value, data, chainId, privateKey);
            if (signedData != null) {
                EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(signedData).send();


                String transactionHash = ethSendTransaction.getTransactionHash();

                //查询哈希值本地是否有记录
                TransferInfo transferInfo = TransferDaoUtils.selectTransfersByHash(transactionHash);

                Message msg = new Message();
                //说明本地有这条记录，说明交易重复
                if (transferInfo != null) {
                    msg.what = 2;
                    handler.sendMessage(msg);
                    //return null;
                } else {


//                Logger.e(TAG, new Gson().toJson(ethSendTransaction));
                    transactionResultBean.setGasPrice(gas.toString());
                    transactionResultBean.setTxHash(ethSendTransaction.getTransactionHash());

                    Response.Error error = ethSendTransaction.getError();

                    if (error != null) {
                        String message = error.getMessage();

                        if (!TextUtils.isEmpty(message)) {
                            transactionResultBean.setErrorMsg(message);
                        }
                    }

                    if (nonce != null) {
                        transactionResultBean.setNonce(nonce);
                    } else {
                        transactionResultBean.setNonce(new BigInteger("0"));
                    }
                }
                return transactionResultBean;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 添加交易备注
     */
    public void addTxRemark(String txid, String content) {

        List<String> data = new ArrayList<>();
        data.add(txid);
        data.add(content);
        RequestBody body = SealApi.toRequestBody("addTxRemark", data);
        EasyHttp.getInstance().setBaseUrl(Constant.HttpServiceUrl.BASE_MAIN_URL).post("/")
                .addConverterFactory(GsonConverterFactory.create()).requestBody(body).execute(new SimpleCallBack<String>() {
            @Override
            public void onError(ApiException e) {

            }

            @Override
            public void onSuccess(String s) {

            }
        });
    }

    public void constructTxByNeo(final String tAddress, final String frAddress, final String mon, final String consAddress, final String priKey, final String pubKey) {
        List<Object> parms = new ArrayList<>();//Arrays.asList(tAddress, frAddress, mon, consAddress);
        parms.add(frAddress);
        parms.add(tAddress);
        parms.add(new BigDecimal(mon));
        parms.add(consAddress);

        RequestBody body = SealApi.toRequestBody("constructTx", parms);
        EasyHttp.getInstance().setBaseUrl(Constant.NEOParams.NEO_BASE_URL).post("/")
                .addConverterFactory(GsonConverterFactory.create()).requestBody(body).execute(new SimpleCallBack<String>() {
            @Override
            public void onError(ApiException e) {
                stopProgressDialog();
            }

            @Override
            public void onSuccess(String s) {

                try {
                    NeoTransferInfo neoTransferInfo = GsonUtils.decodeJSON(s, NeoTransferInfo.class);
                    if (neoTransferInfo != null) {
                        NeoTransferInfo.ResultBean result = neoTransferInfo.getResult();
                        if (result != null) {

                            String txData = result.getTxData();

                            byte[] sign = Neoutils.sign(Neoutils.hexTobytes(txData), priKey);

                            String signDate = Helper.toHexString(sign);

                            String witness = result.getWitness();

                            String txid = result.getTxid();

                            String resultStr = reString(witness, signDate, pubKey);

                            String tranStr = txData + resultStr;


                            sendRawTxByNeo(tAddress, frAddress, mon, consAddress, tranStr, txid);
                        }
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    /**
     * 广播交易
     */
    public void sendRawTxByNeo(final String tAddress, final String frAddress, final String mon, String consAddress, String sign, final String txid) {
        List<String> parms = Arrays.asList(sign);

        RequestBody body = SealApi.toRequestBody("sendRawTx", parms);
        EasyHttp.getInstance().setBaseUrl(Constant.NEOParams.NEO_BASE_URL).post("/")
                .addConverterFactory(GsonConverterFactory.create()).requestBody(body).execute(new SimpleCallBack<String>() {
            @Override
            public void onError(ApiException e) {
                stopProgressDialog();
            }

            @Override
            public void onSuccess(String s) {

                NeoResultInfo neoResultInfo = GsonUtils.decodeJSON(s, NeoResultInfo.class);

                if (neoResultInfo != null) {
                    boolean result = neoResultInfo.isResult();

                    if (result) {
                        stopProgressDialog();
                        TransferInfo transferInfo = new TransferInfo();
                        transferInfo.setFromAddress(frAddress);
                        transferInfo.setToAddress(tAddress);
                        transferInfo.setBlockHeighe(new BigInteger("0"));
                        transferInfo.setBlockTime("");
                        transferInfo.setCoinType(coinType);
                        transferInfo.setData("0/1");
                        transferInfo.setRemark(remark);
                        transferInfo.setToAddress(toAddress + "");
                        transferInfo.setTxId(txid);
                        transferInfo.setStatus("0");
                        transferInfo.setTokenSynbol(tokenSynbol);
                        transferInfo.setValue(mon);
                        transferInfo.setWalletAddress(frAddress);
                        transferInfo.setTransferTime(CommonUtils.getCurrentTime());
                        transferInfo.insert();
                        ActivityManager.getInstance().pushActivity(TransferConfirmActivity.this);

                        if (!TextUtils.isEmpty(remark)) {
                            addTxRemark(txid, remark);
                        }


                        Router.build("TransactionDetailsActivity").with("txid", txid).go(context);


                    } else {
                        stopProgressDialog();
                        ToastHelper.showToast(getResources().getString(R.string.transfer_failed));
                    }
                }


            }
        });
    }

    public void ontTransfer(String frOntAddress, String wif, String assets, String toAddressOnt, float amount) {
        try {


            String transfer = Neoutils.ontologyTransfer(Constant.ONTParams.ONT_BASE_URL, 500l, 20000l, wif, assets, toAddressOnt, amount);

            if (transfer.contains("has no balance enough to cover gas cos")) {

                int savedLanguageType = SPUtil.getInstance(this).getSelectLanguage();

                //1是中文 3 是英文
                switch (savedLanguageType) {
                    // 1;    中文 3; //英文
                    case 3:
                        stopProgressDialog();
                        Looper.prepare();
                        ToastHelper.showToast(transfer);
                        Looper.loop();
                        break;
                    case 1:
                        stopProgressDialog();
                        Looper.prepare();
                        ToastHelper.showToast("没有足够的余额来支付燃气成本");
                        Looper.loop();
                        break;
                    default:
                        stopProgressDialog();
                        Looper.prepare();
                        ToastHelper.showToast("没有足够的余额来支付燃气成本");
                        Looper.loop();
                        break;
                }
                stopProgressDialog();

                return;
            }


            if (!TextUtils.isEmpty(transfer)) {
                stopProgressDialog();
                if (!transfer.contains("error")) {
                    stopProgressDialog();
                    TransferInfo transferInfo = new TransferInfo();
                    transferInfo.setFromAddress(frOntAddress);
                    transferInfo.setToAddress(toAddressOnt);
                    transferInfo.setBlockHeighe(new BigInteger("0"));
                    transferInfo.setBlockTime("");
                    transferInfo.setCoinType(coinType);
                    transferInfo.setTokenSynbol(tokenSynbol);
                    transferInfo.setData("0/1");
                    transferInfo.setRemark(remark);
                    transferInfo.setToAddress(toAddress + "");
                    transferInfo.setTxId(transfer);
                    transferInfo.setStatus("0");
                    transferInfo.setValue(amount + "");
                    transferInfo.setWalletAddress(frOntAddress);
                    transferInfo.setTransferTime(CommonUtils.getCurrentTime());
                    transferInfo.insert();
                    ActivityManager.getInstance().pushActivity(TransferConfirmActivity.this);

                    if (!TextUtils.isEmpty(remark)) {
                        addTxRemark(transfer, remark);
                    }

                    Router.build("TransactionDetailsActivity").with("txid", transfer).go(context);
                } else {
                    ToastHelper.showToast(transfer);
                }


            } else {
                stopProgressDialog();
                ToastHelper.showToast(getResources().getString(R.string.transfer_failed));
            }


        } catch (Exception e) {
            stopProgressDialog();
            Looper.prepare();
            ToastHelper.showToast(e.getMessage());
            Looper.loop();
            e.printStackTrace();
        }
    }



    public String reString(String str, String signStr, String pubKeyStr) {

        String[] substring = str.split("\\{");

        String s1 = substring[0];

        String s = substring[1];

        String[] split = s.split("\\}");
        String s2 = split[1];

        String s3 = substring[2];

        String[] split1 = s3.split("\\}");

        String s4 = split1[1];

        String restr = s1 + signStr + s2 + pubKeyStr + s4;

        Log.d("restr", "restr");

        return restr;
    }


}

