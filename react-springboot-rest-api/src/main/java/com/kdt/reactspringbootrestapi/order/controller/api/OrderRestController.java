package com.kdt.reactspringbootrestapi.order.controller.api;

import com.kdt.reactspringbootrestapi.order.Email;
import com.kdt.reactspringbootrestapi.order.OrderBook;
import com.kdt.reactspringbootrestapi.order.OrderStatus;
import com.kdt.reactspringbootrestapi.order.dto.request.CreateOrderRequest;
import com.kdt.reactspringbootrestapi.order.dto.request.OrderBookDto;
import com.kdt.reactspringbootrestapi.order.dto.request.UpdateOrderRequest;
import com.kdt.reactspringbootrestapi.order.dto.response.OrderResponseDto;
import com.kdt.reactspringbootrestapi.order.service.OrderService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
public class OrderRestController {

    private final OrderService orderService;

    public OrderRestController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/api/v1/orders")
    public List<OrderResponseDto> orderList(@RequestParam Optional<String> email){
        if (email.isPresent()){
            return orderService.getOrderByEmail(new Email(email.get())).stream().map(
                    order -> new OrderResponseDto(order.getOrderId(), order.getEmail(), order.getAddress(), order.getAddress(), order.getOrderBooks().stream().map(orderBook -> OrderBookDto.dtoConverter(orderBook)).collect(Collectors.toList()), order.getOrderStatus())
            ).collect(Collectors.toList());
        }else{
            return orderService.getAllOrders().stream().map(
                    order -> new OrderResponseDto(order.getOrderId(), order.getEmail(), order.getAddress(), order.getAddress(), order.getOrderBooks().stream().map(orderBook -> OrderBookDto.dtoConverter(orderBook)).collect(Collectors.toList()), order.getOrderStatus())
            ).collect(Collectors.toList());
        }
    }

    @PostMapping("/api/v1/orders")
    public OrderResponseDto createOrder(@RequestBody CreateOrderRequest orderRequest){
        var createOrder = orderService.createOrder(UUID.randomUUID(), new Email(orderRequest.email()), orderRequest.address(), orderRequest.postcode(), orderRequest.orderBooks(), OrderStatus.valueOf(orderRequest.orderStatus()), LocalDateTime.now());
        var orderBookDtos = new ArrayList<OrderBookDto>();
        createOrder.getOrderBooks().forEach(orderBook -> orderBookDtos.add(new OrderBookDto(orderBook.bookId(), orderBook.quantity(),orderBook.price())));
        return new OrderResponseDto(createOrder.getOrderId(), createOrder.getEmail(), createOrder.getAddress(), createOrder.getPostcode(),orderBookDtos, createOrder.getOrderStatus());
    }

    @PutMapping("/api/v1/orders/{orderId}")
    public OrderResponseDto updateOrder(@PathVariable("orderId") UUID orderId, @RequestBody UpdateOrderRequest orderRequest){
        var updatedOrder = orderService.updateOrder(orderId, new Email(orderRequest.email()), orderRequest.address(), orderRequest.postcode(), OrderStatus.valueOf(orderRequest.orderStatus()), LocalDateTime.now());

        return new OrderResponseDto(updatedOrder.getOrderId(), updatedOrder.getEmail(), updatedOrder.getAddress(), updatedOrder.getAddress(), updatedOrder.getOrderBooks().stream().map(orderBook -> OrderBookDto.dtoConverter(orderBook)).collect(Collectors.toList()), updatedOrder.getOrderStatus());
    }

    @DeleteMapping("/api/v1/orders/{orderId}")
    public boolean deleteOrder(@PathVariable("orderId") UUID orderId){
        return orderService.deleteOrder(orderId);
    }
}
