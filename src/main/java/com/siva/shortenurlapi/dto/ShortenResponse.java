package com.siva.shortenurlapi.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ShortenResponse {

    private String shortUrl;
    private String alias;
    private LocalDateTime expiryDate;

}
