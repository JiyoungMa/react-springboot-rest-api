package com.kdt.reactspringbootrestapi.order.dto.request;

import com.kdt.reactspringbootrestapi.order.OrderStatus;

public record UpdateOrderRequest(String email, String address, String postcode, String orderStatus) {
}
