package com.github.ontio.account;


import com.crypho.plugins.ScryptPlugin;
import com.github.ontio.common.Address;
import com.github.ontio.common.ErrorCode;
import com.github.ontio.common.Helper;
import com.github.ontio.crypto.Base58;
import com.github.ontio.crypto.Curve;
import com.github.ontio.crypto.Digest;
import com.github.ontio.crypto.KeyType;
import com.github.ontio.crypto.Signature;
import com.github.ontio.crypto.SignatureHandler;
import com.github.ontio.crypto.SignatureScheme;
import com.github.ontio.sdk.exception.SDKException;

import org.spongycastle.crypto.generators.SCrypt;
import org.spongycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey;
import org.spongycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
import org.spongycastle.jce.ECNamedCurveTable;
import org.spongycastle.jce.ECPointUtil;
import org.spongycastle.jce.provider.BouncyCastleProvider;
import org.spongycastle.jce.spec.ECNamedCurveParameterSpec;
import org.spongycastle.jce.spec.ECNamedCurveSpec;
import org.spongycastle.util.Strings;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.ECPrivateKeySpec;
import java.security.spec.ECPublicKeySpec;
import java.util.Arrays;

import android.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


public class Account {
    private KeyType keyType;
    private Object[] curveParams;
    private PrivateKey privateKey;
    private PublicKey publicKey;
    private Address addressU160;
    private SignatureScheme signatureScheme;

    public SignatureScheme getSignatureScheme(){
        return signatureScheme;
    }

    // create an account with the specified key type
    public Account(SignatureScheme scheme) throws Exception {
        Security.addProvider(new BouncyCastleProvider());
        KeyPairGenerator gen;
        AlgorithmParameterSpec paramSpec;
        signatureScheme = scheme;

        if (scheme == SignatureScheme.SHA256WITHECDSA) {
            this.keyType = KeyType.ECDSA;
            this.curveParams = new Object[]{Curve.P256.toString()};
        } else if (scheme == SignatureScheme.SM3WITHSM2) {
            this.keyType = KeyType.SM2;
            this.curveParams = new Object[]{Curve.SM2P256V1.toString()};
        }

        switch (scheme) {
            case SHA256WITHECDSA:
            case SM3WITHSM2:
                if (!(curveParams[0] instanceof String)) {
                    throw new Exception(ErrorCode.InvalidParams);
                }
                String curveName = (String) curveParams[0];
                paramSpec = new ECGenParameterSpec(curveName);
                gen = KeyPairGenerator.getInstance("EC", "SC");
                break;
            default:
                //should not reach here
                throw new Exception(ErrorCode.UnsupportedKeyType);
        }
        gen.initialize(paramSpec, new SecureRandom());
        KeyPair keyPair = gen.generateKeyPair();
        this.privateKey = keyPair.getPrivate();
        this.publicKey = keyPair.getPublic();
        this.addressU160 = Address.addressFromPubKey(serializePublicKey());
    }

    public Account(byte[] prikey, SignatureScheme scheme) throws Exception {
        Security.addProvider(new BouncyCastleProvider());
        signatureScheme = scheme;

        if (scheme == SignatureScheme.SM3WITHSM2) {
            this.keyType = KeyType.SM2;
            this.curveParams = new Object[]{Curve.SM2P256V1.toString()};
        } else if (scheme == SignatureScheme.SHA256WITHECDSA) {
            this.keyType = KeyType.ECDSA;
            this.curveParams = new Object[]{Curve.P256.toString()};
        }

        switch (scheme) {
            case SHA256WITHECDSA:
            case SM3WITHSM2:
                BigInteger d = new BigInteger(1, prikey);
                ECNamedCurveParameterSpec spec = ECNamedCurveTable.getParameterSpec((String) this.curveParams[0]);
                ECParameterSpec paramSpec = new ECNamedCurveSpec(spec.getName(), spec.getCurve(), spec.getG(), spec.getN());
                ECPrivateKeySpec priSpec = new ECPrivateKeySpec(d, paramSpec);
                KeyFactory kf = KeyFactory.getInstance("EC", "SC");
                this.privateKey = kf.generatePrivate(priSpec);

                org.spongycastle.math.ec.ECPoint Q = spec.getG().multiply(d).normalize();
                if (Q == null || Q.getAffineXCoord() == null || Q.getAffineYCoord() == null) {
                    throw new SDKException(ErrorCode.KeyAddressPwdNotMatch);
                }
                ECPublicKeySpec pubSpec = new ECPublicKeySpec(
                        new ECPoint(Q.getAffineXCoord().toBigInteger(), Q.getAffineYCoord().toBigInteger()),
                        paramSpec);
                this.publicKey = kf.generatePublic(pubSpec);
                this.addressU160 = Address.addressFromPubKey(serializePublicKey());
                break;
            default:
                throw new Exception(ErrorCode.UnsupportedKeyType);
        }
    }

