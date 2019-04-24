package com.raistone.wallet.sealwallet.utils;

import android.support.annotation.NonNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.ontio.common.Helper;
import com.raistone.wallet.sealwallet.datavases.LocalDataUtils;
import com.raistone.wallet.sealwallet.datavases.MultiChainInfoDaoUtils;
import com.raistone.wallet.sealwallet.datavases.WalletDaoUtils;
import com.raistone.wallet.sealwallet.entity.ETHWallet;
import com.raistone.wallet.sealwallet.entity.HomeShowInfo;
import com.raistone.wallet.sealwallet.entity.MultiChainInfo;
import com.raistone.wallet.sealwallet.exception.MnemonicException;

import org.bitcoinj.crypto.ChildNumber;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.crypto.HDKeyDerivation;
import org.bitcoinj.crypto.MnemonicCode;
import org.bitcoinj.wallet.DeterministicSeed;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;
import org.web3j.crypto.Wallet;
import org.web3j.crypto.WalletFile;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.ObjectMapperFactory;
import org.web3j.utils.Numeric;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.List;

import neoutils.Neoutils;

public class MultiChainCreateManager {

    public static final String ETH_COIN_TYPE="ETH";
    public static final String NEO_COIN_TYPE="NEO";
    public static final String ONT_COIN_TYPE="ONT";

    private static ObjectMapper objectMapper = ObjectMapperFactory.getObjectMapper();
    /**
     * 随机
     */
    private static final SecureRandom secureRandom = SecureRandomUtils.secureRandom();
    private Credentials credentials;
    /**
     * 通用的以太坊基于bip44协议的助记词路径 （imtoken jaxx Metamask myetherwallet）
     */
    public static String ETH_JAXX_TYPE = "m/44'/60'/0'/0/0";


    /**
     * =============================================================================创建ETH链===================================================================================================================
     */


    /**
     * 创建助记词，并通过助记词创建钱包
     *
     * @param walletName
     * @param pwd
     * @return
     */
    public static MultiChainInfo generateMnemonic(String walletName, String pwd) {
        String[] pathArray = ETH_JAXX_TYPE.split("/");
        String passphrase = "";
        long creationTimeSeconds = System.currentTimeMillis() / 1000;

        DeterministicSeed ds = new DeterministicSeed(secureRandom, 128, passphrase, creationTimeSeconds);
        return generateWalletByMnemonic(walletName, ds, pathArray, pwd);
    }

    /**
     * 创建助记词，并通过助记词创建钱包
     * @param jaxx_type 协议路径
     * @param walletName
     * @param pwd
     * @return
     */
    public static MultiChainInfo generateMnemonic(String jaxx_type,String walletName, String pwd,List<String> list) {
        //String[] pathArray = ETH_JAXX_TYPE.split("/");

        String[] pathArray= jaxx_type.split("/");
        String passphrase = "";
        long creationTimeSeconds = System.currentTimeMillis() / 1000;

        //DeterministicSeed ds = new DeterministicSeed(secureRandom, 128, passphrase, creationTimeSeconds);
        DeterministicSeed ds = new DeterministicSeed(list, null, passphrase, creationTimeSeconds);
        return generateWalletByMnemonic(walletName, ds, pathArray, pwd);
    }

    /**
     * 通过导入助记词，导入钱包
     *
     * @param
     * @param list 助记词
     * @param pwd  密码
     * @return
     */
    public static MultiChainInfo importMnemonic(List<String> list, String pwd) {
        if (!ETH_JAXX_TYPE.startsWith("m") && !ETH_JAXX_TYPE.startsWith("M")) {
            //参数非法
            return null;
        }
        String[] pathArray = ETH_JAXX_TYPE.split("/");
        if (pathArray.length <= 1) {
            //内容不对
            return null;
        }

        //效验助记词
        try {
            byte[] mnemonicSeedBytes = MnemonicCode.INSTANCE.toEntropy(list);
        } catch (Exception e) {
            throw new MnemonicException("mnemonicMsg");
        }

        String passphrase = "";
        long creationTimeSeconds = System.currentTimeMillis() / 1000;
        DeterministicSeed ds = new DeterministicSeed(list, null, passphrase, creationTimeSeconds);
        return generateWalletByMnemonic(generateNewWalletName(), ds, pathArray, pwd);
    }


