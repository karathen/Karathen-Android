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


public class TradingUtils {

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
        System.out.println("nonce " + nonce);
        BigInteger value = BigInteger.ZERO;
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

        BigInteger value = Convert.toWei(BigDecimal.valueOf(amount.doubleValue()), Convert.Unit.ETHER).toBigInteger();
        byte chainId = ChainId.NONE;
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


    private static String hexString = "0123456789ABCDEF";


    public static String encode(String str) {
        byte[] bytes = str.getBytes();
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (int i = 0; i < bytes.length; i++) {
            sb.append(hexString.charAt((bytes[i] & 0xf0) >> 4));
            sb.append(hexString.charAt((bytes[i] & 0x0f) >> 0));
        }
        return sb.toString();
    }

}
