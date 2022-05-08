package com.kdt.reactspringbootrestapi.book.service;

import com.kdt.reactspringbootrestapi.book.Book;
import com.kdt.reactspringbootrestapi.book.Genre;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface BookService {

    List<Book> getAllBooks();

    boolean checkDuplication(Book book);

    Book createBook(String title, UUID authorId, Genre genre, long price, LocalDateTime createdAt);

    Book updateBook(UUID bookId,String title, UUID authorId, Genre genre, long price, LocalDateTime updatedAt);

    Book getBookById(UUID bookId);

    List<Book> getBookByTitle(String title);

    List<Book> getBookByGenre(Genre genre);

    boolean deleteBook(UUID bookId);
}
