package com.masquerade.app.stockearnings.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import com.google.android.material.textfield.TextInputEditText;
import com.masquerade.app.stockearnings.R;
import com.masquerade.app.stockearnings.exceptions.*;
import com.masquerade.app.stockearnings.utilities.StockDataInputValidator;
import com.masquerade.app.stockearnings.utilities.StockDetailsAPI;

import java.io.IOException;
import java.util.ArrayList;

public class AddStockActivity extends AppCompatActivity {

    ImageButton backButton;
    TextInputEditText inputScripCode, inputQuantity, inputPurchasePrice, inputQuantityReceived;
    Button submitButton;
    Context addStockActivityContext;

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
                    String scripCode = inputScripCode.getText().toString();
                    int quantity = Integer.parseInt(inputQuantity.getText().toString());
                    int quantityReceived = Integer.parseInt(inputQuantityReceived.getText().toString());
                    double purchasePrice = Double.parseDouble(inputPurchasePrice.getText().toString());
                    StockDataInputValidator stockDataValidatorObject = new StockDataInputValidator(
                            scripCode, quantity, purchasePrice, quantityReceived
                    );
                    if (stockDataValidatorObject.validateData()) {
                        /*
                         * @todo
                         *   Send the data upon add stock button click to the main activity adapter
                         *   so that it can be loaded
                         * */
                        StockDetailsAPI stockDetailsFetcher = new StockDetailsAPI(scripCode);
                        ArrayList<String> stockData = stockDetailsFetcher.fetchStockDetails();

                    }
                } catch (QuantityZeroException quantityException) {
                    String message = quantityException.getMessage();
                    showExceptionToast(message);
                } catch (InvalidScripCodeException invalidISIN) {
                    String message = invalidISIN.getMessage();
                    showExceptionToast(message);
                } catch (IOException fetchError) {
                    String message = "Something went wrong while fetching stock info";
                    showExceptionToast(message);
                }
            }
        });

    }

    protected void initVariables() {
        backButton = findViewById(R.id.backButton);
        this.inputScripCode = (TextInputEditText) findViewById(R.id.scrip_code_editText);
        this.inputQuantity = (TextInputEditText) findViewById(R.id.quantity_editText);
        this.inputQuantityReceived = (TextInputEditText) findViewById(R.id.quantity_received_editText);
        this.inputPurchasePrice = (TextInputEditText) findViewById(R.id.purchase_editText);
        this.submitButton = (Button) findViewById(R.id.submitButton);
    }

    protected void showExceptionToast(String message) {
        int toastDuration = Toast.LENGTH_SHORT;
        Toast quantityErrorToast = Toast.makeText(addStockActivityContext, message, toastDuration);
        quantityErrorToast.show();
    }
}
