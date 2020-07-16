package com.masquerade.app.stockearnings.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import com.google.android.material.textfield.TextInputEditText;
import com.masquerade.app.stockearnings.R;
import com.masquerade.app.stockearnings.exceptions.*;
import com.masquerade.app.stockearnings.utilities.StockDataInputValidator;

public class AddStockActivity extends AppCompatActivity {

    ImageButton backButton;
    TextInputEditText inputISIN, inputQuantity, inputPurchasePrice, inputQuantityReceived;
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
                    String ISIN_NUmber = inputISIN.getText().toString();
                    int quantity = Integer.parseInt(inputQuantity.getText().toString());
                    int quantityReceived = Integer.parseInt(inputQuantityReceived.getText().toString());
                    double purchasePrice = Double.parseDouble(inputPurchasePrice.getText().toString());
                    StockDataInputValidator stockDataValidatorObject = new StockDataInputValidator(
                            ISIN_NUmber, quantity, purchasePrice, quantityReceived
                    );
                    if (stockDataValidatorObject.validateData()) {
                        /*
                         * @todo
                         *   Write logic to fetch the stock details and create an API for the same
                         *   Check out the python code for bse web scrapping for the same
                         * */
                    }
                } catch (QuantityZeroException quantityException) {
                    String message = quantityException.getMessage();
                    showExceptionToast(message);
                } catch (InvalidISINNumberException invalidISIN) {
                    String message = invalidISIN.getMessage();
                    showExceptionToast(message);
                }
            }
        });

    }

    protected void initVariables() {
        backButton = findViewById(R.id.backButton);
        this.inputISIN = (TextInputEditText) findViewById(R.id.ISIN_number_editText);
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
