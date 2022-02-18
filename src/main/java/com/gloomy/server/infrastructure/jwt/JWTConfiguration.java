package com.gloomy.server.infrastructure.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.charset.StandardCharsets;

@Configuration
class JWTConfiguration {

    private static final byte[] SECRET = "SOME_SIGNATURE_SECRET".getBytes(StandardCharsets.UTF_8);
    private static final int JWT_ACCESS_TOKEN_EXPIRE_TIME = 21599;
    private static final int JWT_REFRESH_TOKEN_EXPIRE_TIME=5183999;

    @Bean
    HmacSHA256JWTService hmacSHA256JWTService(ObjectMapper objectMapper) {
        return new HmacSHA256JWTService(SECRET, JWT_ACCESS_TOKEN_EXPIRE_TIME,JWT_REFRESH_TOKEN_EXPIRE_TIME, objectMapper);
    }

}