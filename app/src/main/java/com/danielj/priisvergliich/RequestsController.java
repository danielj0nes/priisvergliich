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
    public interface ListCallback {
        List<String> onSuccess(List<String> result);
    }
    private List<String> parseMigrosIds(String response) {
        List<String> data = new ArrayList<String>();
        try {
            JSONObject completeObject = new JSONObject(response);
            JSONArray results = completeObject.getJSONArray("results");
            for (int i = 0; i < results.length(); i++) {
                JSONObject jo = results.getJSONObject(i);
                JSONObject idTest = jo.getJSONObject("_product");
                String id = idTest.optString("id");
                data.add(id);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return data;
    }
    private List<String> parseMigrosData(String response) {
        List<String> data = new ArrayList<String>();
        try {
            JSONArray mainArray = new JSONArray(response);
            for (int i = 0; i < mainArray.length(); i++) {
                JSONObject jso = mainArray.getJSONObject(i);
                String priceInfo = jso.getString("price_info");
                String imageInfo = jso.getString("image");
                JSONObject t = new JSONObject(imageInfo);
                System.out.println(t.getString("src"));
                String name = jso.getString("name");
                System.out.println(name + " " + priceInfo);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return data;
    }

    private void getMigrosIds(String query, ListCallback callback) {
        List<String> migrosIds = new ArrayList<String>();
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
    public void getMigrosProducts(String query, ListCallback callback) {
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
                        .header("Origin", "https://www.migros.ch")
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
