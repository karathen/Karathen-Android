/*
 * Copyright (C) 2018 The ontology Authors
 * This file is part of The ontology library.
 *
 *  The ontology is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  The ontology is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with The ontology.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.github.ontio.sdk.manager;

import com.github.ontio.account.Account;
import com.github.ontio.common.ErrorCode;
import com.github.ontio.common.Helper;
import com.github.ontio.crypto.KeyType;
import com.github.ontio.crypto.SignatureScheme;

import org.spongycastle.crypto.AsymmetricCipherKeyPair;
import org.spongycastle.crypto.Digest;
import org.spongycastle.crypto.digests.SHA1Digest;
import org.spongycastle.crypto.generators.KDF2BytesGenerator;
import org.spongycastle.crypto.kems.ECIESKeyEncapsulation;
import org.spongycastle.crypto.params.ECDomainParameters;
import org.spongycastle.crypto.params.ECPrivateKeyParameters;
import org.spongycastle.crypto.params.ECPublicKeyParameters;
import org.spongycastle.crypto.params.KeyParameter;
import org.spongycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey;
import org.spongycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
import org.spongycastle.jce.ECNamedCurveTable;
import org.spongycastle.jce.spec.ECNamedCurveParameterSpec;
import org.spongycastle.util.encoders.Hex;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * @Description:
 * @date 2018/4/11
 */
public class ECIES {
    public static KeyType keyType = KeyType.ECDSA;
    public static Object[] curveParaSpec = new Object[]{"P-256"};
    public static SignatureScheme signatureScheme = SignatureScheme.SHA256WITHECDSA;
    public static Digest digest = new SHA1Digest();

    public ECIES(Digest dig) {
        digest = dig;
    }

    public static void setDigest(Digest dig) {
        digest = dig;
    }

    public static String[] Encrypt(String pubkey, byte[] msg) throws Exception {
        return Encrypt(pubkey, msg, 32);
    }

    //keylen: 16/24/32
    public static String[] Encrypt(String pubkey, byte[] msg, int keylen) throws Exception {
        com.github.ontio.account.Account account = new com.github.ontio.account.Account(false, Helper.hexToBytes(pubkey));

        ECNamedCurveParameterSpec spec = ECNamedCurveTable.getParameterSpec((String) curveParaSpec[0]);
        ECDomainParameters ecDomain = new ECDomainParameters(spec.getCurve(), spec.getG(), spec.getN());
        AsymmetricCipherKeyPair keys = new AsymmetricCipherKeyPair(
                new ECPublicKeyParameters(((BCECPublicKey) account.getPublicKey()).getQ(), ecDomain), null);

        byte[] out = new byte[(ecDomain.getCurve().getFieldSize() / 8) * 2 + 1];
        ECIESKeyEncapsulation kem = new ECIESKeyEncapsulation(new KDF2BytesGenerator(digest), new SecureRandom());
        KeyParameter key1;

        kem.init(keys.getPublic());
        key1 = (KeyParameter) kem.encrypt(out, keylen); //AES key = key1 (is encrypted in out)

        byte[] IV = Hex.decode(getRandomString(keylen)); //choose random IV of length = keylen
        byte[] ciphertext;
        Cipher en = Cipher.getInstance("AES/CBC/PKCS7Padding", "SC");
        Key key = new SecretKeySpec(key1.getKey(), "AES");
        en.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(IV));
        ciphertext = en.doFinal(msg);

        //(IV, out, ciphertext)
        return new String[]{Helper.toHexString(IV), Helper.toHexString(out), Helper.toHexString(ciphertext)};
    }

    public static byte[] Decrypt(Account account, String[] params) throws Exception {
        if (params.length != 3) {
            throw new Exception(ErrorCode.ParamError);
        }
        return Decrypt(account.serializePrivateKey(), Helper.hexToBytes(params[0]), Helper.hexToBytes(params[1]), Helper.hexToBytes(params[2]), 32);
    }

    public static byte[] Decrypt(String prikey, String[] params) throws Exception {
        if (params.length != 3) {
            throw new Exception(ErrorCode.ParamError);
        }
        return Decrypt(Helper.hexToBytes(prikey), Helper.hexToBytes(params[0]), Helper.hexToBytes(params[1]), Helper.hexToBytes(params[2]), 32);
    }

    public static byte[] Decrypt(byte[] prikey, byte[] IV, byte[] key_cxt, byte[] ciphertext) throws Exception {
        return Decrypt(prikey, IV, key_cxt, ciphertext, 32);
    }

    public static byte[] Decrypt(byte[] prikey, byte[] IV, byte[] key_cxt, byte[] ciphertext, int keylen) throws Exception {
        com.github.ontio.account.Account account = new com.github.ontio.account.Account(prikey, signatureScheme);

        ECNamedCurveParameterSpec spec = ECNamedCurveTable.getParameterSpec((String) curveParaSpec[0]);
        ECDomainParameters ecDomain = new ECDomainParameters(spec.getCurve(), spec.getG(), spec.getN());
        AsymmetricCipherKeyPair keys = new AsymmetricCipherKeyPair(
                null,
                new ECPrivateKeyParameters(((BCECPrivateKey) account.getPrivateKey()).getD(), ecDomain));

        byte[] out = key_cxt;
        ECIESKeyEncapsulation kem = new ECIESKeyEncapsulation(new KDF2BytesGenerator(new SHA1Digest()), new SecureRandom());
        KeyParameter key1;

        kem.init(keys.getPrivate());
        key1 = (KeyParameter) kem.decrypt(out, keylen);

        byte[] plaintext;
        Cipher dec = Cipher.getInstance("AES/CBC/PKCS7Padding", "SC");
        Key key = new SecretKeySpec(key1.getKey(), "AES");
        dec.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(IV));
        plaintext = dec.doFinal(ciphertext);

        return plaintext;
    }

    public static String getRandomString(int length) {
        String KeyStr = "ABCDEF0123456789";
        StringBuffer sb = new StringBuffer();
        int len = KeyStr.length();
        for (int i = 0; i < length; i++) {
            sb.append(KeyStr.charAt((int) Math.round(Math.random() * (len - 1))));
        }
        return sb.toString();
    }
}
