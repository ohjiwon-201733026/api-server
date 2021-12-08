package com.gloomy.server.domain.feed;

import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Getter
@Embeddable
public class IsUser {
    @Column(name = "is_user", nullable = false)
    private Boolean isUser;

    private IsUser() {
    }

    public IsUser(Boolean isUser) {
        this.isUser = isUser;
    }
}
