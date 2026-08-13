package com.siva.shortenurlapi.controller;

import com.siva.shortenurlapi.dto.UrlAnalyticsResponse;
import com.siva.shortenurlapi.service.UrlShortenerServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/analytics")
@RequiredArgsConstructor
public class AnalyticsController {
    private final UrlShortenerServiceImpl urlShortenerService;

    @GetMapping("/{alias}")
    public ResponseEntity<UrlAnalyticsResponse> getAnalytics(@PathVariable String alias) {
            UrlAnalyticsResponse analytics = urlShortenerService.getAnalytics(alias);
        return ResponseEntity.ok(analytics);
    }

}
