package com.danielj.priisvergliich.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.danielj.priisvergliich.Controllers.ProductDBController;
import com.danielj.priisvergliich.Models.ProductModel;
import com.danielj.priisvergliich.R;
import com.r0adkll.slidr.Slidr;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class SavedComparisonsActivity extends AppCompatActivity {
    ProductDBController dbc = new ProductDBController(this);
    /*Adapter class that translates an item of class ProductModel into an item of the list view
     * This is used each time a search is made to display the correctly search items returned
     * from the parsed request.*/
    static class SavedItemAdapter extends ArrayAdapter<ProductModel> {
        public SavedItemAdapter(Context context, List<ProductModel> products) {
            super(context, 0, products);
        }
        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            ProductModel product = getItem(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.row, parent, false);
            }
            ImageView ivImageSrc = convertView.findViewById(R.id.iv_productImage);
            TextView tvProductName = convertView.findViewById(R.id.tv_productName);
            TextView tvProductPrice = convertView.findViewById(R.id.tv_productPrice);
            TextView tvProductInfo = convertView.findViewById(R.id.tv_productInfo);
            TextView tvProductOrigin = convertView.findViewById(R.id.tv_productOrigin);
            tvProductName.setText(product.getProductName());
            tvProductPrice.setText(product.getProductPrice());
            tvProductInfo.setText(product.getProductInfo());
            tvProductOrigin.setText(product.getProductOrigin());
            ivImageSrc.setImageBitmap(product.getImageAsBitmap());
            return convertView;
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_comparisons);
        ListView listView = findViewById(R.id.lv_savedItems);
        SavedItemAdapter adapter = new SavedItemAdapter(this,
                dbc.getAllSavedProducts());
        listView.setAdapter(adapter);
        Slidr.attach(this);
    }
}