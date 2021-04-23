package com.danielj.priisvergliich;

/*
* Class model for a product (i.e. extracted supermarket item) in the app.
* */
public class ProductModel {
    private String productName;
    private String imageSrc;
    private String productPrice;
    private String productInfo;
    private String productOrigin;

    /*Variable instantiation*/
    public ProductModel(String productName, String imageSrc, String productPrice, String productInfo, String productOrigin) {
        this.productName = productName;
        this.imageSrc = imageSrc;
        this.productPrice = productPrice;
        this.productInfo = productInfo; // e.g. weight / quantity
        this.productOrigin = productOrigin;
    }
    /*Empty instantiation*/
    public ProductModel() {
        // Default variable initilisations.
        this.productPrice = "Price unknown";
        this.productInfo = "";
    }
    /*String representation of the class following convention*/
    @Override
    public String toString() {
        return "ProductModel{" +
                "productName=" + productName +
                ", imageSrc='" + imageSrc + '\'' +
                ", productPrice=" + productPrice +
                ", productInfo=" + productInfo +
                ", productOrigin=" + productOrigin +
                ";";
    }
    /*Default class method implementations*/
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
        productPrice = productPrice.replaceAll("\\–", "00");
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
