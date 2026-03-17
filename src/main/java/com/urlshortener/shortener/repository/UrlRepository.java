package com.urlshortener.shortener.repository;

import com.urlshortener.shortener.model.UrlMapping;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UrlRepository extends JpaRepository<UrlMapping, Long> {

    // find mapping by short code (used for redirect)
    Optional<UrlMapping> findByShortCode(String shortCode);

    // find mapping by original URL (used to reuse existing short link)
    Optional<UrlMapping> findByOriginalUrl(String originalUrl);
}