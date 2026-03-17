
package com.urlshortener.shortener.service;

import com.urlshortener.shortener.model.UrlMapping;
import com.urlshortener.shortener.repository.UrlRepository;
import com.urlshortener.shortener.util.Base62Util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class UrlService {

    private static final Pattern URL_SCHEME_PATTERN =
            Pattern.compile("^[a-zA-Z][a-zA-Z0-9+.-]*://.*$");

    @Autowired
    private UrlRepository repo;

    public String shortenUrl(String originalUrl, String customCode) {

        String normalizedUrl = normalizeUrl(originalUrl);

        Optional<UrlMapping> existingUrl = repo.findByOriginalUrl(normalizedUrl);

        if (existingUrl.isPresent()) {
            return existingUrl.get().getShortCode();
        }

        // custom short url
        if (customCode != null && !customCode.isBlank()) {

            Optional<UrlMapping> existing = repo.findByShortCode(customCode);

            if (existing.isPresent()) {
                throw new IllegalArgumentException("Custom short code already exists");
            }

            UrlMapping url = new UrlMapping();
            url.setOriginalUrl(normalizedUrl);
            url.setShortCode(customCode);
            url.setCreatedAt(LocalDateTime.now());
            url.setClickCount(0);

            repo.save(url);

            return customCode;
        }

        // auto generated short url
        UrlMapping url = new UrlMapping();
        url.setOriginalUrl(normalizedUrl);
        url.setCreatedAt(LocalDateTime.now());
        url.setClickCount(0);

        repo.save(url);

        String shortCode = Base62Util.encode(url.getId());

        url.setShortCode(shortCode);
        repo.save(url);

        return shortCode;
    }

    public void setExpiry(String code, int days) {

        UrlMapping url = repo.findByShortCode(code)
                .orElseThrow(() -> new RuntimeException("Short URL not found"));

        url.setExpiresAt(LocalDateTime.now().plusDays(days));

        repo.save(url);
    }

    private String normalizeUrl(String originalUrl) {

        if (originalUrl == null || originalUrl.trim().isEmpty()) {
            throw new IllegalArgumentException("URL is required");
        }

        String candidateUrl = originalUrl.trim();

        if (!URL_SCHEME_PATTERN.matcher(candidateUrl).matches()) {
            candidateUrl = "https://" + candidateUrl;
        }

        URI parsedUri = URI.create(candidateUrl);

        if (parsedUri.getHost() == null || parsedUri.getHost().isBlank()) {
            throw new IllegalArgumentException("Enter a valid URL");
        }

        return parsedUri.toString();
    }
}
