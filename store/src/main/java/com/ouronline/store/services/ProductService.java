package com.ouronline.store.services;

import com.ouronline.store.models.Product;
import com.ouronline.store.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductService implements IProductServices {

    private final ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public void saveProduct(Product product) {
        productRepository.save(product);
    }

    @Override
    public List<Product> getAllProducts() {
        List<Product> products = productRepository.findAll();
        System.out.println("Products from DB: " + products); // Adaugă această linie pentru a verifica lista în consolă
        return products;
    }

    @Override
    public Optional<Product> getProductById(Long productId) {
        return productRepository.findById(productId);
    }

    @Override
    public List<Product> getProductsByCategory(String category) {
        List<Product> allProducts = productRepository.findAll();
        return allProducts.stream()
                .filter(product -> category.equalsIgnoreCase(product.getProductCategory()))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteProductById(Long productId) {
        productRepository.deleteById(productId);
    }
}
