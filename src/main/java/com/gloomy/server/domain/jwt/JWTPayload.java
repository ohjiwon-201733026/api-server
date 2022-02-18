package com.gloomy.server.domain.jwt;

import java.io.Serializable;

public interface JWTPayload extends Serializable {

    Long getUserId();
    boolean isExpired();
    long getExpiredTime();

}
