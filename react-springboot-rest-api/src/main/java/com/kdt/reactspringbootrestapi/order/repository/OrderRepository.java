package com.kdt.reactspringbootrestapi.order.repository;

import com.kdt.reactspringbootrestapi.order.Email;
import com.kdt.reactspringbootrestapi.order.Order;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository {

    List<Order> findAll();

    Order insert(Order order);

    Order update(Order order);

    Optional<Order> findById(UUID orderId);

    List<Order> findByEmail(Email email);

    void deleteAll();

    boolean deleteOrder(Order order);
}
