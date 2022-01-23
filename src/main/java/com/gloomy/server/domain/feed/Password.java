package com.gloomy.server.domain.feed;

import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Getter
@Embeddable
public class Password {
    @Column(name = "password")
    private String password;

    protected Password() {
    }

    public Password(String password) {
        this.password = password;
    }
}
