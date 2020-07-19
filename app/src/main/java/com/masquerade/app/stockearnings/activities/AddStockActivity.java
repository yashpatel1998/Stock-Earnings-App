package com.masquerade.app.stockearnings.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import com.google.android.material.textfield.TextInputEditText;
import com.masquerade.app.stockearnings.MainActivity;
import com.masquerade.app.stockearnings.R;
import com.masquerade.app.stockearnings.exceptions.*;
import com.masquerade.app.stockearnings.models.Stock;
import com.masquerade.app.stockearnings.utilities.StockDataInputValidator;
import com.masquerade.app.stockearnings.utilities.StockDatabaseHelper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.ArrayList;

import com.masquerade.app.stockearnings.exceptions.InvalidScripCodeException;

public class AddStockActivity extends AppCompatActivity {

    final static String baseUrl = "https://m.bseindia.com/StockReach.aspx?scripcd=";


    ImageButton backButton;
    TextInputEditText inputScripCode, inputQuantity, inputPurchasePrice, inputQuantityReceived;
    Button submitButton;
    Context addStockActivityContext;
    StockDatabaseHelper stockdb;
    int quantity, quantityReceived;
    String scripCode;
    double purchasePrice;
    ProgressDialog fetchProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_new_stock);

        initVariables();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent upIntent = NavUtils.getParentActivityIntent(AddStockActivity.this);
                startActivity(upIntent);
            }
        });
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    addStockActivityContext = getApplicationContext();
                    scripCode = inputScripCode.getText().toString();
                    quantity = Integer.parseInt(inputQuantity.getText().toString());
                    quantityReceived = Integer.parseInt(inputQuantityReceived.getText().toString());
                    purchasePrice = Double.parseDouble(inputPurchasePrice.getText().toString());
                    StockDataInputValidator stockDataValidatorObject = new StockDataInputValidator(
                            scripCode, quantity, purchasePrice, quantityReceived
                    );
                    if (stockDataValidatorObject.validateData()) {
                        /*
                         * @todo
                         *   Send the data upon add stock button click to the main activity adapter
                         *   so that it can be loaded
                         * */

                        // Company Name - index 0
                        // current price - index 1
                        StockDetailsAPI fetchStockDetails = new StockDetailsAPI();
                        fetchStockDetails.execute(baseUrl + scripCode);
                        // Add Stock to sql data base
                    }
                } catch (QuantityZeroException quantityException) {
                    String message = quantityException.getMessage();
                    showToast(message);
                } catch (InvalidScripCodeException invalidISIN) {
                    String message = invalidISIN.getMessage();
                    showToast(message);
                }
            }
        });

    }

    protected void initVariables() {
        backButton = findViewById(R.id.backButton);
        this.inputScripCode = findViewById(R.id.scrip_code_editText);
        this.inputQuantity = findViewById(R.id.quantity_editText);
        this.inputQuantityReceived = findViewById(R.id.quantity_received_editText);
        this.inputPurchasePrice = findViewById(R.id.purchase_editText);
        this.submitButton = findViewById(R.id.submitButton);
        stockdb = new StockDatabaseHelper(this);
    }

    protected void showToast(String message) {
        int toastDuration = Toast.LENGTH_SHORT;
        Toast quantityErrorToast = Toast.makeText(addStockActivityContext, message, toastDuration);
        quantityErrorToast.show();
    }

    private class StockDetailsAPI extends AsyncTask<String, Void, ArrayList<String>> {

        protected void onPreExecute() {
            super.onPreExecute();
            fetchProgress = new ProgressDialog(AddStockActivity.this);
            fetchProgress.setMessage("Please wait...It is downloading");
            fetchProgress.setIndeterminate(false);
            fetchProgress.setCancelable(false);
            fetchProgress.show();
        }

        @Override
        protected ArrayList<String> doInBackground(String... strings) {
            ArrayList<String> stockData = new ArrayList<String>();
            try {
                String stockURL = strings[0];
                Log.d("doInBackground", stockURL);
                Document stockWebsite = Jsoup.connect(stockURL).get();
                String companyName = stockWebsite.getElementById("spanCname").text();
                String stockCurrentValue = stockWebsite.getElementById("strongCvalue").text();
                Log.d("doInBackground", "Copmany Name : " + companyName);
                Log.d("doInBackground", "Current Value : " + stockCurrentValue);
                if (companyName.isEmpty() || stockCurrentValue.isEmpty()) {
                    throw new InvalidScripCodeException("Srip Code Entered is Invalid");
                } else {
                    Log.d("doInBackground", "Stock data fetched");
                    stockData.add(companyName);
                    stockData.add(stockCurrentValue);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return stockData;
        }

        @Override
        protected void onPostExecute(ArrayList<String> stockData) {
            super.onPostExecute(stockData);
            Log.d("onPostExecute", " " + stockData.size());
            Stock newStockData = new Stock(scripCode, stockData.get(0), purchasePrice,
                    quantityReceived, quantity, Double.parseDouble(stockData.get(1)));
            double profit = newStockData.calculateProfit();
            boolean isInserted = stockdb.insertData(scripCode, stockData.get(0), purchasePrice,
                    Double.parseDouble(stockData.get(1)), quantity, quantityReceived, profit);
            if (isInserted)
                showToast("Stock inserted in databse");
            else
                showToast("Something went wrong");
            Intent toMainActivity = new Intent(AddStockActivity.this, MainActivity.class);
            startActivity(toMainActivity);
        }
    }
}
