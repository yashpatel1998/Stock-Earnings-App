package com.masquerade.app.stockearnings.holders;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.masquerade.app.stockearnings.R;

public class StockCardRecyclerViewHolder extends RecyclerView.ViewHolder {

    private TextView stockName, stockISIN, stockProfit, stockQuantityPurchased;
    private TextView stockQuantityReceived, stockPurchasePrice, stockCurrentPrice;

    public TextView getStockName() {
        return stockName;
    }

    public void setStockName(TextView stockName) {
        this.stockName = stockName;
    }

    public TextView getStockISIN() {
        return stockISIN;
    }

    public void setStockISIN(TextView stockISIN) {
        this.stockISIN = stockISIN;
    }

    public TextView getStockProfit() {
        return stockProfit;
    }

    public void setStockProfit(TextView stockProfit) {
        this.stockProfit = stockProfit;
    }

    public TextView getStockQuantityPurchased() {
        return stockQuantityPurchased;
    }

    public void setStockQuantityPurchased(TextView stockQuantityPurchased) {
        this.stockQuantityPurchased = stockQuantityPurchased;
    }

    public TextView getStockQuantityReceived() {
        return stockQuantityReceived;
    }

    public void setStockQuantityReceived(TextView stockQuantityReceived) {
        this.stockQuantityReceived = stockQuantityReceived;
    }

    public TextView getStockCurrentPrice() {
        return stockCurrentPrice;
    }

    public void setStockCurrentPrice(TextView stockCurrentPrice) {
        this.stockCurrentPrice = stockCurrentPrice;
    }

    public TextView getStockPurchasePrice() {
        return stockPurchasePrice;
    }

    public void setStockPurchasePrice(TextView stockPurchasePrice) {
        this.stockPurchasePrice = stockPurchasePrice;
    }

    public StockCardRecyclerViewHolder(@NonNull View itemView) {
        super(itemView);
        this.stockQuantityPurchased = itemView.findViewById(R.id.stock_quantity_purchased);
        this.stockQuantityReceived = itemView.findViewById(R.id.stock_quantity_received);
        this.stockPurchasePrice = itemView.findViewById(R.id.stock_purchase_price);
        this.stockCurrentPrice = itemView.findViewById(R.id.stock_current_price);
        this.stockProfit = itemView.findViewById(R.id.stock_profit);
        this.stockName = itemView.findViewById(R.id.stock_name);
        this.stockISIN = itemView.findViewById(R.id.stock_ISIN);
    }
}
