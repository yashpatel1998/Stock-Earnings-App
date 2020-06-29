package com.masquerade.app.stockearnings.models;

public class Stock {
    private String ISIN_Number;
    private String stockName;
    private double purchasePrice;
    private int quantityReceived;
    private int quantityBought;
    private double netProfit;
    private double netPurchasePrice;

    public String getISIN_Number() {
        return ISIN_Number;
    }

    public void setISIN_Number(String ISIN_Number) {
        this.ISIN_Number = ISIN_Number;
    }

    public String getStockName() {
        return stockName;
    }

    public void setStockName(String stockName) {
        this.stockName = stockName;
    }

    public double getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(double purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public int getQuantityReceived() {
        return quantityReceived;
    }

    public void setQuantityReceived(int quantityReceived) {
        this.quantityReceived = quantityReceived;
    }

    public int getQuantityBought() {
        return quantityBought;
    }

    public void setQuantityBought(int quantityBought) {
        this.quantityBought = quantityBought;
    }

    public double getNetProfit() {
        return netProfit;
    }

    public void setNetProfit(double netProfit) {
        this.netProfit = netProfit;
    }

    public double getNetPurchasePrice() {
        return netPurchasePrice;
    }

    public void setNetPurchasePrice(double netPurchasePrice) {
        this.netPurchasePrice = netPurchasePrice;
    }

    public void calculateNetPurchasePrice() {

    }
}
