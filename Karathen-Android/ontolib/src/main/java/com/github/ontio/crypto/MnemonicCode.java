package com.github.ontio.crypto;

import android.util.Base64;

import com.crypho.plugins.ScryptPlugin;
import com.github.ontio.account.Account;
import com.github.ontio.common.Address;
import com.github.ontio.common.ErrorCode;
import com.github.ontio.common.Helper;
import com.github.ontio.crypto.bip32.ExtendedPrivateKey;
import com.github.ontio.sdk.exception.SDKException;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import io.github.novacrypto.bip32.networks.Bitcoin;
import io.github.novacrypto.bip39.MnemonicGenerator;
import io.github.novacrypto.bip39.SeedCalculator;
import io.github.novacrypto.bip39.Words;
import io.github.novacrypto.bip39.wordlists.English;

public class MnemonicCode {

    public static String generateMnemonicCodesStr(){
        final StringBuilder sb = new StringBuilder();
        byte[] entropy = new byte[Words.TWELVE.byteLength()];
        new SecureRandom().nextBytes(entropy);
        new MnemonicGenerator(English.INSTANCE).createMnemonic(entropy, new MnemonicGenerator.Target() {
            @Override
            public void append(CharSequence string) {
                sb.append(string);
            }
        });
        new SecureRandom().nextBytes(entropy);
        return sb.toString();
    }

//    public static byte[] getPrikeyFromMnemonicCodesStr(String mnemonicCodesStr){
//        String[] mnemonicCodesArray = mnemonicCodesStr.split(" ");
//        byte[] seed = new SeedCalculator()
//                .withWordsFromWordList(English.INSTANCE)
//                .calculateSeed(Arrays.asList(mnemonicCodesArray), "");
//        mnemonicCodesArray = null;
//        mnemonicCodesStr = null;
//        byte[] prikey = Arrays.copyOfRange(seed,0,32);
//        return prikey;
//    }
    public static byte[] getSeedFromMnemonicCodesStr(String mnemonicCodesStr){
        String[] mnemonicCodesArray = mnemonicCodesStr.split(" ");
        byte[] seed = new SeedCalculator()
                .withWordsFromWordList(English.INSTANCE)
                .calculateSeed(Arrays.asList(mnemonicCodesArray), "");
        mnemonicCodesArray = null;
        mnemonicCodesStr = null;
        return seed;
    }

    public static byte[] getPrikeyFromMnemonicCodesStrBip44(String jaxxType,String mnemonicCodesStr) throws Exception{
        byte[] seed = MnemonicCode.getSeedFromMnemonicCodesStr(mnemonicCodesStr);
        ExtendedPrivateKey key = ExtendedPrivateKey.fromSeed(seed,"Nist256p1 seed".getBytes("UTF-8"), Bitcoin.MAIN_NET);
        ExtendedPrivateKey child = key.derive(jaxxType);
        byte[] p = child.extendedKeyByteArray();
        byte[] tmp = new byte[32];
        System.arraycopy(p, 46, tmp, 0, 32);
        return tmp;
    }

    public static byte[] getPrikeyFromMnemonicCodesStrBip44(String mnemonicCodesStr) throws Exception{
        byte[] seed = MnemonicCode.getSeedFromMnemonicCodesStr(mnemonicCodesStr);
        ExtendedPrivateKey key = ExtendedPrivateKey.fromSeed(seed,"Nist256p1 seed".getBytes("UTF-8"), Bitcoin.MAIN_NET);
        ExtendedPrivateKey child = key.derive("m/44'/888'/0'/0/0");
        byte[] p = child.extendedKeyByteArray();
        byte[] tmp = new byte[32];
        System.arraycopy(p, 46, tmp, 0, 32);
        return tmp;
    }
    public static String encryptMnemonicCodesStr(String mnemonicCodesStr, String password, String address) throws Exception {
        int N = 4096;
        int r = 8;
        int p = 8;
        int dkLen = 64;

        byte[] addresshashTmp = Digest.sha256(Digest.sha256(address.getBytes()));
        byte[] salt = Arrays.copyOfRange(addresshashTmp, 0, 4);
        byte[] derivedkey = ScryptPlugin.scrypt(password.getBytes(StandardCharsets.UTF_8), getChars(salt), N, r, p, dkLen);
        password = null;

        byte[] derivedhalf2 = new byte[32];
        byte[] iv = new byte[16];
        System.arraycopy(derivedkey, 0, iv, 0, 16);
        System.arraycopy(derivedkey, 32, derivedhalf2, 0, 32);

        SecretKeySpec skeySpec = new SecretKeySpec(derivedhalf2, "AES");
        Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, new IvParameterSpec(iv));
        byte[] encryptedkey = cipher.doFinal(mnemonicCodesStr.getBytes());
        mnemonicCodesStr = null;

        return new String(Base64.encode(encryptedkey, Base64.NO_WRAP));

    }

    public static String decryptMnemonicCodesStr(String encryptedMnemonicCodesStr, String password,String address) throws Exception {
        if (encryptedMnemonicCodesStr == null) {
            throw new SDKException(ErrorCode.ParamError);
        }
        byte[] encryptedkey = Base64.decode(encryptedMnemonicCodesStr, Base64.NO_WRAP);

        int N = 4096;
        int r = 8;
        int p = 8;
        int dkLen = 64;

        byte[] addresshashTmp = Digest.sha256(Digest.sha256(address.getBytes()));
        byte[] salt = Arrays.copyOfRange(addresshashTmp, 0, 4);

        byte[] derivedkey = ScryptPlugin.scrypt(password.getBytes(StandardCharsets.UTF_8), getChars(salt), N, r, p, dkLen);
        password = null;
        byte[] derivedhalf2 = new byte[32];
        byte[] iv = new byte[16];
        System.arraycopy(derivedkey, 0, iv, 0, 16);
        System.arraycopy(derivedkey, 32, derivedhalf2, 0, 32);

        SecretKeySpec skeySpec = new SecretKeySpec(derivedhalf2, "AES");
        Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, skeySpec, new IvParameterSpec(iv));
        byte[] rawMns = cipher.doFinal(encryptedkey);
        String mnemonicCodesStr = new String(rawMns);
        byte[] rawkey = MnemonicCode.getPrikeyFromMnemonicCodesStrBip44(mnemonicCodesStr);
        String addressNew = new Account(rawkey, SignatureScheme.SHA256WITHECDSA).getAddressU160().toBase58();
        byte[] addressNewHashTemp = Digest.sha256(Digest.sha256(addressNew.getBytes()));
        byte[] saltNew = Arrays.copyOfRange(addressNewHashTemp, 0, 4);
        if (!Arrays.equals(saltNew, salt)) {
            throw new SDKException(ErrorCode.KeyAddressPwdNotMatch);
        }
        return mnemonicCodesStr;
    }


    public static char[] getChars(byte[] bytes) {
        char[] chars = new char[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            chars[i] = (char) bytes[i];
        }
        return chars;
    }
}
