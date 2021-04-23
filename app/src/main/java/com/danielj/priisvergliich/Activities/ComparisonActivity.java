package com.danielj.priisvergliich.Activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Build;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.danielj.priisvergliich.Activities.MainActivity;
import com.danielj.priisvergliich.Controllers.DataParseController;
import com.danielj.priisvergliich.R;
import com.r0adkll.slidr.Slidr;



public class ComparisonActivity extends AppCompatActivity {
    DataParseController dpc = new DataParseController();
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comparison);
        ListView listView = findViewById(R.id.lv_comparisonList);
        TextView itemTextView = findViewById(R.id.tv_numberOfItems);
        TextView cheapestTextView = findViewById(R.id.tv_cheapestShop);
        MainActivity.ProductAdapter adapter = new MainActivity.ProductAdapter(this,
                MainActivity.TEMP_PRODUCT_LIST);
        String numberOfItems = String.valueOf(MainActivity.TEMP_PRODUCT_LIST.size());
        itemTextView.setText(numberOfItems);
        cheapestTextView.setText(dpc.calculateCheapest(MainActivity.TEMP_PRODUCT_LIST));
        listView.setAdapter(adapter);
        Slidr.attach(this);
    }
}