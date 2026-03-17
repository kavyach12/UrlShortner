package com.urlshortener.shortener.controller;

import com.urlshortener.shortener.model.UrlMapping;
import com.urlshortener.shortener.repository.UrlRepository;
import com.urlshortener.shortener.service.UrlService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Optional;

@RestController
public class UrlController {

    @Autowired
    private UrlService service;

    @Autowired
    private UrlRepository repo;

    @PostMapping("/shorten")
    public ResponseEntity<String> shorten(
            @RequestParam String url,
            @RequestParam(required = false) String custom,
            @RequestParam(required = false) Integer expireDays) {

        try {

            String code = service.shortenUrl(url, custom);

            if (expireDays != null) {
                service.setExpiry(code, expireDays);
            }

            String shortUrl = ServletUriComponentsBuilder
                    .fromCurrentContextPath()
                    .path("/{code}")
                    .buildAndExpand(code)
                    .toUriString();

            return ResponseEntity.ok(shortUrl);

        } catch (IllegalArgumentException e) {

            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{code:[a-zA-Z0-9]+}")
    public ResponseEntity<?> redirect(@PathVariable String code) {

        Optional<UrlMapping> optionalUrl = repo.findByShortCode(code);

        if (optionalUrl.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Short URL not found");
        }

        UrlMapping url = optionalUrl.get();

        if (url.getExpiresAt() != null &&
                url.getExpiresAt().isBefore(LocalDateTime.now())) {

            return ResponseEntity.status(HttpStatus.GONE)
                    .body("Link expired");
        }

        url.setClickCount(url.getClickCount() + 1);
        repo.save(url);

        return ResponseEntity
                .status(HttpStatus.FOUND)
                .location(URI.create(url.getOriginalUrl()))
                .build();
    }

    // analytics endpoint
    @GetMapping("/analytics/{code}")
    public ResponseEntity<?> analytics(@PathVariable String code) {

        Optional<UrlMapping> optionalUrl = repo.findByShortCode(code);

        if (optionalUrl.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Short URL not found");
        }

        return ResponseEntity.ok(optionalUrl.get());
    }
}