package com.siva.shortenurlapi.service;

import com.siva.shortenurlapi.dto.ShortenRequest;
import com.siva.shortenurlapi.dto.ShortenResponse;
import com.siva.shortenurlapi.dto.UrlAnalyticsResponse;
import com.siva.shortenurlapi.entity.UrlMapping;
import com.siva.shortenurlapi.exception.InvalidAliasException;
import com.siva.shortenurlapi.helper.UrlValidationHelper;
import com.siva.shortenurlapi.repository.UrlMappingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UrlShortenerServiceImplTest {
    private UrlMappingRepository repository;
    private UrlValidationHelper validator;
    private UrlShortenerServiceImpl service;

    @BeforeEach
    void setup() {
        repository = mock(UrlMappingRepository.class);
        validator = mock(UrlValidationHelper.class);

        service = new UrlShortenerServiceImpl(repository, validator);

        // Correct ReflectionTestUtils usage
        ReflectionTestUtils.setField(service, "aliasPattern", "^[a-zA-Z0-9]+$");
        ReflectionTestUtils.setField(service, "shortDomain", "http://short.ly/");
    }

    @Test
    void shortenUrl_ShouldGenerateAlias_WhenNoneProvided() {
        ShortenRequest request = new ShortenRequest();
        request.setLongUrl("https://google.com");

        when(validator.isValidUrl(anyString())).thenReturn(true);

        UrlMapping saved = new UrlMapping();
        saved.setId(100L);
        saved.setLongUrl("https://google.com");
        saved.setCreatedAt(LocalDateTime.now());

        when(repository.save(any())).thenReturn(saved);

        ShortenResponse response = service.shortenUrl(request);

        assertNotNull(response.getAlias());
        assertTrue(response.getShortUrl().contains(response.getAlias()));
    }

    @Test
    void shortenUrl_ShouldThrowException_WhenAliasInvalid() {
        ShortenRequest request = new ShortenRequest();
        request.setLongUrl("https://google.com");
        request.setAlias("invalid-alias!");

        when(validator.isValidUrl(anyString())).thenReturn(true);

        assertThrows(InvalidAliasException.class, () -> service.shortenUrl(request));
    }

    @Test
    void getLongUrlByAlias_ShouldReturnLongUrl() {
        UrlMapping mapping = new UrlMapping();
        mapping.setAlias("abc123");
        mapping.setLongUrl("https://google.com");

        when(repository.findByAlias("abc123")).thenReturn(Optional.of(mapping));

        String result = service.getLongUrlByAlias("abc123");

        assertEquals("https://google.com", result);
    }

    @Test
    void updateAnalytics_ShouldIncrementClickCount() {
        UrlMapping mapping = new UrlMapping();
        mapping.setAlias("abc123");
        mapping.setClickCount(5);

        when(repository.findByAlias("abc123")).thenReturn(Optional.of(mapping));

        service.updateAnalytics("abc123");

        assertEquals(6, mapping.getClickCount());
        verify(repository, times(1)).save(mapping);
    }

    @Test
    void getAnalytics_ShouldReturnAnalyticsResponse() {
        UrlMapping mapping = new UrlMapping();
        mapping.setAlias("abc123");
        mapping.setLongUrl("https://google.com");
        mapping.setClickCount(10);
        mapping.setCreatedAt(LocalDateTime.now());

        when(repository.findByAlias("abc123")).thenReturn(Optional.of(mapping));

        UrlAnalyticsResponse response = service.getAnalytics("abc123");

        assertEquals("abc123", response.getAlias());
        assertEquals(10, response.getClickCount());
    }
}

