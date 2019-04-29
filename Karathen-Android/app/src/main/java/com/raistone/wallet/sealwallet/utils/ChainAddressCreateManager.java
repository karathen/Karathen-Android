package com.raistone.wallet.sealwallet.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.ontio.common.Helper;
import com.raistone.wallet.sealwallet.daoutils.ChainAddressDaoUtils;
import com.raistone.wallet.sealwallet.daoutils.HdWalletDaoUtils;
import com.raistone.wallet.sealwallet.exception.MnemonicException;
import com.raistone.wallet.sealwallet.factory.ChainAddressInfo;
import com.raistone.wallet.sealwallet.factory.HdWallet;

import org.bitcoinj.crypto.ChildNumber;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.crypto.HDKeyDerivation;
import org.bitcoinj.crypto.MnemonicCode;
import org.bitcoinj.wallet.DeterministicSeed;
import org.spongycastle.crypto.digests.SHA256Digest;
import org.spongycastle.crypto.generators.PKCS5S2ParametersGenerator;
import org.spongycastle.crypto.generators.SCrypt;
import org.spongycastle.crypto.params.KeyParameter;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Hash;
import org.web3j.crypto.Keys;
import org.web3j.crypto.Wallet;
import org.web3j.crypto.WalletFile;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.ObjectMapperFactory;
import org.web3j.utils.Numeric;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import neoutils.Neoutils;

public class ChainAddressCreateManager {

    public static final String ETH_COIN_TYPE = "ETH";
    public static final String NEO_COIN_TYPE = "NEO";
    public static final String ONT_COIN_TYPE = "ONT";

    private static ObjectMapper objectMapper = ObjectMapperFactory.getObjectMapper();

    private static final SecureRandom secureRandom = SecureRandomUtils.secureRandom();
    private Credentials credentials;

    public static String ETH_JAXX_TYPE = "m/44'/60'/0'/0/0";


    private static final int N_LIGHT = 1 << 12;
    private static final int P_LIGHT = 6;

    private static final int N_STANDARD = 1 << 18;
    private static final int P_STANDARD = 1;

    private static final int R = 8;
    private static final int DKLEN = 32;

    private static final int CURRENT_VERSION = 3;

    private static final String CIPHER = "aes-128-ctr";
    static final String AES_128_CTR = "pbkdf2";
    static final String SCRYPT = "scrypt";


    public static ChainAddressInfo generateMnemonic(String walletName, String pwd) {
        String[] pathArray = ETH_JAXX_TYPE.split("/");
        String passphrase = "";
        long creationTimeSeconds = System.currentTimeMillis() / 1000;

        DeterministicSeed ds = new DeterministicSeed(secureRandom, 128, passphrase, creationTimeSeconds);
        return generateWalletByMnemonic(walletName, ds, pathArray, pwd);
    }

    public static ChainAddressInfo generateMnemonic(String jaxx_type, String walletName, String pwd, List<String> list) {
        //String[] pathArray = ETH_JAXX_TYPE.split("/");

        String[] pathArray = jaxx_type.split("/");
        String passphrase = "";
        long creationTimeSeconds = System.currentTimeMillis() / 1000;

        DeterministicSeed ds = new DeterministicSeed(list, null, passphrase, creationTimeSeconds);
        return generateWalletByMnemonic(walletName, ds, pathArray, pwd);
    }


