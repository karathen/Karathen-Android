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

package com.github.ontio.core.program;

import com.github.ontio.common.Common;
import com.github.ontio.common.ErrorCode;
import com.github.ontio.common.Helper;
import com.github.ontio.core.scripts.ScriptBuilder;
import com.github.ontio.core.scripts.ScriptOp;
import com.github.ontio.crypto.ECC;
import com.github.ontio.crypto.KeyType;
import com.github.ontio.io.BinaryReader;
import com.github.ontio.sdk.exception.SDKException;

import org.spongycastle.math.ec.ECPoint;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * @Description:
 * @date 2018/6/10
 */
public class Program {
    public static byte[] ProgramFromParams(byte[][] sigData) throws IOException, SDKException {
        ScriptBuilder sb = new ScriptBuilder();

        Arrays.sort(sigData, new Comparator<byte[]>() {
            @Override
            public int compare(byte[] a, byte[] b) {
                return Helper.toHexString(a).compareTo(Helper.toHexString(b));
            }
        });
        for (byte[] sig : sigData) {
            sb.emitPushByteArray(sig);
        }
        return sb.toArray();
    }
    public static byte[] ProgramFromPubKey(byte[] publicKey) throws Exception {
        ScriptBuilder sb = new ScriptBuilder();
        sb.emitPushByteArray(publicKey);
        sb.add(ScriptOp.OP_CHECKSIG);
        return sb.toArray();
    }
    public static byte[] ProgramFromMultiPubKey(int m, byte[]... publicKeys) throws Exception {
        int n = publicKeys.length;

        if (m <= 0 || m > n || n > Common.MULTI_SIG_MAX_PUBKEY_SIZE) {
            throw new SDKException(ErrorCode.ParamError);
        }
        try (ScriptBuilder sb = new ScriptBuilder()) {
            sb.emitPushInteger(BigInteger.valueOf(m));
            publicKeys = sortPublicKeys(publicKeys);
            for (byte[] publicKey : publicKeys) {
                sb.emitPushByteArray(publicKey);
            }
            sb.emitPushInteger(BigInteger.valueOf(publicKeys.length));
            sb.add(ScriptOp.OP_CHECKMULTISIG);
            return sb.toArray();
        }
    }

    public static byte[][] sortPublicKeys(byte[]... publicKeys){
        Arrays.sort(publicKeys, new Comparator<byte[]>() {
            @Override
            public int compare(byte[] o1, byte[] o2) {
                if (KeyType.fromPubkey(o1).getLabel() != KeyType.fromPubkey(o2).getLabel()) {
                    return KeyType.fromPubkey(o1).getLabel() >= KeyType.fromPubkey(o2).getLabel() ? 1 : -1;
                }
                switch (KeyType.fromPubkey(o1)) {
                    case SM2:
                        byte[] p = new byte[33];
                        System.arraycopy(o1, 2, p, 0, p.length);
                        o1 = p;
                        byte[] p2 = new byte[33];
                        System.arraycopy(o2, 2, p2, 0, p2.length);
                        o2 = p2;
                        ECPoint smPk1 = ECC.sm2p256v1.getCurve().decodePoint(o1);
                        ECPoint smPk2 = ECC.sm2p256v1.getCurve().decodePoint(o2);
                        return ECC.compare(smPk1, smPk2);
                    case ECDSA:
                        ECPoint pk1 = ECC.secp256r1.getCurve().decodePoint(o1);
                        ECPoint pk2 = ECC.secp256r1.getCurve().decodePoint(o2);
                        return ECC.compare(pk1, pk2);
                    case EDDSA:
                        //TODO
                        return Helper.toHexString(o1).compareTo(Helper.toHexString(o1));
                    default:
                        return Helper.toHexString(o1).compareTo(Helper.toHexString(o1));
                }
            }
        });
        return publicKeys;
    }

