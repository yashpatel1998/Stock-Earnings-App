package com.masquerade.app.stockearnings;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.masquerade.app.stockearnings.activities.AddStockActivity;
import com.masquerade.app.stockearnings.adapters.StockCardRecyclerViewAdapter;
import com.masquerade.app.stockearnings.asyncTask.StockDataFetcherAsyncTask;
import com.masquerade.app.stockearnings.models.Stock;
import com.masquerade.app.stockearnings.utilities.StockDatabaseHelper;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    StockDatabaseHelper stockDB;
    MaterialToolbar topAppBar;
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
        stockDB = new StockDatabaseHelper(this);

        stockRecyclerView = findViewById(R.id.stock_card_recyclerview);
        noStockEnteredByUser = findViewById(R.id.noStockEnteredByUser);
        stockRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        netProfitTextView = findViewById(R.id.profit_amount);
        topAppBar = findViewById(R.id.topAppBar);

        addStockBUtton = findViewById(R.id.fab);
        addStockBUtton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addStockIntent = new Intent(MainActivity.this, AddStockActivity.class);
                startActivity(addStockIntent);
            }
        });
        refreshRecyclerView(false, false);
        topAppBar.setOnMenuItemClickListener(new MaterialToolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.refresh_button: {
                        try {
                            if (stockData.isEmpty()) {
                                showToast("Nothing to refresh");
                            } else {
                                refreshRecyclerView(false, false);
                                showToast("Refreshed");
                                return true;
                            }
                        } catch (IllegalStateException e) {
                            showToast("Already Refreshed");
                        }
                        return false;
                    }
                }
                return false;
            }
        });
    }

    protected void refreshRecyclerView(boolean isOnResume, boolean isOnRestart) {
        if (stockDB.isEmpty()) {
            stockRecyclerView.setVisibility(View.GONE);
            noStockEnteredByUser.setVisibility(View.VISIBLE);
        } else {
            stockRecyclerView.setVisibility(View.VISIBLE);
            noStockEnteredByUser.setVisibility(View.GONE);
            stockData = stockDB.getStockFromDB();
            if (!isOnRestart && !isOnResume) {
                new StockDataFetcherAsyncTask(MainActivity.this,
                        "Updating Current Prices\nPlease Wait ...",
                        stockDB, stockData).execute();
            }
            createRecyclerView();
        }
    }

    protected void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stockDB.close();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshRecyclerView(true, false);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        refreshRecyclerView(false, true);
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
        ItemTouchHelper.SimpleCallback cardTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                if (stockDB.deleteEntry(stockData.get(viewHolder.getAdapterPosition()))) {
                    stockData.remove(viewHolder.getAdapterPosition());
                    netProfitTextView.setText(String.format(Locale.ENGLISH, "%.2f", getNetProfit(stockData)));
                    stockCardRecyclerViewAdapter.notifyDataSetChanged();
                    if (stockDB.isEmpty()) {
                        stockRecyclerView.setVisibility(View.GONE);
                        noStockEnteredByUser.setVisibility(View.VISIBLE);
                    }
                }
            }
        };
        new ItemTouchHelper(cardTouchHelperCallback).attachToRecyclerView(stockRecyclerView);
        netProfitTextView.setText(String.format(Locale.ENGLISH, "%.2f", getNetProfit(stockData)));
        stockRecyclerView.setAdapter(stockCardRecyclerViewAdapter);
    }
}
/*
 *
 * @todo
 *
 * @possible_features
 *       1) Google sign in so everything is stored in the cloud
 *           - When user sends request to get data the server must
 *               # Fetch new current prices from the url and then send the data to user
 *       2) SearchBar
 *       3) Keep track of earnings like daily, weekly and monthly
 *           - Have three bars above the app selection interface
 *       4) Machine Learning to predict stock prices
 *       5) Add a portfolio option where protfolio for individual users can be created
 *
 * */