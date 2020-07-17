package com.masquerade.app.stockearnings.utilities;

import com.masquerade.app.stockearnings.exceptions.InvalidScripCodeException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;

public class StockDetailsAPI {

    final static String baseUrl = "https://m.bseindia.com/StockReach.aspx?scripcd=";
    private String scripCode;

    public StockDetailsAPI(String scripCode) {
        this.scripCode = scripCode;
    }

    public ArrayList<String> fetchStockDetails() throws IOException, InvalidScripCodeException {
        StringBuffer sb = new StringBuffer(baseUrl);
        sb.append(this.scripCode);
        String stockURL = sb.toString();
        ArrayList<String> stockData = new ArrayList<String>();
        Document stockWebsite = Jsoup.connect(stockURL).get();
        String companyName = stockWebsite.getElementById("spanCname").text();
        String stockCurrentValue = stockWebsite.getElementById("strongCvalue").text();
        if (companyName.isEmpty() || stockCurrentValue.isEmpty()) {
            throw new InvalidScripCodeException("Srip Code Entered is Invalid");
        } else {
            stockData.add(companyName);
            stockData.add(stockCurrentValue);
        }
        return stockData;
    }
}
