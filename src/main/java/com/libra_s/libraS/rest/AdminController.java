package com.libra_s.libraS.rest;

import com.libra_s.libraS.domain.AppUser;
import com.libra_s.libraS.domain.enums.Role;
import com.libra_s.libraS.dtos.AdminLoginResponseDto;
import com.libra_s.libraS.dtos.BookDto;
import com.libra_s.libraS.dtos.BookFilterDto;
import com.libra_s.libraS.dtos.LoginUserDto;
import com.libra_s.libraS.service.AuthenticationService;
import com.libra_s.libraS.service.BookService;
import com.libra_s.libraS.service.JwtService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestMapping("/api/admin")
@RestController
public class AdminController {
    
    private final JwtService jwtService;
    private final AuthenticationService authenticationService;
    private final BookService bookService;

    public AdminController(JwtService jwtService, AuthenticationService authenticationService, BookService bookService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
        this.bookService = bookService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> adminLogin(@RequestBody LoginUserDto loginUserDto) {
        try {
            AppUser authenticatedUser = authenticationService.authenticate(loginUserDto);
            
            if (!authenticatedUser.getRoles().contains(Role.ADMIN)) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Accès refusé. Seuls les administrateurs peuvent se connecter.");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
            }

            String jwtToken = jwtService.generateToken(authenticatedUser);

            AdminLoginResponseDto loginResponse = new AdminLoginResponseDto();
            loginResponse.setToken(jwtToken);
            loginResponse.setExpiresIn(jwtService.getExpirationTime());
            loginResponse.setUserId(authenticatedUser.getId());
            loginResponse.setDisplayName(authenticatedUser.getDisplayname());
            loginResponse.setEmail(authenticatedUser.getEmail());
            loginResponse.setRoles(authenticatedUser.getRoles());

            return ResponseEntity.ok(loginResponse);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Identifiants invalides");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }

    @GetMapping("/verify")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> verifyAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AppUser user = (AppUser) authentication.getPrincipal();
        Map<String, Object> response = new HashMap<>();
        response.put("valid", true);
        response.put("user", Map.of(
            "id", user.getId(),
            "displayName", user.getDisplayname(),
            "email", user.getEmail(),
            "roles", user.getRoles()
        ));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/books")
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
} 