package com.danielj.priisvergliich;

/*
* Class model for a product in the app. Standard default get/set method implementations.
* */
public class ProductModel {
    private String productName;
    private String imageSrc;
    private String productPrice;
    private String productInfo;
    private String productOrigin;

    public ProductModel(String productName, String imageSrc, String productPrice, String productInfo, String productOrigin) {
        this.productName = productName;
        this.imageSrc = imageSrc;
        this.productPrice = productPrice;
        this.productInfo = productInfo; // e.g. weight / quantity
        this.productOrigin = productOrigin;
    }
    public ProductModel() {
        // Default variable initilisations.
        this.productPrice = "No price information available";
        this.productInfo = "";
    }
    @Override
    public String toString() {
        return "ProductModel{" +
                "productName=" + productName +
                ", imageSrc='" + imageSrc + '\'' +
                ", productPrice=" + productPrice +
                ", productInfo=" + productInfo +
                '}';
    }
    public String getProductName() {
        return productName;
    }
    public void setProductName(String productName) {
        this.productName = productName;
    }
    public String getImageSrc() {
        return imageSrc;
    }
    public void setImageSrc(String imageSrc) {
        this.imageSrc = imageSrc;
    }
    public String getProductPrice() {
        return productPrice;
    }
    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }
    public String getProductInfo() {
        return productInfo;
    }
    public void setProductInfo(String productInfo) {
        this.productInfo = productInfo;
    }
    public String getProductOrigin() {
        return productOrigin;
    }
    public void setProductOrigin(String productOrigin) {
        this.productOrigin = productOrigin;
    }
}
