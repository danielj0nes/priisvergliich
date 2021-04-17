package com.danielj.priisvergliich;

public class ProductModel {
    private String productName;
    private String imageSrc;
    private String productPrice;
    private String productInfo;

    public ProductModel(String productName, String imageSrc, String productPrice, String productInfo) {
        this.productName = productName;
        this.imageSrc = imageSrc;
        this.productPrice = productPrice;
        this.productInfo = productInfo; // e.g. weight / quantity
    }
    public ProductModel() {
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
    /*
    * Default get/set method implementations
    * */
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
}
