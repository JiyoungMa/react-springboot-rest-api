package com.kdt.reactspringbootrestapi.author.repository;

import com.kdt.reactspringbootrestapi.JdbcUtils;
import com.kdt.reactspringbootrestapi.author.Author;
import com.kdt.reactspringbootrestapi.exception.JdbcFailException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class AuthorJdbcRepository implements AuthorRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public AuthorJdbcRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Author> findAll() {
        return jdbcTemplate.query("select * from authors",Collections.emptyMap(), authorRowMapper);
    }

    @Override
    public Author insert(Author author) {
        var insertResult = jdbcTemplate.update("INSERT INTO AUTHORS(author_id, author_name, created_at,updated_at) " +
                "VALUES(UNHEX(REPLACE( :author_id, '-', '')), :author_name, :created_at,:updated_at)",toParamMap(author));
        if (insertResult != 1)
            throw new JdbcFailException("Author의 저장이 실패했습니다");

        return author;
    }

    @Override
    public Author update(Author author) {
        var updateResult = jdbcTemplate.update(
                "UPDATE authors SET author_name = :author_name , updated_at = :updated_at where author_id = UNHEX(REPLACE( :author_id, '-', ''))", toParamMap(author));

        if (updateResult != 1)
            throw new JdbcFailException("Author의 Update가 실패했습니다.");

        return author;
    }

    @Override
    public Optional<Author> findById(UUID authorId) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject("select * from authors where author_id = UNHEX(REPLACE( :author_id, '-', ''))",
                    Collections.singletonMap("author_id", authorId.toString().getBytes()), authorRowMapper));
        }catch (EmptyResultDataAccessException e){
            return Optional.empty();
        }
    }

    @Override
    public List<Author> findByName(String name) {
        return jdbcTemplate.query("select * from authors where author_name = :author_name",Collections.singletonMap("author_name",name), authorRowMapper);
    }

    @Override
    public void deleteAll() {
        jdbcTemplate.update("delete from authors",Collections.emptyMap());
    }

    @Override
    public boolean deleteAuthor(Author author) {
        var deleteResult = jdbcTemplate.update("delete from authors where author_id = UNHEX(REPLACE( :author_id, '-', ''))",
                Collections.singletonMap("author_id", author.getAuthorId().toString().getBytes()));

        if (deleteResult != 1)
            return false;
        return true;
    }

    private static final RowMapper<Author> authorRowMapper = (resultSet, i) -> {
        var authorId = JdbcUtils.toUUID(resultSet.getBytes("author_id"));
        var name = resultSet.getString("author_name");
        var createdAt = JdbcUtils.toLocalDateTime(resultSet.getTimestamp("created_at"));
        var updatedAt = JdbcUtils.toLocalDateTime(resultSet.getTimestamp("updated_at"));

        return new Author(authorId,name,createdAt,updatedAt);
    };

    private Map<String, Object> toParamMap(Author author){
        var paramMap = new HashMap<String, Object>();
        paramMap.put("author_id", author.getAuthorId().toString().getBytes());
        paramMap.put("author_name", author.getAuthorName());
        paramMap.put("created_at", author.getCreatedAt());
        paramMap.put("updated_at", author.getUpdatedAt());

        return paramMap;
    }
}
