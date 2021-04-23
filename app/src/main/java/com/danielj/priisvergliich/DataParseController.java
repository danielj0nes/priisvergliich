package com.danielj.priisvergliich;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Arrays;
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
    /*The parseCoopData method is used to parse the data obtained in the request into usable
    * products of class ProductModel. Since Coop doesn't have public API endpoints, Jsoup is utilised
    * to parse the HTML data and extract the values.*/
    public List<ProductModel> parseCoopData(String response) {
        List<ProductModel> products = new ArrayList<>();
        Document doc = Jsoup.parse(response);
        // First obtain all of the 'scripts' as product data is stored within variable of script
        Elements scripts = doc.getElementsByTag("script");
        Elements weights = doc.select("span.productTile__quantity-text");
        for (Element script : scripts) {
            // Find the correct script
            if (script.data().contains("utag_data")) {
                // Use regex to extract data into a JSON format
                Pattern pattern = Pattern.compile(".*utag_data = ([^;]*)");
                Matcher matcher = pattern.matcher(script.data());
                if (matcher.find()) {
                    try {
                        // Build the ProductModel classes
                        JSONObject jso = new JSONObject(matcher.group(1));
                        JSONArray productNames = jso.getJSONArray("product_productInfo_productName");
                        JSONArray productPrices = jso.getJSONArray("product_attributes_basePrice");
                        JSONArray productIds = jso.getJSONArray("product_productInfo_sku");
                        String baseUrl = "https://www.coop.ch/img/produkte/310_310/RGB/";
                        for (int i = 0; i < productNames.length(); i++) {
                            ProductModel product = new ProductModel();
                            product.setProductInfo(weights.get(i).text());
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
    /*
    * A primitive attempt at a relevance sorting algorithm. Based on a threshold, take lower index
    * results from each company and create a new intertwined list of products. Idea based on the
    * assumption that retrieved products are already mostly relevance sorted.
    * */
    public List<ProductModel> sortRelevance(List<ProductModel> products, int threshold) {
        List<ProductModel> relevantProducts = new ArrayList<>();
        List<ProductModel> migrosProducts = new ArrayList<>();
        List<ProductModel> coopProducts = new ArrayList<>();
        for (ProductModel product : products) {
            switch (product.getProductOrigin()) {
                case "Migros":
                    migrosProducts.add(product);
                    break;
                case "Coop":
                    coopProducts.add(product);
                    break;
            }
        }
        if (migrosProducts.size() > threshold && coopProducts.size() > threshold) {
            for (int i = 0; i <= threshold - 1; i++) {
                relevantProducts.add(migrosProducts.get(i));
                relevantProducts.add(coopProducts.get(i));
            }
            if (migrosProducts.size() < coopProducts.size()) {
                relevantProducts.addAll(migrosProducts.subList(threshold, migrosProducts.size() - 1));
                relevantProducts.addAll(coopProducts.subList(threshold, coopProducts.size() - 1));
            } else {
                relevantProducts.addAll(coopProducts.subList(threshold, coopProducts.size() - 1));
                relevantProducts.addAll(migrosProducts.subList(threshold, migrosProducts.size() - 1));
            }
            return relevantProducts;
        }
        else if (migrosProducts.size() != 0 && coopProducts.size() != 0) {
            relevantProducts.add(migrosProducts.remove(0));
            relevantProducts.add(coopProducts.remove(0));
            if (migrosProducts.size() < coopProducts.size()) {
                relevantProducts.addAll(migrosProducts);
                relevantProducts.addAll(coopProducts);
            } else {
                relevantProducts.addAll(coopProducts);
                relevantProducts.addAll(migrosProducts);
            }
            return relevantProducts;
        } else {
            if (migrosProducts.size() < coopProducts.size()) {
                relevantProducts.addAll(migrosProducts);
                relevantProducts.addAll(coopProducts);
            } else {
                relevantProducts.addAll(coopProducts);
                relevantProducts.addAll(migrosProducts);
            }
            return relevantProducts;
        }
    }
}
