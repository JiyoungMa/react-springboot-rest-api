package com.kdt.reactspringbootrestapi.book;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

public class Book {
    private final UUID bookId;
    private String title;
    private Genre genre;
    private long price;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Book(UUID bookId, String title, Genre genre, long price, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.bookId = bookId;
        this.title = title;
        this.genre = genre;
        this.price = price;
        this.createdAt = createdAt.truncatedTo(ChronoUnit.MILLIS);
        this.updatedAt = updatedAt.truncatedTo(ChronoUnit.MILLIS);
    }

    public Book(UUID bookId, String title, Genre genre, long price) {
        this(bookId,title,genre,price, LocalDateTime.now(), LocalDateTime.now());
    }
}
