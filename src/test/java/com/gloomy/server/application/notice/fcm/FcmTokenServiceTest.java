package com.gloomy.server.application.notice.fcm;


import com.gloomy.server.application.user.TestUserDTO;
import com.gloomy.server.domain.notice.fcm.FcmToken;
import com.gloomy.server.domain.notice.fcm.FcmTokenService;
import com.gloomy.server.domain.user.User;
import com.gloomy.server.domain.user.UserRepository;
import groovy.util.logging.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest(properties = {
        "spring.config.location=classpath:test-application.yml,classpath:aws.yml"
})
@Transactional
public class FcmTokenServiceTest {

    @Autowired
    FcmTokenService fcmTokenService;

    @Autowired
    UserRepository userRepository;

    private User user;
    private String fcmToken;
    private FcmDto.Request fcmDto;

    @BeforeEach
    public void setUp(){
        user= TestUserDTO.TestUser.makeTestUser();
        User saveUser=userRepository.save(user);
        fcmToken="fcmToken";
        fcmDto=new FcmDto.Request(saveUser.getId(),fcmToken);
    }

    @DisplayName("fcmToken save")
    @Test
    public void save(){
        FcmToken saveFcmToken=fcmTokenService.saveFcmToken(fcmDto);

        Assertions.assertEquals(saveFcmToken.getUserId().getId(),fcmDto.getUserId());
        Assertions.assertEquals(saveFcmToken.getFcmToken(),fcmDto.getFcmToken());
    }
}
