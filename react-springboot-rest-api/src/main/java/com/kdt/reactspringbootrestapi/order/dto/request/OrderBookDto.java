package com.kdt.reactspringbootrestapi.order.dto.request;

import com.kdt.reactspringbootrestapi.order.OrderBook;

import java.util.UUID;

public record OrderBookDto(UUID bookId, int quantity, long price) {
    public static OrderBookDto dtoConverter(OrderBook orderBook){
        return new OrderBookDto(orderBook.bookId(), orderBook.quantity(), orderBook.price());
    }
}
