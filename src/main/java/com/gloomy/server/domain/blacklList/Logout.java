package com.gloomy.server.domain.blacklList;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@NoArgsConstructor
@Getter
@ToString
public class Logout {

    @GeneratedValue
    @Id
    private Long id;

    private String accessToken;

    private Logout(String accessToken) {
        this.accessToken = accessToken;
    }

    public static Logout of(String accessToken){
        return new Logout(accessToken);
    }
}
