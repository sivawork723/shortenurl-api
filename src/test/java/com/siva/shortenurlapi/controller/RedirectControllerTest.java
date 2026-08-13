package com.siva.shortenurlapi.controller;

import com.siva.shortenurlapi.service.UrlShortenerServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class RedirectControllerTest {

    @Test
    void redirect_ShouldReturn301AndLocationHeader() {
        UrlShortenerServiceImpl service = mock(UrlShortenerServiceImpl.class);
        RedirectController controller = new RedirectController(service);

        String alias = "abc123";
        String longUrl = "https://google.com";

        when(service.getLongUrlByAlias(alias)).thenReturn(longUrl);

        ResponseEntity<Void> response = controller.redirect(alias);

        assertEquals(301, response.getStatusCode().value());
        assertEquals(URI.create(longUrl), response.getHeaders().getLocation());
        verify(service, times(1)).updateAnalytics(alias);
    }

    @Test
    void redirect_ShouldThrowException_WhenAliasNotFound() {
        UrlShortenerServiceImpl service = mock(UrlShortenerServiceImpl.class);
        RedirectController controller = new RedirectController(service);

        String alias = "unknown";

        when(service.getLongUrlByAlias(alias))
                .thenThrow(new RuntimeException("Alias not found"));

        assertThrows(RuntimeException.class, () -> controller.redirect(alias));
    }
}
