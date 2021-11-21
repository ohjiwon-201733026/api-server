package com.gloomy.server.config.security.jwt.domain;

import com.gloomy.server.domain.user.User;

public interface JWTSerializer {

    String jwtFromUser(User user);

}
