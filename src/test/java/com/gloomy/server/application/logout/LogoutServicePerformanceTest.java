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
import java.util.Random;

@ExtendWith(SpringExtension.class)
@SpringBootTest(properties = {
        "spring.config.location=classpath:application.yml,classpath:aws.yml"
})
@Transactional
public class LogoutServicePerformanceTest {
    @Autowired
    LogoutRepository logoutRepository;

    private Logout logout;

    @BeforeEach
    public void setUp(){
        logout=Logout.of("log_out_token");
    }

    private String [] alphabet={
            "A","B","C","D","E","F","G","H","I","G","K","L","M","N","O","P",
            "Q","R","S","T","U","V","W","X","Y","Z",
            "a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p",
            "q","r","s","t","u","v","w","x","y","z"
    };
//    @Test
//    public void getLogoutToken(){
//
//        Optional<Logout> findLogout=logoutRepository.findByLogoutToken(logout.getLogoutToken());
//
//        Assertions.assertEquals(findLogout.get().getLogoutToken(),"log_out_token");
//    }
}
