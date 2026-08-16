package com.siva.shortenurlapi.service;

import com.siva.shortenurlapi.dto.ShortenRequest;
import com.siva.shortenurlapi.dto.ShortenResponse;
import com.siva.shortenurlapi.dto.UrlAnalyticsResponse;
import com.siva.shortenurlapi.entity.UrlMapping;
import com.siva.shortenurlapi.exception.AliasAlreadyExistsException;
import com.siva.shortenurlapi.exception.InvalidAliasException;
import com.siva.shortenurlapi.exception.UrlAlreadyExistsException;
import com.siva.shortenurlapi.helper.EnterpriseUrlValidationHelper;
import com.siva.shortenurlapi.repository.UrlMappingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UrlShortenerServiceImplTest {
    private UrlMappingRepository repository;
    private EnterpriseUrlValidationHelper validator;
    private UrlShortenerServiceImpl service;

    @BeforeEach
    void setup() {
        repository = mock(UrlMappingRepository.class);
        validator = mock(EnterpriseUrlValidationHelper.class);

        service = new UrlShortenerServiceImpl(repository, validator);

        // Correct ReflectionTestUtils usage
        ReflectionTestUtils.setField(service, "aliasPattern", "^[a-zA-Z0-9]+$");
        ReflectionTestUtils.setField(service, "shortDomain", "http://short.ly/");
    }

    @Test
    void shortenUrl_ShouldGenerateAlias_WhenNoneProvided() {
        ShortenRequest request = new ShortenRequest();
        request.setLongUrl("https://google.com");

        when(validator.isValid(anyString())).thenReturn(true);

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

        when(validator.isValid(anyString())).thenReturn(true);

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

    @Test
    void shortenUrl_shouldReturnExistingAlias_whenLongUrlAlreadyExists() {
        String longUrl = "https://google.com";

        when(validator.isValid(longUrl)).thenReturn(true);
        when(validator.canonicalize(longUrl)).thenReturn(longUrl);

        UrlMapping existing = new UrlMapping();
        existing.setId(1L);
        existing.setAlias("abc123");
        existing.setLongUrl(longUrl);

        when(repository.findByLongUrl(longUrl))
                .thenReturn(Optional.of(existing));

        ShortenRequest request = new ShortenRequest();
        request.setLongUrl(longUrl);

        UrlAlreadyExistsException ex = assertThrows(
                UrlAlreadyExistsException.class,
                () -> service.shortenUrl(request)
        );

        assertTrue(ex.getMessage().contains("abc123"));
    }

    @Test
    void shortenUrl_shouldGenerateNewAlias_whenLongUrlIsNew() {

        String longUrl = "https://newsite.com";
        when(validator.canonicalize(longUrl)).thenReturn(longUrl);
        when(validator.isValid(longUrl)).thenReturn(true);
        when(validator.isValid(longUrl)).thenReturn(true);

        when(repository.findByLongUrl(longUrl)).thenReturn(Optional.empty());

        UrlMapping saved = new UrlMapping();
        saved.setId(100L);
        saved.setLongUrl(longUrl);

        when(repository.save(any())).thenReturn(saved);

        ShortenRequest request = new ShortenRequest();
        request.setLongUrl(longUrl);

        ShortenResponse response = service.shortenUrl(request);

        assertNotNull(response.getAlias());
        assertTrue(response.getAlias().length() > 0);
    }

    @Test
    void shortenUrl_shouldUseCustomAlias_whenProvided() {
        String longUrl = "https://google.com";
        String customAlias = "myalias";

        // URL validation MUST be mocked
        when(validator.canonicalize(longUrl)).thenReturn(longUrl);
        when(validator.isValid(longUrl)).thenReturn(true);

        // Idempotency check
        when(repository.findByLongUrl(longUrl)).thenReturn(Optional.empty());

        // Alias existence check
        when(repository.existsByAlias(customAlias)).thenReturn(false);

        // First save (without alias)
        UrlMapping saved = new UrlMapping();
        saved.setId(10L);
        saved.setLongUrl(longUrl);

        when(repository.save(any())).thenReturn(saved);

        ShortenRequest request = new ShortenRequest();
        request.setLongUrl(longUrl);
        request.setAlias(customAlias);

        ShortenResponse response = service.shortenUrl(request);

        assertEquals(customAlias, response.getAlias());
    }

    @Test
    void shortenUrl_shouldThrowException_whenCustomAliasExists() {
        String longUrl = "https://google.com";
        String customAlias = "taken";

        // URL validation MUST be mocked
        when(validator.isValid(longUrl)).thenReturn(true);

        // Alias existence check
        when(repository.existsByAlias(customAlias)).thenReturn(true);

        ShortenRequest request = new ShortenRequest();
        request.setLongUrl(longUrl);
        request.setAlias(customAlias);

        assertThrows(AliasAlreadyExistsException.class, () -> service.shortenUrl(request));
    }

}

