package com.kdt.reactspringbootrestapi.author.repository;

import com.kdt.reactspringbootrestapi.author.Author;
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

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = "classpath:application-test.yml")
class AuthorJdbcRepositoryTest {

    @Autowired
    AuthorRepository authorRepository;

    Author author = new Author(UUID.randomUUID(),"author");

    @BeforeEach
    void setup() {
        authorRepository.insert(author);
    }

    @AfterEach
    void cleanup(){
        authorRepository.deleteAll();
    }

    @Test
    void findAll() {
        List<Author> authorList = new ArrayList<>();
        authorList.add(author);

        for (int i = 0; i<9; i++){
            Author tempAuthor = new Author(UUID.randomUUID(), MessageFormat.format("author{0}", i));
            authorList.add(tempAuthor);
            authorRepository.insert(tempAuthor);
        }

        List<Author> foundAuthorList = authorRepository.findAll();

        assertThat(foundAuthorList).usingRecursiveFieldByFieldElementComparator()
                .hasSameElementsAs(authorList);
    }

    @Test
    void insert() {
        Author newAuthor = new Author(UUID.randomUUID(), "authorInserted");

        var insertResult = authorRepository.insert(newAuthor);

        assertThat(insertResult).as("Author").isEqualTo(newAuthor);

    }


    @Test
    void update() {
        author.setAuthorName("AuthorUpdated");

        var updatedResult = authorRepository.update(author);

        assertThat(updatedResult).as("Author").usingRecursiveComparison().isEqualTo(author);
    }

    @Test
    void findById() {
        var findResult = authorRepository.findById(author.getAuthorId());

        assertThat(findResult.isEmpty()).isFalse();
        assertThat(findResult.get()).as("Author").usingRecursiveComparison().isEqualTo(author);
    }

    @Test
    void findByName() {
        List<Author> authorList = new ArrayList<>();
        authorList.add(author);

        for (int i = 0; i<9; i++){
            Author tempAuthor = new Author(UUID.randomUUID(), author.getAuthorName());
            authorList.add(tempAuthor);
            authorRepository.insert(tempAuthor);
        }

        List<Author> foundAuthorList = authorRepository.findByName(author.getAuthorName());

        assertThat(foundAuthorList).usingRecursiveFieldByFieldElementComparator()
                .hasSameElementsAs(authorList);

    }

    @Test
    void deleteAll() {
        authorRepository.deleteAll();

        var findResult = authorRepository.findAll();

        assertThat(findResult.size()).isEqualTo(0);
    }

    @Test
    void deleteAuthor() {
        var findResult = authorRepository.findAll();

        var deleteResult = authorRepository.deleteAuthor(author);
        findResult.remove(findResult.stream().filter(a -> a.getAuthorId().equals(author.getAuthorId())).collect(Collectors.toList()).get(0));
        var deletedFindResult = authorRepository.findAll();

        assertThat(deleteResult).isTrue();
        assertThat(deletedFindResult).usingRecursiveFieldByFieldElementComparator()
                .hasSameElementsAs(findResult);
    }
}