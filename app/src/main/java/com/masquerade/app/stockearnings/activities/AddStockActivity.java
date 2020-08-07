package com.masquerade.app.stockearnings.activities;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.masquerade.app.stockearnings.R;
import com.masquerade.app.stockearnings.asyncTask.StockDataFetcherAsyncTask;
import com.masquerade.app.stockearnings.exceptions.InvalidScripCodeException;
import com.masquerade.app.stockearnings.exceptions.QuantityZeroException;
import com.masquerade.app.stockearnings.utilities.StockDataInputValidator;
import com.masquerade.app.stockearnings.utilities.StockDatabaseHelper;

public class AddStockActivity extends AppCompatActivity {

    final static String baseUrl = "https://m.bseindia.com/StockReach.aspx?scripcd=";


    ImageButton backButton;
    TextInputEditText inputScripCode, inputQuantity, inputPurchasePrice, inputQuantityReceived;
    Button submitButton;
    Context addStockActivityContext;
    StockDatabaseHelper stockdb;
    int quantity, quantityReceived;
    String scripCode, progressMessage;
    double purchasePrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_new_stock);

        initVariables();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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
                        // Company Name - index 0
                        // current price - index 1
                        new StockDataFetcherAsyncTask(AddStockActivity.this, progressMessage,
                                stockdb, scripCode, purchasePrice, quantity,
                                quantityReceived).execute();
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
        this.progressMessage = "Fetching Stock Info";
        stockdb = new StockDatabaseHelper(this);
    }

    protected void showToast(String message) {
        int toastDuration = Toast.LENGTH_SHORT;
        Toast quantityErrorToast = Toast.makeText(addStockActivityContext, message, toastDuration);
        quantityErrorToast.show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stockdb.close();
    }
}
