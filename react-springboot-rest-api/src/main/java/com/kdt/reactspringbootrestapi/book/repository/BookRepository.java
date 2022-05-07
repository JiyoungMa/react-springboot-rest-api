package com.kdt.reactspringbootrestapi.book.repository;

import com.kdt.reactspringbootrestapi.book.Book;
import com.kdt.reactspringbootrestapi.book.Genre;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BookRepository {

    List<Book> findAll();

    Book insert(Book book);

    Book update(Book book);

    Optional<Book> findById(UUID bookId);

    List<Book> findByTitle(String title);

    List<Book> findByGenre(Genre genre);

    void deleteAll();

    boolean deleteBook(Book book);
}
