package com.gloomy.server.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gloomy.server.application.jwt.JwtServiceTest;
import com.gloomy.server.domain.user.UserService;
import com.gloomy.server.infrastructure.jwt.ExpiredTimeTokenHmacSHA256JWTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.nio.charset.StandardCharsets;

@TestConfiguration
public class TestConfig {

    private static final byte[] SECRET = "SOME_SIGNATURE_SECRET".getBytes(StandardCharsets.UTF_8);
    private static final int JWT_ACCESS_TOKEN_EXPIRE_TIME = 21599;
    private static final int JWT_REFRESH_TOKEN_EXPIRE_TIME=5183999;

//    @Autowired
//    private UserService userService;

//    @Bean
//    public ExpiredTimeTokenHmacSHA256JWTService expiredTimeTokenHmacSHA256JWTService(ObjectMapper objectMapper) {
//        System.out.println("TestConfig.expiredTimeTokenHmacSHA256JWTService");
//        return new ExpiredTimeTokenHmacSHA256JWTService(SECRET, JWT_ACCESS_TOKEN_EXPIRE_TIME,JWT_REFRESH_TOKEN_EXPIRE_TIME, objectMapper);
//    }

//    @Bean
//    public ObjectMapper objectMapper(){
//        return new ObjectMapper();
//    }

//    @Bean
//    public JwtServiceTest jwtServiceTest()


//    @Bean
//    public ApplicationRunner applicationRunner() {
//        return args -> {
//        };
//    }

}
