package com.kdt.reactspringbootrestapi.author;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

public class Author {
    private final UUID authorId;
    private String authorName;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Author(UUID authorId, String authorName, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.authorId = authorId;
        this.authorName = authorName;
        this.createdAt = createdAt.truncatedTo(ChronoUnit.MILLIS);
        this.updatedAt = updatedAt.truncatedTo(ChronoUnit.MILLIS);
    }

    public Author(UUID authorId, String name, LocalDateTime updatedAt) {
        this(authorId, name, updatedAt, updatedAt);
    }

    public Author(UUID authorId, String name) {
        this(authorId, name, LocalDateTime.now(), LocalDateTime.now());
    }

    public UUID getAuthorId() {
        return authorId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
