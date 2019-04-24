package com.raistone.wallet.sealwallet.entity;

import java.math.BigDecimal;
import java.util.List;

public class PriceInfo {

    /**
     * id : 1
     * jsonrpc : 2.0
     * result : [{"cny_price":1378.2546698966,"price":[1378.2546698966,200.013738593],"symbol":"ETH","usd_price":200.013738593},{"cny_price":0.0611566164,"price":[0.0611566164,0.0088751112],"symbol":"TNC","usd_price":0.0088751112},{"cny_price":6.9148955098,"price":[6.9148955098,1.0034967652],"symbol":"USDT","usd_price":1.0034967652},{"cny_price":6.9053403904,"price":[6.9053403904,1.0021101164],"symbol":"GUSD","usd_price":1.0021101164},{"cny_price":6.8948902047,"price":[6.8948902047,1.0005935747],"symbol":"PAX","usd_price":1.0005935747},{"cny_price":0,"price":[0,0],"symbol":"WBT","usd_price":0},{"cny_price":0,"price":[0,0],"symbol":"WBA","usd_price":0},{"cny_price":0.0172125176,"price":[0.0172125176,0.0024978983],"symbol":"IFOOD","usd_price":0.0024978983},{"cny_price":0,"price":[0,0],"symbol":"CK","usd_price":0}]
     */

    private String id;
    private String jsonrpc;
    private List<ResultBean> result;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getJsonrpc() {
        return jsonrpc;
    }

    public void setJsonrpc(String jsonrpc) {
        this.jsonrpc = jsonrpc;
    }

    public List<ResultBean> getResult() {
        return result;
    }

    public void setResult(List<ResultBean> result) {
        this.result = result;
    }

    public static class ResultBean {
        /**
         * cny_price : 1378.2546698966
         * price : [1378.2546698966,200.013738593]
         * symbol : ETH
         * usd_price : 200.013738593
         */

        private BigDecimal cny_price;
        private String symbol;
        private BigDecimal usd_price;
        private List<BigDecimal> price;

        public BigDecimal getCny_price() {
            return cny_price;
        }

        public void setCny_price(BigDecimal cny_price) {
            this.cny_price = cny_price;
        }

        public String getSymbol() {
            return symbol;
        }

        public void setSymbol(String symbol) {
            this.symbol = symbol;
        }

        public BigDecimal getUsd_price() {
            return usd_price;
        }

        public void setUsd_price(BigDecimal usd_price) {
            this.usd_price = usd_price;
        }

        public List<BigDecimal> getPrice() {
            return price;
        }

        public void setPrice(List<BigDecimal> price) {
            this.price = price;
        }
    }
}
