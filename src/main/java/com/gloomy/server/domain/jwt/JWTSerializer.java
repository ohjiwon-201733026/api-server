package com.gloomy.server.domain.jwt;

import com.gloomy.server.domain.user.User;

public interface JWTSerializer {

    public String jwtFromUser(User user);
    public String createRefreshToken();

}
