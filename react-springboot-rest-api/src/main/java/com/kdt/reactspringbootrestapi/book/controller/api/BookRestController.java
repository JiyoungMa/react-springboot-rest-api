package com.kdt.reactspringbootrestapi.book.controller.api;

import com.kdt.reactspringbootrestapi.book.Genre;
import com.kdt.reactspringbootrestapi.book.dto.request.CreateBookRequest;
import com.kdt.reactspringbootrestapi.book.dto.request.UpdateBookRequest;
import com.kdt.reactspringbootrestapi.book.dto.response.BookResponseDto;
import com.kdt.reactspringbootrestapi.book.service.BookService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
public class BookRestController {

    private final BookService bookService;

    public BookRestController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/api/v1/books")
    public List<BookResponseDto> bookList(@RequestParam Optional<String> title, @RequestParam Optional<String> genre){
        if (title.isPresent()) {
            return bookService.getBookByTitle(title.get()).stream().map(
                    book -> new BookResponseDto(book.getBookId(), book.getTitle(), book.getGenre(), book.getAuthorId(), book.getPrice())
            ).collect(Collectors.toList());
        }else if (genre.isPresent()) {
            return bookService.getBookByGenre(Genre.valueOf(genre.get())).stream().map(
                    book -> new BookResponseDto(book.getBookId(), book.getTitle(), book.getGenre(), book.getAuthorId(), book.getPrice())
            ).collect(Collectors.toList());
        }
        else{
            return bookService.getAllBooks().stream().map(
                    book -> new BookResponseDto(book.getBookId(),book.getTitle(), book.getGenre(), book.getAuthorId(), book.getPrice())
            ).collect(Collectors.toList());
        }
    }

    @PostMapping("/api/v1/books")
    public BookResponseDto createBook(@RequestBody CreateBookRequest bookRequest){
        var createdBook = bookService.createBook(bookRequest.title(), bookRequest.authorId(), Genre.valueOf(bookRequest.genre()), bookRequest.price(), LocalDateTime.now());

        return new BookResponseDto(createdBook.getBookId(), createdBook.getTitle(), createdBook.getGenre(), createdBook.getAuthorId(),createdBook.getPrice());
    }

    @PutMapping("/api/v1/books/{bookId}")
    public BookResponseDto updateBook(@PathVariable("bookId") UUID bookId, @RequestBody UpdateBookRequest bookRequest){
        var updatedBook = bookService.updateBook(bookId, bookRequest.title(),bookRequest.authorId(),Genre.valueOf(bookRequest.genre()),bookRequest.price(), LocalDateTime.now());

        return new BookResponseDto(updatedBook.getBookId(),updatedBook.getTitle(),updatedBook.getGenre(),updatedBook.getAuthorId(), updatedBook.getPrice());
    }

    @DeleteMapping("/api/v1/books/{bookId}")
    public boolean deleteBook(@PathVariable("bookId") UUID bookId){
        return bookService.deleteBook(bookId);
    }
}
