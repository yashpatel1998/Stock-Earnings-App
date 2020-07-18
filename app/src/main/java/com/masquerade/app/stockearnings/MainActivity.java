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

public class MainActivity extends AppCompatActivity {

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

        stockRecyclerView = findViewById(R.id.stock_card_recyclerview);
        noStockEnteredByUser = findViewById(R.id.noStockEnteredByUser);
        stockRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        netProfitTextView = findViewById(R.id.profit_amount);

        /*      Floating action button task                     */
        addStockBUtton = findViewById(R.id.fab);
        addStockBUtton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addStockIntent = new Intent(MainActivity.this, AddStockActivity.class);
                startActivityForResult(addStockIntent, 999);
            }
        });
        /*      Floating action button task                     */

        /*
         * @todo
         *   Create a dedicated user class which will do the following
         *       - Fetch user subscribed stocks
         *       - Create an account in firebase so that all data stays online
         *       - Have support for google account sign in
         *       - Store the data online as well as local storage
         * */

        /*      To populate the recycler view with card         */
        if (stockData.isEmpty()) {
            stockRecyclerView.setVisibility(View.GONE);
            noStockEnteredByUser.setVisibility(View.VISIBLE);
        } else {
            stockRecyclerView.setVisibility(View.VISIBLE);
            noStockEnteredByUser.setVisibility(View.GONE);
            createRecyclerView();
        }
        /*      To populate the recycler view with card         */


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 999 && resultCode == RESULT_OK) {
            Stock newStock = (Stock) data.getSerializableExtra("newStock");
            Log.i("New Stock Info", newStock.getStockName());
            stockData.add(newStock);
        }
    }

    private double getNetProfit(ArrayList<Stock> stockList) {
        double prof = 0.0;
        for (int i = 0; i < stockList.size(); i++) {
            prof += stockList.get(i).getNetProfit();
        }
        return prof;
    }

    protected void getUserSubscribedStockList() {
        stockData.add(new Stock("500696 ", "Hindustan Uniliver", 183.57
                , 0, 100, 183.62));
        stockData.add(new Stock("532540", "TCS Ltd", 1012.02
                , 30, 10, 2156.5));
        stockData.add(new Stock("500325 ", "Reliance Industries", 1043.8
                , 0, 25, 900));
    }

    public void createRecyclerView() {
        double netProfit = getNetProfit(stockData);
        netProfitTextView.setText(String.format(Locale.ENGLISH, "%.2f", netProfit));
        stockCardRecyclerViewAdapter = new StockCardRecyclerViewAdapter(this, stockData);
        stockRecyclerView.setAdapter(stockCardRecyclerViewAdapter);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        double netProfit = getNetProfit(stockData);
        netProfitTextView.setText(String.format(Locale.ENGLISH, "%.2f", netProfit));
        stockCardRecyclerViewAdapter = new StockCardRecyclerViewAdapter(this, stockData);
        stockRecyclerView.setAdapter(stockCardRecyclerViewAdapter);
    }
}