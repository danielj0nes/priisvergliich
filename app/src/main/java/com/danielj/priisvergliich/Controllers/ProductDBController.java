package com.danielj.priisvergliich.Controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.Nullable;

import com.danielj.priisvergliich.Models.ProductModel;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

/*This controller is used to handle the primary CRUD database operations.*/
public class ProductDBController extends SQLiteOpenHelper {

    public static final String COMPARISON_LIST_TABLE = "COMPARISON_LIST_TABLE";
    public static final String PRODUCT_NAME = "PRODUCT_NAME";
    public static final String PRODUCT_PRICE = "PRODUCT_PRICE";
    public static final String PRODUCT_ORIGIN = "PRODUCT_ORIGIN";
    public static final String PRODUCT_INFO = "PRODUCT_INFO";
    public static final String PRODUCT_IMAGE = "PRODUCT_IMAGE";
    private ByteArrayOutputStream productImageBAOS;
    private byte[] imageInBytes;

    public ProductDBController(@Nullable Context context) {
        super(context, "product.db", null, 1);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            String createCL = "CREATE TABLE IF NOT EXISTS " + COMPARISON_LIST_TABLE
                    + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + PRODUCT_NAME + " TEXT, "
                    + PRODUCT_PRICE + " TEXT, "
                    + PRODUCT_ORIGIN + " TEXT, "
                    + PRODUCT_INFO + " TEXT, "
                    + PRODUCT_IMAGE + " BLOB)";
            db.execSQL(createCL);
        } catch (Exception e) {
            // This really shouldn't occur unless the SQL queries are fundamentally incorrect
            System.out.println(e.getMessage());
        }
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
    /*Given a list of products from a comparison list, inserts the items into the database to save
     * them/their values. This includes storing the image in byte form as opposed to a URL.*/
    public boolean insertComparisonList(List<ProductModel> productList) {
        SQLiteDatabase db = this.getWritableDatabase();
        for (ProductModel p : productList) {
            ContentValues cv = new ContentValues();
            cv.put(PRODUCT_NAME, p.getProductName());
            cv.put(PRODUCT_PRICE, p.getProductPrice());
            cv.put(PRODUCT_ORIGIN, p.getProductOrigin());
            cv.put(PRODUCT_INFO, p.getProductInfo());
            // Convert, compress and store image of product
            Bitmap productImage = p.getImageAsBitmap();
            productImageBAOS = new ByteArrayOutputStream();
            productImage.compress(Bitmap.CompressFormat.JPEG, 100, productImageBAOS);
            imageInBytes = productImageBAOS.toByteArray();
            cv.put(PRODUCT_IMAGE, imageInBytes);
            long query = db.insert(COMPARISON_LIST_TABLE, null, cv);
            if (query != 0) {
                System.out.println("Data added successfully");
            } else {
                // Since primary keys are auto-incremented, this should not occur
                System.out.println("Failed to add data");
            }
        }
        return true;
    }
    public List<ProductModel> getAllSavedProducts(){
        SQLiteDatabase db = this.getWritableDatabase();
        List<ProductModel> products = new ArrayList<>();
        Cursor queryCursor = db.rawQuery("SELECT * FROM " + COMPARISON_LIST_TABLE, null);
        if (queryCursor.getCount() != 0) {
            while (queryCursor.moveToNext()) {
                ProductModel product = new ProductModel();
                product.setProductName(queryCursor.getString(1));
                product.setProductPrice(queryCursor.getString(2));
                product.setProductOrigin(queryCursor.getString(3));
                product.setProductInfo(queryCursor.getString(4));
                byte[] imageBytes = queryCursor.getBlob(5);
                Bitmap imageBitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                product.setImageBitmap(imageBitmap);
                products.add(product);
            }
        }
        return products;
    }
}
