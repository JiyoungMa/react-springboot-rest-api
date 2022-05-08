package com.kdt.reactspringbootrestapi.author.service;

import com.kdt.reactspringbootrestapi.author.Author;
import com.kdt.reactspringbootrestapi.author.repository.AuthorRepository;
import com.kdt.reactspringbootrestapi.exception.NoSuchResource;
import com.kdt.reactspringbootrestapi.exception.ResourceDuplication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class DefaultAuthorService implements AuthorService {

    private final AuthorRepository authorRepository;

    public DefaultAuthorService(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    @Override
    public List<Author> getAllAuthors() {
        return authorRepository.findAll();
    }

    @Override
    public boolean checkDuplication(Author author) {
        return authorRepository.findById(author.getAuthorId()).isPresent();
    }

    @Override
    public Author createAuthor(String authorName, LocalDateTime createdAt) {
        Author author = new Author(UUID.randomUUID(),authorName,createdAt,createdAt);
        if (checkDuplication(author))
            throw new ResourceDuplication("동일한 Author이 이미 존재합니다.");

        return authorRepository.insert(author);
    }

    @Override
    public Author updateAuthor(UUID authorId, String authorName, LocalDateTime updatedAt) {
        Author author = new Author(authorId, authorName, updatedAt);

        var foundResult = authorRepository.findById(authorId);
        if (foundResult.isEmpty())
            throw new NoSuchResource("일치하는 Author이 없습니다.");

        return authorRepository.update(author);
    }

    @Override
    public List<Author> getAuthorsByName(String authorName) {
        return authorRepository.findByName(authorName);
    }

    @Override
    public boolean deleteAuthor(UUID authorId) {
        return authorRepository.deleteAuthor(authorId);
    }
}
