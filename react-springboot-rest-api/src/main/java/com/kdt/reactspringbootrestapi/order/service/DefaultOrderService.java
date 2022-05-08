package com.kdt.reactspringbootrestapi.order.service;

import com.kdt.reactspringbootrestapi.exception.NoSuchResource;
import com.kdt.reactspringbootrestapi.exception.ResourceDuplication;
import com.kdt.reactspringbootrestapi.order.Email;
import com.kdt.reactspringbootrestapi.order.Order;
import com.kdt.reactspringbootrestapi.order.OrderBook;
import com.kdt.reactspringbootrestapi.order.OrderStatus;
import com.kdt.reactspringbootrestapi.order.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class DefaultOrderService implements OrderService {

    private final OrderRepository orderRepository;

    public DefaultOrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    public boolean checkDuplication(Order order) {
        return orderRepository.findById(order.getOrderId()).isPresent();
    }

    @Override
    public Order createOrder(UUID orderId, Email email, String address, String postcode, List<OrderBook> orderBooks, OrderStatus orderStatus, LocalDateTime createdAt) {
        Order order = new Order(orderId, email, address,postcode,orderBooks,orderStatus, createdAt,createdAt);
        if (checkDuplication(order))
            throw new ResourceDuplication("동일한 Order이 이미 존재합니다.");
        return orderRepository.insert(order);
    }

    @Override
    public Order updateOrder(UUID orderId, Email email, String address, String postcode, OrderStatus orderStatus, LocalDateTime updatedAt) {
        Order order = new Order(orderId, email,address, postcode,new ArrayList<>(),orderStatus, updatedAt, updatedAt);

        var foundResult = orderRepository.findById(orderId);
        if (foundResult.isEmpty())
            throw new NoSuchResource("일치하는 Order 없습니다.");

        return orderRepository.update(order);
    }

    @Override
    public Order getOrderById(UUID orderId) {
        var foundResult = orderRepository.findById(orderId);
        if (foundResult.isEmpty())
            throw new NoSuchResource("일치하는 Order이 없습니다.");

        return foundResult.get();
    }

    @Override
    public List<Order> getOrderByEmail(Email email) {
        return orderRepository.findByEmail(email);
    }


    @Override
    public boolean deleteOrder(UUID orderId) {
        return orderRepository.deleteOrder(orderId);
    }
}
