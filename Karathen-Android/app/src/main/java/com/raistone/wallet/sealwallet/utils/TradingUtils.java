package com.raistone.wallet.sealwallet.utils;


import com.raistone.wallet.sealwallet.entity.TransactionResultBean;
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
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthEstimateGas;
import org.web3j.protocol.core.methods.response.EthGasPrice;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.tx.ChainId;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;


/**
 * ColdWallet 冷钱包 (交易工具)
 */
public class TradingUtils {

    private static final String TAG = "TradingUtils";
//    private static Web3j web3j = new JsonRpc2_0Web3j(new HttpService(NetUrl.BASE_URL_TRANSFER));


    public static void main(String[] args) {


//        String txHash = TokenTransaction(
//                web3j,
//                "6ba2ed8473d0ceec9ad80b9b7835708054ccd3568aa6f7576e942a0644fbc0b8",
//                "0x01C91B804AF8A00D074F3E901D21c73c79e8c1c8",
//                "0x182B6dBc3bC49cd63b1bc47631860D7067D61d93",
//                "0xdecfb5be0aA112080C0868fcad079c67Cfb46915",
//                new BigDecimal("1000"),
//                18,
//                null,
//                null
//        );
//
//
//        String txHash = ETHTransaction(web3j,
//                "6ba2ed8473d0ceec9ad80b9b7835708054ccd3568aa6f7576e942a0644fbc0b8",
//                "0x01C91B804AF8A00D074F3E901D21c73c79e8c1c8",
//                "0xdecfb5be0aA112080C0868fcad079c67Cfb46915",
//                new BigDecimal("0.01"),
//                "",
//                null,
//                null);


//        System.out.println(txHash);


    }

    /**
     * token代币转账
     *
     * @param web3j           web3链接
     * @param privateKey      私钥
     * @param fromAddress     from地址
     * @param contractAddress 合约地址
     * @param toAddress       to地址
     * @param amount          金额
     * @param decimals        代币单位
     * @param gas             gas(没有不填)
     * @param gaslimit        gaslimit(没有不填)
     */
    public static TransactionResultBean TokenTransaction(Web3j web3j,
                                                         String privateKey,
                                                         String fromAddress,
                                                         String contractAddress,
                                                         String toAddress,
                                                         BigDecimal amount,
                                                         int decimals,
                                                         BigInteger gas,
                                                         BigInteger gaslimit,
                                                         int wallet_id) {
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
       /* BigInteger maxNonce = DBInTransferUtils.getMaxNonce(wallet_id + "");
        if (maxNonce != null && nonce != null) {
            if (nonce.compareTo(maxNonce) <= 0) {
                nonce = maxNonce.add(new BigInteger("1"));
            }
        }*/
//        DBInTransferUtils.getTransferHash()
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
                System.out.println(ethEstimateGas.getAmountUsed());
                gasPrice = ethGasPrice.getGasPrice().multiply(new BigInteger("2"));
                amountUsed = ethEstimateGas.getAmountUsed().multiply(new BigInteger("2"));
            }
            signedData = signTransaction(nonce, gasPrice, amountUsed, contractAddress, value, data, chainId, privateKey);
            if (signedData != null) {
                EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(signedData).send();

//                Logger.e(TAG, new Gson().toJson(ethSendTransaction));
                transactionResultBean.setGasPrice(gasPrice.toString());
                transactionResultBean.setTxHash(ethSendTransaction.getTransactionHash());
                if (nonce != null) {
                    transactionResultBean.setNonce(nonce);
                } else {
                    transactionResultBean.setNonce(new BigInteger("0"));
                }
                return transactionResultBean;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 以太币转账
     *
     * @param web3j       web3连接
     * @param privateKey  私钥
     * @param fromAddress from
     * @param toAddress   to
     * @param amount      金额
     * @param data        备注
     * @param gas         gas(没有为null)
     * @param gaslimit    gaslimit(没有为null)
     */
    public static TransactionResultBean ETHTransaction(Web3j web3j,
                                                       String privateKey,
                                                       String fromAddress,
                                                       String toAddress,
                                                       BigDecimal amount,
                                                       String data,
                                                       BigInteger gas,
                                                       BigInteger gaslimit,
                                                       int wallet_id) {
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
      /*  BigInteger maxNonce = DBInTransferUtils.getMaxNonce(wallet_id + "");
        if (maxNonce != null && nonce != null) {
            if (nonce.compareTo(maxNonce) <= 0) {
                nonce = maxNonce.add(new BigInteger("1"));
            }
        }*/
        BigInteger value = Convert.toWei(BigDecimal.valueOf(amount.doubleValue()), Convert.Unit.ETHER).toBigInteger();
        byte chainId = ChainId.NONE;//测试网络
        String signedData;
        try {
            BigInteger gasPrice = null;
            BigInteger amountUsed = null;
            if (gas != null && gaslimit != null) {
                gasPrice = Convert.toWei(new BigDecimal(gas), Convert.Unit.GWEI).toBigInteger();
                amountUsed = gaslimit;

            } else {
                Transaction transaction = makeTransaction(fromAddress, toAddress,
                        null, null, null, value);
                EthGasPrice ethGasPrice = web3j.ethGasPrice().send();
                EthEstimateGas ethEstimateGas = web3j.ethEstimateGas(transaction).send();
                gasPrice = ethGasPrice.getGasPrice().multiply(new BigInteger("2"));
                amountUsed = ethEstimateGas.getAmountUsed().multiply(new BigInteger("2"));
            }
            data = "0x" + encode(data);
            signedData = signTransaction(nonce, gasPrice, amountUsed, toAddress, value, data, chainId, privateKey);
            if (signedData != null) {
                EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(signedData).send();
                System.out.println(ethSendTransaction.getTransactionHash());
                transactionResultBean.setGasPrice(gasPrice.toString());
                transactionResultBean.setTxHash(ethSendTransaction.getTransactionHash());
                if (nonce != null) {
                    transactionResultBean.setNonce(nonce);
                } else {
                    transactionResultBean.setNonce(new BigInteger("0"));
                }
                return transactionResultBean;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 生成一个普通交易对象
     *
     * @param fromAddress 放款方
     * @param toAddress   收款方
     * @param nonce       交易序号
     * @param gasPrice    gas 价格
     * @param gasLimit    gas 数量
     * @param value       金额
     * @return 交易对象
     */
    private static Transaction makeTransaction(String fromAddress, String toAddress,
                                               BigInteger nonce, BigInteger gasPrice,
                                               BigInteger gasLimit, BigInteger value) {
        Transaction transaction;
        transaction = Transaction.createEtherTransaction(fromAddress, nonce, gasPrice, gasLimit, toAddress, value);
        return transaction;
    }


    /**
     * 生成一个token交易对象
     *
     * @param fromAddress 放款方
     * @param toAddress   收款方
     * @param nonce       交易序号
     * @param gasPrice    gas 价格
     * @param gasLimit    gas 数量
     * @param data        token转账信息
     * @return 交易对象
     */
    private static Transaction makeTokenTransaction(String fromAddress, String toAddress,
                                                    BigInteger nonce, BigInteger gasPrice,
                                                    BigInteger gasLimit, String data) {
        Transaction transaction;
        transaction = Transaction.createFunctionCallTransaction(fromAddress, nonce, gasPrice, gasLimit, toAddress, data);
        return transaction;
    }

    /**
     * 签名交易
     */
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

}
