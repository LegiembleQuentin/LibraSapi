package com.libra_s.libraS.service;

import com.libra_s.libraS.domain.AppUser;
import com.libra_s.libraS.domain.enums.Role;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class JwtServiceTest {

    @InjectMocks
    private JwtService jwtService;

    private AppUser testUser;
    private UserDetails mockUserDetails;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Configuration des propriétés pour les tests
        ReflectionTestUtils.setField(jwtService, "secretKey", "dGVzdC1zZWNyZXQta2V5LWZvci1qd3QtdGVzdGluZy1wdXJwb3Nlcy1vbmx5LXRoaXMtaXMtdmVyeS1sb25nLWtleQ==");
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", 86400000L); // 24 heures

        // Création d'un utilisateur de test
        testUser = new AppUser();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setDisplayname("Test User");
        testUser.setRoles(Arrays.asList(Role.USER, Role.ADMIN));

        // Mock UserDetails simple
        mockUserDetails = mock(UserDetails.class);
        when(mockUserDetails.getUsername()).thenReturn("simple@example.com");
    }

    // Test de génération de token : vérifie la création d'un token avec AppUser
    @Test
    void generateToken_withAppUser_createsValidToken() {
        String token = jwtService.generateToken(testUser);

        assertNotNull(token);
        assertTrue(token.length() > 0);
        assertTrue(token.contains(".")); // JWT contient des points
        
        // Le token doit être composé de 3 parties séparées par des points
        String[] parts = token.split("\\.");
        assertEquals(3, parts.length);
    }

    // Test de génération de token : vérifie la création avec UserDetails simple
    @Test
    void generateToken_withSimpleUserDetails_createsValidToken() {
        String token = jwtService.generateToken(mockUserDetails);

        assertNotNull(token);
        assertTrue(token.length() > 0);
        
        String[] parts = token.split("\\.");
        assertEquals(3, parts.length);
    }

    // Test d'extraction des claims : vérifie l'extraction du username
    @Test
    void extractUsername_fromValidToken_returnsCorrectUsername() {
        String token = jwtService.generateToken(testUser);

        String extractedUsername = jwtService.extractUsername(token);

        assertEquals("test@example.com", extractedUsername);
    }

    // Test d'extraction des claims : vérifie l'extraction des rôles
    @Test
    void extractRoles_fromValidToken_returnsCorrectRoles() {
        String token = jwtService.generateToken(testUser);

        List<String> extractedRoles = jwtService.extractRoles(token);

        assertNotNull(extractedRoles);
        assertEquals(2, extractedRoles.size());
        assertTrue(extractedRoles.contains("USER"));
        assertTrue(extractedRoles.contains("ADMIN"));
    }

    // Test d'extraction des claims : vérifie l'extraction de l'userId
    @Test
    void extractUserId_fromValidToken_returnsCorrectUserId() {
        String token = jwtService.generateToken(testUser);

        Long extractedUserId = jwtService.extractUserId(token);

        assertEquals(1L, extractedUserId);
    }

    // Test d'extraction des claims : vérifie que les rôles sont null pour UserDetails simple
    @Test
    void extractRoles_fromSimpleUserDetailsToken_returnsNull() {
        String token = jwtService.generateToken(mockUserDetails);

        List<String> extractedRoles = jwtService.extractRoles(token);

        assertNull(extractedRoles); // Pas de rôles pour UserDetails simple
    }

    // Test de validation de token : vérifie la validation avec le bon utilisateur
    @Test
    void isTokenValid_withCorrectUser_returnsTrue() {
        String token = jwtService.generateToken(testUser);

        boolean isValid = jwtService.isTokenValid(token, testUser);

        assertTrue(isValid);
    }

    // Test de validation de token : vérifie l'échec avec un mauvais utilisateur
    @Test
    void isTokenValid_withWrongUser_returnsFalse() {
        String token = jwtService.generateToken(testUser);

        AppUser differentUser = new AppUser();
        differentUser.setEmail("different@example.com");

        boolean isValid = jwtService.isTokenValid(token, differentUser);

        assertFalse(isValid);
    }

    // Test de token expiré : vérifie qu'une exception est lancée avec un token expiré
    @Test
    void isTokenValid_withExpiredToken_throwsExpiredJwtException() {
        // Créer un token avec une expiration très courte
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", 1L); // 1 milliseconde
        
        String token = jwtService.generateToken(testUser);
        
        // Attendre que le token expire
        try {
            Thread.sleep(10); // Attendre 10ms pour être sûr que le token expire
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // isTokenValid() lance ExpiredJwtException car elle appelle extractUsername()
        assertThrows(ExpiredJwtException.class, () -> {
            jwtService.isTokenValid(token, testUser);
        });
    }

    // Test de validation de token : vérifie que les tokens valides ne sont pas expirés
    @Test
    void isTokenValid_withValidNonExpiredToken_returnsTrue() {
        // Remettre une expiration normale pour ce test
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", 86400000L); // 24 heures
        
        String token = jwtService.generateToken(testUser);
        
        // Le token vient d'être créé, il ne devrait pas être expiré
        boolean isValid = jwtService.isTokenValid(token, testUser);
        
        assertTrue(isValid);
    }

    // Test de token expiré : vérifie l'exception lors de l'extraction d'un token expiré
    @Test
    void extractUsername_withExpiredToken_throwsExpiredJwtException() {
        // Créer un token avec une expiration très courte
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", 1L);
        
        String token = jwtService.generateToken(testUser);
        
        // Attendre que le token expire
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        assertThrows(ExpiredJwtException.class, () -> {
            jwtService.extractUsername(token);
        });
    }

    // Test avec token malformé : vérifie l'exception avec un token invalide
    @Test
    void extractUsername_withMalformedToken_throwsMalformedJwtException() {
        String malformedToken = "invalid.token.format";

        assertThrows(MalformedJwtException.class, () -> {
            jwtService.extractUsername(malformedToken);
        });
    }

    // Test avec différents types d'utilisateurs : utilisateur avec rôle unique
    @Test
    void generateToken_withSingleRoleUser_extractsCorrectly() {
        AppUser singleRoleUser = new AppUser();
        singleRoleUser.setId(2L);
        singleRoleUser.setEmail("user@example.com");
        singleRoleUser.setDisplayname("Simple User");
        singleRoleUser.setRoles(Arrays.asList(Role.USER));

        String token = jwtService.generateToken(singleRoleUser);
        
        List<String> extractedRoles = jwtService.extractRoles(token);
        Long extractedUserId = jwtService.extractUserId(token);
        String extractedUsername = jwtService.extractUsername(token);

        assertEquals(1, extractedRoles.size());
        assertTrue(extractedRoles.contains("USER"));
        assertEquals(2L, extractedUserId);
        assertEquals("user@example.com", extractedUsername);
    }

    // Test avec différents types d'utilisateurs : utilisateur admin seul
    @Test
    void generateToken_withAdminUser_extractsCorrectly() {
        AppUser adminUser = new AppUser();
        adminUser.setId(3L);
        adminUser.setEmail("admin@example.com");
        adminUser.setDisplayname("Admin User");
        adminUser.setRoles(Arrays.asList(Role.ADMIN));

        String token = jwtService.generateToken(adminUser);
        
        List<String> extractedRoles = jwtService.extractRoles(token);
        Long extractedUserId = jwtService.extractUserId(token);

        assertEquals(1, extractedRoles.size());
        assertTrue(extractedRoles.contains("ADMIN"));
        assertEquals(3L, extractedUserId);
    }

    // Test de l'expiration time : vérifie le getter
    @Test
    void getExpirationTime_returnsConfiguredValue() {
        long expirationTime = jwtService.getExpirationTime();

        assertEquals(86400000L, expirationTime); // 24 heures en millisecondes
    }

    // Test de validation avec token null : vérifie la gestion des tokens null
    @Test
    void extractUsername_withNullToken_throwsException() {
        assertThrows(Exception.class, () -> {
            jwtService.extractUsername(null);
        });
    }

    // Test de validation avec token vide : vérifie la gestion des tokens vides
    @Test
    void extractUsername_withEmptyToken_throwsException() {
        assertThrows(Exception.class, () -> {
            jwtService.extractUsername("");
        });
    }
}