    // construct an account from a serialized pubic key or private key
    public Account(boolean fromPrivate, byte[] pubkey) throws Exception {
        Security.insertProviderAt(new org.spongycastle.jce.provider.BouncyCastleProvider(), 1);
        if (fromPrivate) {
            //parsePrivateKey(data);
        } else {
            parsePublicKey(pubkey);
        }
    }

    /**
     * Private Key From WIF
     *
     * @param wif
     * @return
     */
    public static byte[] getPrivateKeyFromWIF(String wif) throws Exception {
        if (wif == null) {
            throw new SDKException(ErrorCode.ParamError);
        }
        byte[] data = Base58.decode(wif);
        if (data.length != 38 || data[0] != (byte) 0x80 || data[33] != 0x01) {
            throw new SDKException(ErrorCode.ParamError);
        }
        byte[] checksum = Digest.sha256(Digest.sha256(data, 0, data.length - 4));
        for (int i = 0; i < 4; i++) {
            if (data[data.length - 4 + i] != checksum[i]) {
                throw new SDKException(ErrorCode.ParamError);
            }
        }
        byte[] privateKey = new byte[32];
        System.arraycopy(data, 1, privateKey, 0, privateKey.length);
        Arrays.fill(data, (byte) 0);
        return privateKey;
    }

    public KeyType getKeyType() {
        return keyType;
    }

    public Object[] getCurveParams() {
        return curveParams;
    }


    /**
     * @param encryptedPriKey
     * @param passphrase
     * @return
     * @throws Exception
     */
    public static String getEcbDecodedPrivateKey(String encryptedPriKey, String passphrase, int n, SignatureScheme scheme) throws Exception {
        if (encryptedPriKey == null) {
            throw new SDKException(ErrorCode.ParamError);
        }
        byte[] decoded = Base58.decodeChecked(encryptedPriKey);
        if (decoded.length != 43 || decoded[0] != (byte) 0x01 || decoded[1] != (byte) 0x42 || decoded[2] != (byte) 0xe0) {
            throw new SDKException(ErrorCode.Decoded3bytesError);
        }
        byte[] data = Arrays.copyOfRange(decoded, 0, decoded.length - 4);
        return decode(passphrase, data, n, scheme);
    }

