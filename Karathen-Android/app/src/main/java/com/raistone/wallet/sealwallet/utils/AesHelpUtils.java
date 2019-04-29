package com.raistone.wallet.sealwallet.utils;


import android.annotation.TargetApi;
import android.os.Build;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


public class AesHelpUtils {



    public static final String CHAR_ENCODING = "UTF-8";
    public static final String AES_ALGORITHM = "AES";


    public static byte[] encrypt(String content, String key) {
        try {
            KeyGenerator kgen = KeyGenerator.getInstance(AES_ALGORITHM);
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            random.setSeed(key.getBytes());
            //kgen.init(128, new SecureRandom(key.getBytes()));
            kgen.init(128, random);
            SecretKey secretKey = kgen.generateKey();
            byte[] enCodeFormat = secretKey.getEncoded();
            SecretKeySpec skey = new SecretKeySpec(enCodeFormat, AES_ALGORITHM);
            Cipher cipher = Cipher.getInstance(AES_ALGORITHM);// 创建密码器
            byte[] byteContent = content.getBytes(CHAR_ENCODING);
            cipher.init(Cipher.ENCRYPT_MODE, skey);// 初始化
            byte[] result = cipher.doFinal(byteContent);
            return result; // 加密
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static byte[] decrypt(byte[] content, String key) {
        try {
            KeyGenerator kgen = KeyGenerator.getInstance(AES_ALGORITHM);
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            random.setSeed(key.getBytes());
            //kgen.init(128, new SecureRandom(key.getBytes()));
            kgen.init(128, random);
            SecretKey secretKey = kgen.generateKey();
            byte[] enCodeFormat = secretKey.getEncoded();
            SecretKeySpec skey = new SecretKeySpec(enCodeFormat, AES_ALGORITHM);
            Cipher cipher = Cipher.getInstance("AES");// 创建密码器
            cipher.init(Cipher.DECRYPT_MODE, skey);// 初始化
            byte[] result = cipher.doFinal(content);
            return result; // 加密
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String parseByte2HexStr(byte buf[]) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < buf.length; i++) {
            String hex = Integer.toHexString(buf[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }


    public static byte[] parseHexStr2Byte(String hexStr) {
        if (hexStr.length() < 1)
            return null;
        byte[] result = new byte[hexStr.length() / 2];
        for (int i = 0; i < hexStr.length() / 2; i++) {
            int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
            int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
            result[i] = (byte) (high * 16 + low);
        }
        return result;
    }


    public static String encryptString(String content, String key) {
        byte[] encrypt = encrypt(content, key);
        return parseByte2HexStr(encrypt);
    }


    public static String decryptString(String content, String key) {

        byte[] encryptbyte = parseHexStr2Byte(content);

        byte[] decrypt = decrypt(encryptbyte, key);

        try {
            return new String(decrypt, CHAR_ENCODING);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] initKey() throws Exception {
        KeyGenerator kgen = KeyGenerator.getInstance(AES_ALGORITHM);
        kgen.init(128);
        SecretKey skey = kgen.generateKey();
        return skey.getEncoded();
    }

    public static void main(String[] args) throws Exception {



    }

}








