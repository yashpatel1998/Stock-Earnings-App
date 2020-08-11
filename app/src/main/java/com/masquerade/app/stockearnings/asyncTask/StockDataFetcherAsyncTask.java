package com.masquerade.app.stockearnings.asyncTask;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.masquerade.app.stockearnings.MainActivity;
import com.masquerade.app.stockearnings.R;
import com.masquerade.app.stockearnings.activities.AddStockActivity;
import com.masquerade.app.stockearnings.exceptions.InvalidScripCodeException;
import com.masquerade.app.stockearnings.models.Stock;
import com.masquerade.app.stockearnings.utilities.StockDatabaseHelper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;

public class StockDataFetcherAsyncTask extends AsyncTask<Void, Void, Void> {

    private final String baseUrl = "https://m.bseindia.com/StockReach.aspx?scripcd=";
    private final String BSESensexURL = "https://m.bseindia.com/index.aspx";
    private final String NSEURL = "https://www1.nseindia.com/";

    private Context activityContext;
    private String scripCode;
    private ProgressDialog progressBar;
    private String progressDialogMessage;
    private ArrayList<Stock> stockData;
    StockDatabaseHelper stockDB;
    private String currentPrice, companyName;
    private int quantityReceived, quantity;
    private double purchasePrice;
    private String BSECurrentValue, BSEChangeValue;
    ArrayList<TextView> bsePriceTextViews;

    public StockDataFetcherAsyncTask(Context ctx,
                                     String progressDialogMessage, StockDatabaseHelper stockDB,
                                     String scripCode, double purchasePrice,
                                     int quantity, int quantityReceived) {
        this.activityContext = ctx;
        this.scripCode = scripCode;
        this.progressDialogMessage = progressDialogMessage;
        this.stockDB = stockDB;
        this.purchasePrice = purchasePrice;
        this.quantity = quantity;
        this.quantityReceived = quantityReceived;
    }

    public StockDataFetcherAsyncTask(Context ctx,
                                     String progressDialogMessage, StockDatabaseHelper stockDB,
                                     ArrayList<Stock> stockData, ArrayList<TextView> bseViews) {
        this.activityContext = ctx;
        this.progressDialogMessage = progressDialogMessage;
        this.stockData = stockData;
        this.stockDB = stockDB;
        this.bsePriceTextViews = bseViews;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressBar = new ProgressDialog(activityContext);
        progressBar.setMessage(progressDialogMessage);
        progressBar.setIndeterminate(false);
        progressBar.setCancelable(false);
        progressBar.show();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            if (stockData == null && this.activityContext instanceof AddStockActivity) {
                addStockActivityStockDataBackgroundFetcher();
            } else if (stockData != null && this.activityContext instanceof MainActivity) {
                mainActivityStockDataBackgroundFetcher();
                getBSECurrentPrice();
            }
        } catch (Exception e) {
            e.printStackTrace();
            showToast(e.getMessage());
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        try {
            if (stockData == null && this.activityContext instanceof AddStockActivity) {
                boolean isInserted = stockDB.insertData(scripCode, companyName, purchasePrice,
                        Double.parseDouble(currentPrice), quantity, quantityReceived);
                if (isInserted)
                    showToast("Stock inserted in databse");
                else
                    showToast("Something went wrong");
                progressBar.dismiss();
                Intent toMainActivity = new Intent(activityContext, MainActivity.class);
                activityContext.startActivity(toMainActivity);
            } else if (stockData != null && this.activityContext instanceof MainActivity) {
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
                    TextView bseCurrentValue = ((MainActivity) this.activityContext).findViewById(R.id.BSE_Current_value);
                    bseCurrentValue.setText(BSECurrentValue);
                    TextView bseChangeValue = ((MainActivity) this.activityContext).findViewById(R.id.BSE_Value_Change);
                    bseChangeValue.setText(this.BSEChangeValue);
                    if (Double.parseDouble(this.BSEChangeValue) < 0) {
                        bseChangeValue.setTextColor(Color.parseColor("#d23f31"));
                    } else {
                        bseChangeValue.setTextColor(Color.parseColor("#0f9d58"));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.progressBar.dismiss();
    }

    protected void showToast(String message) {
        int toastDuration = Toast.LENGTH_SHORT;
        Toast quantityErrorToast = Toast.makeText(this.activityContext, message, toastDuration);
        quantityErrorToast.show();
    }

    private void mainActivityStockDataBackgroundFetcher() throws IOException {
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
    }

    private void addStockActivityStockDataBackgroundFetcher() throws Exception {
        String stockURL = baseUrl + this.scripCode;
        Log.d("AddStockActivity async-", stockURL);
        Document stockWebsite = Jsoup.connect(stockURL).get();
        this.companyName = stockWebsite.getElementById("spanCname").text();
        this.currentPrice = stockWebsite.getElementById("strongCvalue").text();
        Log.d("doInBackground", "Copmany Name : " + companyName);
        Log.d("doInBackground", "Current Value : " + this.currentPrice);
        if (companyName.isEmpty() || this.currentPrice.isEmpty()) {
            throw new InvalidScripCodeException("Srip Code entered is Invalid");
        } else {
            Log.d("AddStockActivity", "Stock data fetched");
        }
    }

    private void getBSECurrentPrice() throws IOException {
        Document stockWebsite = Jsoup.connect(BSESensexURL).get();
        String currentPrice = stockWebsite.getElementById("UcHeaderMenu1_sensexLtp").text();
        String priceChange = stockWebsite.getElementById("UcHeaderMenu1_sensexChange").text();
        this.BSEChangeValue = priceChange;
        this.BSECurrentValue = currentPrice;
    }
}
