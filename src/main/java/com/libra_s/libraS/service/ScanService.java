package com.libra_s.libraS.service;

import com.libra_s.libraS.domain.AppUser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class ScanService {
    private final BookService bookService;

    @Value("${serpapi.key}")
    private String apiKey;

    private static final String API_URL = "https://serpapi.com/search";

    public ScanService(BookService bookService) {
        this.bookService = bookService;
    }

    public String scan(String imgUrl, AppUser currentUser) {
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
//            ResponseEntity<String> response = restTemplate.exchange(finalUrl, HttpMethod.GET, new HttpEntity<>(new HttpHeaders()), String.class);

//            return response.getBody();
            return imgUrl;
        } catch (Exception ex) {
            System.out.println("Exception:");
            System.out.println(ex.toString());
            return "Error: " + ex.getMessage();
        }
    }

//    public String scan(String imgUrl, AppUser currentUser) {
//        Map<String, String> parameter = new HashMap<>();
//
//        parameter.put("api_key", apiKey);
//        parameter.put("engine", "google_lens");
//        parameter.put("url", "https://firebasestorage.googleapis.com/v0/b/libras-ab46c.appspot.com/o/scans%2F1720896847657.jpg?alt=media&token=3f89d631-0943-488a-808d-ded4c9b88380");
//        parameter.put("hl", "fr");
//        parameter.put("country", "fr");
//
//        GoogleSearch search = new GoogleSearch(parameter);
//
//        try {
//            JsonObject results = search.getJson();
//        } catch (SerpApiSearchException ex) {
//            System.out.println("Exception:");
//            System.out.println(ex.toString());
//        }
//    }
}
