package com.libra_s.libraS.rest;

import com.libra_s.libraS.domain.AppUser;
import com.libra_s.libraS.dtos.BookDto;
import com.libra_s.libraS.dtos.DiscoverPageDto;
import com.libra_s.libraS.dtos.TagDto;
import com.libra_s.libraS.service.AppUserService;
import com.libra_s.libraS.service.BookService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api")
@RestController
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService, AppUserService appUserService) {
        this.bookService = bookService;
    }

    @GetMapping("/books")
    public ResponseEntity<List<BookDto>> getBooks() {
        List<BookDto> result = bookService.getBooks();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/books/discover")
    public ResponseEntity<?> getDiscoverPageInfos() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            AppUser currentUser = (AppUser) authentication.getPrincipal();

            if (currentUser != null) {
                DiscoverPageDto result = bookService.getDiscoverPageInfos(currentUser.getId());
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Must be logged in");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Must be logged in");
        }
    }

    @GetMapping("/book-details/{bookId}")
    public ResponseEntity<?> getBookDetails(
            @PathVariable Long bookId
    ) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            AppUser currentUser = (AppUser) authentication.getPrincipal();

            if (currentUser != null) {
                BookDto result = bookService.getBookDetailsForUser(bookId, currentUser.getId());
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Must be logged in");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Must be logged in");
        }
    }

    @PostMapping("/books/by-tags")
    public ResponseEntity<List<BookDto>> getBooksByTags(
            @RequestBody(required = true) List<TagDto> tagDtos
    ) {
        List<BookDto> result = bookService.getBooksByTags(tagDtos);
        return ResponseEntity.ok(result);
    }
}
