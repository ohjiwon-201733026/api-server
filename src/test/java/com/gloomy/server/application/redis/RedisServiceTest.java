package com.gloomy.server.application.redis;

import com.gloomy.server.application.AbstractControllerTest;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
@SpringBootTest(properties = {
        "spring.config.location=classpath:test-application.yml,classpath:aws.yml"
})
@Transactional
public class RedisServiceTest extends AbstractControllerTest {

    @Autowired private RedisService redisService;

    private String key;
    private String value;

    @BeforeEach
    public void setUp(){
        key="key";
        value="value";
    }

    @AfterEach
    public void shutDown(){
        redisService.deleteKey(key);
    }


    @DisplayName("Redis 값 저장, 조회")
    @Test
    public void getValue(){
        redisService.setKey(key,value,30);

        String val=redisService.getValue(key);

        Assertions.assertEquals(value,val);
    }



}
