package com.masquerade.app.stockearnings;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.masquerade.app.stockearnings.adapters.StockCardRecyclerViewAdapter;
import com.masquerade.app.stockearnings.models.Stock;

import java.util.ArrayList;
import java.util.Locale;

import com.masquerade.app.stockearnings.activities.AddStockActivity;
import com.masquerade.app.stockearnings.utilities.StockDatabaseHelper;

public class MainActivity extends AppCompatActivity {

    StockDatabaseHelper stockDB;
    RecyclerView stockRecyclerView;
    StockCardRecyclerViewAdapter stockCardRecyclerViewAdapter;
    TextView netProfitTextView;
    FloatingActionButton addStockBUtton;
    TextView noStockEnteredByUser;
    public static ArrayList<Stock> stockData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        /*      TESTING THE DATABASE CREATION           */

        stockDB = new StockDatabaseHelper(this);

        /*      TESTING THE DATABASE CREATION           */


        stockRecyclerView = findViewById(R.id.stock_card_recyclerview);
        noStockEnteredByUser = findViewById(R.id.noStockEnteredByUser);
        stockRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        netProfitTextView = findViewById(R.id.profit_amount);

        addStockBUtton = findViewById(R.id.fab);
        addStockBUtton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addStockIntent = new Intent(MainActivity.this, AddStockActivity.class);
                startActivity(addStockIntent);
            }
        });

        if (stockDB.isEmpty()) {
            stockRecyclerView.setVisibility(View.GONE);
            noStockEnteredByUser.setVisibility(View.VISIBLE);
        } else {
            stockRecyclerView.setVisibility(View.VISIBLE);
            noStockEnteredByUser.setVisibility(View.GONE);
            stockData = stockDB.getStockFromDB();
            createRecyclerView();
        }

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        double netProfit = getNetProfit(stockData);
        netProfitTextView.setText(String.format(Locale.ENGLISH, "%.2f", netProfit));
        stockCardRecyclerViewAdapter = new StockCardRecyclerViewAdapter(this, stockData);
        stockRecyclerView.setAdapter(stockCardRecyclerViewAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stockDB.close();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (stockDB.isEmpty()) {
            stockRecyclerView.setVisibility(View.GONE);
            noStockEnteredByUser.setVisibility(View.VISIBLE);
        } else {
            stockRecyclerView.setVisibility(View.VISIBLE);
            noStockEnteredByUser.setVisibility(View.GONE);
            stockData = stockDB.getStockFromDB();
            createRecyclerView();
        }
    }

    private double getNetProfit(ArrayList<Stock> stockList) {
        double prof = 0.0;
        for (int i = 0; i < stockList.size(); i++) {
            prof += stockList.get(i).getNetProfit();
        }
        return prof;
    }

    public void createRecyclerView() {
        double netProfit = getNetProfit(stockData);
        netProfitTextView.setText(String.format(Locale.ENGLISH, "%.2f", netProfit));
        stockCardRecyclerViewAdapter = new StockCardRecyclerViewAdapter(this, stockData);
        stockRecyclerView.setAdapter(stockCardRecyclerViewAdapter);
    }


}