    private static String decode(String passphrase, byte[] input, int n, SignatureScheme scheme) throws Exception {
        int N = n;
        int r = 8;
        int p = 8;
        int dkLen = 64;
        byte[] addresshash = new byte[4];
        byte[] encryptedkey = new byte[32];
        System.arraycopy(input, 3, addresshash, 0, 4);
        System.arraycopy(input, 7, encryptedkey, 0, 32);
        byte[] derivedkey = ScryptPlugin.scrypt(passphrase.getBytes(StandardCharsets.UTF_8), getChars(addresshash), N, r, p, dkLen);
        byte[] derivedhalf1 = new byte[32];
        byte[] derivedhalf2 = new byte[32];
        System.arraycopy(derivedkey, 0, derivedhalf1, 0, 32);
        System.arraycopy(derivedkey, 32, derivedhalf2, 0, 32);

        SecretKeySpec skeySpec = new SecretKeySpec(derivedhalf2, "AES");
        Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding");// AES/ECB/NoPadding
        cipher.init(Cipher.DECRYPT_MODE, skeySpec);
        byte[] rawkey = cipher.doFinal(encryptedkey);

        String priKey = Helper.toHexString(XOR(rawkey, derivedhalf1));
        Account account = new Account(Helper.hexToBytes(priKey), scheme);
        Address script_hash = Address.addressFromPubKey(account.serializePublicKey());
        String address = script_hash.toBase58();
        byte[] addresshashTmp = Digest.sha256(Digest.sha256(address.getBytes()));
        byte[] addresshashNew = Arrays.copyOfRange(addresshashTmp, 0, 4);

        if (!new String(addresshash).equals(new String(addresshashNew))) {
            throw new SDKException(ErrorCode.DecodePrikeyPassphraseError + Helper.toHexString(addresshash) + "," + Helper.toHexString(addresshashNew));
        }
        return priKey;
    }

    private static byte[] XOR(byte[] x, byte[] y) throws Exception {
        if (x.length != y.length) {
            throw new SDKException(ErrorCode.ParamError);
        }
        byte[] ret = new byte[x.length];
        for (int i = 0; i < x.length; i++) {
            ret[i] = (byte) (x[i] ^ y[i]);
        }
        return ret;
    }

