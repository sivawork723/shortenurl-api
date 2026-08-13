package com.siva.shortenurlapi.service;

import com.siva.shortenurlapi.dto.ShortenRequest;
import com.siva.shortenurlapi.dto.ShortenResponse;
import com.siva.shortenurlapi.dto.UrlAnalyticsResponse;
import com.siva.shortenurlapi.entity.UrlMapping;
import com.siva.shortenurlapi.exception.AliasAlreadyExistsException;
import com.siva.shortenurlapi.exception.InvalidAliasException;
import com.siva.shortenurlapi.exception.InvalidUrlException;
import com.siva.shortenurlapi.helper.UrlValidationHelper;
import com.siva.shortenurlapi.repository.UrlMappingRepository;
import com.siva.shortenurlapi.util.Base62Encoder;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UrlShortenerServiceImpl implements UrlShortenerService {

    @Value("${shortener.domain}")
    private String shortDomain;

    @Value("${shortener.alias-pattern}")
    private String aliasPattern;

    private final UrlMappingRepository urlMappingRepository;
    private final UrlValidationHelper urlValidationHelper;

    public ShortenResponse shortenUrl(ShortenRequest request) {

        // ---------------------------------------------------------
        // STEP 1: Validate long URL (DTO already does basic checks)
        // ---------------------------------------------------------
        String longUrl = request.getLongUrl().trim();

        if (!urlValidationHelper.isValidUrl(longUrl)) {
            throw new InvalidUrlException("Invalid URL format");
        }

        // ---------------------------------------------------------
        // STEP 2: If custom alias is provided → validate + check existence
        // ---------------------------------------------------------
        String alias = null;

        if (request.getAlias() != null && !request.getAlias().isBlank()) {

            alias = request.getAlias().trim();

            // Validate alias format (only a-zA-Z0-9)
            if (!alias.matches(aliasPattern)) {
                throw new InvalidAliasException("Alias must be alphanumeric");
            }

            // Check if alias already exists
            if (urlMappingRepository.existsByAlias(alias)) {
                throw new AliasAlreadyExistsException("Alias already exists");
            }
        }

        // ---------------------------------------------------------
        // STEP 3: Create initial record (without alias)
        //         This is required because we need the auto-increment ID
        // ---------------------------------------------------------
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
        }

        // ---------------------------------------------------------
        // STEP 5: Update record with alias
        // ---------------------------------------------------------
        mapping.setAlias(alias);
        urlMappingRepository.save(mapping);

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

        UrlMapping mapping = urlMappingRepository.findByAlias(alias)
                .orElseThrow(() -> new InvalidAliasException("Alias not found"));

        if (mapping.getExpiryDate() != null &&
                mapping.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new InvalidUrlException("URL has expired");
        }

        return mapping.getLongUrl();
    }

    public void updateAnalytics(String alias) {
        UrlMapping mapping = urlMappingRepository.findByAlias(alias)
                .orElseThrow(() -> new InvalidAliasException("Alias not found"));

        mapping.setClickCount(mapping.getClickCount() + 1);
        mapping.setLastAccessedAt(LocalDateTime.now());

        if (mapping.getExpiryDate() != null &&
                mapping.getExpiryDate().isBefore(LocalDateTime.now())) {
            mapping.setExpired(true);
        }

        urlMappingRepository.save(mapping);
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
