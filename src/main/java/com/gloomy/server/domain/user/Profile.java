package com.gloomy.server.domain.user;

import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;

@Embeddable
@Getter
public class Profile {

    @Column(name = "name")
    private String name;

    protected Profile() {
    }

    static Profile from(String name) {
        return new Profile(name);
    }

    private Profile(String name){
        this.name = name;
    }

    public String getUserNickName() {
        return name;
    }

    void changeName(String name) {
        this.name = name;
    }
}
