package com.gloomy.server.config;

import com.gloomy.server.application.user.UserDTO;
import com.gloomy.server.domain.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestConfig {

    @Autowired
    private UserService userService;

    @Bean
    public ApplicationRunner applicationRunner() {
        return args -> {
            UserDTO.PostRequest postRequest = UserDTO.PostRequest.builder()
                    .email("test2@gamil.com")
                    .userName("test2")
                    .password("test234")
                    .build();

            userService.signUp(postRequest);
        };
    }
}
