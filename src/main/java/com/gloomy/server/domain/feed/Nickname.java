package com.gloomy.server.domain.feed;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.Objects;

@EqualsAndHashCode
@Embeddable
@Getter
public class Nickname {
    @Column(name = "nickname")
    private String nickname;

    protected Nickname() {
    }

    public Nickname(String nickName) {
        this.nickname = nickName;
    }
}

