package com.kdt.reactspringbootrestapi.book.service;

import com.kdt.reactspringbootrestapi.author.Author;
import com.kdt.reactspringbootrestapi.book.Book;
import com.kdt.reactspringbootrestapi.book.Genre;
import com.kdt.reactspringbootrestapi.book.repository.BookJdbcRepository;
import com.kdt.reactspringbootrestapi.book.repository.BookRepository;
import com.kdt.reactspringbootrestapi.exception.NoSuchResource;
import com.kdt.reactspringbootrestapi.exception.ResourceDuplication;
import org.junit.jupiter.api.Test;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DefaultBookServiceTest {

    BookRepository bookRepository = mock(BookJdbcRepository.class);

    BookService bookService = new DefaultBookService(bookRepository);

    Author author = new Author(UUID.randomUUID(), "author");
    Book book = new Book(UUID.randomUUID(),"Book", author.getAuthorId(), Genre.HORROR, 20000);

    @Test
    void getAllBooks() {
        //Given
        List<Book> bookList = new ArrayList<>();
        for (int i = 0; i<9; i++){
            bookList.add(new Book(UUID.randomUUID(), MessageFormat.format("Book{0}", i), author.getAuthorId(), Genre.HORROR, i * 10000));
        }
        when(bookRepository.findAll()).thenReturn(bookList);

        //When
        var resultAllBooks = bookRepository.findAll();

        //Then
        assertThat(resultAllBooks)
                .usingRecursiveFieldByFieldElementComparator()
                .hasSameElementsAs(bookList);
    }

    @Test
    void checkDuplicationExist() {
        //Given
        when(bookRepository.findById(book.getBookId())).thenReturn(Optional.of(book));

        //When
        var duplicationResult = bookService.checkDuplication(book);

        //Then
        assertThat(duplicationResult).isTrue();
    }

    @Test
    void checkDuplicationNotExist(){
        //Given
        var newBook = new Book(UUID.randomUUID(),"newBook", author.getAuthorId(), Genre.HORROR, 20000);
        when(bookRepository.findById(newBook.getBookId())).thenReturn(Optional.empty());

        //When
        var duplicationResult = bookService.checkDuplication(newBook);

        //Then
        assertThat(duplicationResult).isFalse();
    }

    @Test
    void createBookNotDuplicated() {
        //Given
        var newBook = new Book(UUID.randomUUID(),"newBook", author.getAuthorId(), Genre.HORROR, 20000);
        when(bookRepository.findById(newBook.getBookId())).thenReturn(Optional.empty());
        when(bookRepository.insert(any())).thenReturn(newBook);

        //When
        var createResult = bookService.createBook(newBook.getTitle(), newBook.getAuthorId(), newBook.getGenre(), newBook.getPrice(), newBook.getCreatedAt());

        //Then
        assertThat(createResult).as("Book"). usingRecursiveComparison().ignoringFields("bookId").isEqualTo(newBook);
    }

    @Test
    void createBookDuplicated(){
        //Given
        when(bookRepository.findById(any())).thenReturn(Optional.of(book));

        //Then
        assertThatThrownBy(() -> {
            bookService.createBook(book.getTitle(), book.getAuthorId(), book.getGenre(), book.getPrice(), book.getCreatedAt());
        }).isInstanceOf(ResourceDuplication.class);
    }

    @Test
    void updateBookExist() {
        //Given
        when(bookRepository.findById(any())).thenReturn(Optional.of(book));
        Book updateBook = new Book(book.getBookId(),"Updated Book", book.getAuthorId(), book.getGenre(), book.getPrice() , book.getCreatedAt(), LocalDateTime.now());
        when(bookRepository.update(any())).thenReturn(updateBook);

        //When
        var updateResult = bookService.updateBook(updateBook.getBookId(), updateBook.getTitle(), updateBook.getAuthorId(), updateBook.getGenre(), updateBook.getPrice(), updateBook.getUpdatedAt());

        //Then
        assertThat(updateResult).as("Book").usingRecursiveComparison().ignoringFields("title", "updatedAt").isEqualTo(updateBook);
    }

    @Test
    void updateBookNotExist(){
        //Given
        var newBook = new Book(UUID.randomUUID(),"newBook", author.getAuthorId(), Genre.HORROR, 20000);
        when(bookRepository.findById(newBook.getBookId())).thenReturn(Optional.empty());

        //Then
        assertThatThrownBy(() -> {
            bookService.updateBook(newBook.getBookId(), newBook.getTitle(), newBook.getAuthorId(), newBook.getGenre(), newBook.getPrice(), newBook.getUpdatedAt());
        }).isInstanceOf(NoSuchResource.class);
    }

    @Test
    void getBookById() {
        //Given
        when(bookRepository.findById(book.getBookId())).thenReturn(Optional.of(book));

        //When
        var foundResult = bookService.getBookById(book.getBookId());

        //Then
        assertThat(foundResult).usingRecursiveComparison().isEqualTo(book);
    }

    @Test
    void getBookByTitle() {
        //Given
        List<Book> bookList = new ArrayList<>();
        bookList.add(book);
        when(bookRepository.findByTitle(book.getTitle())).thenReturn(bookList);

        //When
        var foundResult = bookService.getBookByTitle(book.getTitle());

        //Then
        assertThat(foundResult)
                .usingRecursiveFieldByFieldElementComparator()
                .hasSameElementsAs(bookList);
    }

    @Test
    void getBookByGenre() {
        //Given
        List<Book> bookList = new ArrayList<>();
        bookList.add(book);
        when(bookRepository.findByGenre(book.getGenre())).thenReturn(bookList);

        //When
        var foundResult = bookService.getBookByGenre(book.getGenre());

        //Then
        assertThat(foundResult)
                .usingRecursiveFieldByFieldElementComparator()
                .hasSameElementsAs(bookList);
    }

    @Test
    void deleteBook() {
        //Given
        when(bookRepository.deleteBook(book.getBookId())).thenReturn(true);

        //When
        var deleteResult = bookService.deleteBook(book.getBookId());

        //Then
        assertThat(deleteResult).isTrue();
    }
}