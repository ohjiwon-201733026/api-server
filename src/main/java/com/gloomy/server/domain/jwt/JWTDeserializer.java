package com.gloomy.server.domain.jwt;


import com.fasterxml.jackson.core.JsonProcessingException;

public interface JWTDeserializer {

    JWTPayload jwtPayloadFromJWT(String jwtToken);

    boolean isExpired(String jwtToken);

    boolean isValidToken(String jwtToken);

    Long getUserId(String jwtToken);
}
