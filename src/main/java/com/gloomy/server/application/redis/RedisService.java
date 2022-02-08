package com.gloomy.server.application.redis;

import io.lettuce.core.dynamic.annotation.Value;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

//@RequiredArgsConstructor
//@Service
public class RedisService {
/*
    private final RedisTemplate<String,Object> redisTemplate;

    public String redisString(String param){
        ValueOperations<String,Object> operations=redisTemplate.opsForValue();
        operations.set(param,param+" value");
        String redis=(String) operations.get(param);
        return redis;
    }

 */
}
