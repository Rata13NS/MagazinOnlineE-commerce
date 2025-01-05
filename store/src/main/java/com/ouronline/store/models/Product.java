package com.ouronline.store.models;

import jakarta.persistence.*;

import java.util.Base64;

@Entity
@Table(name = "products")
public class Product {
    @Id
    @SequenceGenerator(
            name = "products_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            generator = "products_sequence",
            strategy = GenerationType.SEQUENCE
    )
    private Long productId;
    private String productName;
    private String productDescription;
    private String productDimensions;
    private String productWeight;
    private String productPrice;
    private String productCategory;
    @Lob
    private byte[] productImage;
    @Transient // nu va fi salvat în baza de date
    private String productImageBase64;

    public Product() { }


    public Long getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }


    public String getProductDimensions() {
        return productDimensions;
    }

    public void setProductDimensions(String productDimensions) {
        this.productDimensions = productDimensions;
    }

    public String getProductWeight() {
        return productWeight;
    }

    public void setProductWeight(String productWeight) {
        this.productWeight = productWeight;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public String getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(String productCategory) {
        this.productCategory = productCategory;
    }

    public byte[] getProductImage() {
        return productImage;
    }

    public void setProductImage(byte[] productImage) {
        this.productImage = productImage;
        this.productImageBase64 = Base64.getEncoder().encodeToString(productImage); // setează automat Base64 când imaginea este setată
    }

    public String getProductImageBase64() {
        return productImageBase64;
    }

    public void setProductImageBase64(String productImageBase64) {
        this.productImageBase64 = productImageBase64;
    }
}