    /**
     * 导入助记词
     * @param walletName 钱包名称
     * @param list 助记词
     * @param pwd 密码
     * @return
     */
    public static MultiChainInfo importMnemonic(String walletName,List<String> list, String pwd) {
        if (!ETH_JAXX_TYPE.startsWith("m") && !ETH_JAXX_TYPE.startsWith("M")) {
            //参数非法
            return null;
        }
        String[] pathArray = ETH_JAXX_TYPE.split("/");
        if (pathArray.length <= 1) {
            //内容不对
            return null;
        }

        //效验助记词
        try {
            byte[] mnemonicSeedBytes = MnemonicCode.INSTANCE.toEntropy(list);
        } catch (Exception e) {
            throw new MnemonicException("mnemonicMsg");
        }

        String passphrase = "";
        long creationTimeSeconds = System.currentTimeMillis() / 1000;
        DeterministicSeed ds = new DeterministicSeed(list, null, passphrase, creationTimeSeconds);
        return generateWalletByMnemonic(walletName, ds, pathArray, pwd);
    }


    /**
     * 导入助记词
     * @param walletName 钱包名称
     * @param list 助记词
     * @param pwd 密码
     * @return
     */
    public static MultiChainInfo importMnemonic(String javaType,String walletName,List<String> list, String pwd) {
        if (!javaType.startsWith("m") && !javaType.startsWith("M")) {
            //参数非法
            return null;
        }
        String[] pathArray = javaType.split("/");
        if (pathArray.length <= 1) {
            //内容不对
            return null;
        }

        //效验助记词
        try {
            byte[] mnemonicSeedBytes = MnemonicCode.INSTANCE.toEntropy(list);
        } catch (Exception e) {
            throw new MnemonicException("mnemonicMsg");
        }

        String passphrase = "";
        long creationTimeSeconds = System.currentTimeMillis() / 1000;
        DeterministicSeed ds = new DeterministicSeed(list, null, passphrase, creationTimeSeconds);
        return generateWalletByMnemonic(walletName, ds, pathArray, pwd);
    }



    private static String generateNewWalletName() {
        char letter1 = (char) (int) (Math.random() * 26 + 97);
        char letter2 = (char) (int) (Math.random() * 26 + 97);
        String walletName = String.valueOf(letter1) + String.valueOf(letter2) + "-新钱包";
      /*  while (NEOWalletDaoUtils.walletNameChecking(walletName)) {
            letter1 = (char) (int) (Math.random() * 26 + 97);
            letter2 = (char) (int) (Math.random() * 26 + 97);
            walletName = String.valueOf(letter1) + String.valueOf(letter2) + "-新钱包";
        }*/
        return walletName;
    }

    /**
     * @param walletName 钱包名称
     * @param ds         助记词加密种子
     * @param pathArray  助记词标准
     * @param pwd        密码
     * @return
     */

