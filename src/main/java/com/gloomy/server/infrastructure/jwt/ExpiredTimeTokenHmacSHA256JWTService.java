package com.gloomy.server.infrastructure.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gloomy.server.domain.jwt.JWTDeserializer;
import com.gloomy.server.domain.jwt.JWTPayload;
import com.gloomy.server.domain.jwt.JWTSerializer;
import com.gloomy.server.domain.user.User;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

import static com.gloomy.server.infrastructure.jwt.Base64URL.base64URLFromBytes;
import static com.gloomy.server.infrastructure.jwt.Base64URL.base64URLFromString;
import static java.lang.String.format;
import static java.time.Instant.now;
import static java.util.regex.Pattern.compile;

public class ExpiredTimeTokenHmacSHA256JWTService implements JWTSerializer {
    private static final String JWT_HEADER = base64URLFromString("{\"alg\":\"HS256\",\"type\":\"JWT\"}");
    private static final String BASE64URL_PATTERN = "[\\w_\\-]+";
    private static final Pattern JWT_PATTERN = compile(format("^(%s\\.)(%s\\.)(%s)$",
            BASE64URL_PATTERN, BASE64URL_PATTERN, BASE64URL_PATTERN));

    protected final byte[] secret;
    protected final int accessTokenExpireIn;
    protected final int refreshTokenExpireIn;
    private final ObjectMapper objectMapper;

    public ExpiredTimeTokenHmacSHA256JWTService(byte[] secret, int accessTokenExpireIn, int refreshTokenExpireIn, ObjectMapper objectMapper) {
        this.secret = secret;
        this.accessTokenExpireIn = accessTokenExpireIn;
        this.refreshTokenExpireIn = refreshTokenExpireIn;
        this.objectMapper = objectMapper;
    }

    @Override
    public String jwtFromUser(User user) {
        final var messageToSign = JWT_HEADER.concat(".").concat(jwtPayloadFromUser(user));
        final var signature = HmacSHA256.sign(secret, messageToSign);
        return messageToSign.concat(".").concat(base64URLFromBytes(signature));
    }

    @Override
    public String createRefreshToken() {
        System.out.println("ExpiredTimeTokenHmacSHA256JWTService.createRefreshToken");
        final var messageToSign = JWT_HEADER.concat(".").concat(refreshJwtPayload());
        final var signature = HmacSHA256.sign(secret, messageToSign);
        return messageToSign.concat(".").concat(base64URLFromBytes(signature));
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
