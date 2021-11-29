package com.gloomy.server.domain.user;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Token {

    @Column(name = "kakao_token")
    private String kakaoToken;

    @Column(name = "refresh_token")
    private String refreshToken;
}
