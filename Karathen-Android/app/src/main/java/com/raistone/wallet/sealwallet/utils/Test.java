package com.raistone.wallet.sealwallet.utils;


import com.raistone.wallet.sealwallet.daoutils.ChainAddressDaoUtils;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class Test {


    public static void main(String[] args) {


       /* String ss = "74e01e27e49718311c387bf6d0793cc27a3ed7ee59b1f29b1db085c10515d6df";
        String ssq = "f97474bd1e4019011a3f1d38b74c15ef27ccf38bc67eb8553ed7bf20b09d0e33";


        System.out.println(ss.length());


        System.out.println(ssq.length());

        String str = "0xadmin";
        boolean b = str.startsWith("0x");//true


        System.out.println(b);

        if(b) {
            String data = str.substring(2);
            System.out.println(data);
        }
*/
        /*String file=" /ssdfsdfsd/sdfsdfsdf/123123.png";

        String substring = file.substring(file.lastIndexOf("/")+1);

        System.out.println(substring);

        List<String> strings=new ArrayList<>();

        strings.add("https://www.baidu.com");
       strings.add("https://www.tmall.com");
        *//*      strings.add("https://www.taobao.com");*//*

        StringBuffer stringBuffer=new StringBuffer();
        for(int i=0;i<strings.size();i++){
            if(i>=strings.size()-1){
                stringBuffer.append(strings.get(i));
            }else {
                stringBuffer.append(strings.get(i) + ",");
            }
        }

        System.out.println(stringBuffer.toString());


        stringBuffer.lastIndexOf(",");*/
       /* String ss="0x23846238828167cf334e759b38a9e56eeb601d588550c15aec3d0789e6d1d287";

        if(ss.startsWith("0x")){
            ss=ss.substring(2);
        }

        System.out.println(ss);


        boolean valid = isValid("0x7fbA934b13B3DE602B49607ac54f2b9A77f1c3a2");

        System.out.println(valid);*/

       String ss="1.0.4";
        ss=ss.replace(".","");
        System.out.println(ss);

    }

    private static String ICAP_XE_PREFIX = "XE";
    private static String IBAN_SCHEME = "iban:";
    private static String IBAN_MOD = "97";

    public static boolean isValid(String icapAddress) {
        if (!icapAddress.startsWith("iban:XE") || icapAddress.length() != 40) {
            return false;
        }
        String base10Str = "";
        for (Character c : icapAddress.substring(9).toCharArray()) {
            base10Str += new BigInteger(c.toString(), 36);
        }
        for (Character c : icapAddress.substring(5, 9).toCharArray()) {
            base10Str += new BigInteger(c.toString(), 36);
        }
        Integer checkSum
                = (new BigInteger(base10Str)).mod(new BigInteger(IBAN_MOD)).intValue();
        return checkSum == 1;
    }

    public static String buildICAP(String ethAddress) {
        if (!ethAddress.startsWith("0x") || ethAddress.length() != 42) {
            throw new IllegalArgumentException("Invalid ethereum address.");
        }
        BigInteger ethInt = new BigInteger(ethAddress.substring(2), 16);
        String base36Addr = ethInt.toString(36).toUpperCase();
        String checkAddr = base36Addr + ICAP_XE_PREFIX + "00";
        String base10Str = "";
        for (Character c : checkAddr.toCharArray()) {
            base10Str += new BigInteger(c.toString(), 36);
        }
        Integer checkSum = 98
                - (new BigInteger(base10Str)).mod(new BigInteger(IBAN_MOD)).intValue();
        String icapAddress = IBAN_SCHEME + ICAP_XE_PREFIX
                + checkSum.toString() + base36Addr;
        return icapAddress;
    }

}
