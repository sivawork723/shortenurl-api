package com.siva.shortenurlapi.controller;

import com.siva.shortenurlapi.service.UrlShortenerServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@Slf4j
@RestController
@RequestMapping("/api/v1/redirect")
@RequiredArgsConstructor
public class RedirectController {
    private final UrlShortenerServiceImpl urlShortenerService;

    @GetMapping("/{alias}")
    public ResponseEntity<Void> redirect(@PathVariable String alias) {

        log.info("Redirect requested for alias: {}", alias);
        String longUrl = urlShortenerService.getLongUrlByAlias(alias);
        log.info("Alias {} resolved to {}", alias, longUrl);
        urlShortenerService.updateAnalytics(alias);
        log.info("Analytics updated for alias: {}", alias);
        return ResponseEntity
                .status(HttpStatus.MOVED_PERMANENTLY)
                .location(URI.create(longUrl))
                .build();
    }

}