    public static ChainAddressInfo importMnemonic(List<String> list, String pwd) {
        if (!ETH_JAXX_TYPE.startsWith("m") && !ETH_JAXX_TYPE.startsWith("M")) {
            return null;
        }
        String[] pathArray = ETH_JAXX_TYPE.split("/");
        if (pathArray.length <= 1) {
            return null;
        }

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

    public static ChainAddressInfo importMnemonic(String walletName, List<String> list, String pwd) {
        if (!ETH_JAXX_TYPE.startsWith("m") && !ETH_JAXX_TYPE.startsWith("M")) {
            return null;
        }
        String[] pathArray = ETH_JAXX_TYPE.split("/");
        if (pathArray.length <= 1) {
            return null;
        }

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


    public static ChainAddressInfo importMnemonic(String javaType, String walletName, List<String> list, String pwd) {
        if (!javaType.startsWith("m") && !javaType.startsWith("M")) {
            return null;
        }
        String[] pathArray = javaType.split("/");
        if (pathArray.length <= 1) {
            return null;
        }

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

        return walletName;
    }


    public static ChainAddressInfo generateWalletByMnemonic(String walletName, DeterministicSeed ds,
                                                            String[] pathArray, String pwd) {
        byte[] seedBytes = ds.getSeedBytes();
        List<String> mnemonic = ds.getMnemonicCode();
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
        ChainAddressInfo ethWallet = generateWallet(walletName, pwd, keyPair);
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

    private static ChainAddressInfo generateWallet(String walletName, String pwd, ECKeyPair ecKeyPair) {
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

        if (!createParentDir(destination)) {
            return null;
        }
        try {
            objectMapper.writeValue(destination, walletFile);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        ChainAddressInfo ethWallet = new ChainAddressInfo();
        ethWallet.setName(walletName);
        ethWallet.setAddress(Keys.toChecksumAddress(walletFile.getAddress()));
        ethWallet.setKeystorePath(destination.getAbsolutePath());
        ethWallet.setPrivateScrect(ecKeyPair.getPrivateKey().toString(16));
        ethWallet.setPublicScrect(ecKeyPair.getPublicKey().toString(16));
        ethWallet.setPassword(Md5Utils.md5(pwd));
        return ethWallet;
    }

    public static ChainAddressInfo loadWalletByKeystore(String walletName, String keystore, String pwd) throws Exception {
        Credentials credentials = null;
        WalletFile walletFile = null;
        walletFile = objectMapper.readValue(keystore, WalletFile.class);


        ECKeyPair decrypt = decrypt(pwd, walletFile);

        if (decrypt != null) {
            return generateWallet(walletName, pwd, decrypt);
        }
        return null;
    }








    private static byte[] generateDerivedScryptKey(
            byte[] password, byte[] salt, int n, int r, int p, int dkLen) throws CipherException {
        return SCrypt.generate(password, salt, n, r, p, dkLen);
    }

    private static byte[] generateAes128CtrDerivedKey(
            byte[] password, byte[] salt, int c, String prf) throws CipherException {

        if (!prf.equals("hmac-sha256")) {
            throw new CipherException("Unsupported prf:" + prf);
        }

        PKCS5S2ParametersGenerator gen = new PKCS5S2ParametersGenerator(new SHA256Digest());
        gen.init(password, salt, c);
        return ((KeyParameter) gen.generateDerivedParameters(256)).getKey();
    }

    private static byte[] performCipherOperation(
            int mode, byte[] iv, byte[] encryptKey, byte[] text) throws CipherException {

        try {
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
            Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding");

            SecretKeySpec secretKeySpec = new SecretKeySpec(encryptKey, "AES");
            cipher.init(mode, secretKeySpec, ivParameterSpec);
            return cipher.doFinal(text);
        } catch (NoSuchPaddingException e) {
            return throwCipherException(e);
        } catch (NoSuchAlgorithmException e) {
            return throwCipherException(e);
        } catch (InvalidAlgorithmParameterException e) {
            return throwCipherException(e);
        } catch (InvalidKeyException e) {
            return throwCipherException(e);
        } catch (BadPaddingException e) {
            return throwCipherException(e);
        } catch (IllegalBlockSizeException e) {
            return throwCipherException(e);
        }
    }

    private static byte[] throwCipherException(Exception e) throws CipherException {
        throw new CipherException("Error performing cipher operation", e);
    }

    private static byte[] generateMac(byte[] derivedKey, byte[] cipherText) {
        byte[] result = new byte[16 + cipherText.length];

        System.arraycopy(derivedKey, 16, result, 0, 16);
        System.arraycopy(cipherText, 0, result, 16, cipherText.length);

        return Hash.sha3(result);
    }

    public static ECKeyPair decrypt(String password, WalletFile walletFile)
            throws CipherException {

        validate(walletFile);

        WalletFile.Crypto crypto = walletFile.getCrypto();

        byte[] mac = Numeric.hexStringToByteArray(crypto.getMac());
        byte[] iv = Numeric.hexStringToByteArray(crypto.getCipherparams().getIv());
        byte[] cipherText = Numeric.hexStringToByteArray(crypto.getCiphertext());

        byte[] derivedKey;


        if (crypto.getKdfparams() instanceof WalletFile.ScryptKdfParams) {
            WalletFile.ScryptKdfParams scryptKdfParams =
                    (WalletFile.ScryptKdfParams) crypto.getKdfparams();
            int dklen = scryptKdfParams.getDklen();
            int n = scryptKdfParams.getN();
            int p = scryptKdfParams.getP();
            int r = scryptKdfParams.getR();
            byte[] salt = Numeric.hexStringToByteArray(scryptKdfParams.getSalt());
            derivedKey = com.lambdaworks.crypto.SCrypt.scryptN(password.getBytes(Charset.forName("UTF-8")), salt, n, r, p, dklen);
        } else if (crypto.getKdfparams() instanceof WalletFile.Aes128CtrKdfParams) {
            WalletFile.Aes128CtrKdfParams aes128CtrKdfParams =
                    (WalletFile.Aes128CtrKdfParams) crypto.getKdfparams();
            int c = aes128CtrKdfParams.getC();
            String prf = aes128CtrKdfParams.getPrf();
            byte[] salt = Numeric.hexStringToByteArray(aes128CtrKdfParams.getSalt());

            derivedKey = generateAes128CtrDerivedKey(
                    password.getBytes(Charset.forName("UTF-8")), salt, c, prf);
        } else {
            throw new CipherException("Unable to deserialize params: " + crypto.getKdf());
        }

        byte[] derivedMac = generateMac(derivedKey, cipherText);

        if (!Arrays.equals(derivedMac, mac)) {
            throw new CipherException("Invalid password provided");
        }

        byte[] encryptKey = Arrays.copyOfRange(derivedKey, 0, 16);
        byte[] privateKey = performCipherOperation(Cipher.DECRYPT_MODE, iv, encryptKey, cipherText);
        return ECKeyPair.create(privateKey);
    }

    static void validate(WalletFile walletFile) throws CipherException {
        WalletFile.Crypto crypto = walletFile.getCrypto();

        if (walletFile.getVersion() != CURRENT_VERSION) {
            throw new CipherException("Wallet version is not supported");
        }

        if (!crypto.getCipher().equals(CIPHER)) {
            throw new CipherException("Wallet cipher is not supported");
        }

        if (!crypto.getKdf().equals(AES_128_CTR) && !crypto.getKdf().equals(SCRYPT)) {
            throw new CipherException("KDF type is not supported");
        }
    }



    public static ChainAddressInfo loadWalletByPrivateKey(String privateKey, String pwd) {
        Credentials credentials = null;
        ECKeyPair ecKeyPair = ECKeyPair.create(Numeric.toBigInt(privateKey));
        return generateWallet(generateNewWalletName(), pwd, ecKeyPair);
    }


    public static ChainAddressInfo loadWalletByPrivateKey(String wallerName, String privateKey, String pwd) {

        try {
            ECKeyPair ecKeyPair = ECKeyPair.create(Numeric.toBigInt(privateKey));
            return generateWallet(wallerName, pwd, ecKeyPair);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static String derivePrivateKey(long walletId, String pwd) {
        ChainAddressInfo ethWallet = ChainAddressDaoUtils.loadSingle(walletId);
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


    public static String deriveKeystore(long walletId, String pwd) {
        ChainAddressInfo ethWallet = ChainAddressDaoUtils.loadSingle(walletId);
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
        if (!file.getParentFile().exists()) {
            System.out.println("目标文件所在目录不存在，准备创建");
            if (!file.getParentFile().mkdirs()) {
                System.out.println("创建目标文件所在目录失败！");
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) {
        String datas = "m/44'/60'/0'/0/0";

        String[] split = datas.split("/");

        System.out.println();

        for (String value : split) {
            System.out.print("\t" + value);
        }
    }


    public static String NEO_JAXX_TYPE = "m/44'/888'/0'/0/0";


    public static String ONT_JAXX_TYPE = "m/44'/1024'/0'/0/0";


    public static ChainAddressInfo generateMnemonicByNeoOrOnt(String name, String word, String coinType, String jaxxType) {

        ChainAddressInfo neoWallet = null;
        try {

            byte[] prikey = com.github.ontio.crypto.MnemonicCode.getPrikeyFromMnemonicCodesStrBip44(jaxxType, word);

            String hexString = Helper.toHexString(prikey);


            neoutils.Wallet wallet = Neoutils.generateFromPrivateKey(hexString);

            neoWallet = new ChainAddressInfo();

            neoWallet.setName(name);
            neoWallet.setAddress(wallet.getAddress());
            neoWallet.setWif(wallet.getWIF());
            neoWallet.setCoinType(coinType);
            neoWallet.setIsImport(false);
            neoWallet.setIsCurrent(true);
            neoWallet.setPublicScrect(Helper.toHexString(wallet.getPublicKey()));
            neoWallet.setMnemonic(word);
            neoWallet.setPrivateScrect(hexString);
            neoWallet.setType_flag(jaxxType);

        } catch (Exception e) {
            e.printStackTrace();
        }


        return neoWallet;
    }


    public static ChainAddressInfo generateByPrivateKeyNeoOrOnt(String privateKey, String name, String coinType) {

        ChainAddressInfo neoWallet = null;
        try {

            neoutils.Wallet wallet = Neoutils.generateFromPrivateKey(privateKey);

            neoWallet = new ChainAddressInfo();

            //
            neoWallet.setName(name);
            neoWallet.setAddress(wallet.getAddress());
            neoWallet.setWif(wallet.getWIF());
            neoWallet.setPublicScrect(Helper.toHexString(wallet.getPublicKey()));
            neoWallet.setCoinType(coinType);
            neoWallet.setIsImport(true);
            neoWallet.setImportType(1);
            neoWallet.setIsCurrent(false);
            neoWallet.setPrivateScrect(privateKey);


        } catch (Exception e) {
            e.printStackTrace();
        }


        return neoWallet;
    }


    public static ChainAddressInfo importMnemonicByNeoOrOnt(String walletName, String jaxxType, String word, String coinType) {

        ChainAddressInfo neoWallet = null;
        try {

            byte[] prikey = com.github.ontio.crypto.MnemonicCode.getPrikeyFromMnemonicCodesStrBip44(jaxxType, word);

            String hexString = Helper.toHexString(prikey);


            neoutils.Wallet wallet = Neoutils.generateFromPrivateKey(hexString);


            neoWallet = new ChainAddressInfo();

            neoWallet.setName(walletName);
            neoWallet.setAddress(wallet.getAddress());
            neoWallet.setWif(wallet.getWIF());
            neoWallet.setPublicScrect(Helper.toHexString(wallet.getPublicKey()));
            neoWallet.setCoinType(coinType);
            neoWallet.setIsImport(true);
            neoWallet.setIsCurrent(false);
            neoWallet.setMnemonic(word);
            neoWallet.setImportType(0);
            neoWallet.setPrivateScrect(hexString);
            neoWallet.setType_flag(jaxxType);

        } catch (Exception e) {
            e.printStackTrace();
        }


        return neoWallet;
    }

    public static ChainAddressInfo importWifByWalletByNeoOrOnt(String walletName, String wif, String coinType) {

        ChainAddressInfo neoWallet = null;
        try {

            neoutils.Wallet wallet = Neoutils.generateFromWIF(wif);


            neoWallet = new ChainAddressInfo();

            neoWallet.setName(walletName);
            neoWallet.setAddress(wallet.getAddress());
            neoWallet.setWif(wallet.getWIF());
            neoWallet.setCoinType(coinType);
            neoWallet.setPublicScrect(Helper.toHexString(wallet.getPublicKey()));
            neoWallet.setIsImport(true);
            neoWallet.setIsCurrent(false);
            neoWallet.setWif(wif);
            neoWallet.setImportType(2);
            neoWallet.setPrivateScrect(Helper.toHexString(wallet.getPrivateKey()));

        } catch (Exception e) {
            e.printStackTrace();
        }


        return neoWallet;
    }


}
