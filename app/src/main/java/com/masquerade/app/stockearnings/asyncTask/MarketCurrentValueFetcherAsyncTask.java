package com.masquerade.app.stockearnings.asyncTask;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import com.masquerade.app.stockearnings.MainActivity;
import com.masquerade.app.stockearnings.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;

public class MarketCurrentValueFetcherAsyncTask extends AsyncTask<Void,Void,Void> {
    private Context activityContext;
    private final String BSESensexURL = "https://m.bseindia.com/index.aspx";

    private ProgressDialog progressBar;
    private String BSEChangeValue;
    private String BSECurrentValue;
    public MarketCurrentValueFetcherAsyncTask(Context ctx) {
        activityContext = ctx;
        Log.v("MarketCurrentValueFetch","Object instantiated");
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressBar = new ProgressDialog(activityContext);
        progressBar.setMessage("Fetching Market Value");
        progressBar.setIndeterminate(false);
        progressBar.setCancelable(false);
        progressBar.show();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            getBSECurrentPrice();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        TextView bseCurrentValue = ((MainActivity) this.activityContext).findViewById(R.id.BSE_Current_value);
        bseCurrentValue.setText(BSECurrentValue);
        TextView bseChangeValue = ((MainActivity) this.activityContext).findViewById(R.id.BSE_Value_Change);
        bseChangeValue.setText(this.BSEChangeValue);
        if (Double.parseDouble(this.BSEChangeValue) < 0) {
            bseChangeValue.setTextColor(Color.parseColor("#d23f31"));
        } else {
            bseChangeValue.setTextColor(Color.parseColor("#0f9d58"));
        }
        progressBar.dismiss();
    }

    private void getBSECurrentPrice() throws IOException {
        Document stockWebsite = Jsoup.connect(BSESensexURL).get();
        String currentPrice = stockWebsite.getElementById("UcHeaderMenu1_sensexLtp").text();
        String priceChange = stockWebsite.getElementById("UcHeaderMenu1_sensexChange").text();
        this.BSEChangeValue = priceChange;
        this.BSECurrentValue = currentPrice;
    }
}
