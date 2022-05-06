package com.kdt.reactspringbootrestapi.book;

import com.kdt.reactspringbootrestapi.author.Author;
import com.kdt.reactspringbootrestapi.author.repository.AuthorRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = "classpath:application-test.yml")
class BookJdbcRepositoryTest {

    @Autowired
    BookRepository bookRepository;

    @Autowired
    AuthorRepository authorRepository;

    Author author = new Author(UUID.randomUUID(),"author");
    Book book = new Book(UUID.randomUUID(),"book",author.getAuthorId(),Genre.COMIC_BOOK,10000);

    @BeforeEach
    void setup() {
        authorRepository.insert(author);
        bookRepository.insert(book);
    }

    @AfterEach
    void cleanup(){
        authorRepository.deleteAll();
        bookRepository.deleteAll();
    }

    @Test
    void findAll() {
        List<Book> bookList = new ArrayList<>();
        bookList.add(book);

        for (int i = 0; i<10; i++){
            Book tempBook = new Book(UUID.randomUUID(), MessageFormat.format("book{0}", i), author.getAuthorId(), Genre.ADVENTURE, i*10000);
            bookList.add(tempBook);
            bookRepository.insert(tempBook);
        }

        List<Book> foundBookList = bookRepository.findAll();

        assertThat(foundBookList).usingRecursiveFieldByFieldElementComparator()
                .hasSameElementsAs(bookList);
    }

    @Test
    void insert() {
        Book newBook = new Book(UUID.randomUUID(), "newBook", author.getAuthorId(),Genre.FANTASY, 20000);

        var insertResult = bookRepository.insert(newBook);

        assertThat(insertResult).as("Book").isEqualTo(newBook);
    }

    @Test
    void update() {
        book.setGenre(Genre.HORROR);

        var updatedResult = bookRepository.update(book);

        assertThat(updatedResult).as("Book").usingRecursiveComparison().isEqualTo(book);
    }

    @Test
    void findById() {
        var findResult = bookRepository.findById(book.getBookId());

        assertThat(findResult.isEmpty()).isFalse();
        assertThat(findResult.get()).as("Book").usingRecursiveComparison().isEqualTo(book);
    }

    @Test
    void findByTitle() {
        List<Book> bookList = new ArrayList<>();
        bookList.add(book);

        for (int i = 0; i<10; i++){
            Book tempBook = new Book(UUID.randomUUID(), book.getTitle(), author.getAuthorId(), Genre.ADVENTURE, i*10000);
            bookList.add(tempBook);
            bookRepository.insert(tempBook);
        }

        List<Book> foundBookList = bookRepository.findByTitle(book.getTitle());

        assertThat(foundBookList).usingRecursiveFieldByFieldElementComparator()
                .hasSameElementsAs(bookList);

    }

    @Test
    void findByGenre() {
        List<Book> bookList = new ArrayList<>();
        bookList.add(book);

        for (int i = 0; i<10; i++){
            Book tempBook = new Book(UUID.randomUUID(), MessageFormat.format("book{0}", i), author.getAuthorId(), book.getGenre(), i*10000);
            bookList.add(tempBook);
            bookRepository.insert(tempBook);
        }

        List<Book> foundBookList = bookRepository.findByGenre(book.getGenre());

        assertThat(foundBookList).usingRecursiveFieldByFieldElementComparator()
                .hasSameElementsAs(bookList);
    }

    @Test
    void deleteAll() {
        bookRepository.deleteAll();

        var findResult = bookRepository.findAll();

        assertThat(findResult.size()).isEqualTo(0);
    }

    @Test
    void deleteBook() {
        var findResult = bookRepository.findAll();

        var deleteResult = bookRepository.deleteBook(book);
        findResult.remove(findResult.stream().filter(a -> a.getBookId().equals(book.getBookId())).collect(Collectors.toList()).get(0));
        var deletedFindResult = bookRepository.findAll();

        assertThat(deleteResult).isTrue();
        assertThat(deletedFindResult).usingRecursiveFieldByFieldElementComparator()
                .hasSameElementsAs(findResult);
    }
}