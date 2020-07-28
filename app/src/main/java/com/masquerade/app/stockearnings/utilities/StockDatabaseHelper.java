package com.masquerade.app.stockearnings.utilities;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.masquerade.app.stockearnings.models.Stock;

import java.util.ArrayList;

public class StockDatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "stock.db";
    public static final String TABLE_NAME = "stock_details";
    public static final String SCRIP_CODE_COL = "Scrip_Code";
    public static final String COMPANY_NAME_COL = "Company_Name";
    public static final String PURCHASE_PRICE_COL = "Purhcase_Price";
    public static final String CURRENT_PRICE_COL = "Current_Price";
    public static final String QUANTITY_BOUGHT_COL = "Qunatity_Bought";
    public static final String QUANTITY_RECEIVED_COL = "Quantity_Received";
    public static final String IS_DATABASE_EMPTY = "SELECT count(*) FROM stock_details";

    public StockDatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
        SQLiteDatabase db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + " (Scrip_Code INTEGER PRIMARY KEY, Company_Name" +
                " TEXT, Purhcase_Price REAL, Current_Price REAL, Qunatity_Bought INTEGER," +
                "Quantity_Received INTEGER) ");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean insertData(String scripCode, String companyName, double purchasePrice,
                              double currentPrice, int quantityBought, int quantityReceived) {
        SQLiteDatabase stockdb = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(SCRIP_CODE_COL, Integer.parseInt(scripCode));
        cv.put(COMPANY_NAME_COL, companyName);
        cv.put(PURCHASE_PRICE_COL, purchasePrice);
        cv.put(CURRENT_PRICE_COL, currentPrice);
        cv.put(QUANTITY_BOUGHT_COL, quantityBought);
        cv.put(QUANTITY_RECEIVED_COL, quantityReceived);
        long result = stockdb.insert(TABLE_NAME, null, cv);
        return result != -1;
    }

    public boolean updateCurrentPrice(String scriptCode, double currentPrice) {
        return true;
    }

    public boolean isEmpty() {
        long numEntries = DatabaseUtils.queryNumEntries(this.getReadableDatabase(), TABLE_NAME);
        return numEntries == 0;
    }

    public ArrayList<Stock> getStockFromDB() {
        ArrayList<Stock> stockInDB = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME, null);
        res.moveToFirst();
        while (!res.isAfterLast()) {
            String scripCode = Integer.toString(res.getInt(res.getColumnIndex(SCRIP_CODE_COL)));
            String companyName = res.getString(res.getColumnIndex(COMPANY_NAME_COL));
            double purchasePrice = res.getDouble(res.getColumnIndex(PURCHASE_PRICE_COL));
            double currentPrice = res.getDouble(res.getColumnIndex(CURRENT_PRICE_COL));
            int quantityBought = res.getInt(res.getColumnIndex(QUANTITY_BOUGHT_COL));
            int quantityReceived = res.getInt(res.getColumnIndex(QUANTITY_RECEIVED_COL));
            Stock tempStock = new Stock(scripCode, companyName, purchasePrice, quantityReceived,
                    quantityBought, currentPrice);
            tempStock.calculateProfit();
            stockInDB.add(tempStock);
            res.moveToNext();
        }
        res.close();
        return stockInDB;
    }

    public boolean updateCurrentPrice(String scripCode, String companyName, double purchasePrice,
                                      double currentPrice, int quantityBought, int quantityReceived) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(SCRIP_CODE_COL, Integer.parseInt(scripCode));
        cv.put(COMPANY_NAME_COL, companyName);
        cv.put(PURCHASE_PRICE_COL, purchasePrice);
        cv.put(CURRENT_PRICE_COL, currentPrice);
        cv.put(QUANTITY_BOUGHT_COL, quantityBought);
        cv.put(QUANTITY_RECEIVED_COL, quantityReceived);
        int rowsAffected = db.update(TABLE_NAME, cv, "Scrip_Code = ?",
                new String[]{scripCode});
        return rowsAffected > 0;
    }

    public boolean deleteEntry(Stock stock) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, SCRIP_CODE_COL + "=" + stock.getScripCode(), null) > 0;
    }
}
