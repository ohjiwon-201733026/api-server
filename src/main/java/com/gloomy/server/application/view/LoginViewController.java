package com.gloomy.server.application.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class LoginViewController {

    @GetMapping("/kakao")
    public String kakaoLoginView() {
        return "kakaoLogin";
    }

    @GetMapping("/kakaoLogout")
    public String logout() {
        return "kakaoLogout";
    }

}
