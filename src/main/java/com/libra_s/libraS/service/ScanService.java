package com.libra_s.libraS.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.libra_s.libraS.dtos.BookDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

@Service
public class ScanService {
    private final BookService bookService;

    @Value("${serpapi.key}")
    private String apiKey;

    @Value("${firebase.service-account.path}")
    private String jsonFilePath;

//    private String jsonFileName = "firebase-service-account.json";

    private String bucketName = "libras-ab46c.appspot.com";

    private static final String API_URL = "https://serpapi.com/search";

    private final ResourceLoader resourceLoader;

    public ScanService(BookService bookService, ResourceLoader resourceLoader) {
        this.bookService = bookService;
        this.resourceLoader = resourceLoader;
    }



    public String requestToReverseImgSearch(String imgUrl) {
        RestTemplate restTemplate = new RestTemplate();

        Map<String, String> parameters = new HashMap<>();
        parameters.put("api_key", apiKey);
        parameters.put("engine", "google_lens");
        parameters.put("url", imgUrl);
        parameters.put("hl", "fr");
        parameters.put("country", "fr");

        StringBuilder urlBuilder = new StringBuilder(API_URL);
        urlBuilder.append("?");
        parameters.forEach((key, value) -> urlBuilder.append(key).append("=").append(value).append("&"));
        String finalUrl = urlBuilder.toString();

        try {
            ResponseEntity<String> response = restTemplate.exchange(finalUrl, HttpMethod.GET, new HttpEntity<>(new HttpHeaders()), String.class);

            return response.getBody();
        } catch (Exception ex) {
            System.out.println("Exception:");
            System.out.println(ex.toString());
            return "Error: " + ex.getMessage();
        }
    }

    public String upload(MultipartFile multipartFile) {
        Path tempDir = null;
        try {
            String fileName = multipartFile.getOriginalFilename();
            fileName = UUID.randomUUID().toString().concat(this.getExtension(fileName));

            File file = this.convertToFile(multipartFile, fileName);
            String URL = this.uploadFile(file, fileName);
            file.delete();
            return URL;
        } catch (Exception e) {
            e.printStackTrace();
            return "Image couldn't upload, Something went wrong";
        } finally {
            if (tempDir != null && Files.exists(tempDir)) {
                try {
                    Files.walk(tempDir)
                            .map(Path::toFile)
                            .forEach(File::delete);
                    Files.deleteIfExists(tempDir);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public String uploadFile(File file, String fileName) throws IOException {
        String uuid = UUID.randomUUID().toString();
        BlobId blobId = BlobId.of(bucketName, fileName);

        BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                .setContentType("image/jpeg")
                .setMetadata(Map.of("firebaseStorageDownloadTokens", uuid))
                .build();

        InputStream inputStream = this.getJsonFileInputStream();
        Credentials credentials = GoogleCredentials.fromStream(inputStream);
        Storage storage = StorageOptions.newBuilder().setCredentials(credentials).build().getService();
        storage.create(blobInfo, Files.readAllBytes(file.toPath()));

        String DOWNLOAD_URL = "https://firebasestorage.googleapis.com/v0/b/" + bucketName + "/o/%s?alt=media&token=" + uuid;
        return String.format(DOWNLOAD_URL, URLEncoder.encode(fileName, StandardCharsets.UTF_8));
    }

    private File convertToFile(MultipartFile multipartFile, String fileName) throws IOException {
        //on créer un repertoire temporaire que l'on supprimera apres l'upload de l'image sur firebase storage
        Path tempDir = Files.createTempDirectory("tempFiles");

        File tempFile = new File(tempDir.toFile(), fileName);

        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            fos.write(multipartFile.getBytes());
        }
        return tempFile;
    }

    private String getExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf("."));
    }

    public InputStream getJsonFileInputStream() throws IOException {
        Resource resource = resourceLoader.getResource(jsonFilePath);
        return resource.getInputStream();
    }

    public String scan(MultipartFile file) {
        String imgUrl = upload(file);
        String jsonResult = requestToReverseImgSearch(imgUrl);
        List<String> titles = mapJsonResultToList(jsonResult);

        return "";

//        List<BookDto> bookResult = bookService.searchBooksByList(titles);
//        BookDto book = bookResult.get(0);
//
//        // Return or process the titles as needed
//        return book.getId().toString();
    }

    public List<String> mapJsonResultToList(String jsonResult) {
        List<String> titles = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            JsonNode root = objectMapper.readTree(jsonResult);
            //on va chercher les 20 1ers resultats de la recherche
            int count = 0;
            int maxResults = 20;

            //Extraction des titres des resultats
            JsonNode textResults = root.path("text_results");
            if (textResults.isArray()) {
                for (JsonNode textResult : textResults) {
                    if (count >= maxResults) break;
                    String text = textResult.path("text").asText(null);
                    if (text != null) {
                        titles.add(text);
                        count++;
                    }
                }
            }

            //Extractions des visuals matches si le titre n'a pas été bien scanné visuellement
            JsonNode visualMatches = root.path("visual_matches");
            if (visualMatches.isArray()) {
                for (JsonNode visualMatch : visualMatches) {
                    if (count >= maxResults) break;
                    String title = visualMatch.path("title").asText(null);
                    if (title != null) {
                        titles.add(title);
                        count++;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return titles;
    }
}
