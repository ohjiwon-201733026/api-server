package com.gloomy.server.domain.user;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ResponseBody;

@Getter
@RequiredArgsConstructor
public enum Role {
    USER,ADMIN;
}
