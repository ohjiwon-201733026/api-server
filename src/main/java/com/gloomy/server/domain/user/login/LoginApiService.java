package com.gloomy.server.domain.user.login;

import com.gloomy.server.application.user.UserDTO;

public interface LoginApiService<T,U> {

    public T getToken(UserDTO.CodeRequest request);
    public U getUserInfo(String accessToken);
    public Long logout(Long userId);
}
