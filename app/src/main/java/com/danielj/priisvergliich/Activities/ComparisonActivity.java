package com.danielj.priisvergliich.Activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.danielj.priisvergliich.Controllers.DataParseController;
import com.danielj.priisvergliich.Controllers.ProductDBController;
import com.danielj.priisvergliich.Models.ProductModel;
import com.danielj.priisvergliich.R;
import com.r0adkll.slidr.Slidr;

import java.util.ArrayList;
import java.util.List;

import static android.widget.Toast.LENGTH_SHORT;

public class ComparisonActivity extends AppCompatActivity {
    DataParseController dpc = new DataParseController();
    ProductDBController dbc = new ProductDBController(this);
    public void openActivityMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*Object instantiations*/
        setContentView(R.layout.activity_comparison);
        ListView listView = findViewById(R.id.lv_comparisonList);
        TextView itemTextView = findViewById(R.id.tv_numberOfItems);
        TextView cheapestTextView = findViewById(R.id.tv_cheapestShop);
        Button saveComparisonList = findViewById(R.id.btn_saveComparisonList);
        MainActivity.ProductAdapter adapter = new MainActivity.ProductAdapter(this,
                MainActivity.TEMP_PRODUCT_LIST);
        String numberOfItems = String.valueOf(MainActivity.TEMP_PRODUCT_LIST.size());
        /*Setting values*/
        itemTextView.setText(numberOfItems);
        cheapestTextView.setText(dpc.calculateCheapest(MainActivity.TEMP_PRODUCT_LIST));
        listView.setAdapter(adapter);
        /*Listeners*/
        saveComparisonList.setOnClickListener(v -> {
            dbc.insertComparisonList(MainActivity.TEMP_PRODUCT_LIST);
            Toast.makeText(this,
                    "Comparison list successfully saved",
                    LENGTH_SHORT).show();
            openActivityMain();
            // Reset the temporary product list since current has been added to DB
            MainActivity.TEMP_PRODUCT_LIST = new ArrayList<>();
        });
        Slidr.attach(this);
    }
}