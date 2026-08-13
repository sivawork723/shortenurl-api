package com.siva.shortenurlapi.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

        @Bean
        public OpenAPI apiInfo() {
            return new OpenAPI()
                    .info(new Info()
                            .title("URL Shortener API")
                            .description("Standard URL shortener with Base62 encoding")
                            .version("v1.0"));
        }
    }
