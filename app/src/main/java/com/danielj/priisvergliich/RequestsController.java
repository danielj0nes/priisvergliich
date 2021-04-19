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
/*
* This controller manages all of the functions related to requests made using OkHttp.
* The syntax of the requests are asynchronous - using callbacks specifically.
* The functions work in conjunction with the data parsers to build ProductModel classes which are
* then sent to the main thread of the application.
* */
public class RequestsController {
    DataParseController dataparser = new DataParseController();
    /*Helper interfaces to get data back from async request functions*/
    public interface ListCallback {
        void onSuccess(List<String> result);
    }
    public interface ProductCallback {
        void onSuccess(List<ProductModel> result);
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
                    List<String> parsed = dataparser.parseMigrosIds(queryResponse);
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
                        callback.onSuccess(dataparser.parseMigrosData(queryResponse));
                    } else {
                        // Handle
                    }
                }
            });
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
                    callback.onSuccess(dataparser.parseCoopData(queryResponse));
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
