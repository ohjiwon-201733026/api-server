package com.gloomy.server.domain.jwt;

import com.gloomy.server.domain.user.User;

public interface JWTSerializer {

    String jwtFromUser(User user);

}
