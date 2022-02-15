package com.gloomy.server.domain.user.login;

import com.gloomy.server.application.user.UserDTO;
import com.gloomy.server.domain.user.User;
import reactor.core.publisher.Mono;

public interface LoginApiService<T,U> {

    public Mono<UserDTO.KakaoToken> getToken(UserDTO.CodeRequest request);
    public Mono<UserDTO.KakaoUser> getUserInfo(String accessToken);
    public Long logout(Long userId,String token);
}
