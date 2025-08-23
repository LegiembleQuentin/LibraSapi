package com.libra_s.libraS.service;

import com.libra_s.libraS.domain.AppUser;
import com.libra_s.libraS.domain.enums.Role;
import com.libra_s.libraS.dtos.LoginUserDto;
import com.libra_s.libraS.dtos.RegisterUserDto;
import com.libra_s.libraS.repository.AppUserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthenticationServiceTest {

    @Mock
    private AppUserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private HttpServletRequest httpRequest;

    @InjectMocks
    private AuthenticationService authenticationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Configuration de l'API key pour les tests
        ReflectionTestUtils.setField(authenticationService, "apiKey", "test-api-key");
        authenticationService.init(); // Initialiser la clé statique
    }

    // Test d'inscription utilisateur : vérifie la création d'un nouvel utilisateur
    @Test
    void signup_withValidData_createsUserSuccessfully() {
        RegisterUserDto registerDto = new RegisterUserDto();
        registerDto.setEmail("test@example.com");
        registerDto.setPassword("password123");
        registerDto.setDisplayname("Test User");

        AppUser savedUser = new AppUser();
        savedUser.setId(1L);
        savedUser.setEmail("test@example.com");
        savedUser.setDisplayname("Test User");

        when(passwordEncoder.encode("password123")).thenReturn("encoded_password");
        when(userRepository.save(any(AppUser.class))).thenReturn(savedUser);

        AppUser result = authenticationService.signup(registerDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("test@example.com", result.getEmail());
        assertEquals("Test User", result.getDisplayname());

        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(any(AppUser.class));
    }

    // Test d'inscription utilisateur : vérifie l'attribution du rôle USER par défaut
    @Test
    void signup_withValidData_assignsUserRole() {
        RegisterUserDto registerDto = new RegisterUserDto();
        registerDto.setEmail("user@example.com");
        registerDto.setPassword("password");
        registerDto.setDisplayname("User");

        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
        when(userRepository.save(any(AppUser.class))).thenAnswer(invocation -> {
            AppUser user = invocation.getArgument(0);
            user.setId(2L);
            return user;
        });

        AppUser result = authenticationService.signup(registerDto);

        assertNotNull(result.getRoles());
        assertEquals(1, result.getRoles().size());
        assertTrue(result.getRoles().contains(Role.USER));
    }

    // Test d'authentification réussie : vérifie l'authentification avec des identifiants valides
    @Test
    void authenticate_withValidCredentials_returnsUser() {
        LoginUserDto loginDto = new LoginUserDto();
        loginDto.setEmail("valid@example.com");
        loginDto.setPassword("correctpassword");

        AppUser existingUser = new AppUser();
        existingUser.setId(1L);
        existingUser.setEmail("valid@example.com");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mock(Authentication.class));
        when(userRepository.findByEmail("valid@example.com")).thenReturn(Optional.of(existingUser));

        AppUser result = authenticationService.authenticate(loginDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("valid@example.com", result.getEmail());

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository).findByEmail("valid@example.com");
    }

    // Test d'authentification échouée : vérifie l'exception avec des identifiants invalides
    @Test
    void authenticate_withInvalidCredentials_throwsException() {
        LoginUserDto loginDto = new LoginUserDto();
        loginDto.setEmail("invalid@example.com");
        loginDto.setPassword("wrongpassword");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThrows(BadCredentialsException.class, () -> {
            authenticationService.authenticate(loginDto);
        });

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository, never()).findByEmail(anyString());
    }

    // Test d'authentification échouée : utilisateur non trouvé après authentification
    @Test
    void authenticate_withUserNotFound_throwsException() {
        LoginUserDto loginDto = new LoginUserDto();
        loginDto.setEmail("notfound@example.com");
        loginDto.setPassword("password");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mock(Authentication.class));
        when(userRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());

        assertThrows(Exception.class, () -> {
            authenticationService.authenticate(loginDto);
        });

        verify(userRepository).findByEmail("notfound@example.com");
    }

    // Test de validation API key : vérifie l'authentification avec une clé API valide
    @Test
    void getAuthentication_withValidApiKey_returnsAuthentication() {
        when(httpRequest.getHeader("API-KEY")).thenReturn("test-api-key");

        Authentication result = AuthenticationService.getAuthentication(httpRequest);

        assertNotNull(result);
        assertTrue(result.isAuthenticated());
        assertEquals("test-api-key", result.getPrincipal()); // L'API key est dans getPrincipal()
        assertNull(result.getCredentials()); // getCredentials() retourne null selon ApiKeyAuthentication

        verify(httpRequest).getHeader("API-KEY");
    }

    // Test de validation API key : vérifie l'exception avec une clé API invalide
    @Test
    void getAuthentication_withInvalidApiKey_throwsException() {
        when(httpRequest.getHeader("API-KEY")).thenReturn("invalid-key");

        assertThrows(BadCredentialsException.class, () -> {
            AuthenticationService.getAuthentication(httpRequest);
        });

        verify(httpRequest).getHeader("API-KEY");
    }

    // Test de validation API key : vérifie l'exception avec une clé API manquante
    @Test
    void getAuthentication_withMissingApiKey_throwsException() {
        when(httpRequest.getHeader("API-KEY")).thenReturn(null);

        assertThrows(BadCredentialsException.class, () -> {
            AuthenticationService.getAuthentication(httpRequest);
        });

        verify(httpRequest).getHeader("API-KEY");
    }

    // Test de validation API key : vérifie l'exception avec une clé API vide
    @Test
    void getAuthentication_withEmptyApiKey_throwsException() {
        when(httpRequest.getHeader("API-KEY")).thenReturn("");

        assertThrows(BadCredentialsException.class, () -> {
            AuthenticationService.getAuthentication(httpRequest);
        });

        verify(httpRequest).getHeader("API-KEY");
    }
}
