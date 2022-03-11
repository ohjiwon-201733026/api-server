package com.gloomy.server.application.feed;

import com.gloomy.server.domain.user.Type;
import com.gloomy.server.domain.user.User;

public class TestUserDTO {
    private static final String email = "test@test.com";
    private static final String name = "사용자이름";
    private static final Integer year = 2020;
    private static final Integer month = 1;
    private static final Integer day = 1;

    public static User makeTestUser() {
        return User.of(email, name, Type.KAKAO);
    }
}
