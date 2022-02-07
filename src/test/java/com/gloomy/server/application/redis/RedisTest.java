package com.gloomy.server.application.redis;

import com.gloomy.server.application.AbstractControllerTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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
public class RedisTest extends AbstractControllerTest {

    @Autowired
    StringRedisTemplate redisTemplate;

    @Test
    @DisplayName("redis connect test")
    public void redis_connect_test(){
        ValueOperations<String,String> redis=redisTemplate.opsForValue();
        redis.set("key","value");

        ValueOperations<String,String> redis2=redisTemplate.opsForValue();
        String val=redis2.get("key");
        Assertions.assertEquals(val.equals("value"),true);
    }
}
