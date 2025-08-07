package com.libra_s.libraS.rest;

import com.libra_s.libraS.domain.AppUser;
import com.libra_s.libraS.domain.enums.Role;
import com.libra_s.libraS.dtos.AdminLoginResponseDto;
import com.libra_s.libraS.dtos.LoginUserDto;
import com.libra_s.libraS.service.AuthenticationService;
import com.libra_s.libraS.service.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RequestMapping("/api/admin")
@RestController
public class AdminController {
    
    private final JwtService jwtService;
    private final AuthenticationService authenticationService;

    public AdminController(JwtService jwtService, AuthenticationService authenticationService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
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
} 