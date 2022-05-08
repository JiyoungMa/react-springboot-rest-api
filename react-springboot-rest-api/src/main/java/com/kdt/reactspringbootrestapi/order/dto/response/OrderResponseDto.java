package com.kdt.reactspringbootrestapi.order.dto.response;

import com.kdt.reactspringbootrestapi.order.Email;
import com.kdt.reactspringbootrestapi.order.OrderBook;
import com.kdt.reactspringbootrestapi.order.OrderStatus;
import com.kdt.reactspringbootrestapi.order.dto.request.OrderBookDto;

import java.util.List;
import java.util.UUID;

public record OrderResponseDto(UUID orderId, Email email,
                               String address, String postcode,
                               List<OrderBookDto> orderBooks,
                               OrderStatus orderStatus) {

}
