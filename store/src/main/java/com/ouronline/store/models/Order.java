package com.ouronline.store.models;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "orders")
public class Order {
    @Id
    @SequenceGenerator(
            name = "orders_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            generator = "orders_sequence",
            strategy = GenerationType.SEQUENCE
    )
    private Long orderId;
    private Long orderNumber;
    private String orderProductsName;
    private String orderProductsNumber;
    private String orderClientName;
    private String orderClientSirName;
    private String orderDeliveryType;
    private String orderPaymentType;
    private LocalDate orderData;
    private String orderDeliveryCity;
    private String orderDeliverySector;
    private String orderDeliveryStreet;
    private String orderDeliveryNumber;
    private String orderDeliveryBlock;
    private String orderDeliveryBlockEntrance;
    private String orderDeliveryApartmentNumber;
    private String orderDeliveryEasyboxAdress;
    private String orderStatus;
    private String orderTerms;
    private String emailAddress;
    private String orderUsername;
    private Double orderTotalPrice;
    private LocalTime orderPlacementHour;
    private String orderProductsId;

    public Order() {}

    // Getters and Setters
    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(Long orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getOrderProductsName() {
        return orderProductsName;
    }

    public void setOrderProductsName(String orderProductsName) {
        this.orderProductsName = orderProductsName;
    }

    public String getOrderProductsNumber() {
        return orderProductsNumber;
    }

    public void setOrderProductsNumber(String orderProductsNumber) {
        this.orderProductsNumber = orderProductsNumber;
    }

    public String getOrderClientName() {
        return orderClientName;
    }

    public void setOrderClientName(String orderClientName) {
        this.orderClientName = orderClientName;
    }

    public String getOrderClientSirName() {
        return orderClientSirName;
    }

    public void setOrderClientSirName(String orderClientSirName) {
        this.orderClientSirName = orderClientSirName;
    }

    public String getOrderDeliveryType() {
        return orderDeliveryType;
    }

    public void setOrderDeliveryType(String orderDeliveryType) {
        this.orderDeliveryType = orderDeliveryType;
    }

    public String getOrderPaymentType() {
        return orderPaymentType;
    }

    public void setOrderPaymentType(String orderPaymentType) {
        this.orderPaymentType = orderPaymentType;
    }

    public LocalDate getOrderData() {
        return orderData;
    }

    public void setOrderData(LocalDate orderData) {
        this.orderData = orderData;
    }

    public String getOrderDeliveryCity() {
        return orderDeliveryCity;
    }

    public void setOrderDeliveryCity(String orderDeliveryCity) {
        this.orderDeliveryCity = orderDeliveryCity;
    }

    public String getOrderDeliverySector() {
        return orderDeliverySector;
    }

    public void setOrderDeliverySector(String orderDeliverySector) {
        this.orderDeliverySector = orderDeliverySector;
    }

    public String getOrderDeliveryStreet() {
        return orderDeliveryStreet;
    }

    public void setOrderDeliveryStreet(String orderDeliveryStreet) {
        this.orderDeliveryStreet = orderDeliveryStreet;
    }

    public String getOrderDeliveryNumber() {
        return orderDeliveryNumber;
    }

    public void setOrderDeliveryNumber(String orderDeliveryNumber) {
        this.orderDeliveryNumber = orderDeliveryNumber;
    }

    public String getOrderDeliveryBlock() {
        return orderDeliveryBlock;
    }

    public void setOrderDeliveryBlock(String orderDeliveryBlock) {
        this.orderDeliveryBlock = orderDeliveryBlock;
    }

    public String getOrderDeliveryBlockEntrance() {
        return orderDeliveryBlockEntrance;
    }

    public void setOrderDeliveryBlockEntrance(String orderDeliveryBlockEntrance) {
        this.orderDeliveryBlockEntrance = orderDeliveryBlockEntrance;
    }

    public String getOrderDeliveryApartmentNumber() {
        return orderDeliveryApartmentNumber;
    }

    public void setOrderDeliveryApartmentNumber(String orderDeliveryApartmentNumber) {
        this.orderDeliveryApartmentNumber = orderDeliveryApartmentNumber;
    }

    public String getOrderDeliveryEasyboxAdress() {
        return orderDeliveryEasyboxAdress;
    }

    public void setOrderDeliveryEasyboxAdress(String orderDeliveryEasyboxAdress) {
        this.orderDeliveryEasyboxAdress = orderDeliveryEasyboxAdress;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getOrderTerms() {
        return orderTerms;
    }

    public void setOrderTerms(String orderTerms) {
        this.orderTerms = orderTerms;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getOrderUsername() {
        return orderUsername;
    }

    public void setOrderUsername(String orderUsername) {
        this.orderUsername = orderUsername;
    }
    public Double getOrderTotalPrice() {
        return orderTotalPrice;
    }

    public void setOrderTotalPrice(Double orderTotalPrice) {
        this.orderTotalPrice = orderTotalPrice;
    }

    // Getter și Setter pentru orderPlacementHour
    public LocalTime getOrderPlacementHour() {
        return orderPlacementHour;
    }

    public void setOrderPlacementHour(LocalTime orderPlacementHour) {
        this.orderPlacementHour = orderPlacementHour;
    }

    public String getOrderProductsId() {
        return orderProductsId;
    }

    public void setOrderProductsId(String orderProductsId) {
        this.orderProductsId = orderProductsId;
    }
}