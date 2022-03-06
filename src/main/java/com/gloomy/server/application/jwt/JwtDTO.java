package com.gloomy.server.application.jwt;

import lombok.*;

import javax.validation.constraints.NotNull;

public class JwtDTO {

    @Builder
    @ToString
    @AllArgsConstructor
    @Getter
    @Setter
    @NoArgsConstructor
    static class Request{
        @NotNull
        String accessToken;
        @NotNull
        String refreshToken;
    }

    @Builder
    @ToString
    @AllArgsConstructor
    @Getter
    @Setter
    @NoArgsConstructor
    static class Response{
        String accessToken;
        String refreshToken;
    }
}
