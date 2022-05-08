package com.kdt.reactspringbootrestapi.author.service;

import com.kdt.reactspringbootrestapi.author.Author;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface AuthorService {

    List<Author> getAllAuthors();

    boolean checkDuplication(Author author);

    Author createAuthor(String authorName, LocalDateTime createdAt);

    Author updateAuthor(UUID authorId, String authorName, LocalDateTime updatedAt);

    List<Author> getAuthorsByName(String authorName);

    boolean deleteAuthor(UUID authorId);

}
