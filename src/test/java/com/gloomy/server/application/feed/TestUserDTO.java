package com.gloomy.server.application.feed;

import com.gloomy.server.domain.user.Password;
import com.gloomy.server.domain.user.User;

public class TestUserDTO {
    private final String testEmail;
    private final String testUserName;
    private final Password testUserPassword;

    public TestUserDTO() {
        this.testEmail = "test@test.com";
        this.testUserName = "사용자이름";
        this.testUserPassword = new Password("12345");
    }

    public User makeTestUser() {
        return User.of(testEmail, testUserName, testUserPassword);
    }
}
