package com.kdt.reactspringbootrestapi.author.repository;

import com.kdt.reactspringbootrestapi.author.Author;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AuthorRepository {

    List<Author> findAll();

    Author insert(Author author);

    Author update(Author author);

    Optional<Author> findById(UUID authorId);

    List<Author> findByName(String name);

    void deleteAll();

    boolean deleteAuthor(UUID authorId);
}
