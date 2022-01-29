package com.gloomy.server.application.feed;

import com.gloomy.server.domain.user.Password;
import com.gloomy.server.domain.user.Sex;
import com.gloomy.server.domain.user.User;

public class TestUserDTO {
    private final String email;
    private final String name;
    private final Password password;
    private final Sex sex;
    private final Integer year;
    private final Integer month;
    private final Integer day;

    public TestUserDTO() {
        this.email = "test@test.com";
        this.name = "사용자이름";
        this.password = new Password("12345");
        this.sex = Sex.MALE;
        this.year = 2020;
        this.month = 1;
        this.day = 1;
    }

    public User makeTestUser() {
        return User.of(email, name, password, sex, year, month, day);
    }
}
