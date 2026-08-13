package com.siva.shortenurlapi.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UrlAnalyticsResponse {
    private String alias;
    private String longUrl;
    private long clickCount;
    private LocalDateTime createdAt;
    private LocalDateTime lastAccessedAt;
    private boolean expired;
}

