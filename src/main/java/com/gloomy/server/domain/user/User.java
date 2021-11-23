package com.gloomy.server.domain.user;

import lombok.AccessLevel;
import lombok.Builder;

import javax.persistence.*;

@Entity
public class User {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", nullable = false)
    private String email;

    @Embedded
    private Profile profile;

    @Embedded
    private Password password;

    protected User() {
    }

    @Builder(access = AccessLevel.PROTECTED)
    private User(String email, Profile profile, Password password) {
        this.email = email;
        this.profile = profile;
        this.password = password;
    }

    static User of(String email, String name, Password password) {
//        return new User(email, Profile.from(name), password);
        return User.builder()
                .email(email)
                .profile(Profile.from(name))
                .password(password)
                .build();
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public Profile getProfile() {
        return profile;
    }

    public String getName() {
        return getProfile().get
    }

    public Password getPassword() {
        return password;
    }
}
