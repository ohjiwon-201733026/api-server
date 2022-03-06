package com.gloomy.server.domain.logout;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;

@Getter
@ToString
@Entity
@NoArgsConstructor
public class Logout {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "logout_id")
    private Long id;

    private String logoutToken;

    public Logout(String logoutToken) {
        this.logoutToken = logoutToken;
    }

    public static Logout of( String logoutToken){
        return new Logout(logoutToken);
    }
}
