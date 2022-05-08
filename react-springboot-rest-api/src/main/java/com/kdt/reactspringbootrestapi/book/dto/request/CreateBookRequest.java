package com.kdt.reactspringbootrestapi.book.dto.request;

import com.kdt.reactspringbootrestapi.book.Genre;

import java.util.UUID;

public record CreateBookRequest(String title, String genre,
                                UUID authorId, long price) {
}
