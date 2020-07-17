package com.masquerade.app.stockearnings.utilities;

import com.masquerade.app.stockearnings.exceptions.QuantityZeroException;
import com.masquerade.app.stockearnings.exceptions.InvalidScripCodeException;

public class StockDataInputValidator {

    private String scripCode;
    private int quantity, quantityReceived;
    private double purchasePrice;

    public StockDataInputValidator(String scripCode, int quantity, double purchasePrice, int quantityReceived) {
        this.scripCode = scripCode;
        this.quantity = quantity;
        this.purchasePrice = purchasePrice;
        this.quantityReceived = quantityReceived;
    }

    public boolean validateData() throws QuantityZeroException, InvalidScripCodeException {
        if (this.quantity > 0 && this.purchasePrice > 0 && this.quantityReceived >= 0 && this.scripCode.length() == 6)
            return true;
        else {
            if (this.quantity <= 0)
                throw new QuantityZeroException("Quantity of stock purchased should be positive");
            if (this.quantityReceived < 0)
                throw new QuantityZeroException("Quantity Received cannot be negative");
            if (this.purchasePrice < 0)
                throw new QuantityZeroException("Purchase price cannot be negative");
            if (this.scripCode.length() != 6)
                throw new InvalidScripCodeException("Scrip Code is invalid");
        }
        return false;
    }
}
