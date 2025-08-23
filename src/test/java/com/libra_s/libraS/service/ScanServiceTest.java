package com.libra_s.libraS.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ScanServiceTest {

    @Mock
    private BookService bookService;

    @Mock
    private ResourceLoader resourceLoader;

    @Mock
    private MultipartFile multipartFile;

    @Mock
    private Resource resource;

    @InjectMocks
    private ScanService scanService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Configuration des propriétés via ReflectionTestUtils
        ReflectionTestUtils.setField(scanService, "apiKey", "test-api-key");
        ReflectionTestUtils.setField(scanService, "jsonFilePath", "classpath:config/test-firebase.json");
    }

    // Test de validation des formats d'image : vérifie l'extraction correcte de l'extension
    @Test
    void getExtension_withValidImageFormats_extractsCorrectExtension() throws Exception {
        // Utilisation de la réflexion pour accéder à la méthode privée
        java.lang.reflect.Method getExtensionMethod = ScanService.class.getDeclaredMethod("getExtension", String.class);
        getExtensionMethod.setAccessible(true);

        assertEquals(".jpg", getExtensionMethod.invoke(scanService, "image.jpg"));
        assertEquals(".jpeg", getExtensionMethod.invoke(scanService, "photo.jpeg"));
        assertEquals(".png", getExtensionMethod.invoke(scanService, "screenshot.png"));
        assertEquals(".gif", getExtensionMethod.invoke(scanService, "animation.gif"));
        assertEquals(".bmp", getExtensionMethod.invoke(scanService, "bitmap.bmp"));
        assertEquals(".webp", getExtensionMethod.invoke(scanService, "modern.webp"));
    }

    // Test de validation des formats d'image : cas limites avec noms de fichiers complexes
    @Test
    void getExtension_withComplexFilenames_extractsCorrectExtension() throws Exception {
        java.lang.reflect.Method getExtensionMethod = ScanService.class.getDeclaredMethod("getExtension", String.class);
        getExtensionMethod.setAccessible(true);

        assertEquals(".jpg", getExtensionMethod.invoke(scanService, "my.photo.with.dots.jpg"));
        assertEquals(".png", getExtensionMethod.invoke(scanService, "file-name_with-special.chars.png"));
        assertEquals(".jpeg", getExtensionMethod.invoke(scanService, "IMG_20231201_143045.jpeg"));
    }

    // Test de validation des formats d'image : gestion des cas d'erreur dans getExtension
    @Test
    void getExtension_withInvalidFilenames_returnsEmptyString() throws Exception {
        java.lang.reflect.Method getExtensionMethod = ScanService.class.getDeclaredMethod("getExtension", String.class);
        getExtensionMethod.setAccessible(true);

        assertEquals("", getExtensionMethod.invoke(scanService, (String) null));
        assertEquals("", getExtensionMethod.invoke(scanService, ""));
        assertEquals("", getExtensionMethod.invoke(scanService, "   "));
        assertEquals("", getExtensionMethod.invoke(scanService, "filename_without_extension"));
        assertEquals("", getExtensionMethod.invoke(scanService, "filename."));
    }

    // Test de gestion d'erreurs : vérifie le comportement avec un fichier null
    @Test
    void upload_withNullFile_returnsErrorMessage() {
        String result = scanService.upload(null);
        
        assertTrue(result.contains("Image couldn't upload, Something went wrong"));
    }

    // Test de gestion d'erreurs : vérifie le comportement avec un fichier sans nom
    @Test
    void upload_withFileWithoutName_handlesGracefully() {
        when(multipartFile.getOriginalFilename()).thenReturn(null);
        
        String result = scanService.upload(multipartFile);
        
        assertTrue(result.contains("Image couldn't upload, Something went wrong"));
    }

    // Test de gestion d'erreurs : vérifie le comportement avec un fichier sans extension
    @Test
    void upload_withFileWithoutExtension_handlesGracefully() {
        when(multipartFile.getOriginalFilename()).thenReturn("filename_without_extension");
        
        String result = scanService.upload(multipartFile);
        
        assertTrue(result.contains("Image couldn't upload, Something went wrong"));
    }

    // Test de gestion d'erreurs : vérifie le comportement avec une IOException lors de la lecture du fichier JSON
    @Test
    void getJsonFileInputStream_withInvalidPath_throwsIOException() throws IOException {
        when(resourceLoader.getResource(anyString())).thenReturn(resource);
        when(resource.getInputStream()).thenThrow(new IOException("File not found"));

        assertThrows(IOException.class, () -> scanService.getJsonFileInputStream());
    }

    // Test de gestion d'erreurs : vérifie le comportement avec un fichier JSON valide
    @Test
    void getJsonFileInputStream_withValidPath_returnsInputStream() throws IOException {
        InputStream mockInputStream = new ByteArrayInputStream("test content".getBytes());
        when(resourceLoader.getResource(anyString())).thenReturn(resource);
        when(resource.getInputStream()).thenReturn(mockInputStream);

        InputStream result = scanService.getJsonFileInputStream();

        assertNotNull(result);
        verify(resourceLoader).getResource(anyString());
        verify(resource).getInputStream();
    }

    // Test de parsing JSON : vérifie l'extraction des titres à partir d'un JSON valide
    @Test
    void mapJsonResultToList_withValidJson_extractsTitles() {
        String validJson = """
            {
                "text_results": [
                    {"text": "One Piece Volume 1"},
                    {"text": "Naruto Chapter 1"},
                    {"text": "Dragon Ball Z"}
                ],
                "visual_matches": [
                    {"title": "Attack on Titan"},
                    {"title": "Death Note"}
                ]
            }
            """;

        List<String> result = scanService.mapJsonResultToList(validJson);

        assertNotNull(result);
        assertEquals(5, result.size());
        assertTrue(result.contains("One Piece Volume 1"));
        assertTrue(result.contains("Naruto Chapter 1"));
        assertTrue(result.contains("Dragon Ball Z"));
        assertTrue(result.contains("Attack on Titan"));
        assertTrue(result.contains("Death Note"));
    }

    // Test de parsing JSON : vérifie la gestion d'un JSON vide
    @Test
    void mapJsonResultToList_withEmptyJson_returnsEmptyList() {
        String emptyJson = "{}";

        List<String> result = scanService.mapJsonResultToList(emptyJson);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // Test de nettoyage des titres : vérifie la suppression des termes indésirables
    @Test
    void cleanSearchTitles_withTermsToRemove_cleansCorrectly() {
        List<String> titles = Arrays.asList(
            "One Piece Volume 1",
            "Naruto Tome 5",
            "Dragon Ball vol 3",
            "Attack on Titan t2",
            "Death Note mangadex"
        );

        List<String> result = ScanService.cleanSearchTitles(titles);

        assertNotNull(result);
        assertTrue(result.contains("one piece"));
        assertTrue(result.contains("naruto"));
        assertTrue(result.contains("dragon ball"));
        assertTrue(result.contains("attack on titan"));
        assertTrue(result.contains("death note"));
    }

    // Test de nettoyage des titres : vérifie la suppression des caractères indésirables
    @Test
    void cleanSearchTitles_withSpecialCharacters_cleansCorrectly() {
        List<String> titles = Arrays.asList(
            "One Piece---",
            "Naruto...",
            "Dragon Ball___",
            "Attack on Titan-._-",
            ""  // titre vide
        );

        List<String> result = ScanService.cleanSearchTitles(titles);

        assertNotNull(result);
        assertEquals(4, result.size()); // Le titre vide doit être exclu
        assertTrue(result.contains("one piece"));
        assertTrue(result.contains("naruto"));
        assertTrue(result.contains("dragon ball"));
        assertTrue(result.contains("attack on titan"));
    }

    // Test de tri des titres : vérifie le regroupement et tri par fréquence
    @Test
    void groupAndSortTitles_withDuplicates_sortsCorrectly() {
        List<String> titles = Arrays.asList(
            "one piece", "naruto", "one piece", "dragon ball", 
            "one piece", "naruto", "attack on titan"
        );

        List<String> result = ScanService.groupAndSortTitles(titles);

        assertNotNull(result);
        assertEquals(7, result.size());
        // "one piece" apparaît 3 fois, donc doit être en premier
        assertEquals("one piece", result.get(0));
        assertEquals("one piece", result.get(1));
        assertEquals("one piece", result.get(2));
        // "naruto" apparaît 2 fois
        assertEquals("naruto", result.get(3));
        assertEquals("naruto", result.get(4));
    }
}
