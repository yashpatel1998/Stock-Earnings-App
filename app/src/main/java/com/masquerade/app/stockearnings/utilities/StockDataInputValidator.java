package com.masquerade.app.stockearnings.utilities;

import android.widget.EditText;

import com.masquerade.app.stockearnings.exceptions.QuantityZeroException;
import com.masquerade.app.stockearnings.exceptions.InvalidISINNumberException;

public class StockDataInputValidator {

    private String ISINNumber;
    private int quantity, quantityReceived;
    private double purchasePrice;

    public StockDataInputValidator(String ISINNumber, int quantity, double purchasePrice, int quantityReceived) {
        this.ISINNumber = ISINNumber;
        this.quantity = quantity;
        this.purchasePrice = purchasePrice;
        this.quantityReceived = quantityReceived;
    }

    public boolean validateData() throws QuantityZeroException, InvalidISINNumberException {
        if (this.quantity > 0 && this.purchasePrice > 0 && this.quantityReceived >= 0 && this.ISINNumber.length() == 12)
            return true;
        else {
            if (this.quantity <= 0)
                throw new QuantityZeroException("Quantity of stock purchased should be positive");
            if (this.quantityReceived < 0)
                throw new QuantityZeroException("Quantity Received cannot be negative");
            if (this.purchasePrice < 0)
                throw new QuantityZeroException("Purchase price cannot be negative");
            if (this.ISINNumber.length() != 12)
                throw new InvalidISINNumberException("ISIN number is invalid");
        }
        return false;
    }
}
