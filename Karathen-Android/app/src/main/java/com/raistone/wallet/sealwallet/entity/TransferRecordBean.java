package com.raistone.wallet.sealwallet.entity;

/**
 * 消息
 */
public class TransferRecordBean {

    /**
     * blockNumber : 区块数
     * timeStamp : 时间 单位秒
     * hash : 交易hash
     * nonce : nonce
     * blockHash : 区块hahs
     * transactionIndex : 交易在区块中的下标
     * from : 来自地址（跟自己的地址一样则是转出）
     * to : 发给地址（跟自己的地址一样则是转入）
     * value : 转账金额
     * gas : 设置gas
     * gasPrice : 	设置gaslimit
     * isError : 是否错误 0转账成功 1转账失败
     * txreceipt_status :
     * input : 输入
     * contractAddress :合约地址
     * cumulativeGasUsed : 64764597
     * gasUsed : 转账使用gas
     * confirmations : 确认数
     * coinName : coinName
     * decimals : decimals
     * isReceive : isReceive 是否收款
     * realMoney : realMoney 可直接显示的金额
     * refundFEee : refundFEee 矿工费返回
     */
    private Integer wallet_id;
    private String blockNumber;
    private String timeStamp;
    private String hash;
    private String nonce;
    private String blockHash;
    private String transactionIndex;
    private String from;
    private String to;
    private String value;
    private String gas;
    private String gasPrice;
    private String isError;
    private String txreceipt_status;
    private String input;
    private String contractAddress;
    private String cumulativeGasUsed;
    private String gasUsed;
    private String confirmations;
    private String realMoney;
    private boolean isReceive;
    private String coinName;
    private Integer tokenId;
    private boolean refundFEee;
    private boolean isInTransfer;

    public boolean isInTransfer() {
        return isInTransfer;
    }

    public void setInTransfer(boolean inTransfer) {
        isInTransfer = inTransfer;
    }

    public boolean isRefundFEee() {
        return refundFEee;
    }

    public void setRefundFEee(boolean refundFEee) {
        this.refundFEee = refundFEee;
    }

    public Integer getTokenId() {
        return tokenId;
    }

    public void setTokenId(Integer tokenId) {
        this.tokenId = tokenId;
    }

    public Integer getWallet_id() {
        return wallet_id;
    }

    public void setWallet_id(Integer wallet_id) {
        this.wallet_id = wallet_id;
    }

    public String getCoinName() {
        return coinName;
    }

    public void setCoinName(String coinName) {
        this.coinName = coinName;
    }

    public boolean isReceive() {
        return isReceive;
    }

    public void setReceive(boolean receive) {
        isReceive = receive;
    }

    public String getRealMoney() {
        return realMoney;
    }

    public void setRealMoney(String realMoney) {
        this.realMoney = realMoney;
    }

    public String getBlockNumber() {
        return blockNumber;
    }

    public void setBlockNumber(String blockNumber) {
        this.blockNumber = blockNumber;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    public String getBlockHash() {
        return blockHash;
    }

    public void setBlockHash(String blockHash) {
        this.blockHash = blockHash;
    }

    public String getTransactionIndex() {
        return transactionIndex;
    }

    public void setTransactionIndex(String transactionIndex) {
        this.transactionIndex = transactionIndex;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getGas() {
        return gas;
    }

    public void setGas(String gas) {
        this.gas = gas;
    }

    public String getGasPrice() {
        return gasPrice;
    }

    public void setGasPrice(String gasPrice) {
        this.gasPrice = gasPrice;
    }

    public String getIsError() {
        return isError;
    }

    public void setIsError(String isError) {
        this.isError = isError;
    }

    public String getTxreceipt_status() {
        return txreceipt_status;
    }

    public void setTxreceipt_status(String txreceipt_status) {
        this.txreceipt_status = txreceipt_status;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public String getContractAddress() {
        return contractAddress;
    }

    public void setContractAddress(String contractAddress) {
        this.contractAddress = contractAddress;
    }

    public String getCumulativeGasUsed() {
        return cumulativeGasUsed;
    }

    public void setCumulativeGasUsed(String cumulativeGasUsed) {
        this.cumulativeGasUsed = cumulativeGasUsed;
    }

    public String getGasUsed() {
        return gasUsed;
    }

    public void setGasUsed(String gasUsed) {
        this.gasUsed = gasUsed;
    }

    public String getConfirmations() {
        return confirmations;
    }

    public void setConfirmations(String confirmations) {
        this.confirmations = confirmations;
    }
}
