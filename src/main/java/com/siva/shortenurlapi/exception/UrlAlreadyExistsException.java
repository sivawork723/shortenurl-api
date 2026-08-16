package com.siva.shortenurlapi.exception;

public class UrlAlreadyExistsException extends RuntimeException {
    private final String alias;
    private final String shortUrl;

    public UrlAlreadyExistsException(String message, String alias, String shortUrl) {
        super(message);
        this.alias = alias;
        this.shortUrl = shortUrl;
    }

    public String getAlias() {
        return alias;
    }

    public String getShortUrl() {
        return shortUrl;
    }
}
