package com.libra_s.libraS.rest;

import com.libra_s.libraS.domain.AppUser;
import com.libra_s.libraS.dtos.DiscoverPageDto;
import com.libra_s.libraS.service.ScanService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api")
public class ScanController {
    private final ScanService scanService;

    public ScanController(ScanService scanService) {
        this.scanService = scanService;
    }

//    @GetMapping("/scan")
//    public ResponseEntity<String>  scan(@RequestParam("imgUrl") String imgUrl) {
//        try {
//            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//            AppUser currentUser = (AppUser) authentication.getPrincipal();
//
//            if (currentUser != null) {
//                String result = scanService.scan(imgUrl, currentUser);
//                return ResponseEntity.ok(result);
//            } else {
//                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Must be logged in");
//            }
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Must be logged in");
//        }
//    }

    @PostMapping("/scan")
    public ResponseEntity<String> handleFileUpload(@RequestParam("image") MultipartFile file) {
        if (file.isEmpty()) {
            return new ResponseEntity<>("Please select a file to upload", HttpStatus.BAD_REQUEST);
        }

        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            AppUser currentUser = (AppUser) authentication.getPrincipal();

            if (currentUser != null) {
                String result = scanService.scan(file);
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Must be logged in");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Must be logged in");
        }
    }
}
