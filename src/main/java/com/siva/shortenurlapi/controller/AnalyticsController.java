package com.siva.shortenurlapi.controller;

import com.siva.shortenurlapi.dto.UrlAnalyticsResponse;
import com.siva.shortenurlapi.service.UrlShortenerServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/analytics")
@RequiredArgsConstructor
public class AnalyticsController {
    private final UrlShortenerServiceImpl urlShortenerService;

    @GetMapping("/{alias}")
    public ResponseEntity<UrlAnalyticsResponse> getAnalytics(@PathVariable String alias) {
            log.info("Analytics requested for alias: {}", alias);
            UrlAnalyticsResponse analytics = urlShortenerService.getAnalytics(alias);
            log.info("Analytics fetched for alias {}: clicks={}", alias, analytics.getClickCount());
        return ResponseEntity.ok(analytics);
    }

}
