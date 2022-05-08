package com.kdt.reactspringbootrestapi.order.service;

import com.kdt.reactspringbootrestapi.author.Author;
import com.kdt.reactspringbootrestapi.order.Email;
import com.kdt.reactspringbootrestapi.order.Order;
import com.kdt.reactspringbootrestapi.order.OrderBook;
import com.kdt.reactspringbootrestapi.order.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderService {

    List<Order> getAllOrders();

    boolean checkDuplication(Order order);

    Order createOrder(UUID orderId, Email email, String address, String postcode, List<OrderBook> orderBooks, OrderStatus orderStatus, LocalDateTime createdAt);

    Order updateOrder(UUID orderId, Email email, String address, String postcode, OrderStatus orderStatus, LocalDateTime updatedAt);

    Order getOrderById(UUID orderId);

    List<Order> getOrderByEmail(Email email);

    boolean deleteOrder(UUID orderId);
}
