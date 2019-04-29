package com.raistone.wallet.sealwallet.entity;

public class UpateWalletInfo {
    private String walletName;

    public UpateWalletInfo(String walletName) {
        this.walletName = walletName;
    }

    public String getMessage() {
        return walletName;
    }

    public void setMessage(String walletName) {

        this.walletName = walletName;
    }
}
