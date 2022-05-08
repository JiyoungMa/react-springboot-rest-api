package com.kdt.reactspringbootrestapi.order.service;

import com.kdt.reactspringbootrestapi.author.Author;
import com.kdt.reactspringbootrestapi.book.Book;
import com.kdt.reactspringbootrestapi.book.Genre;
import com.kdt.reactspringbootrestapi.order.Email;
import com.kdt.reactspringbootrestapi.order.Order;
import com.kdt.reactspringbootrestapi.order.OrderBook;
import com.kdt.reactspringbootrestapi.order.OrderStatus;
import com.kdt.reactspringbootrestapi.order.repository.OrderJdbcRepository;
import com.kdt.reactspringbootrestapi.order.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class DefaultOrderServiceTest {

    OrderRepository orderRepository = mock(OrderJdbcRepository.class);

    OrderService orderService = new DefaultOrderService(orderRepository);

    Author author = new Author(UUID.randomUUID(), "author");
    Book book = new Book(UUID.randomUUID(),"Book", author.getAuthorId(), Genre.HORROR, 20000);
    List<OrderBook> orderBookList = new ArrayList<>();
    Order order = new Order(UUID.randomUUID(), new Email("tester@gmail.com"), "Address Example", "Postcode Example", orderBookList, OrderStatus.ACCEPTED);

    @BeforeEach
    void setup() {
        orderBookList.add(new OrderBook(order.getOrderId(), book.getBookId(), 1, book.getPrice()));
        orderBookList.add(new OrderBook(order.getOrderId(), book.getBookId(), 10, book.getPrice()*10));
    }

    @Test
    void getAllOrders() {
        //Given
        List<Order> orderList = new ArrayList<>();
        orderList.add(order);
        for (int i = 0; i<9; i++){
            List<OrderBook> newOrderBookList = new ArrayList<>();
            Order newOrder = new Order(UUID.randomUUID(), new Email(MessageFormat.format("tester{0}@gmail.com", i)), MessageFormat.format("Address Example {0}", i), MessageFormat.format("Postcode Example {0}", i), newOrderBookList, OrderStatus.ACCEPTED);
            for (int j = 0; j<3; j++){
                newOrderBookList.add(new OrderBook(newOrder.getOrderId(), book.getBookId(), i*10 + j, book.getPrice() * (i*10 + j)));
            }
            orderList.add(newOrder);
        }
    }

    @Test
    void checkDuplication() {

    }

    @Test
    void createOrder() {
    }

    @Test
    void updateOrder() {
    }

    @Test
    void getOrderById() {
    }

    @Test
    void getOrderByEmail() {
    }

    @Test
    void deleteOrder() {
    }
}