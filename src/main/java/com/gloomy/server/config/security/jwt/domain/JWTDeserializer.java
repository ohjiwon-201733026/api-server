package com.gloomy.server.config.security.jwt.domain;

public interface JWTDeserializer {

    JWTPayload jwtPayloadFromJWT(String jwtToken);

}
