package com.masquerade.app.stockearnings.utilities;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class StockDatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "stock.db";
    public static final String TABLE_NAME = "stock_details";
    public static final String SCRIP_CODE_COL = "Scrip_Code";
    public static final String COMPANY_NAME_COL = "Company_Name";
    public static final String PURCHASE_PRICE_COL = "Purhcase_Price";
    public static final String CURRENT_PRICE_COL = "Current_Price";
    public static final String QUANTITY_BOUGHT_COL = "Qunatity_Bought";
    public static final String QUANTITY_RECEIVED_COL = "Quantity_Received";
    public static final String PROFIT_COL = "Profit";

    public StockDatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
        SQLiteDatabase db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + " (Scrip_Code INTEGER PRIMARY KEY, Company_Name" +
                " TEXT, Purhcase_Price REAL, Current_Price REAL, Qunatity_Bought INTEGER," +
                "Quantity_Received INTEGER, Profit REAL) ");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean insertData(String scripCode, String companyName, double purchasePrice,
                              double currentPrice, int quantityBought, int quantityReceived,
                              double profit) {
        SQLiteDatabase stockdb = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(SCRIP_CODE_COL, Integer.parseInt(scripCode));
        cv.put(COMPANY_NAME_COL, companyName);
        cv.put(PURCHASE_PRICE_COL, purchasePrice);
        cv.put(CURRENT_PRICE_COL, currentPrice);
        cv.put(QUANTITY_BOUGHT_COL, quantityBought);
        cv.put(QUANTITY_RECEIVED_COL, quantityReceived);
        cv.put(PROFIT_COL, profit);
        long result = stockdb.insert(TABLE_NAME, null, cv);
        return result != -1;
    }

}
