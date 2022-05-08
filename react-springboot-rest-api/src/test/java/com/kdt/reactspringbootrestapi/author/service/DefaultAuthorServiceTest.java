package com.kdt.reactspringbootrestapi.author.service;

import com.kdt.reactspringbootrestapi.author.Author;
import com.kdt.reactspringbootrestapi.author.repository.AuthorJdbcRepository;
import com.kdt.reactspringbootrestapi.author.repository.AuthorRepository;
import com.kdt.reactspringbootrestapi.exception.NoSuchResource;
import com.kdt.reactspringbootrestapi.exception.ResourceDuplication;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

class DefaultAuthorServiceTest {

    AuthorRepository authorRepository = mock(AuthorJdbcRepository.class);

    AuthorService authorService = new DefaultAuthorService(authorRepository);

    Author author = new Author(UUID.randomUUID(), "author");

    @Test
    void getAllAuthors() {
        //Given
        List<Author> authorList = new ArrayList<>();
        for (int i = 0; i<9; i++){
            authorList.add(new Author(UUID.randomUUID(), MessageFormat.format("author{0}", i)));
        }
        when(authorRepository.findAll()).thenReturn(authorList);

        //When
        var resultAllAuthors = authorRepository.findAll();

        //Then
        assertThat(resultAllAuthors)
                .usingRecursiveFieldByFieldElementComparator()
                .hasSameElementsAs(authorList);
    }

    @Test
    void checkDuplicationExist() {
        //Given
        when(authorRepository.findById(author.getAuthorId())).thenReturn(Optional.of(author));

        //When
        var duplicationResult = authorService.checkDuplication(author);

        //Then
        assertThat(duplicationResult).isTrue();
    }

    @Test
    void checkDuplicationNotExist(){
        //Given
        var newAuthor = new Author(UUID.randomUUID(), "newAuthor");
        when(authorRepository.findById(newAuthor.getAuthorId())).thenReturn(Optional.empty());

        //When
        var duplicationResult = authorService.checkDuplication(newAuthor);

        //Then
        assertThat(duplicationResult).isFalse();
    }

    @Test
    void createAuthorNotDuplication() {
        //Given
        var newAuthor = new Author(UUID.randomUUID(), "newAuthor");
        when(authorRepository.findById(any())).thenReturn(Optional.empty());
        when(authorRepository.insert(any())).thenReturn(newAuthor);

        //When
        var createResult = authorService.createAuthor(newAuthor.getAuthorName(), newAuthor.getCreatedAt());

        //Then
        assertThat(createResult).as("Customer"). usingRecursiveComparison().ignoringFields("authorId").isEqualTo(newAuthor);
    }

    @Test
    void createAuthorDuplicated() {
        //Given
        when(authorRepository.findById(any())).thenReturn(Optional.of(author));

        //Then
        assertThatThrownBy(() -> {
            authorService.createAuthor(author.getAuthorName(), author.getCreatedAt());
        }).isInstanceOf(ResourceDuplication.class);
    }

    @Test
    void updateAuthorExist() {
        //Given
        when(authorRepository.findById(author.getAuthorId())).thenReturn(Optional.of(author));
        Author updateAuthor = new Author(author.getAuthorId(),author.getAuthorName(),author.getCreatedAt(),LocalDateTime.now());
        when(authorRepository.update(any())).thenReturn(updateAuthor);

        //When
        var updateResult = authorService.updateAuthor(updateAuthor.getAuthorId(), updateAuthor.getAuthorName(), updateAuthor.getUpdatedAt());

        //Then
        assertThat(updateResult).as("Customer"). usingRecursiveComparison().isEqualTo(updateAuthor);
    }

    @Test
    void updatedAuthorNotExist(){
        //Given
        var newAuthor = new Author(UUID.randomUUID(), "newAuthor");
        when(authorRepository.findById(any())).thenReturn(Optional.empty());

        //Then
        assertThatThrownBy(() -> {
            authorService.updateAuthor(newAuthor.getAuthorId(), newAuthor.getAuthorName(), newAuthor.getUpdatedAt());
        }).isInstanceOf(NoSuchResource.class);
    }

    @Test
    void getAuthorsByName() {
        //Given
        List<Author> authorList = new ArrayList<>();
        authorList.add(author);
        authorList.add(new Author(UUID.randomUUID(), author.getAuthorName()));
        when(authorRepository.findByName(author.getAuthorName())).thenReturn(authorList);

        //When
        var authorByNameList = authorService.getAuthorsByName(author.getAuthorName());

        //Then
        assertThat(authorByNameList)
                .usingRecursiveFieldByFieldElementComparator()
                .hasSameElementsAs(authorList);
    }

    @Test
    void deleteAuthor() {
        //Given
        when(authorRepository.deleteAuthor(author.getAuthorId())).thenReturn(true);

        //When
        var deleteResult = authorService.deleteAuthor(author.getAuthorId());

        //Then
        assertThat(deleteResult).isTrue();
    }
}