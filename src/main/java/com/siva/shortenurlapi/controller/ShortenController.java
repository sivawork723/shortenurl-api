package com.siva.shortenurlapi.controller;

import com.siva.shortenurlapi.dto.ShortenRequest;
import com.siva.shortenurlapi.dto.ShortenResponse;
import com.siva.shortenurlapi.service.UrlShortenerService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/shorten")
public class ShortenController {

    private final UrlShortenerService urlShortenerService;

    public ShortenController(UrlShortenerService urlShortenerService) {
        this.urlShortenerService = urlShortenerService;
    }

    @PostMapping
    public ResponseEntity<ShortenResponse> shortenUrl(@Valid @RequestBody ShortenRequest request) {

        ShortenResponse response = urlShortenerService.shortenUrl(request);
        return ResponseEntity.status(201).body(response);

    }

}
