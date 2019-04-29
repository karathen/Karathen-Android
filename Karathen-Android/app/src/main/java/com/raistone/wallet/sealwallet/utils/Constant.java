package com.raistone.wallet.sealwallet.utils;


import com.github.ontio.network.rest.http;

public class Constant {
    public static final class Device {
        public static final String factory = "Xiaomi、小米、xiaomi、Meizu、魅族、honor";
    }

    public static final class IntentKey {
        public static String WEB_TITLE = "web_title";
        public static String WEB_URL = "web_url";
        public static String WORD_MNEMONICS = "";
        public static String MNEMONICS_LIST = "";
    }

    public static final class SealWebUrl {
        public static String SEAL_OFFICIAL_WEBSITE_URL = "";
        public static String SERVICE_AGREEMENT_URL = "";
        public static String SERVIE_AGREEMENT_URL_EN = "";
        public static String PRIVACY_POLICY_URL = "";
        public static String PRIVACY_POLICY_URL_EN = "";
    }

    public static final class HttpServiceUrl {
        public static String MAIN_URL = "";
        public static String BASE_MAIN_URL = "";

        public static String ROPSTEN_URL = "";
    }

    public static final class ETHParams {

        public static String BASE_MAIN_URL = "";
        public static String GAS_PRICE = "";

        public static String BLOCK_NUMBER = "";

        public static String FETCH_NONCE = "";
    }

    public static final class ONTParams {
        public static String ONT_BASE_URL = "";
        public static String ONT_BALANCE_URL = "";
        public static String ONT_TRAN_URL="";
    }
    public static final class NEOParams {
        public static String NEO_BASE_URL = "";
    }
}
