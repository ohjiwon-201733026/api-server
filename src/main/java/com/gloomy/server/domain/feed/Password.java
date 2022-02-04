package com.gloomy.server.domain.feed;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@EqualsAndHashCode
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

    public void setPassword(String password) {
        this.password = password;
    }
}

