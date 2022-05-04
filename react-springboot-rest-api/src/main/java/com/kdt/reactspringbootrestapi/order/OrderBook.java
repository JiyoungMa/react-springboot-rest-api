package com.kdt.reactspringbootrestapi.order;

import java.util.UUID;

public record OrderBook(UUID bookId, int quantity, long price) {
}
