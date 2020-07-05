package com.masquerade.app.stockearnings;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.TextView;

import com.masquerade.app.stockearnings.adapters.StockCardRecyclerViewAdapter;
import com.masquerade.app.stockearnings.models.Stock;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    RecyclerView stockRecyclerView;
    StockCardRecyclerViewAdapter stockCardRecyclerViewAdapter;
    TextView netProfitTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        stockRecyclerView = findViewById(R.id.stock_card_recyclerview);
        stockRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        /*
         * @todo
         *   Create a dedicated user class which will do the following
         *       - Fetch user subscribed stocks
         *       - Create an account in firebase so that all data stays online
         *       - Have support for google account sign in
         *       - Store the data online as well as local storage
         * */
        netProfitTextView = (TextView) findViewById(R.id.profit_amount);
        ArrayList<Stock> stockList = getUserSubscribedStockList();
        double netProfit = getNetProfit(stockList);
        netProfitTextView.setText(String.format(Locale.ENGLISH, "%.2f", netProfit));
        stockCardRecyclerViewAdapter = new StockCardRecyclerViewAdapter(this, stockList);
        stockRecyclerView.setAdapter(stockCardRecyclerViewAdapter);
    }

    private double getNetProfit(ArrayList<Stock> stockList) {
        double prof = 0.0;
        for (int i = 0; i < stockList.size(); i++) {
            prof += stockList.get(i).getNetProfit();
        }
        return prof;
    }

    protected ArrayList<Stock> getUserSubscribedStockList() {
        ArrayList<Stock> temp = new ArrayList<>();
        temp.add(new Stock("INE030A01027", "Hindustan Uniliver", 183.57
                , 0, 100, 183.62));
        temp.add(new Stock("INE467B01029", "TCS Ltd", 1012.02
                , 30, 10, 2156.5));
        temp.add(new Stock("INE002A01018", "Reliance Industries", 1043.8
                , 0, 25, 900));
        return temp;
    }
}