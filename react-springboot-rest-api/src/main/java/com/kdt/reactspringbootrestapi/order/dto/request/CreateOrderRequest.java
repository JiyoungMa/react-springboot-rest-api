package com.kdt.reactspringbootrestapi.order.dto.request;

import com.kdt.reactspringbootrestapi.order.Email;
import com.kdt.reactspringbootrestapi.order.OrderBook;
import com.kdt.reactspringbootrestapi.order.OrderStatus;

import java.util.List;
import java.util.UUID;

public record CreateOrderRequest(String email,
                                 String address, String postcode,
                                 List<OrderBookDto> orderBooks,
                                 String orderStatus) {
}
