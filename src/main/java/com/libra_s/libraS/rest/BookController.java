package com.libra_s.libraS.rest;

import com.libra_s.libraS.domain.AppUser;
import com.libra_s.libraS.dtos.BookDto;
import com.libra_s.libraS.dtos.DiscoverPageDto;
import com.libra_s.libraS.repository.AppUserRepository;
import com.libra_s.libraS.service.AppUserService;
import com.libra_s.libraS.service.BookService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RequestMapping("/api")
@RestController
public class BookController {

    private final BookService bookService;

    private final AppUserService appUserService;

    public BookController(BookService bookService, AppUserService appUserService) {
        this.bookService = bookService;
        this.appUserService = appUserService;
    }

    @GetMapping("/books")
    public ResponseEntity<List<BookDto>> getBooks() {
        List<BookDto> result = bookService.getBooks();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/books/discover")
    public ResponseEntity<?> getDiscoverPageInfos() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        AppUser currentUser = (AppUser) authentication.getPrincipal();

        if (currentUser != null){
            DiscoverPageDto result = bookService.getDiscoverPageInfos(currentUser.getId());
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body("Must be logged in");
        }
    }
}
