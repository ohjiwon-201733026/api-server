package com.gloomy.server.infrastructure.jwt;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gloomy.server.application.user.TestUserDTO;
import com.gloomy.server.domain.jwt.JWTPayload;
import com.gloomy.server.domain.user.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.time.Instant;

import static org.apache.tomcat.jni.Time.now;

public class HmacSHA256JWTServiceTest {

    private HmacSHA256JWTService hmacSHA256JWTService;
    private final Long userId=1L;
    private int JWT_ACCESS_TOKEN_EXPIRE_TIME;
    private int JWT_REFRESH_TOKEN_EXPIRE_TIME;

    @BeforeEach
    public void setUp(){
        byte[] secret="TEST_SECRET".getBytes(StandardCharsets.UTF_8);
        JWT_ACCESS_TOKEN_EXPIRE_TIME = 10;
        JWT_REFRESH_TOKEN_EXPIRE_TIME= 100;
        ObjectMapper objectMapper=new ObjectMapper();
        hmacSHA256JWTService=new HmacSHA256JWTService(secret,JWT_ACCESS_TOKEN_EXPIRE_TIME,JWT_REFRESH_TOKEN_EXPIRE_TIME,objectMapper);
    }
    
    @DisplayName("accessToken 발급받기")
    @Test
    public void jwtFromUser() throws JsonProcessingException {
        User user= TestUserDTO.TestUser.makeTestUser();
        user.changeId(userId);
        String accessToken=hmacSHA256JWTService.jwtFromUser(user);
        long now= Instant.now().getEpochSecond();

        JWTPayload jwtPayload= hmacSHA256JWTService.jwtPayloadFromJWT(accessToken);
        Assertions.assertThat(jwtPayload.getUserId()).isEqualTo(userId);
        Assertions.assertThat(jwtPayload.getExpiredTime()- now).isEqualTo(JWT_ACCESS_TOKEN_EXPIRE_TIME);

    }

    @DisplayName("refreshToken 발급받기")
    @Test
    public void createRefreshToken() throws JsonProcessingException {
        String refreshToken=hmacSHA256JWTService.createRefreshToken();
        long now= Instant.now().getEpochSecond();

        JWTPayload jwtPayload=hmacSHA256JWTService.jwtPayloadFromJWT(refreshToken);
        Assertions.assertThat(jwtPayload.getExpiredTime()-now).isEqualTo(JWT_REFRESH_TOKEN_EXPIRE_TIME);
        Assertions.assertThat(jwtPayload.getUserId()).isEqualTo(null);
    }

    @DisplayName("token expire 체크")
    @Test
    public void isExpired(){

    }
}
