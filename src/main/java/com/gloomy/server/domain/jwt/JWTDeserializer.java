package com.gloomy.server.domain.jwt;


public interface JWTDeserializer {

    JWTPayload jwtPayloadFromJWT(String jwtToken);

    boolean isExpired(String jwtToken);

    boolean isValidToken(String jwtToken);

    Long getUserId(String jwtToken);
}
