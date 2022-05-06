package com.kdt.reactspringbootrestapi.book;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

public class Book {
    private final UUID bookId;
    private String title;
    private Genre genre;
    private final UUID authorId;
    private long price;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Book(UUID bookId, String title, UUID authorId,Genre genre, long price, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.bookId = bookId;
        this.title = title;
        this.authorId =authorId;
        this.genre = genre;
        this.price = price;
        this.createdAt = createdAt.truncatedTo(ChronoUnit.MILLIS);
        this.updatedAt = updatedAt.truncatedTo(ChronoUnit.MILLIS);
    }

    public Book(UUID bookId, String title, UUID authorId,Genre genre, long price) {
        this(bookId,title,authorId,genre,price, LocalDateTime.now(), LocalDateTime.now());
    }

    public UUID getBookId() {
        return bookId;
    }

    public String getTitle() {
        return title;
    }

    public Genre getGenre() {
        return genre;
    }

    public long getPrice() {
        return price;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public UUID getAuthorId() {
        return authorId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setGenre(Genre genre) {
        this.genre = genre;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
