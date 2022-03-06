package com.gloomy.server.application.notice.fcm;


import com.gloomy.server.application.user.TestUserDTO;
import com.gloomy.server.domain.notice.fcm.FcmTokenService;
import com.gloomy.server.domain.user.User;
import groovy.util.logging.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest(properties = {
        "spring.config.location=classpath:test-application.yml,classpath:aws.yml"
})
public class FcmTokenServiceMockTest {

    @Autowired
    FcmTokenService fcmTokenService;

    private User user;
    private String fcmToken;

    @BeforeEach
    public void setUp(){
        user= TestUserDTO.TestUser.makeTestUser();
        fcmToken="fcmToken";
    }

    @DisplayName("fcmToken save")
    @Test
    public void save(){

    }
}
