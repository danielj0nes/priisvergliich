package com.danielj.priisvergliich;

import android.os.Build;

import androidx.annotation.RequiresApi;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    public interface StringCallback {
        // For debugging
        String onSuccess(String result);
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
                product.setProductOrigin("Migros");
                products.add(product);
                }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return products;
    }
    private List<ProductModel> parseCoopData(String response) {
        List<ProductModel> products = new ArrayList<>();
        Document doc = Jsoup.parse(response);
        Elements scripts = doc.getElementsByTag("script");
        for (Element script : scripts) {
            if (script.data().contains("utag_data")) {
                Pattern pattern = Pattern.compile(".*utag_data = ([^;]*)");
                Matcher matcher = pattern.matcher(script.data());
                if (matcher.find()) {
                    try {
                        JSONObject jso = new JSONObject(matcher.group(1));
                        JSONArray productNames = jso.getJSONArray("product_productInfo_productName");
                        JSONArray productPrices = jso.getJSONArray("product_attributes_basePrice");
                        JSONArray productIds = jso.getJSONArray("product_productInfo_sku");
                        String baseUrl = "https://www.coop.ch/img/produkte/310_310/RGB/";
                        for (int i = 0; i < productNames.length(); i++) {
                            ProductModel product = new ProductModel();
                            String productUrl = baseUrl + productIds.getString(i) + "_001.jpg?_=1581484027184";
                            product.setProductName(productNames.getString(i));
                            product.setProductPrice(productPrices.getString(i));
                            product.setImageSrc(productUrl);
                            product.setProductInfo("");
                            product.setProductOrigin("Coop");
                            products.add(product);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    System.out.println("Not found");
                    // Handle
                }
                break;
            }
        }
        return products;
    }
    /*Helper function for getMigrosProducts() as a required first step in obtaining product data
     * Works by making a request to the public unrestricted search-api and saving the ID values
     * of products using the parseMigrosID() function*/
    private void getMigrosIds(String query, ListCallback callback) {
        query = query.replaceAll("\\s", "%20");
        String url = "https://search-api.migros.ch/products?lang=de&key=migros_components_search&limit=20&offset=0&q="+query;
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
    @RequiresApi(api = Build.VERSION_CODES.O)
    public List<ProductModel> getAllProducts(String query) throws IOException {
        List<ProductModel> products = new ArrayList<>();
        OkHttpClient client = new OkHttpClient();
        query = query.replaceAll("\\s", "%20");
        String url = "https://www.coop.ch/de/search/?text="+query;
        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = client.newCall(request).execute();
        products.addAll(parseCoopData(response.body().string()));
        return products;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void getMigrosProducts(String query, ProductCallback callback) {
        getMigrosIds(query, result -> {
            String formattedIds = String.join("%2c", result);
            OkHttpClient client = new OkHttpClient();
            String url = "https://web-api.migros.ch/widgets/product_fragments_json?ids=" +
                    formattedIds + "&lang=de&limit=20&offset=0&key=5reweDEbruthex8s";
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
        });
    }
    public void getCoopProducts(String query, ProductCallback callback) {
        query = query.replaceAll("\\s", "%20");
        String url = "https://www.coop.ch/de/search/?text="+query;
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
                    callback.onSuccess(parseCoopData(queryResponse));
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
}
