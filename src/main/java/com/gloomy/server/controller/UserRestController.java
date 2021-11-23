package com.gloomy.server.controller;

import com.gloomy.server.config.security.jwt.domain.JWTSerializer;
import com.gloomy.server.domain.user.User;
import com.gloomy.server.dto.UserDto;
import com.gloomy.server.service.UserService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user")
public class UserRestController {

    private final UserService userService;
    private final JWTSerializer jwtSerializer;

    public UserRestController(UserService userService, JWTSerializer jwtSerializer) {
        this.userService = userService;
        this.jwtSerializer = jwtSerializer;
    }

    @PostMapping
    public Object postUser(@Validated @RequestBody UserDto.Request dto) {
        final User = userService.signUp(dto);
        return null;
    }

}
