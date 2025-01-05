package com.ouronline.store.services;

import com.ouronline.store.models.Product;
import com.ouronline.store.models.User;

import java.util.List;
import java.util.Optional;

public interface IProductServices {
    void saveProduct(Product product);
    List<Product> getAllProducts();
    Optional<Product> getProductById(Long productId);
    List<Product> getProductsByCategory(String category);
    void deleteProductById(Long productId);
}
