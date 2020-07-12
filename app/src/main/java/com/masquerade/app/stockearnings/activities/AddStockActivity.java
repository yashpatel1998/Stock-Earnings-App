package com.masquerade.app.stockearnings.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import com.masquerade.app.stockearnings.R;

public class AddStockActivity extends AppCompatActivity {

    ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_new_stock);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent upIntent = NavUtils.getParentActivityIntent(AddStockActivity.this);
                startActivity(upIntent);
            }
        });
    }
}
