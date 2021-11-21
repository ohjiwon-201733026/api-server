package com.gloomy.server.controller;

import com.gloomy.server.dto.UserDto;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user")
public class UserRestController {

    @PostMapping
    public Object postUser(@Validated @RequestBody UserDto.Request dto) {
        return null;
    }

}
