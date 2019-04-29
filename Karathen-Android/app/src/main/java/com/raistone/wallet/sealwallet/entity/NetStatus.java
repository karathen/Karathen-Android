package com.raistone.wallet.sealwallet.entity;

public class NetStatus {
    private boolean netStatus; //网络状态 true  为可用， false 为不可用

    public NetStatus(boolean netStatus) {
        this.netStatus = netStatus;
    }

    public boolean isNetStatus() {
        return netStatus;
    }

    public void setNetStatus(boolean netStatus) {
        this.netStatus = netStatus;
    }
}
