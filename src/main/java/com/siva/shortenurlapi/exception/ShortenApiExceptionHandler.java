package com.siva.shortenurlapi.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class ShortenApiExceptionHandler {

    @ExceptionHandler(InvalidUrlException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidUrl(InvalidUrlException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", "INVALID_URL");
        body.put("message", ex.getMessage());
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(InvalidAliasException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidAlias(InvalidAliasException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", "INVALID_ALIAS");
        body.put("message", ex.getMessage());
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(MethodArgumentNotValidException ex) {

        Map<String, Object> response = new HashMap<>();

        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .findFirst()
                .orElse("Validation error");

        response.put("error", "VALIDATION_FAILED");
        response.put("message", message);

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(AliasAlreadyExistsException.class)
    public ResponseEntity<Map<String, Object>> handleAliasAlreadyExists(AliasAlreadyExistsException ex) {

        log.warn("Alias already exists error: {}", ex.getMessage());

        Map<String, Object> body = new HashMap<>();
        body.put("error", "ALIAS_ALREADY_EXISTS");
        body.put("message", ex.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(UrlNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(UrlNotFoundException ex) {
        return ResponseEntity.status(404).body(Map.of(
                "error", "URL_NOT_FOUND",
                "message", ex.getMessage()
        ));
    }

    @ExceptionHandler(ExpiredUrlException.class)
    public ResponseEntity<Map<String, Object>> handleExpired(ExpiredUrlException ex) {
        return ResponseEntity.status(410).body(Map.of(
                "error", "URL_EXPIRED",
                "message", ex.getMessage()
        ));
    }

    @ExceptionHandler(RateLimitExceededException.class)
    public ResponseEntity<Map<String, Object>> handleRateLimit(RateLimitExceededException ex) {
        return ResponseEntity.status(429).body(Map.of(
                "error", "RATE_LIMIT_EXCEEDED",
                "message", ex.getMessage()
        ));
    }

}
