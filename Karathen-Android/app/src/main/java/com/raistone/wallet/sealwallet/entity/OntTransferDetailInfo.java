package com.raistone.wallet.sealwallet.entity;

import java.util.List;

public class OntTransferDetailInfo {


    /**
     * GasConsumed : 10000000
     * State : 1
     * TxHash : 283fc132e4cdcdc4a4933cf51e48b5938b23f254bc9c47c1f2921d135381bc9b
     * Notify : [{"States":["transfer","AURRtYmGrrg1hLiZWcWf62AiZN1SG5s7Vk","AURRtYmGrrg1hLiZWcWf62AiZN1SG5s7Vk",1],"ContractAddress":"0100000000000000000000000000000000000000"},{"States":["transfer","AURRtYmGrrg1hLiZWcWf62AiZN1SG5s7Vk","AFmseVrdL9f9oyCzZefL9tG6UbviEH9ugK",10000000],"ContractAddress":"0200000000000000000000000000000000000000"}]
     */

    private int GasConsumed;
    private int State;
    private String TxHash;
    private List<NotifyBean> Notify;

    public int getGasConsumed() {
        return GasConsumed;
    }

    public void setGasConsumed(int GasConsumed) {
        this.GasConsumed = GasConsumed;
    }

    public int getState() {
        return State;
    }

    public void setState(int State) {
        this.State = State;
    }

    public String getTxHash() {
        return TxHash;
    }

    public void setTxHash(String TxHash) {
        this.TxHash = TxHash;
    }

    public List<NotifyBean> getNotify() {
        return Notify;
    }

    public void setNotify(List<NotifyBean> Notify) {
        this.Notify = Notify;
    }

    public static class NotifyBean {
        /**
         * States : ["transfer","AURRtYmGrrg1hLiZWcWf62AiZN1SG5s7Vk","AURRtYmGrrg1hLiZWcWf62AiZN1SG5s7Vk",1]
         * ContractAddress : 0100000000000000000000000000000000000000
         */

        private String ContractAddress;
        private List<String> States;

        public String getContractAddress() {
            return ContractAddress;
        }

        public void setContractAddress(String ContractAddress) {
            this.ContractAddress = ContractAddress;
        }

        public List<String> getStates() {
            return States;
        }

        public void setStates(List<String> States) {
            this.States = States;
        }
    }
}
