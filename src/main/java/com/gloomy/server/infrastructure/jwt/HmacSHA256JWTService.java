package com.gloomy.server.infrastructure.jwt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gloomy.server.domain.jwt.JWTDeserializer;
import com.gloomy.server.domain.jwt.JWTPayload;
import com.gloomy.server.domain.jwt.JWTSerializer;
import com.gloomy.server.domain.user.User;

import java.util.regex.Pattern;

import static com.gloomy.server.application.core.ErrorMessage.invalidTokenErrorMessage;
import static com.gloomy.server.application.core.ErrorMessage.tokenExpired;
import static com.gloomy.server.infrastructure.jwt.Base64URL.*;
import static java.lang.String.format;
import static java.time.Instant.now;
import static java.util.regex.Pattern.compile;

class HmacSHA256JWTService implements JWTSerializer, JWTDeserializer {

    private static final String JWT_HEADER = base64URLFromString("{\"alg\":\"HS256\",\"type\":\"JWT\"}");
    private static final String BASE64URL_PATTERN = "[\\w_\\-]+";
    private static final Pattern JWT_PATTERN = compile(format("^(%s\\.)(%s\\.)(%s)$",
            BASE64URL_PATTERN, BASE64URL_PATTERN, BASE64URL_PATTERN));

    protected final byte[] secret;
    protected final int accessTokenExpireIn;
    protected final int refreshTokenExpireIn;
    private final ObjectMapper objectMapper;

    HmacSHA256JWTService(byte[] secret, int accessTokenExpireIn, int refreshTokenExpireIn, ObjectMapper objectMapper) {
        this.secret = secret;
        this.accessTokenExpireIn = accessTokenExpireIn;
        this.refreshTokenExpireIn = refreshTokenExpireIn;
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

    @Override
    public String createRefreshToken(){
        final var messageToSign = JWT_HEADER.concat(".").concat(refreshJwtPayload());
        final var signature = HmacSHA256.sign(secret, messageToSign);
        return messageToSign.concat(".").concat(base64URLFromBytes(signature));
    }

    private String refreshJwtPayload(){
        var jwtPayload=UserJWTPayload.of(now().getEpochSecond()+refreshTokenExpireIn);
        return base64URLFromString(jwtPayload.toString());
    }
    private String jwtPayloadFromUser(User user) {
        var jwtPayload = UserJWTPayload.of(user, now().getEpochSecond() + accessTokenExpireIn);
        return base64URLFromString(jwtPayload.toString());
    }

    @Override
    public JWTPayload jwtPayloadFromJWT(String jwtToken){
        UserJWTPayload jwtPayload=null;
        try {
            if (!isValidToken(jwtToken)) throw new IllegalArgumentException(invalidTokenErrorMessage);

            final var splintedTokens = jwtToken.split("\\.");
            final var decodedPayload = stringFromBase64URL(splintedTokens[1]);
            jwtPayload= objectMapper.readValue(decodedPayload, UserJWTPayload.class);

            if (isExpired(jwtToken)) throw new IllegalArgumentException(tokenExpired);

        }catch (Exception e){
            throw new IllegalArgumentException(e.getMessage());
        }
        return jwtPayload;
    }

    public boolean isValidToken(String jwtToken){

        final var splintedTokens = jwtToken.split("\\.");
        final var signatureBytes = HmacSHA256.sign(secret, splintedTokens[0].concat(".").concat(splintedTokens[1]));
        if (!JWT_PATTERN.matcher(jwtToken).matches() ||
                !splintedTokens[0].equals(JWT_HEADER) ||
                !base64URLFromBytes(signatureBytes).equals(splintedTokens[2])) return false;

        return true;

    }
    @Override
    public boolean isExpired(String jwtToken)  {
        try {
            final var splintedTokens = jwtToken.split("\\.");
            final var decodedPayload = stringFromBase64URL(splintedTokens[1]);

            UserJWTPayload jwtPayload = objectMapper.readValue(decodedPayload, UserJWTPayload.class);

            if (jwtPayload.isExpired()) return true;
            return false;
        }catch (Exception e){
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public Long getUserId(String jwtToken) {
        final var splintedTokens = jwtToken.split("\\.");
        final var decodedPayload = stringFromBase64URL(splintedTokens[1]);
        UserJWTPayload jwtPayload = null;
        try{
            jwtPayload = objectMapper.readValue(decodedPayload, UserJWTPayload.class);
        }catch (Exception e){
            throw new IllegalArgumentException(e.getMessage());
        }
        return jwtPayload.getUserId();
    }



}