package com.siva.shortenurlapi.controller;

import com.siva.shortenurlapi.service.UrlShortenerServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/redirect")
@RequiredArgsConstructor
public class RedirectController {
    private final UrlShortenerServiceImpl urlShortenerService;

    @GetMapping("/{alias}")
    public ResponseEntity<Void> redirect(@PathVariable String alias) {

        String longUrl = urlShortenerService.getLongUrlByAlias(alias);

        urlShortenerService.updateAnalytics(alias);
        return ResponseEntity
                .status(HttpStatus.MOVED_PERMANENTLY)
                .location(URI.create(longUrl))
                .build();
    }

}