    public static MultiChainInfo generateWalletByMnemonic(String walletName, DeterministicSeed ds,
                                                     String[] pathArray, String pwd) {
        //种子
        byte[] seedBytes = ds.getSeedBytes();
//        System.out.println(Arrays.toString(seedBytes));
        //助记词
        List<String> mnemonic = ds.getMnemonicCode();
//        System.out.println(Arrays.toString(mnemonic.toArray()));
        if (seedBytes == null)
            return null;
        DeterministicKey dkKey = HDKeyDerivation.createMasterPrivateKey(seedBytes);
        for (int i = 1; i < pathArray.length; i++) {
            ChildNumber childNumber;
            if (pathArray[i].endsWith("'")) {
                int number = Integer.parseInt(pathArray[i].substring(0,
                        pathArray[i].length() - 1));
                childNumber = new ChildNumber(number, true);
            } else {
                int number = Integer.parseInt(pathArray[i]);
                childNumber = new ChildNumber(number, false);
            }
            dkKey = HDKeyDerivation.deriveChildKey(dkKey, childNumber);
        }
        ECKeyPair keyPair = ECKeyPair.create(dkKey.getPrivKeyBytes());
        MultiChainInfo ethWallet = generateWallet(walletName, pwd, keyPair);
        if (ethWallet != null) {
            ethWallet.setMnemonic(convertMnemonicList(mnemonic));
        }
        return ethWallet;
    }

    private static String convertMnemonicList(List<String> mnemonics) {
        StringBuilder sb = new StringBuilder();
        for (String mnemonic : mnemonics
                ) {
            sb.append(mnemonic);
            sb.append(" ");
        }
        return sb.toString();
    }

