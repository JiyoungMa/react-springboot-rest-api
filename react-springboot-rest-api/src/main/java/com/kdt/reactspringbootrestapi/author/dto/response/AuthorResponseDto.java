package com.kdt.reactspringbootrestapi.author.dto.response;

import java.util.UUID;

public record AuthorResponseDto(UUID authorId, String authorName) {
}
