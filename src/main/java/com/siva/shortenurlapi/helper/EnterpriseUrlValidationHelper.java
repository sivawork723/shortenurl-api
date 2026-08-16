package com.siva.shortenurlapi.helper;

import org.springframework.stereotype.Component;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Set;

@Component
public class EnterpriseUrlValidationHelper {

    private static final Set<String> BLOCKED_DOMAINS = Set.of(
            "malware.com",
            "phishing.net",
            "spam.org"
    );

    public boolean isValid(String url){

        if (!isSyntaxValid(url)) return false;
        if (!isSecure(url)) return false;
        if (!isBusinessValid(url)) return false;

        // Optional: enable if you want HEAD reachability check
        // if (!isReachable(normalized)) return false;

        return true;
    }

    // 1. Syntax Validation (Strict RFC 3986)
    public boolean isSyntaxValid(String url) {
        try {
            URI uri = new URI(url);

            if (uri.getScheme() == null) return false;
            if (uri.getHost() == null) return false;

            // Force strict parsing of path
            String path = uri.getRawPath();
            if (path != null && path.contains(" ")) return false;

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // 3. Canonicalization
    public String canonicalize(String url) {
        try {
            URI uri = new URI(url).normalize();
            return uri.toString();
        } catch (Exception e) {
            throw new RuntimeException("URL cannot be canonicalized");
        }
    }

    // 4. Security Validation
    public boolean isSecure(String url) {
        try {
            URI uri = URI.create(url);

            String scheme = uri.getScheme().toLowerCase();
            if (!scheme.equals("http") && !scheme.equals("https")) {
                return false;
            }

            String host = uri.getHost();
            if (host == null) return false;

            // Block internal networks
            if (host.matches("^(10\\.|192\\.168\\.|172\\.(1[6-9]|2[0-9]|3[0-1])\\.).*")) {
                return false;
            }

            // Block localhost
            if (host.equals("localhost") || host.equals("127.0.0.1")) {
                return false;
            }

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // 5. Business Rule Validation
    public boolean isBusinessValid(String url) {
        try {
            URI uri = URI.create(url);
            String host = uri.getHost().toLowerCase();

            if (BLOCKED_DOMAINS.contains(host)) {
                return false;
            }

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // 6. Optional Reachability Check
    public boolean isReachable(String url) {
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("HEAD");
            conn.setConnectTimeout(2000);
            conn.setReadTimeout(2000);
            int code = conn.getResponseCode();
            return code < 400;
        } catch (Exception e) {
            return false;
        }
    }
}
