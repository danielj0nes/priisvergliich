package com.danielj.priisvergliich;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
* This controller is used to manage all of the functions regarding the manipulation / processing
* of data obtained from the requests in the RequestsController. Since data obtained from requests
* varies from sight to sight, specific functions are needed to parse the specific formats.
* */
public class DataParseController {
    /*Helper parse function used in getting Migros product id's from the public "search-api" API*/
    public List<String> parseMigrosIds(String response) {
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
    public List<ProductModel> parseMigrosData(String response) {
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
                    // handle
                }
                try {
                    JSONObject productData = new JSONObject(jso.getString("product_tile_infos"));
                    String additionalInfo = productData.getString("price_sub_text");
                    if (additionalInfo != "null") {
                        product.setProductInfo(additionalInfo);
                    }
                } catch (JSONException e) {
                    // handle
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
    public List<ProductModel> parseCoopData(String response) {
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
}
