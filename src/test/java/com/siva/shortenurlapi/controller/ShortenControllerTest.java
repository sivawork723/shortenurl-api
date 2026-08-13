package com.siva.shortenurlapi.controller;

import com.siva.shortenurlapi.dto.ShortenRequest;
import com.siva.shortenurlapi.dto.ShortenResponse;
import com.siva.shortenurlapi.service.UrlShortenerService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ShortenControllerTest {

    @Test
    void shortenUrl_ShouldReturn201AndResponseBody() {
        UrlShortenerService service = mock(UrlShortenerService.class);
        ShortenController controller = new ShortenController(service);

        ShortenRequest request = new ShortenRequest();
        request.setLongUrl("https://google.com");

        ShortenResponse mockResponse = ShortenResponse.builder()
                .alias("abc123")
                .shortUrl("http://short.ly/abc123")
                .build();

        when(service.shortenUrl(request)).thenReturn(mockResponse);

        var response = controller.shortenUrl(request);

        assertEquals(201, response.getStatusCode().value());
        assertEquals(mockResponse, response.getBody());
    }

    @Test
    void shortenUrl_ShouldThrowException_WhenInvalidUrl() {
        UrlShortenerService service = mock(UrlShortenerService.class);
        ShortenController controller = new ShortenController(service);

        ShortenRequest request = new ShortenRequest();
        request.setLongUrl("invalid-url");

        when(service.shortenUrl(request))
                .thenThrow(new RuntimeException("Invalid URL"));

        assertThrows(RuntimeException.class, () -> controller.shortenUrl(request));
    }
}

