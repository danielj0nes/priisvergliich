package com.danielj.priisvergliich.Controllers;

import android.content.Context;

import com.danielj.priisvergliich.Models.ProductModel;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

public class ProductDBControllerTest extends TestCase {
    Context testContext = null;
    public void testInsertComparisonList() {
        // First initialise the controller and pass in a null context for test purposes
        ProductDBController dbc = new ProductDBController(testContext);
        // Build the comparison list
        List<ProductModel> products = new ArrayList<>();
        // Create some products to add to the list
        ProductModel product1 = new ProductModel();
        product1.setProductName("Test1");
        ProductModel product2 = new ProductModel();
        product2.setProductName("Test2");
        products.add(product1);
        products.add(product2);
        // Attempt to insert the list
        Boolean check = dbc.insertComparisonList(products); // Returns true upon success
        assertTrue(check);
    }

    public void testGetAllSavedProducts() {
        // First initialise the controller and pass in a null context for test purposes
        ProductDBController dbc = new ProductDBController(testContext);
        // Create and add some values to actually get back
        List<ProductModel> products = new ArrayList<>();
        // Create some products to add to the list
        ProductModel product1 = new ProductModel();
        product1.setProductName("Chocolate");
        ProductModel product2 = new ProductModel();
        product2.setProductName("Eggs");
        products.add(product1);
        products.add(product2);
        // Insert the listed using the tested function
        dbc.insertComparisonList(products);
        // Now check to ensure that the returned result is not null
        List<ProductModel> check = dbc.getAllSavedProducts();
        assertFalse(check == null);
    }
}