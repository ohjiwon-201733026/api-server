package com.gloomy.server.infrastructure.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gloomy.server.domain.user.User;
import org.springframework.stereotype.Service;

import static com.gloomy.server.infrastructure.jwt.Base64URL.base64URLFromString;
import static java.time.Instant.now;


//@Service
public class ExpiredTimeTokenHmacSHA256JWTServiceTest extends HmacSHA256JWTService{
    ExpiredTimeTokenHmacSHA256JWTServiceTest(byte[] secret, int accessTokenExpireIn, int refreshTokenExpireIn, ObjectMapper objectMapper) {
        super(secret, accessTokenExpireIn, refreshTokenExpireIn, objectMapper);
    }

    private String refreshJwtPayload(){
        var jwtPayload=UserJWTPayload.of(now().getEpochSecond()-refreshTokenExpireIn);
        return base64URLFromString(jwtPayload.toString());
    }
    private String jwtPayloadFromUser(User user) {
        var jwtPayload = UserJWTPayload.of(user, now().getEpochSecond() - accessTokenExpireIn);
        return base64URLFromString(jwtPayload.toString());
    }
}
