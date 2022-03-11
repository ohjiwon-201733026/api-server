package com.gloomy.server.domain.user;

import com.gloomy.server.domain.common.entity.Status;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Objects;

import static javax.persistence.GenerationType.IDENTITY;

@Getter
@ToString
@Table(name = "users")
@Entity
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "email", nullable = false)
    private String email;

    @Embedded
    private Profile profile;

    @Enumerated(EnumType.STRING)
    private Status joinStatus;

    @Enumerated(EnumType.STRING)
    private Type type;

    private String kakaoToken;
    private String refreshToken;


    private User(String email, Profile profile, Type type) {
        this.email = email;
        this.profile = profile;
        this.type=type;
        this.joinStatus=Status.ACTIVE;
    }
    private User(String email, Profile profile,Type type,String kakaoToken,String refreshToken) {
        this.email = email;
        this.profile = profile;
        this.type=type;
        this.kakaoToken=kakaoToken;
        this.joinStatus=Status.ACTIVE;
        this.refreshToken=refreshToken;
    }

    public static User of(String email, String nickName, Type type){
        return new User(email, Profile.from(nickName),type);
    }

    public static User of(String email, String name,Type type,String kakaoToken,String refreshToken) {
        return new User(email,Profile.from(name),type,kakaoToken,refreshToken);
    }

    public void inactiveUser(){
        changeJoinStatus(Status.INACTIVE);
    }

    /**
     * change *
     */
    public void changeId(Long id) {
        this.id = id;
    }
    public void changeEmail(String email){this.email=email;}
    public void changeJoinStatus(Status status){this.joinStatus=status;}
    public void changeKakaoToken(String kakaoToken) {this.kakaoToken=kakaoToken;}
    public void changeRefreshToken(String refreshToken) {this.refreshToken=refreshToken;}

    public String getName(){ return this.profile.getName();}

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
