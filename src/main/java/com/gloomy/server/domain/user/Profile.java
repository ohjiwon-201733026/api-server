package com.gloomy.server.domain.user;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;

@Embeddable
public class Profile {

    @Column(name = "name")
    private String name;

    @Column(name = "bio")
    private String bio;

    @Embedded
    private Image image;

    protected Profile() {
    }

    static Profile from(String name) {
        return new Profile(name, null, null);
    }

    private Profile(String name, String bio, Image image) {
        this.name = name;
        this.bio = bio;
        this.image = image;
    }

    void changeName(String name) {
        this.name = name;
    }
}