    public Address getAddressU160() {
        return addressU160;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public byte[] generateSignature(byte[] msg, SignatureScheme scheme, Object param) throws Exception {
        if (msg == null || msg.length == 0) {
            throw new Exception(ErrorCode.InvalidMessage);
        }
        if (this.privateKey == null) {
            throw new Exception(ErrorCode.WithoutPrivate);
        }

        SignatureHandler ctx = new SignatureHandler(keyType, signatureScheme);
        AlgorithmParameterSpec paramSpec = null;
        if (signatureScheme == SignatureScheme.SM3WITHSM2) {
            if (param instanceof String) {
                paramSpec = new SM2ParameterSpec(Strings.toByteArray((String) param));
            } else if (param == null) {
                paramSpec = new SM2ParameterSpec("1234567812345678".getBytes());
            } else {
                throw new Exception(ErrorCode.InvalidSM2Signature);
            }
        }
        byte[] signature = new Signature(
                signatureScheme,
                paramSpec,
                ctx.generateSignature(privateKey, msg, paramSpec)
        ).toBytes();
        return signature;
    }

    public boolean verifySignature(byte[] msg, byte[] signature) throws Exception {
        if (msg == null || signature == null || msg.length == 0 || signature.length == 0) {
            throw new Exception(ErrorCode.AccountInvalidInput);
        }
        if (this.publicKey == null) {
            throw new Exception(ErrorCode.AccountWithoutPublicKey);
        }
        Signature sig = new Signature(signature);
        SignatureHandler ctx = new SignatureHandler(keyType, sig.getScheme());
        return ctx.verifySignature(publicKey, msg, sig.getValue());
    }


    public byte[] serializePublicKey() {
        ByteArrayOutputStream bs = new ByteArrayOutputStream();
        BCECPublicKey pub = (BCECPublicKey) publicKey;
        try {
            switch (this.keyType) {
                case ECDSA:
                    //bs.write(this.keyType.getLabel());
                    //bs.write(Curve.valueOf(pub.getParameters().getCurve()).getLabel());
                    bs.write(pub.getQ().getEncoded(true));
                    break;
                case SM2:
                    bs.write(this.keyType.getLabel());
                    bs.write(Curve.valueOf(pub.getParameters().getCurve()).getLabel());
                    bs.write(pub.getQ().getEncoded(true));
                    break;
                default:
                    // Should not reach here
                    throw new Exception(ErrorCode.UnknownKeyType);
            }
        } catch (Exception e) {
            // Should not reach here
            e.printStackTrace();
            return null;
        }
        return bs.toByteArray();
    }

    private void parsePublicKey(byte[] data) throws Exception {
        if (data == null) {
            throw new Exception(ErrorCode.NullInput);
        }
        if (data.length < 2) {
            throw new Exception(ErrorCode.InvalidData);
        }
        if(data.length == 33){
            this.keyType = KeyType.ECDSA;
        }
        this.privateKey = null;
        this.publicKey = null;
        switch (this.keyType) {
            case ECDSA:
			    this.keyType = KeyType.ECDSA;
                this.curveParams = new Object[]{Curve.P256.toString()};
                ECNamedCurveParameterSpec spec0 = ECNamedCurveTable.getParameterSpec(Curve.P256.toString());
                ECParameterSpec param0 = new ECNamedCurveSpec(spec0.getName(), spec0.getCurve(), spec0.getG(), spec0.getN());
                ECPublicKeySpec pubSpec0 = new ECPublicKeySpec(
                        ECPointUtil.decodePoint(
                                param0.getCurve(),
                                Arrays.copyOfRange(data, 0, data.length)),
                        param0);
                KeyFactory kf0 = KeyFactory.getInstance("EC", "SC");
                this.publicKey = kf0.generatePublic(pubSpec0);
                break;
            case SM2:
                this.keyType = KeyType.fromLabel(data[0]);
                Curve c = Curve.fromLabel(data[1]);
                this.curveParams = new Object[]{c.toString()};
                ECNamedCurveParameterSpec spec = ECNamedCurveTable.getParameterSpec(c.toString());
                ECParameterSpec param = new ECNamedCurveSpec(spec.getName(), spec.getCurve(), spec.getG(), spec.getN());
                ECPublicKeySpec pubSpec = new ECPublicKeySpec(
                        ECPointUtil.decodePoint(
                                param.getCurve(),
                                Arrays.copyOfRange(data, 2, data.length)),
                        param);
                KeyFactory kf = KeyFactory.getInstance("EC", "SC");
                this.publicKey = kf.generatePublic(pubSpec);
                break;
            default:
                throw new Exception(ErrorCode.UnknownKeyType);
        }
    }

    public byte[] serializePrivateKey() throws Exception {
        switch (this.keyType) {
            case ECDSA:
            case SM2:
                BCECPrivateKey pri = (BCECPrivateKey) this.privateKey;
                String curveName = Curve.valueOf(pri.getParameters().getCurve()).toString();
                byte[] d = new byte[32];
                if (pri.getD().toByteArray().length == 33) {
                    System.arraycopy(pri.getD().toByteArray(), 1, d, 0, 32);
                } else {
                    return pri.getD().toByteArray();
                }
                return d;
            default:
                // should not reach here
                throw new Exception(ErrorCode.UnknownKeyType);
        }
    }

    public int compareTo(Account o) throws Exception {
        byte[] pub0 = serializePublicKey();
        byte[] pub1 = o.serializePublicKey();
        for (int i = 0; i < pub0.length && i < pub1.length; i++) {
            if (pub0[i] != pub1[i]) {
                return pub0[i] - pub1[i];
            }
        }

        return pub0.length - pub1.length;
    }

    public String exportWif() throws Exception {
        byte[] data = new byte[38];
        data[0] = (byte) 0x80;
        byte[] prikey = serializePrivateKey();
        System.arraycopy(prikey, 0, data, 1, 32);
        data[33] = (byte) 0x01;
        byte[] checksum = Digest.sha256(Digest.sha256(data, 0, data.length - 4));
        System.arraycopy(checksum, 0, data, data.length - 4, 4);
        String wif = Base58.encode(data);
        Arrays.fill(data, (byte) 0);
        return wif;
    }

    public String exportEcbEncryptedPrikey(String passphrase, int n) throws Exception {

        int N = n;
        int r = 8;
        int p = 8;
        int dkLen = 64;
        Address script_hash = Address.addressFromPubKey(serializePublicKey());
        String address = script_hash.toBase58();

        byte[] addresshashTmp = Digest.sha256(Digest.sha256(address.getBytes()));
        byte[] addresshash = Arrays.copyOfRange(addresshashTmp, 0, 4);

        byte[] derivedkey = ScryptPlugin.scrypt(passphrase.getBytes(StandardCharsets.UTF_8), getChars(addresshash), N, r, p, dkLen);
        byte[] derivedhalf1 = new byte[32];
        byte[] derivedhalf2 = new byte[32];
        System.arraycopy(derivedkey, 0, derivedhalf1, 0, 32);
        System.arraycopy(derivedkey, 32, derivedhalf2, 0, 32);

        SecretKeySpec skeySpec = new SecretKeySpec(derivedhalf2, "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        byte[] derived = XOR(serializePrivateKey(), derivedhalf1);
        byte[] encryptedkey = cipher.doFinal(derived);

        byte[] buffer = new byte[encryptedkey.length + 7];
        buffer[0] = (byte) 0x01;
        buffer[1] = (byte) 0x42;
        buffer[2] = (byte) 0xe0;
        System.arraycopy(addresshash, 0, buffer, 3, addresshash.length);
        System.arraycopy(encryptedkey, 0, buffer, 7, encryptedkey.length);
        return Base58.checkSumEncode(buffer);

    }

    public String exportCtrEncryptedPrikey(String passphrase, int n) throws Exception {
        int N = n;
        int r = 8;
        int p = 8;
        int dkLen = 64;
        Address script_hash = Address.addressFromPubKey(serializePublicKey());
        String address = script_hash.toBase58();

        byte[] addresshashTmp = Digest.sha256(Digest.sha256(address.getBytes()));
        byte[] addresshash = Arrays.copyOfRange(addresshashTmp, 0, 4);
        byte[] derivedkey = ScryptPlugin.scrypt(passphrase.getBytes(StandardCharsets.UTF_8), getChars(addresshash), N, r, p, dkLen);

        byte[] derivedhalf2 = new byte[32];
        byte[] iv = new byte[16];
        System.arraycopy(derivedkey, 0, iv, 0, 16);
        System.arraycopy(derivedkey, 32, derivedhalf2, 0, 32);

        SecretKeySpec skeySpec = new SecretKeySpec(derivedhalf2, "AES");
        Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, new IvParameterSpec(iv));
        byte[] encryptedkey = cipher.doFinal(serializePrivateKey());
        return new String(Base64.encode(encryptedkey, Base64.NO_WRAP));

    }

    public String exportGcmEncryptedPrikey(String passphrase,byte[] salt, int n) throws Exception {
        int N = n;
        int r = 8;
        int p = 8;
        int dkLen = 64;
        if (salt.length != 16) {
            throw new SDKException(ErrorCode.ParamError);
        }

        byte[] derivedkey = ScryptPlugin.scrypt(passphrase.getBytes(StandardCharsets.UTF_8), getChars(salt), N, r, p, dkLen);
        byte[] derivedhalf2 = new byte[32];
        byte[] iv = new byte[12];
        System.arraycopy(derivedkey, 0, iv, 0, 12);
        System.arraycopy(derivedkey, 32, derivedhalf2, 0, 32);
        try {
            SecretKeySpec skeySpec = new SecretKeySpec(derivedhalf2, "AES");
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, new GCMParameterSpec(128,iv));
            cipher.updateAAD(getAddressU160().toBase58().getBytes());
            byte[] encryptedkey = cipher.doFinal(serializePrivateKey());
            return new String(Base64.encode(encryptedkey,Base64.NO_WRAP));
        } catch (Exception e) {
            throw new SDKException(ErrorCode.EncriptPrivateKeyError);
        }
    }

 

