package com.gloomy.server.application.redis;


import com.gloomy.server.application.core.response.RestResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class RedisController {

    private final RedisService redisService;

    @GetMapping("/redis")
    public RestResponse<String> redis(@RequestParam String param){
        return new RestResponse<>(HttpStatus.OK.value(), "성공",redisService.redisString(param));
    }


}
