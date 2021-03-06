package com.masquerade.app.stockearnings;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.masquerade.app.stockearnings.adapters.StockCardRecyclerViewAdapter;
import com.masquerade.app.stockearnings.models.Stock;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import com.masquerade.app.stockearnings.activities.AddStockActivity;
import com.masquerade.app.stockearnings.utilities.StockDatabaseHelper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class MainActivity extends AppCompatActivity {

    StockDatabaseHelper stockDB;
    ImageButton refreshButton;
    RecyclerView stockRecyclerView;
    StockCardRecyclerViewAdapter stockCardRecyclerViewAdapter;
    TextView netProfitTextView;
    FloatingActionButton addStockBUtton;
    TextView noStockEnteredByUser;
    ProgressDialog fetchNewCurrentPriceProgressbar;
    public static ArrayList<Stock> stockData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StockCurrentPriceFetcher currentPriceFetcher = new StockCurrentPriceFetcher();

        stockDB = new StockDatabaseHelper(this);

        stockRecyclerView = findViewById(R.id.stock_card_recyclerview);
        noStockEnteredByUser = findViewById(R.id.noStockEnteredByUser);
        stockRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        netProfitTextView = findViewById(R.id.profit_amount);
        refreshButton = findViewById(R.id.refresh_button);

        addStockBUtton = findViewById(R.id.fab);
        addStockBUtton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addStockIntent = new Intent(MainActivity.this, AddStockActivity.class);
                startActivity(addStockIntent);
            }
        });

        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StockCurrentPriceFetcher currentPriceFetcher = new StockCurrentPriceFetcher();
                currentPriceFetcher.execute();
                createRecyclerView();
                netProfitTextView.setText(String.format(Locale.ENGLISH, "%.2f", getNetProfit(stockData)));
            }
        });

        if (stockDB.isEmpty()) {
            stockRecyclerView.setVisibility(View.GONE);
            noStockEnteredByUser.setVisibility(View.VISIBLE);
        } else {
            stockRecyclerView.setVisibility(View.VISIBLE);
            noStockEnteredByUser.setVisibility(View.GONE);
            stockData = stockDB.getStockFromDB();
            currentPriceFetcher.execute();
            createRecyclerView();
            netProfitTextView.setText(String.format(Locale.ENGLISH, "%.2f", getNetProfit(stockData)));
        }

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        double netProfit = getNetProfit(stockData);
        netProfitTextView.setText(String.format(Locale.ENGLISH, "%.2f", netProfit));
        stockCardRecyclerViewAdapter = new StockCardRecyclerViewAdapter(this, stockData);
        stockRecyclerView.setAdapter(stockCardRecyclerViewAdapter);
        netProfitTextView.setText(String.format(Locale.ENGLISH, "%.2f", getNetProfit(stockData)));

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
            netProfitTextView.setText(String.format(Locale.ENGLISH, "%.2f", getNetProfit(stockData)));

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
        stockCardRecyclerViewAdapter = new StockCardRecyclerViewAdapter(this, stockData);
        stockRecyclerView.setAdapter(stockCardRecyclerViewAdapter);
    }

    private class StockCurrentPriceFetcher extends AsyncTask<Void, Void, Void> {

        private final String baseUrl = "https://m.bseindia.com/StockReach.aspx?scripcd=";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            fetchNewCurrentPriceProgressbar = new ProgressDialog(MainActivity.this);
            fetchNewCurrentPriceProgressbar.setMessage("Updating Current Prices\nPlease Wait ...");
            fetchNewCurrentPriceProgressbar.setIndeterminate(false);
            fetchNewCurrentPriceProgressbar.setCancelable(false);
            fetchNewCurrentPriceProgressbar.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                for (int i = 0; i < stockData.size(); i++) {
                    Stock singleStock = stockData.get(i);
                    String stockURL = baseUrl + singleStock.getScripCode();
                    Document stockWebsite = Jsoup.connect(stockURL).get();
                    String stockCurrentValue = stockWebsite.getElementById("strongCvalue").text();
                    Log.d("MainAct -doInBackground", "Current Value : " + stockCurrentValue);
                    if (stockCurrentValue.isEmpty()) {
                        throw new IOException("Unable to fetch details");
                    } else {
                        Log.d("doInBackground", "Stock data fetched");
                        stockData.get(i).setCurrentPrice(Double.parseDouble(stockCurrentValue));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try {
                for (int i = 0; i < stockData.size(); i++) {
                    String scripCode = stockData.get(i).getScripCode();
                    String companyName = stockData.get(i).getStockName();
                    double purchasePrice = stockData.get(i).getPurchasePrice();
                    double currentPrice = stockData.get(i).getCurrentPrice();
                    int quantityBought = stockData.get(i).getQuantityBought();
                    int quantityReceived = stockData.get(i).getQuantityReceived();
                    if (!stockDB.updateCurrentPrice(scripCode, companyName, purchasePrice,
                            currentPrice, quantityBought, quantityReceived)) {
                        throw new Exception("Error during updating current price");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            fetchNewCurrentPriceProgressbar.dismiss();
        }
    }

}


/*
 *
 * @todo
 *       1) Single implementation for AsyncTask used in AddStockActivity and MainActivity
 *           - Create a separate class for the same.
 *
 * @possible_features
 *       1) Delete Stock option from the database and list
 *       2) Google sign in so everything is stored in the cloud
 *           - When user sends request to get data the server must
 *               # Fetch new current prices from the url and then send the data to user
 *       3) SearchBar
 *       4) Keep track of earnings like daily, weekly and monthly
 *           - Have three bars above the app selection interface
 *       5) Machine Learning to predict stock prices
 *
 *
 * */