package com.kdt.reactspringbootrestapi.author;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

public class Author {
    private final UUID authorId;
    private String name;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Author(UUID authorId, String name, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.authorId = authorId;
        this.name = name;
        this.createdAt = createdAt.truncatedTo(ChronoUnit.MILLIS);
        this.updatedAt = updatedAt.truncatedTo(ChronoUnit.MILLIS);
    }

    public Author(UUID authorId, String name) {
        this(authorId, name, LocalDateTime.now(), LocalDateTime.now());
    }

    public UUID getAuthorId() {
        return authorId;
    }

    public String getName() {
        return name;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
