package com.danielj.priisvergliich;

public class ProductModel {
    private String productName;
    private String imageSrc;
    private double productPrice;

    public ProductModel(String productName, String imageSrc, double productPrice) {
        this.productName = productName;
        this.imageSrc = imageSrc;
        this.productPrice = productPrice;
    }
    public ProductModel() {
    }
    @Override
    public String toString() {
        return "ProductModel{" +
                "productName=" + productName +
                ", imageSrc='" + imageSrc + '\'' +
                ", productPrice=" + productPrice +
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

}
