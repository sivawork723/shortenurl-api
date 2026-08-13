package com.siva.shortenurlapi.service;

import com.siva.shortenurlapi.dto.ShortenRequest;
import com.siva.shortenurlapi.dto.ShortenResponse;
import com.siva.shortenurlapi.dto.UrlAnalyticsResponse;

public interface UrlShortenerService {
    ShortenResponse shortenUrl(ShortenRequest request);

    String getLongUrlByAlias(String alias);

    UrlAnalyticsResponse getAnalytics(String alias);

    void updateAnalytics(String alias);

}
