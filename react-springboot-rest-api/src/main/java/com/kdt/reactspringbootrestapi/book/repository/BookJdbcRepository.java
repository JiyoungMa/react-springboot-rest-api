package com.kdt.reactspringbootrestapi.book.repository;

import com.kdt.reactspringbootrestapi.JdbcUtils;
import com.kdt.reactspringbootrestapi.book.Book;
import com.kdt.reactspringbootrestapi.book.Genre;
import com.kdt.reactspringbootrestapi.exception.JdbcFailException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class BookJdbcRepository implements BookRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public BookJdbcRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Book> findAll() {
        return jdbcTemplate.query("select * from books",Collections.emptyMap(),bookRowMapper);
    }

    @Override
    public Book insert(Book book) {
        var insertResult = jdbcTemplate.update("INSERT INTO BOOKS(book_id, title, author_id, genre, price, created_at, updated_at) " +
                "VALUES(UNHEX(REPLACE( :book_id, '-', '')), :title, UNHEX(REPLACE( :author_id, '-', '')), :genre, :price, :created_at, :updated_at)",
                toParamMap(book));
        if (insertResult != 1)
            throw new JdbcFailException("Book의 저장이 실패했습니다");

        return book;
    }

    @Override
    public Book update(Book book) {
        var updateResult = jdbcTemplate.update(
                "UPDATE books SET title = :title, author_id = UNHEX(REPLACE( :author_id, '-', '')), genre = :genre, price = :price, updated_at = :updated_at where book_id = UNHEX(REPLACE( :book_id, '-', ''))",
                toParamMap(book));
        if (updateResult != 1)
            throw new JdbcFailException("Book의 Update가 실패했습니다.");

        return book;
    }

    @Override
    public Optional<Book> findById(UUID bookId) {
        try{
            return Optional.ofNullable(jdbcTemplate.queryForObject("select * from books where book_id = UNHEX(REPLACE( :book_id, '-', ''))",
                    Collections.singletonMap("book_id", bookId.toString().getBytes()),bookRowMapper));
        }catch (EmptyResultDataAccessException e){
            return Optional.empty();
        }
    }

    @Override
    public List<Book> findByTitle(String title) {
        return jdbcTemplate.query("select * from books where title = :title",
                Collections.singletonMap("title", title), bookRowMapper);
    }

    @Override
    public List<Book> findByGenre(Genre genre) {
        return jdbcTemplate.query("select * from books where genre = :genre",
                Collections.singletonMap("genre", genre.toString()), bookRowMapper);
    }

    @Override
    public void deleteAll() {
        jdbcTemplate.update("delete from books",Collections.emptyMap());
    }

    @Override
    public boolean deleteBook(UUID bookId) {
        var deleteResult = jdbcTemplate.update("delete from books where book_id = UNHEX(REPLACE( :book_id, '-', ''))",
                Collections.singletonMap("book_id", bookId.toString().getBytes()));

        if (deleteResult != 1)
            return false;
        return true;
    }

    private static final RowMapper<Book> bookRowMapper = (resultSet, i) -> {
        var bookId = JdbcUtils.toUUID(resultSet.getBytes("book_id"));
        var title = resultSet.getString("title");
        var authorId = JdbcUtils.toUUID(resultSet.getBytes("author_id"));
        var genre = Genre.valueOf(resultSet.getString("genre"));
        var price = resultSet.getInt("price");
        var createdAt = JdbcUtils.toLocalDateTime(resultSet.getTimestamp("created_at"));
        var updatedAt = JdbcUtils.toLocalDateTime(resultSet.getTimestamp("updated_at"));

        return new Book(bookId,title,authorId,genre,price,createdAt,updatedAt);
    };

    private Map<String, Object> toParamMap(Book book){
        var paramMap = new HashMap<String, Object>();
        paramMap.put("book_id", book.getBookId().toString().getBytes());
        paramMap.put("title", book.getTitle());
        paramMap.put("author_id", book.getAuthorId().toString().getBytes());
        paramMap.put("genre", book.getGenre().toString());
        paramMap.put("price", book.getPrice());
        paramMap.put("created_at", book.getCreatedAt());
        paramMap.put("updated_at", book.getUpdatedAt());

        return paramMap;
    }
}
