package com.kdt.reactspringbootrestapi.author.controller.api;

import com.kdt.reactspringbootrestapi.author.dto.request.CreateAuthorRequest;
import com.kdt.reactspringbootrestapi.author.dto.request.UpdateAuthorRequest;
import com.kdt.reactspringbootrestapi.author.dto.response.AuthorResponseDto;
import com.kdt.reactspringbootrestapi.author.service.AuthorService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
public class AuthorRestController {

    private final AuthorService authorService;

    public AuthorRestController(AuthorService authorService) {
        this.authorService = authorService;
    }

    @GetMapping("/api/v1/authors")
    public List<AuthorResponseDto> authorList(@RequestParam Optional<String> authorName){
        if (authorName.isPresent()){
            return authorService.getAuthorsByName(authorName.get()).stream().map(
                    author -> new AuthorResponseDto(author.getAuthorId(), author.getAuthorName()))
                    .collect(Collectors.toList());
        }else{
            return authorService.getAllAuthors().stream().map(
                    author -> new AuthorResponseDto(author.getAuthorId(), author.getAuthorName()))
                    .collect(Collectors.toList());
        }
    }

    @PostMapping("/api/v1/authors")
    public AuthorResponseDto createAuthor(@RequestBody CreateAuthorRequest authorRequest){
        var createdAuthor = authorService.createAuthor(authorRequest.authorName(), LocalDateTime.now());

        return new AuthorResponseDto(createdAuthor.getAuthorId(), createdAuthor.getAuthorName());
    }

    @PutMapping("/api/v1/authors/{authorId}")
    public AuthorResponseDto updateAuthor(@PathVariable("authorId") UUID authorId, @RequestBody UpdateAuthorRequest authorRequest){
        var updatedAuthor = authorService.updateAuthor(authorId, authorRequest.authorName(), LocalDateTime.now());

        return new AuthorResponseDto(updatedAuthor.getAuthorId(), updatedAuthor.getAuthorName());
    }

    @DeleteMapping("/api/v1/authors/{authorId}")
    public boolean deleteAuthor(@PathVariable("authorId") UUID authorId){
        return authorService.deleteAuthor(authorId);
    }
}
