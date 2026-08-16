package com.siva.shortenurlapi.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShortenRequest {

    @NotBlank(message = "longUrl is required")
    @Pattern(
            regexp = "^(https?://).+",
            message = "URL must start with http:// or https://"
    )
    private String longUrl;
    @NotBlank(message = "Alias is required")
    private String alias;
    @Min(value = 1, message = "Expiry days must be at least 1")
    @Max(value = 14, message = "Expiry days cannot exceed 14")
    private Integer expiryDays;

}
