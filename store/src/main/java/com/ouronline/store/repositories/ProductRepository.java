package com.ouronline.store.repositories;

import com.ouronline.store.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    // Poți adăuga metode personalizate dacă ai nevoie
}
