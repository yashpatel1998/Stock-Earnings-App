package com.masquerade.app.stockearnings.adapters;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.masquerade.app.stockearnings.R;
import com.masquerade.app.stockearnings.holders.*;
import com.masquerade.app.stockearnings.models.Stock;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Locale;

public class StockCardRecyclerViewAdapter extends RecyclerView.Adapter<StockCardRecyclerViewHolder> {


    Context currentContext;
    ArrayList<Stock> stock;

    public StockCardRecyclerViewAdapter(Context currentContext,
                                        ArrayList<Stock> stock) {
        this.currentContext = currentContext;
        this.stock = stock;
    }

    @NonNull
    @Override
    public StockCardRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View stockRecyclerView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.stock_info_card, null);

        return new StockCardRecyclerViewHolder(stockRecyclerView);
    }

    @Override
    public void onBindViewHolder(@NonNull StockCardRecyclerViewHolder holder, int i) {
        holder.getStockName().setText(stock.get(i).getStockName());
        holder.getStockISIN().setText(stock.get(i).getISIN_Number());
        holder.getStockProfit().setText(String.format(Locale.ENGLISH, "%.2f",
                stock.get(i).getNetProfit()));
        holder.getStockQuantityPurchased().setText(String.valueOf(stock.get(i).getQuantityBought()));
        holder.getStockQuantityReceived().setText(String.valueOf(stock.get(i).getQuantityReceived()));
        holder.getStockCurrentPrice().setText(String.format(Locale.ENGLISH, "%.2f",
                stock.get(i).getCurrentPrice()));
        holder.getStockPurchasePrice().setText(String.format(Locale.ENGLISH, "%.2f",
                stock.get(i).getPurchasePrice()));
    }

    @Override
    public int getItemCount() {
        return stock.size();
    }
}
