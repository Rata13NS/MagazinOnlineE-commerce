package com.ouronline.store.services;

import com.ouronline.store.models.Order;
import com.ouronline.store.repositories.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public List<Order> getOrdersByUsername(String username) {
        return orderRepository.findByOrderUsername(username);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
}