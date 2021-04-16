package com.danielj.priisvergliich;

import android.os.Build;

import androidx.annotation.RequiresApi;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RequestsController {
    /*Helper interface to get data back from async request functions*/
    public interface ListCallback {
        List<String> onSuccess(List<String> result);
    }
    public interface ProductCallback {
        List<ProductModel> onSuccess(List<ProductModel> result);
    }
    /*Helper parse function used in getting Migros product id's from the public "search-api" API*/
    private List<String> parseMigrosIds(String response) {
        List<String> data = new ArrayList<String>();
        try {
            JSONObject completeObject = new JSONObject(response);
            JSONArray results = completeObject.getJSONArray("results");
            for (int i = 0; i < results.length(); i++) {
                // Loop through the results, grab product ID's, append to list
                JSONObject jo = results.getJSONObject(i);
                JSONObject idData = jo.getJSONObject("_product");
                String id = idData.optString("id");
                data.add(id);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return data;
    }
    /*Helper function to parse data from the private Migros "web-api" API*/
    private List<ProductModel> parseMigrosData(String response) {
        List<ProductModel> products = new ArrayList<>();
        try {
            JSONArray mainArray = new JSONArray(response);
            for (int i = 0; i < mainArray.length(); i++) {
                ProductModel product = new ProductModel();
                JSONObject jso = mainArray.getJSONObject(i);
                try {
                    JSONObject priceData = new JSONObject(jso.getString("price_info"));
                    product.setProductPrice(priceData.getString("price"));
                } catch (JSONException e) {
                    product.setProductPrice("Null");
                }
                try {
                    JSONObject productData = new JSONObject(jso.getString("product_tile_infos"));
                    product.setProductInfo(productData.getString("price_sub_text"));
                } catch (JSONException e) {
                    product.setProductInfo("Null");
                }
                JSONObject imageData = new JSONObject(jso.getString("image"));
                product.setImageSrc(imageData.getString("src"));
                product.setProductName(jso.getString("name"));
                products.add(product);
                }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return products;
    }
    /*Helper function for getMigrosProducts() as a required first step in obtaining product data
     * Works by making a request to the public unrestricted search-api and saving the ID values
     * of products using the parseMigrosID() function*/
    private void getMigrosIds(String query, ListCallback callback) {
        query = query.replaceAll("\\s", "%20");
        String url = "https://search-api.migros.ch/products?lang=de&key=migros_components_search&limit=10&offset=0&q="+query;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String queryResponse = response.body().string();
                    List<String> parsed = parseMigrosIds(queryResponse);
                    callback.onSuccess(parsed);
                } else {
                    // Handle
                }
            }
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }
        });
    }
    /*Using the IDs obtained from the search-api, query the web API to get exclusive information
     * such as pricing. Used in conjunction with parseMigrosData() to build list of Product classes*/
    public void getMigrosProducts(String query, ProductCallback callback) {
        getMigrosIds(query, new ListCallback() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public List<String> onSuccess(List<String> result) {
                String formattedIds = String.join("%2c", result);
                OkHttpClient client = new OkHttpClient();
                String url = "https://web-api.migros.ch/widgets/product_fragments_json?ids=" +
                        formattedIds + "&lang=de&limit=12&offset=0&key=5reweDEbruthex8s";
                Request request = new Request.Builder()
                        .url(url)
                        .header("Origin", "https://www.migros.ch") // Header required
                        .build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        e.printStackTrace();
                    }
                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        if (response.isSuccessful()) {
                            String queryResponse = response.body().string();
                            callback.onSuccess(parseMigrosData(queryResponse));
                        } else {
                            // Handle
                        }
                    }
                });
                return result;
            }
        });
    }
}
