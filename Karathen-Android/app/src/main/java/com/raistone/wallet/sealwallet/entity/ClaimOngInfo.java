package com.raistone.wallet.sealwallet.entity;

import java.util.List;

public class ClaimOngInfo {


    /**
     * Action : QueryAddressBalance
     * Error : 0
     * Desc : SUCCESS
     * Version : 1.0
     * Result : [{"Balance":"0.49740018","AssetName":"ong"},{"Balance":"0.000098375","AssetName":"waitboundong"},{"Balance":"0.166165425","AssetName":"unboundong"},{"Balance":"5","AssetName":"ont"},{"Balance":"0","AssetName":"pumpkin08"},{"Balance":"0","AssetName":"pumpkin07"},{"Balance":"0","AssetName":"pumpkin06"},{"Balance":"0","AssetName":"pumpkin05"},{"Balance":"0","AssetName":"pumpkin04"},{"Balance":"0","AssetName":"pumpkin03"},{"Balance":"0","AssetName":"pumpkin02"},{"Balance":"0","AssetName":"pumpkin01"},{"Balance":"0","AssetName":"totalpumpkin"},{"Balance":"0","AssetName":"HP"},{"Balance":"0","AssetName":"YLTK"},{"Balance":"0","AssetName":"DICE"}]
     */

    private String Action;
    private int Error;
    private String Desc;
    private String Version;
    private List<ResultBean> Result;

    public String getAction() {
        return Action;
    }

    public void setAction(String Action) {
        this.Action = Action;
    }

    public int getError() {
        return Error;
    }

    public void setError(int Error) {
        this.Error = Error;
    }

    public String getDesc() {
        return Desc;
    }

    public void setDesc(String Desc) {
        this.Desc = Desc;
    }

    public String getVersion() {
        return Version;
    }

    public void setVersion(String Version) {
        this.Version = Version;
    }

    public List<ResultBean> getResult() {
        return Result;
    }

    public void setResult(List<ResultBean> Result) {
        this.Result = Result;
    }

    public static class ResultBean {
        /**
         * Balance : 0.49740018
         * AssetName : ong
         */

        private String Balance;
        private String AssetName;

        public String getBalance() {
            return Balance;
        }

        public void setBalance(String Balance) {
            this.Balance = Balance;
        }

        public String getAssetName() {
            return AssetName;
        }

        public void setAssetName(String AssetName) {
            this.AssetName = AssetName;
        }
    }
}
