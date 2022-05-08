package com.kdt.reactspringbootrestapi.book.dto.request;

import java.util.UUID;

public record UpdateBookRequest (String title, String genre,
                                UUID authorId, long price){
}
