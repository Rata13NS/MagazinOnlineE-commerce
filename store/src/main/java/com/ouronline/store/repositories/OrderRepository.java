package com.ouronline.store.repositories;

import com.ouronline.store.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByOrderUsername(String username);
}