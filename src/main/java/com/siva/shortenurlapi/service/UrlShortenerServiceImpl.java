package com.siva.shortenurlapi.service;

import com.siva.shortenurlapi.dto.ShortenRequest;
import com.siva.shortenurlapi.dto.ShortenResponse;
import com.siva.shortenurlapi.dto.UrlAnalyticsResponse;
import com.siva.shortenurlapi.entity.UrlMapping;
import com.siva.shortenurlapi.exception.*;
import com.siva.shortenurlapi.helper.UrlValidationHelper;
import com.siva.shortenurlapi.repository.UrlMappingRepository;
import com.siva.shortenurlapi.util.Base62Encoder;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlShortenerServiceImpl implements UrlShortenerService {

    @Value("${shortener.domain}")
    private String shortDomain;

    @Value("${shortener.alias-pattern}")
    private String aliasPattern;

    private final UrlMappingRepository urlMappingRepository;
    private final UrlValidationHelper urlValidationHelper;

    @Transactional
    public ShortenResponse shortenUrl(ShortenRequest request) {

        // ---------------------------------------------------------
        // STEP 1: Validate long URL (DTO already does basic checks)
        // ---------------------------------------------------------
        log.info("Validating long URL: {}", request.getLongUrl());
        String longUrl = request.getLongUrl().trim();

        if (!urlValidationHelper.isValidUrl(longUrl)) {
            log.warn("Invalid URL format: {}", longUrl);
            throw new InvalidUrlException("Invalid URL format");
        }

        // If long URL already exists, return existing short URL
        Optional<UrlMapping> existing = urlMappingRepository.findByLongUrl(longUrl);

        if (existing.isPresent()) {
            UrlMapping m = existing.get();
            log.info("Idempotent hit: long URL already shortened as alias {}", m.getAlias());

            return ShortenResponse.builder()
                    .shortUrl(shortDomain + m.getAlias())
                    .alias(m.getAlias())
                    .expiryDate(m.getExpiryDate())
                    .build();
        }

        // ---------------------------------------------------------
        // STEP 2: If custom alias is provided → validate + check existence
        // ---------------------------------------------------------
        String alias = request.getAlias();;

        if (alias!= null && !alias.isBlank()) {
            log.info("Custom alias provided: {}", alias);
            alias = request.getAlias().trim();

            // Validate alias format (only a-zA-Z0-9)
            if (!alias.matches(aliasPattern)) {
                log.warn("Invalid alias format: {}", alias);
                throw new InvalidAliasException("Alias must be alphanumeric");
            }

            // Check if alias already exists
            if (urlMappingRepository.existsByAlias(alias)) {
                log.warn("Alias already exists: {}", alias);
                throw new AliasAlreadyExistsException("Alias already exists");
            }
        }

        // ---------------------------------------------------------
        // STEP 3: Create initial record (without alias)
        //         This is required because we need the auto-increment ID
        // ---------------------------------------------------------
        log.info("Saving initial URL mapping record");
        UrlMapping mapping = new UrlMapping();
        mapping.setLongUrl(longUrl);
        mapping.setCreatedAt(LocalDateTime.now());

        if (request.getExpiryDays() != null) {
            mapping.setExpiryDate(LocalDateTime.now().plusDays(request.getExpiryDays()));
        }

        // Save to get auto-increment ID
        mapping = urlMappingRepository.save(mapping);

        // ---------------------------------------------------------
        // STEP 4: If no custom alias → generate Base62 alias from ID
        // ---------------------------------------------------------
        if (alias == null) {
            alias = Base62Encoder.encode(mapping.getId());
            log.info("Generated alias {} for ID {}", alias, mapping.getId());
        }

        // ---------------------------------------------------------
        // STEP 5: Update record with alias
        // ---------------------------------------------------------
        mapping.setAlias(alias);
        urlMappingRepository.save(mapping);
        log.info("Short URL created: {}{}", shortDomain, alias);

        // ---------------------------------------------------------
        // STEP 6: Build response
        // ---------------------------------------------------------
        return ShortenResponse.builder()
                .shortUrl(shortDomain + alias)
                .alias(alias)
                .expiryDate(mapping.getExpiryDate())
                .build();
    }

    public String getLongUrlByAlias(String alias) {

        log.info("Fetching long URL for alias: {}", alias);
        UrlMapping mapping = urlMappingRepository.findByAlias(alias)
                .orElseThrow(() -> new UrlNotFoundException("Alias not found"));

        if (mapping.getExpiryDate() != null &&
                mapping.getExpiryDate().isBefore(LocalDateTime.now())) {
            log.warn("Alias {} has expired", alias);
            throw new ExpiredUrlException("URL has expired");
        }

        return mapping.getLongUrl();
    }

    public void updateAnalytics(String alias) {
        log.info("Updating analytics for alias: {}", alias);
        UrlMapping mapping = urlMappingRepository.findByAlias(alias)
                .orElseThrow(() -> {
                    log.warn("Alias not found during analytics update: {}", alias);
                    return new InvalidAliasException("Alias not found");
                });

        mapping.setClickCount(mapping.getClickCount() + 1);
        mapping.setLastAccessedAt(LocalDateTime.now());

        if (mapping.getExpiryDate() != null &&
                mapping.getExpiryDate().isBefore(LocalDateTime.now())) {
            mapping.setExpired(true);
        }

        urlMappingRepository.save(mapping);
        log.info("Analytics updated for alias {}: clicks={}", alias, mapping.getClickCount());
    }

    public UrlAnalyticsResponse getAnalytics(String alias) {

        UrlMapping mapping = urlMappingRepository.findByAlias(alias)
                .orElseThrow(() -> new InvalidAliasException("Alias not found"));

        return UrlAnalyticsResponse.builder()
                .alias(mapping.getAlias())
                .longUrl(mapping.getLongUrl())
                .clickCount(mapping.getClickCount())
                .createdAt(mapping.getCreatedAt())
                .lastAccessedAt(mapping.getLastAccessedAt())
                .expired(mapping.isExpired())
                .build();
    }

}
