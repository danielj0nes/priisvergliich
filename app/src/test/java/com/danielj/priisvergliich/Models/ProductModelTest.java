package com.danielj.priisvergliich.Models;

import junit.framework.TestCase;

public class ProductModelTest extends TestCase {
    /*Test every method of the class ProductModel*/
    public void testTestToString() {
        // First build the example testing product
        ProductModel product = new ProductModel("Chocolate bar", "",
                "1.50", "100g", "Migros");
        // Next create expected output string
        String expected = "ProductModel{productName=Chocolate bar, imageSrc='\', " +
                "productPrice=1.50, productInfo=100g, productOrigin=Migros};";
        assertEquals(expected, product.toString());
    }

    public void testGetProductName() {
        // Build an example test product
        ProductModel product = new ProductModel("Steak sandwich", "",
                "2.90", "", "Coop");
        // As we pass 'Steak sandwich' as the productName, we expect this to be obtained
        String expected = "Steak sandwich";
        assertEquals(expected, product.getProductName());
    }

    public void testSetProductName() {
        // Build empty object of class ProductModel so that we can later set values
        ProductModel product = new ProductModel();
        // Utilise the setProductName method
        product.setProductName("Kinder Bueno 3x40g");
        String expected = "Kinder Bueno 3x40g";
        // Utilise already tested getProductName() method to check if the values match
        assertEquals(expected, product.getProductName());
        // Test again to ensure we can set a new value on an already set object
        product.setProductName("Some product");
        expected = "Some product";
        assertEquals(expected, product.getProductName());
    }

    public void testGetImageSrc() {
        // First initialise with imageSrc value
        ProductModel product = new ProductModel("",
                "https://hips.hearstapps.com/hmg-prod.s3.amazonaws.com/images/190322-ham-sandwich-horizontal-1553721016.png",
                "", "", "");
        String expected = "https://hips.hearstapps.com/hmg-prod.s3.amazonaws.com/images/190322-ham-sandwich-horizontal-1553721016.png";
        assertEquals(expected, product.getImageSrc());
        // Next initialise empty object, set the value, then test
        product = new ProductModel();
        product.setImageSrc("https://simply-delicious-food.com/wp-content/uploads/2020/07/Easy-salad-sandwiches-with-herb-mayo-5.jpg");
        expected = "https://simply-delicious-food.com/wp-content/uploads/2020/07/Easy-salad-sandwiches-with-herb-mayo-5.jpg";
        assertEquals(expected, product.getImageSrc());
    }

    public void testSetImageSrc() {
        // Build empty object of class ProductModel so that we can later set values
        ProductModel product = new ProductModel();
        // Utilise the setImageSrc method
        product.setImageSrc("https://i.imgur.com/CUG0Aof.jpg");
        String expected = "https://i.imgur.com/CUG0Aof.jpg";
        // Utilise already tested getImageSrc() method to check if the values match
        assertEquals(expected, product.getImageSrc());
        // Test again to ensure we can set a new value on an already set object
        product.setImageSrc("https://i.imgur.com/yGEyOfa.jpeg");
        expected = "https://i.imgur.com/yGEyOfa.jpeg";
        assertEquals(expected, product.getImageSrc());
    }

    public void testGetProductPrice() {
        // First initialise a product with test values
        ProductModel product = new ProductModel("Test123",
                "www.google.com",
                "5.00", "100g", "Unknown");
        String expected = "5.00";
        assertEquals(expected, product.getProductPrice());
        // Next initialise empty object, set the value, then test
        product = new ProductModel();
        product.setProductPrice("4.00");
        expected = "4.00";
        assertEquals(expected, product.getProductPrice());
    }

    public void testSetProductPrice() {
        // Build empty object of class ProductModel so that we can later set values
        ProductModel product = new ProductModel();
        // Utilise the setProductPrice method
        product.setProductPrice("100.-");
        String expected = "100.-";
        // Utilise already tested getProductPrice() method to check if the values match
        assertEquals(expected, product.getProductPrice());
        // Test again to ensure we can set a new value on an already set object
        product.setProductPrice("250.59");
        expected = "250.59";
        assertEquals(expected, product.getProductPrice());
    }

    public void testGetProductInfo() {
        // First initialise a product with test values
        ProductModel product = new ProductModel("Test123",
                "www.google.com",
                "5.00", "100g", "Unknown");
        String expected = "100g";
        assertEquals(expected, product.getProductInfo());
        // Next initialise empty object, set the value, then test
        product = new ProductModel();
        product.setProductInfo("500g + discount");
        expected = "500g + discount";
        assertEquals(expected, product.getProductInfo());
    }

    public void testSetProductInfo() {
        // Build empty object of class ProductModel so that we can later set values
        ProductModel product = new ProductModel();
        // Utilise the setProductInfo method
        product.setProductInfo("Buy One Get One Free");
        String expected = "Buy One Get One Free";
        // Utilise already tested getProductInfo() method to check if the values match
        assertEquals(expected, product.getProductInfo());
    }

    public void testGetProductOrigin() {
        // Initialise a product with test values
        ProductModel product = new ProductModel("Test123",
                "www.google.com",
                "5.00", "100g", "Unknown");
        String expected = "Unknown";
        assertEquals(expected, product.getProductOrigin());
    }

    public void testSetProductOrigin() {
        // Build empty object of class ProductModel so that we can later set values
        ProductModel product = new ProductModel();
        // Utilise the setProductOrigin method
        product.setProductOrigin("Migros");
        String expected = "Migros";
        // Utilise already tested getProductOrigin() method to check if the values match
        assertEquals(expected, product.getProductOrigin());
    }
}