    public static String getCtrDecodedPrivateKey(String encryptedPriKey, String passphrase, String address, int n, SignatureScheme scheme) throws Exception {
        byte[] addresshashTmp = Digest.sha256(Digest.sha256(address.getBytes()));
        byte[] addresshash = Arrays.copyOfRange(addresshashTmp, 0, 4);
        return getCtrDecodedPrivateKey(encryptedPriKey, passphrase, addresshash, n, scheme);
    }

    /**
     * @param encryptedPriKey
     * @param passphrase
     * @param salt
     * @param n
     * @param scheme
     * @return
     * @throws Exception
     */
    public static String getCtrDecodedPrivateKey(String encryptedPriKey, String passphrase, byte[] salt, int n, SignatureScheme scheme) throws Exception {
        if (encryptedPriKey == null) {
            throw new SDKException(ErrorCode.ParamError);
        }
        if (salt.length == 0) {
            throw new SDKException(ErrorCode.ParamError);
        }
        byte[] encryptedkey = Base64.decode(encryptedPriKey, Base64.NO_WRAP);

        int N = n;
        int r = 8;
        int p = 8;
        int dkLen = 64;
        String s = Helper.toHexString(salt);
        byte[] derivedkey = ScryptPlugin.scrypt(passphrase.getBytes(StandardCharsets.UTF_8), getChars(salt), N, r, p, dkLen);
        byte[] derivedhalf2 = new byte[32];
        byte[] iv = new byte[16];
        System.arraycopy(derivedkey, 0, iv, 0, 16);
        System.arraycopy(derivedkey, 32, derivedhalf2, 0, 32);

        SecretKeySpec skeySpec = new SecretKeySpec(derivedhalf2, "AES");
        Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, skeySpec, new IvParameterSpec(iv));
        byte[] rawkey = cipher.doFinal(encryptedkey);
        String address = new Account(rawkey, scheme).getAddressU160().toBase58();
        byte[] addresshashTmp = Digest.sha256(Digest.sha256(address.getBytes()));
        byte[] addresshash = Arrays.copyOfRange(addresshashTmp, 0, 4);
        if (!Arrays.equals(addresshash, salt)) {
            throw new SDKException(ErrorCode.KeyAddressPwdNotMatch);
        }
        return Helper.toHexString(rawkey);
    }

   public static String getGcmDecodedPrivateKey(String encryptedPriKey, String passphrase,String address, byte[] salt, int n, SignatureScheme scheme) throws Exception {
        if (encryptedPriKey == null) {
            throw new SDKException(ErrorCode.EncryptedPriKeyError);
        }
        if (salt.length != 16) {
            throw new SDKException(ErrorCode.ParamError);
        }
        byte[] encryptedkey = Base64.decode(encryptedPriKey,Base64.NO_WRAP);

        int N = n;
        int r = 8;
        int p = 8;
        int dkLen = 64;

        byte[] derivedkey = ScryptPlugin.scrypt(passphrase.getBytes(StandardCharsets.UTF_8), getChars(salt), N, r, p, dkLen);
        byte[] derivedhalf2 = new byte[32];
        byte[] iv = new byte[12];
        System.arraycopy(derivedkey, 0, iv, 0, 12);
        System.arraycopy(derivedkey, 32, derivedhalf2, 0, 32);

        byte[] rawkey = new byte[0];
        try {
            SecretKeySpec skeySpec = new SecretKeySpec(derivedhalf2, "AES");
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, new GCMParameterSpec(128,iv));
            cipher.updateAAD(address.getBytes());
            rawkey = cipher.doFinal(encryptedkey);
        } catch (Exception e) {
            throw new SDKException(ErrorCode.encryptedPriKeyAddressPasswordErr);
        }
        Account account = new Account(rawkey, scheme);
        if (!address.equals(account.getAddressU160().toBase58())) {
            throw new SDKException(ErrorCode.encryptedPriKeyAddressPasswordErr);
        }
        return Helper.toHexString(rawkey);
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Account)) {
            return false;
        }
        return addressU160.equals(((Account) obj).addressU160);
    }

    @Override
    public int hashCode() {
        return addressU160.hashCode();
    }

    // byte转char


    public static char[] getChars(byte[] bytes) {
        char[] chars = new char[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            chars[i] = (char) bytes[i];
        }
        return chars;
    }
}
