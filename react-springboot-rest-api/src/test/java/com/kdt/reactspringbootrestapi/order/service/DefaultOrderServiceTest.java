package com.kdt.reactspringbootrestapi.order.service;

import com.kdt.reactspringbootrestapi.author.Author;
import com.kdt.reactspringbootrestapi.book.Book;
import com.kdt.reactspringbootrestapi.book.Genre;
import com.kdt.reactspringbootrestapi.exception.NoSuchResource;
import com.kdt.reactspringbootrestapi.exception.ResourceDuplication;
import com.kdt.reactspringbootrestapi.order.Email;
import com.kdt.reactspringbootrestapi.order.Order;
import com.kdt.reactspringbootrestapi.order.OrderBook;
import com.kdt.reactspringbootrestapi.order.OrderStatus;
import com.kdt.reactspringbootrestapi.order.dto.request.OrderBookDto;
import com.kdt.reactspringbootrestapi.order.repository.OrderJdbcRepository;
import com.kdt.reactspringbootrestapi.order.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
        when(orderRepository.findAll()).thenReturn(orderList);

        //When
        var resultAllOrders = orderService.getAllOrders();

        //Then
        assertThat(resultAllOrders)
                .usingRecursiveFieldByFieldElementComparator()
                .hasSameElementsAs(orderList);
    }

    @Test
    void checkDuplicationNotExist() {
        //Given
        Order newOrder = new Order(UUID.randomUUID(), new Email("new@gmail.com"), "Address Example", "Postcode Example", new ArrayList<>(), OrderStatus.ACCEPTED);
        when(orderRepository.findById(newOrder.getOrderId())).thenReturn(Optional.empty());

        //When
        var duplicationResult = orderService.checkDuplication(newOrder);

        //Then
        assertThat(duplicationResult).isFalse();


    }

    @Test
    void checkDuplicationExist() {
        //Given
        when(orderRepository.findById(order.getOrderId())).thenReturn(Optional.of(order));

        //When
        var duplicationResult = orderService.checkDuplication(order);

        //Then
        assertThat(duplicationResult).isTrue();
    }

    @Test
    void createOrderNotDuplicated() {
        //Given
        Order newOrder = new Order(UUID.randomUUID(), new Email("new@gmail.com"), "Address Example", "Postcode Example", new ArrayList<>(), OrderStatus.ACCEPTED);
        when(orderRepository.findById(newOrder.getOrderId())).thenReturn(Optional.empty());
        when(orderRepository.insert(any())).thenReturn(newOrder);

        //When
        List<OrderBookDto> orderBookDtos = new ArrayList<>();
        newOrder.getOrderBooks().forEach(orderBook -> new OrderBookDto(orderBook.bookId(),orderBook.quantity(),orderBook.price()));
        var createResult = orderService.createOrder(newOrder.getOrderId(),newOrder.getEmail(), newOrder.getAddress(), newOrder.getPostcode(),orderBookDtos, newOrder.getOrderStatus(), LocalDateTime.now());

        //Then
        assertThat(createResult).as("Order"). usingRecursiveComparison().isEqualTo(newOrder);
    }

    @Test
    void createOrderDuplicated(){
        //Given
        when(orderRepository.findById(order.getOrderId())).thenReturn(Optional.of(order));

        //Then
        assertThatThrownBy(() -> {
            orderService.createOrder(order.getOrderId(),order.getEmail(), order.getAddress(), order.getPostcode(),new ArrayList<OrderBookDto>(), order.getOrderStatus(), LocalDateTime.now());
        }).isInstanceOf(ResourceDuplication.class);
    }

    @Test
    void updateOrderExist() {
        //Given
        when(orderRepository.findById(order.getOrderId())).thenReturn(Optional.of(order));
        order.setEmail(new Email("update@gmail.com"));
        when(orderRepository.update(any())).thenReturn(order);

        //When
        var updateResult = orderService.updateOrder(order.getOrderId(), order.getEmail(), order.getAddress(), order.getPostcode(), order.getOrderStatus(), LocalDateTime.now());

        //Then
        assertThat(updateResult).as("Order").usingRecursiveComparison().isEqualTo(order);
    }

    @Test
    void updateOrderNotExist() {
        //Given
        Order newOrder = new Order(UUID.randomUUID(), new Email("new@gmail.com"), "Address Example", "Postcode Example", new ArrayList<>(), OrderStatus.ACCEPTED);
        when(orderRepository.findById(newOrder.getOrderId())).thenReturn(Optional.empty());

        //Then
        assertThatThrownBy(() -> {
            orderService.updateOrder(newOrder.getOrderId(), newOrder.getEmail(), newOrder.getAddress(), newOrder.getPostcode(), newOrder.getOrderStatus(), LocalDateTime.now());
        }).isInstanceOf(NoSuchResource.class);
    }

    @Test
    void getOrderById() {
        //Given
        when(orderRepository.findById(order.getOrderId())).thenReturn(Optional.of(order));

        //When
        var foundResult = orderService.getOrderById(order.getOrderId());

        //Then
        assertThat(foundResult).usingRecursiveComparison().isEqualTo(order);
    }

    @Test
    void getOrderByEmail() {
        //Given
        List<Order> orderList = new ArrayList<>();
        orderList.add(order);
        when(orderRepository.findByEmail(order.getEmail())).thenReturn(orderList);

        //When
        var foundResult = orderService.getOrderByEmail(order.getEmail());

        //Then
        assertThat(foundResult)
                .usingRecursiveFieldByFieldElementComparator()
                .hasSameElementsAs(orderList);
    }

    @Test
    void deleteOrder() {
        //Given
        when(orderRepository.deleteOrder(order.getOrderId())).thenReturn(true);

        //When
        var deleteResult = orderService.deleteOrder(order.getOrderId());

        //Then
        assertThat(deleteResult).isTrue();
    }
}