    private static MultiChainInfo generateWallet(String walletName, String pwd, ECKeyPair ecKeyPair) {
        WalletFile walletFile;
        try {
            walletFile = Wallet.create(pwd, ecKeyPair, 1024, 1); // WalletUtils. .generateNewWalletFile();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        BigInteger publicKey = ecKeyPair.getPublicKey();
        String s = publicKey.toString();
        LogUtils.i("ETHWalletUtils", "publicKey = " + s);
        String wallet_dir = AppFilePath.Wallet_DIR;
        LogUtils.i("ETHWalletUtils", "wallet_dir = " + wallet_dir);
        String keystorePath = "keystore_" + walletName + ".json";
        File destination = new File(wallet_dir, "keystore_" + walletName + ".json");

        //目录不存在则创建目录，创建不了则报错
        if (!createParentDir(destination)) {
            return null;
        }
        try {
            objectMapper.writeValue(destination, walletFile);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        MultiChainInfo ethWallet = new MultiChainInfo();
        ethWallet.setName(walletName);
        ethWallet.setAddress(Keys.toChecksumAddress(walletFile.getAddress()));
        ethWallet.setKeystorePath(destination.getAbsolutePath());
        ethWallet.setPrivateScrect(ecKeyPair.getPrivateKey().toString(16));
        ethWallet.setPublicScrect(ecKeyPair.getPublicKey().toString(16));
        ethWallet.setPassword(Md5Utils.md5(pwd));
        return ethWallet;
    }

    /**
     * 通过keystore.json文件导入钱包
     *
     * @param keystore 原json文件
     * @param pwd      json文件密码
     * @return
     */
    public static MultiChainInfo loadWalletByKeystore(String keystore, String pwd) {
        Credentials credentials = null;
        try {
            WalletFile walletFile = null;
            walletFile = objectMapper.readValue(keystore, WalletFile.class);

//            WalletFile walletFile = new Gson().fromJson(keystore, WalletFile.class);
            credentials = Credentials.create(Wallet.decrypt(pwd, walletFile));
        } catch (IOException e) {
            e.printStackTrace();
            LogUtils.e("ETHWalletUtils", e.toString());
        } catch (CipherException e) {
            LogUtils.e("ETHWalletUtils", e.toString());
//            ToastUtils.showToast(R.string.load_wallet_by_official_wallet_keystore_input_tip);
            e.printStackTrace();
        }
        if (credentials != null) {
            return generateWallet(generateNewWalletName(), pwd, credentials.getEcKeyPair());
        }
        return null;
    }

    /**
     * 通过明文私钥导入钱包
     *
     * @param privateKey
     * @param pwd
     * @return
     */
    public static MultiChainInfo loadWalletByPrivateKey(String privateKey, String pwd) {
        Credentials credentials = null;
        ECKeyPair ecKeyPair = ECKeyPair.create(Numeric.toBigInt(privateKey));
        return generateWallet(generateNewWalletName(), pwd, ecKeyPair);
    }

    /**
     * 通过明文私钥导入钱包
     *
     * @param privateKey
     * @param pwd
     * @return
     */
    public static MultiChainInfo loadWalletByPrivateKey(String wallerName,String privateKey, String pwd) {
        ECKeyPair ecKeyPair = ECKeyPair.create(Numeric.toBigInt(privateKey));
        return generateWallet(wallerName, pwd, ecKeyPair);
    }




    /**
     * 导出明文私钥
     *
     * @param walletId 钱包Id
     * @param pwd      钱包密码
     * @return
     */
    public static String derivePrivateKey(long walletId, String pwd) {
        //ETHWallet ethWallet = WalletDaoUtils.ethWalletDao.load(walletId);

        MultiChainInfo ethWallet = MultiChainInfoDaoUtils.loadSingle(walletId);
        Credentials credentials;
        ECKeyPair keypair;
        String privateKey = null;
        try {
            credentials = WalletUtils.loadCredentials(pwd, ethWallet.getKeystorePath());
            keypair = credentials.getEcKeyPair();
            privateKey = Numeric.toHexStringNoPrefixZeroPadded(keypair.getPrivateKey(), Keys.PRIVATE_KEY_LENGTH_IN_HEX);
        } catch (CipherException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return privateKey;
    }

    /**
     * 导出keystore文件
     *
     * @param walletId
     * @param pwd
     * @return
     */
    public static String deriveKeystore(long walletId, String pwd) {
        //ETHWallet ethWallet = WalletDaoUtils.ethWalletDao.load(walletId);
        MultiChainInfo ethWallet =MultiChainInfoDaoUtils.loadSingle(walletId);
        String keystore = null;
        WalletFile walletFile;
        try {
            walletFile = objectMapper.readValue(new File(ethWallet.getKeystorePath()), WalletFile.class);
            keystore = objectMapper.writeValueAsString(walletFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return keystore;
    }



    private static boolean createParentDir(File file) {
        //判断目标文件所在的目录是否存在
        if (!file.getParentFile().exists()) {
            //如果目标文件所在的目录不存在，则创建父目录
            System.out.println("目标文件所在目录不存在，准备创建");
            if (!file.getParentFile().mkdirs()) {
                System.out.println("创建目标文件所在目录失败！");
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args){
        String datas="m/44'/60'/0'/0/0";

        String[] split = datas.split("/");

        System.out.println();

        for (String value:split ) {
            System.out.print("\t"+value);
        }
    }

    /**
     * =============================================================================创建NEO链===================================================================================================================
     */


    /**
     * 通用的NEO基于bip44协议的助记词路径 （imtoken jaxx Metamask myNeowallet）
     */
    public static String NEO_JAXX_TYPE = "m/44'/888'/0'/0/0";


    /**
     * 通用的ONT基于bip44协议的助记词路径 （imtoken jaxx Metamask myOntwallet）
     */
    public static String ONT_JAXX_TYPE = "m/44'/1024'/0'/0/0";


    /**
     * 通过助记词生成钱包
     *
     * @param word
     * @return
     */
    public static MultiChainInfo generateMnemonicByNeoOrOnt(String name,String word,String coinType,String jaxxType) {

        MultiChainInfo neoWallet=null;
        try {

            byte[] prikey = com.github.ontio.crypto.MnemonicCode.getPrikeyFromMnemonicCodesStrBip44(jaxxType,word);

            //私钥
            String hexString = Helper.toHexString(prikey);


            neoutils.Wallet wallet = Neoutils.generateFromPrivateKey(hexString);

            neoWallet=new MultiChainInfo();

            neoWallet.setName(name);
            neoWallet.setAddress(wallet.getAddress());
            neoWallet.setWif(wallet.getWIF());
            neoWallet.setCoinType(coinType);
            neoWallet.setImport(false);
            neoWallet.setCurrent(true);
            neoWallet.setPublicScrect(Helper.toHexString(wallet.getPublicKey()));
            neoWallet.setMnemonic(word);
            neoWallet.setPrivateScrect(hexString);
            neoWallet.setType_flag(jaxxType);

        } catch (Exception e) {
            e.printStackTrace();
        }


        return neoWallet;
    }


    /**
     * 通过私钥导入链
     * @param privateKey 私钥
     * @param name 名称
     * @param coinType 链类型
     * @return
     */
    public static MultiChainInfo generateByPrivateKeyNeoOrOnt(String privateKey,String name,String coinType)  {

        MultiChainInfo neoWallet=null;
        try {

            neoutils.Wallet wallet = Neoutils.generateFromPrivateKey(privateKey);

            neoWallet=new MultiChainInfo();

            //
            neoWallet.setName(name);
            neoWallet.setAddress(wallet.getAddress());
            neoWallet.setWif(wallet.getWIF());
            neoWallet.setPublicScrect(Helper.toHexString(wallet.getPublicKey()));
            neoWallet.setCoinType(coinType);
            neoWallet.setImport(true);
            neoWallet.setImportType(1);  // 导入类型 0 助记词导入 1 私钥导入 2 wif 导入
            neoWallet.setCurrent(false);
            neoWallet.setPrivateScrect(privateKey);


        } catch (Exception e) {
            e.printStackTrace();
        }


        return neoWallet;
    }


    /**
     * 通过导入助记词生成 NEO 或者 ONT 链
     * @param walletName
     * @param jaxxType
     * @param word
     * @return
     */
    public static MultiChainInfo importMnemonicByNeoOrOnt(String walletName,String jaxxType,String word,String coinType) {

        MultiChainInfo neoWallet=null;
        try {

            byte[] prikey = com.github.ontio.crypto.MnemonicCode.getPrikeyFromMnemonicCodesStrBip44(jaxxType,word);

            //私钥
            String hexString = Helper.toHexString(prikey);


            //通过私钥生成钱包
            neoutils.Wallet wallet = Neoutils.generateFromPrivateKey(hexString);


            neoWallet=new MultiChainInfo();

            neoWallet.setName(walletName);
            neoWallet.setAddress(wallet.getAddress());
            neoWallet.setWif(wallet.getWIF());
            neoWallet.setPublicScrect(Helper.toHexString(wallet.getPublicKey()));
            neoWallet.setCoinType(coinType);
            neoWallet.setImport(true);
            neoWallet.setCurrent(false);
            neoWallet.setMnemonic(word);
            neoWallet.setImportType(0);// 导入类型 0 助记词导入 1 私钥导入 2 wif 导入
            neoWallet.setPrivateScrect(hexString);
            neoWallet.setType_flag(jaxxType);

        } catch (Exception e) {
            e.printStackTrace();
        }


        return neoWallet;
    }


    /**
     * 导入WIF生成钱包
     *
     * @param wif
     * @return
     */
    public static MultiChainInfo importWifByWalletByNeoOrOnt(String walletName,String wif,String coinType) {

        MultiChainInfo neoWallet=null;
        try {

            //通过私钥生成钱包
            neoutils.Wallet wallet = Neoutils.generateFromWIF(wif);


            neoWallet=new MultiChainInfo();

            neoWallet.setName(walletName);
            neoWallet.setAddress(wallet.getAddress());
            neoWallet.setWif(wallet.getWIF());
            neoWallet.setCoinType(coinType);
            neoWallet.setPublicScrect(Helper.toHexString(wallet.getPublicKey()));
            neoWallet.setImport(true);
            neoWallet.setCurrent(false);
            neoWallet.setWif(wif);
            neoWallet.setImportType(2);// 导入类型 0 助记词导入 1 私钥导入 2 wif 导入
            neoWallet.setPrivateScrect(Helper.toHexString(wallet.getPrivateKey()));

        } catch (Exception e) {
            e.printStackTrace();
        }


        return neoWallet;
    }



}
