package com.kdt.reactspringbootrestapi.order;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

public class Order {
    private final UUID orderId;
    private Email email;
    private String address;
    private String postcode;
    private List<OrderBook> orderBooks;
    private OrderStatus orderStatus;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Order(UUID orderId, Email email, String address, String postcode, List<OrderBook> orderBooks, OrderStatus orderStatus, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.orderId = orderId;
        this.email = email;
        this.address = address;
        this.postcode = postcode;
        this.orderBooks = orderBooks;
        this.orderStatus = orderStatus;
        this.createdAt = createdAt.truncatedTo(ChronoUnit.MILLIS);
        this.updatedAt = updatedAt.truncatedTo(ChronoUnit.MILLIS);
    }

    public Order(UUID orderId, Email email, String address, String postcode, List<OrderBook> orderBooks, OrderStatus orderStatus) {
        this(orderId,email,address,postcode,orderBooks,orderStatus, LocalDateTime.now(), LocalDateTime.now());
    }

    public UUID getOrderId() {
        return orderId;
    }

    public Email getEmail() {
        return email;
    }

    public String getAddress() {
        return address;
    }

    public String getPostcode() {
        return postcode;
    }

    public List<OrderBook> getOrderBooks() {
        return orderBooks;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setEmail(Email email) {
        this.email = email;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setOrderBooks(List<OrderBook> orderBooks) {
        this.orderBooks = orderBooks;
    }
}
