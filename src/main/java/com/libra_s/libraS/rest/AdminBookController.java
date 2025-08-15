package com.libra_s.libraS.rest;

import com.libra_s.libraS.dtos.BookDto;
import com.libra_s.libraS.dtos.AdminBookDto;
import com.libra_s.libraS.dtos.BookFilterDto;
import com.libra_s.libraS.service.BookService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RequestMapping("/api/admin/books")
@RestController
public class AdminBookController {
    
    private final BookService bookService;

    public AdminBookController(BookService bookService) {
        this.bookService = bookService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getBooksWithFilters(
            @RequestBody(required = false) BookFilterDto filter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        try {
            Pageable pageable = PageRequest.of(page, size);
            BookFilterDto actualFilter = filter != null ? filter : new BookFilterDto();
            
            Page<BookDto> booksPage = bookService.getBooksWithFilters(actualFilter, pageable);
            
            Map<String, Object> response = new HashMap<>();
            response.put("content", booksPage.getContent());
            response.put("totalElements", booksPage.getTotalElements());
            response.put("totalPages", booksPage.getTotalPages());
            response.put("currentPage", booksPage.getNumber());
            response.put("size", booksPage.getSize());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Erreur lors de la récupération des livres");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<BookDto> booksPage = bookService.getBooksWithFilters(new BookFilterDto(), pageable);
            
            Map<String, Object> response = new HashMap<>();
            response.put("content", booksPage.getContent());
            response.put("totalElements", booksPage.getTotalElements());
            response.put("totalPages", booksPage.getTotalPages());
            response.put("currentPage", booksPage.getNumber());
            response.put("size", booksPage.getSize());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Erreur lors de la récupération des livres");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getBookById(@PathVariable Long id) {
        try {
            AdminBookDto book = bookService.getBookById(id);
            if (book != null) {
                return ResponseEntity.ok(book);
            } else {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Livre non trouvé");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Erreur lors de la récupération du livre");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateBook(@PathVariable Long id, @RequestBody AdminBookDto adminBookDto) {
        try {
            AdminBookDto updatedBook = bookService.updateBook(id, adminBookDto);
            if (updatedBook != null) {
                return ResponseEntity.ok(updatedBook);
            } else {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Livre non trouvé");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Erreur lors de la mise à jour du livre: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}
