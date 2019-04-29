package com.raistone.wallet.sealwallet.utils;


import java.math.BigInteger;

public class Test {


    public static void main(String[] args) {




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
