package com.gloomy.server.infrastructure.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gloomy.server.domain.jwt.JWTDeserializer;
import com.gloomy.server.domain.jwt.JWTPayload;
import com.gloomy.server.domain.jwt.JWTSerializer;
import com.gloomy.server.domain.user.User;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

import static com.gloomy.server.infrastructure.jwt.Base64URL.*;
import static java.lang.String.format;
import static java.time.Instant.now;
import static java.util.regex.Pattern.compile;

class HmacSHA256JWTService implements JWTSerializer, JWTDeserializer {

    private static final String JWT_HEADER = base64URLFromString("{\"alg\":\"HS256\",\"type\":\"JWT\"}");
    private static final String BASE64URL_PATTERN = "[\\w_\\-]+";
    private static final Pattern JWT_PATTERN = compile(format("^(%s\\.)(%s\\.)(%s)$",
            BASE64URL_PATTERN, BASE64URL_PATTERN, BASE64URL_PATTERN));

    private final byte[] secret;
    private final long durationSeconds;
    private final ObjectMapper objectMapper;

    HmacSHA256JWTService(byte[] secret, long durationSeconds, ObjectMapper objectMapper) {
        this.secret = secret;
        this.durationSeconds = durationSeconds;
        this.objectMapper = objectMapper;
    }

    // jwtSerializer
    /**
     * User -> String token
     */
    @Override
    public String jwtFromUser(User user) {
        final var messageToSign = JWT_HEADER.concat(".").concat(jwtPayloadFromUser(user));
        final var signature = HmacSHA256.sign(secret, messageToSign);
        return messageToSign.concat(".").concat(base64URLFromBytes(signature));
    }

    private String jwtPayloadFromUser(User user) {
        var jwtPayload = UserJWTPayload.of(user, now().getEpochSecond() + durationSeconds);
        return base64URLFromString(jwtPayload.toString());
    }

    @Override
    public JWTPayload jwtPayloadFromJWT(String jwtToken) {
        isValidToken(jwtToken);

        final var splintedTokens = jwtToken.split("\\.");
        try {
            final var decodedPayload = stringFromBase64URL(splintedTokens[1]);
            UserJWTPayload jwtPayload = objectMapper.readValue(decodedPayload, UserJWTPayload.class);
            isExpired(jwtPayload);
            return jwtPayload;
        } catch (Exception exception) {
            throw new IllegalArgumentException(exception);
        }
    }

    private void isValidToken(String jwtToken){

        final var splintedTokens = jwtToken.split("\\.");
        final var signatureBytes = HmacSHA256.sign(secret, splintedTokens[0].concat(".").concat(splintedTokens[1]));
        if (!JWT_PATTERN.matcher(jwtToken).matches() ||
                !splintedTokens[0].equals(JWT_HEADER) ||
                !base64URLFromBytes(signatureBytes).equals(splintedTokens[2])) {
            throw new IllegalArgumentException("올바르지 않은 토큰");
        }

    }

    private void isExpired(JWTPayload jwtPayload){
        if (jwtPayload.isExpired()) {
            throw new IllegalArgumentException("Token expired");
        }
    }



}
