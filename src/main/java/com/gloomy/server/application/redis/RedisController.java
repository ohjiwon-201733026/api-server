package com.gloomy.server.application.redis;


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class RedisController {

    private final RedisService redisService;

    @GetMapping("/redis")
    public String redis(@RequestParam String param){
        return redisService.redisString(param);
    }
}
