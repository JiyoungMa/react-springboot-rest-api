package com.kdt.reactspringbootrestapi.book.service;

import com.kdt.reactspringbootrestapi.book.Book;
import com.kdt.reactspringbootrestapi.book.Genre;
import com.kdt.reactspringbootrestapi.book.repository.BookRepository;
import com.kdt.reactspringbootrestapi.exception.NoSuchResource;
import com.kdt.reactspringbootrestapi.exception.ResourceDuplication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class DefaultBookService implements BookService {

    private final BookRepository bookRepository;

    public DefaultBookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    @Override
    public boolean checkDuplication(Book book) {
        return bookRepository.findById(book.getBookId()).isPresent();
    }

    @Override
    public Book createBook(String title, UUID authorId, Genre genre, long price, LocalDateTime createdAt) {
        Book book = new Book(UUID.randomUUID(),title, authorId, genre,price, createdAt, createdAt);
        if (checkDuplication(book))
            throw new ResourceDuplication("동일한 Book이 이미 존재합니다.");
        return bookRepository.insert(book);
    }

    @Override
    public Book updateBook(UUID bookId, String title, UUID authorId, Genre genre, long price, LocalDateTime updatedAt) {
        Book book = new Book(bookId, title, authorId, genre, price, updatedAt, updatedAt);

        var foundResult = bookRepository.findById(book.getBookId());
        if (foundResult.isEmpty())
            throw new NoSuchResource("일치하는 Book이 없습니다.");

        return bookRepository.update(book);
    }

    @Override
    public Book getBookById(UUID bookId) {
        var foundResult = bookRepository.findById(bookId);
        if (foundResult.isEmpty())
            throw new NoSuchResource("일치하는 Book 없습니다.");

        return foundResult.get();
    }

    @Override
    public List<Book> getBookByTitle(String title) {
        return bookRepository.findByTitle(title);
    }

    @Override
    public List<Book> getBookByGenre(Genre genre) {
        return bookRepository.findByGenre(genre);
    }

    @Override
    public boolean deleteBook(UUID bookId) {
        return bookRepository.deleteBook(bookId);
    }
}
