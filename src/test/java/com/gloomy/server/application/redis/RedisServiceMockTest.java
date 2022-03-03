package com.gloomy.server.application.redis;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class RedisServiceMockTest {

    @InjectMocks RedisService redisService;

    @Mock
    RedisTemplate<String,Object> redisTemplate;

    @Mock
    ValueOperations<String,Object> valOp;

    private String mockKey;
    private String mockValue;
    private Long mockExpiredTime;

    @BeforeEach
    public void setUp(){
        mockKey="mockKey";
        mockValue="mockValue";
        mockExpiredTime=1L;
    }

    @DisplayName("redis get value")
    @Test
    public void getValue(){
        doReturn(valOp).when(redisTemplate).opsForValue();
        doReturn(mockValue).when(valOp).get(mockKey);

        String value=redisService.getValue(mockKey);

        Assertions.assertEquals(value,mockValue);
    }

    @DisplayName("redis set key-value")
    @Test
    public void setKey(){
        doReturn(valOp).when(redisTemplate).opsForValue();
        doNothing()
                .when(valOp)
                .set(mockKey,mockValue);

        redisService.setKey(mockKey,mockValue,mockExpiredTime);
    }

    @DisplayName("redis delete key")
    @Test
    public void deleteKey(){
        doReturn(true).when(redisTemplate).delete(mockKey);

        redisService.deleteKey(mockKey);
    }

    @DisplayName("redis hasKey")
    @Test
    public void hasKey(){
        doReturn(true).when(redisTemplate).hasKey(mockKey);

        boolean hasKey=redisService.hasKey(mockKey);

        Assertions.assertEquals(hasKey,true);
    }
}
