package com.siva.shortenurlapi.controller;

import com.siva.shortenurlapi.dto.UrlAnalyticsResponse;
import com.siva.shortenurlapi.service.UrlShortenerServiceImpl;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AnalyticsControllerTest {

    @Test
    void getAnalytics_ShouldReturnAnalyticsResponse() {
        UrlShortenerServiceImpl service = mock(UrlShortenerServiceImpl.class);
        AnalyticsController controller = new AnalyticsController(service);

        String alias = "abc123";

        UrlAnalyticsResponse mockResponse = UrlAnalyticsResponse.builder()
                .alias(alias)
                .longUrl("https://google.com")
                .clickCount(10)
                .build();

        when(service.getAnalytics(alias)).thenReturn(mockResponse);

        var response = controller.getAnalytics(alias);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(mockResponse, response.getBody());
    }

    @Test
    void getAnalytics_ShouldThrowException_WhenAliasInvalid() {
        UrlShortenerServiceImpl service = mock(UrlShortenerServiceImpl.class);
        AnalyticsController controller = new AnalyticsController(service);

        String alias = "invalid";

        when(service.getAnalytics(alias))
                .thenThrow(new RuntimeException("Alias not found"));

        assertThrows(RuntimeException.class, () -> controller.getAnalytics(alias));
    }
}