    public static byte[][] getParamInfo(byte[] program) {
        ByteArrayInputStream bais = new ByteArrayInputStream(program);
        BinaryReader br = new BinaryReader(bais);
        List<byte[]> list = new ArrayList();
        while(true){
            try {
                list.add(readBytes(br));
            } catch (IOException e) {
                break;
            }
        }
        byte[][] res = new byte[list.size()][];
        for(int i=0;i < list.size(); i++){
            res[i] = list.get(i);
        }
        return res;
    }

    public static byte[] readBytes(BinaryReader br) throws IOException {

        byte code = br.readByte();
        long keyLen;
        if(code == ScriptOp.OP_PUSHDATA4.getByte()){
            int temp;
            temp = br.readInt();
            keyLen = Long.valueOf(temp);
        } else if(code == ScriptOp.OP_PUSHDATA2.getByte()){
            int temp;
            temp = br.readShort();
            keyLen = Long.valueOf(temp);
        }else if(code == ScriptOp.OP_PUSHDATA1.getByte()) {
            int temp;
            temp = br.readByte();
            keyLen = Long.valueOf(temp);
        }else if(code <= ScriptOp.OP_PUSHBYTES75.getByte() && code >= ScriptOp.OP_PUSHBYTES1.getByte()){
            keyLen = Long.valueOf(code) - Long.valueOf(ScriptOp.OP_PUSHBYTES1.getByte()) + 1;
        }else{
            keyLen = 0;
        }
        byte[] res = br.readBytes((int) keyLen);
        return res;
    }

    public static ProgramInfo getProgramInfo(byte[] program) throws IOException {
        ProgramInfo info = new ProgramInfo();
        if(program.length <= 2) {

        }
        byte end = program[program.length - 1];
        byte[] temp = new byte[program.length - 1];
        System.arraycopy(program,0,temp,0,program.length - 1);
        ByteArrayInputStream bais = new ByteArrayInputStream(temp);
        BinaryReader reader = new BinaryReader(bais);
        if(end == ScriptOp.OP_CHECKSIG.getByte()){
            try {
                byte[] publicKey = readBytes(reader);
                info.setPublicKey(new byte[][]{publicKey});
                info.setM((short)1);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else if(end == ScriptOp.OP_CHECKMULTISIG.getByte()) {
            short m = 0;
            int len = program[program.length - 2] - ScriptOp.OP_PUSH1.getByte() +1;
            try {
                m = (byte)(reader.readByte() - ScriptOp.OP_PUSH1.getByte()+1);
            } catch (Exception e) {
                e.printStackTrace();
            }
            byte[][] pub = new byte[len][];
            for(int i =0; i<(int)len; i++){
                pub[i] = reader.readVarBytes();
            }
            info.setPublicKey(pub);
            info.setM(m);
        }
        return info;
    }

    public static short readNum(BinaryReader reader) throws IOException, SDKException {
        ScriptOp code = readOpCode(reader);
        if(code == ScriptOp.OP_PUSH0){
            readOpCode(reader);
            return 0;
        }else {
            int num = (int)code.getByte() - (int)ScriptOp.OP_PUSH1.getByte() + 1;
            if(num >= 1 && num <= 16) {
                readOpCode(reader);
                return (short)num;
            }
        }
        byte[] buff = readBytes(reader);
        BigInteger bint = Helper.BigIntFromNeoBytes(buff);
        long num = bint.longValue();
        if(num > Short.MAX_VALUE || num < 16){
            throw new SDKException(ErrorCode.ParamErr("num is wrong"));
        }
        return (short)num;
    }
    public static ScriptOp readOpCode(BinaryReader reader) throws IOException {
        return ScriptOp.valueOf(reader.readByte());
    }

    public static byte[] programFromParams(byte[][] sigs) throws SDKException {
        ScriptBuilder builder = new ScriptBuilder();
        for(byte[] sigdata : sigs){
            builder.emitPushByteArray(sigdata);
        }
        return builder.toArray();
    }
    public static byte[] programFromPubKey(byte[] publicKey) throws SDKException {
        ScriptBuilder builder = new ScriptBuilder();
        builder.emitPushByteArray(publicKey);
        builder.add(ScriptOp.OP_CHECKSIG);
        return builder.toArray();
    }
}

