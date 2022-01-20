package com.gloomy.server.domain.jwt;

public interface JWTDeserializer {

    JWTPayload jwtPayloadFromJWT(String jwtToken);

    Long getUserId(String jwtToken);

}
