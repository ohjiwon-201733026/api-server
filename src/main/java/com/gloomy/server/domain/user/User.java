package com.gloomy.server.domain.user;

import org.springframework.security.crypto.password.PasswordEncoder;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Objects;
import static javax.persistence.GenerationType.IDENTITY;

@Setter
@Getter
@Table(name = "users")
@Entity
public class User {

    @Id @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(name = "email", nullable = false)
    private String email;

    @Embedded
    private Profile profile;

    @Embedded
    private Password password;

//    @Embedded
//    private Token token;

    protected User() {
    }

    private User(String email, Profile profile, Password password) {
        this.email = email;
        this.profile = profile;
        this.password = password;
    }

    public static User of(String email, String name, Password password) {
        return new User(email, Profile.from(name), password);
    }

    public static User of(String email, String name) {
        return new User(email, Profile.from(name), null);
    }

    boolean matchesPassword(String rawPassword, PasswordEncoder passwordEncoder) {
        return password.matchesPassword(rawPassword, passwordEncoder);
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
        return getProfile().getUserName();
    }

    public Password getPassword() {
        return password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return email.equals(user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }
}
