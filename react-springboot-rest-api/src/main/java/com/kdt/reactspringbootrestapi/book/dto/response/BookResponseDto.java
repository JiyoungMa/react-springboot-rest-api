package com.kdt.reactspringbootrestapi.book.dto.response;

import com.kdt.reactspringbootrestapi.book.Genre;

import java.util.UUID;

public record BookResponseDto(UUID bookId, String title,
                              Genre genre, UUID authorId, long price) {
}
