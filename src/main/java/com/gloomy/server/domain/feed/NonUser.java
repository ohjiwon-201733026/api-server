package com.gloomy.server.domain.feed;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import java.util.Objects;

@EqualsAndHashCode
@Getter
@Embeddable
public class NonUser {
    @Embedded
    private Nickname nickname;

    @Embedded
    private Password password;

    protected NonUser() {
    }

    public NonUser(Nickname nickname, Password password) {
        this.nickname = nickname;
        this.password = password;
    }

    public static NonUser of(String nickName, String password) {
        return new NonUser(new Nickname(nickName), new Password(password));
    }

    public void setPassword(String password) {
        this.password.setPassword(password);
    }
}
