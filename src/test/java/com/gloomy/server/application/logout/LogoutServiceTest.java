package com.gloomy.server.application.logout;

import com.gloomy.server.domain.logout.Logout;
import com.gloomy.server.domain.logout.LogoutRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@ExtendWith(SpringExtension.class)
@SpringBootTest(properties = {
        "spring.config.location=classpath:test-application.yml,classpath:aws.yml"
})
@Transactional
public class LogoutServiceTest {

    @Autowired
    LogoutRepository logoutRepository;

    private Logout logout;

    @BeforeEach
    public void setUp(){
        logout=Logout.of("log_out_token");
    }

    @Test
    public void getLogoutToken(){
        Logout saveLogout=logoutRepository.save(logout);

        Optional<Logout> findLogout=logoutRepository.findByLogoutToken(logout.getLogoutToken());

        Assertions.assertEquals(findLogout.get().getLogoutToken(),saveLogout.getLogoutToken());
        Assertions.assertEquals(findLogout.get().getId(),saveLogout.getId());
    }
}
