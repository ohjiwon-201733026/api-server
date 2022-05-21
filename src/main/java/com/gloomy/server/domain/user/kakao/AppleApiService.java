package com.gloomy.server.domain.user.kakao;

import com.gloomy.server.application.user.UserDTO;
import com.gloomy.server.domain.user.login.LoginApiService;
import io.grpc.Grpc;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Transactional(isolation = Isolation.READ_COMMITTED)
public class AppleApiService implements LoginApiService<UserDTO.KakaoToken, UserDTO.KakaoUser> {
    @Override
    public Mono<UserDTO.KakaoToken> getToken(UserDTO.CodeRequest request) {
        return null;
    }

    @Override
    public Mono<UserDTO.KakaoUser> getUserInfo(String accessToken) {
        return null;
    }

    @Override
    public Long logout(Long userId, String token) {
        return null;
    }
}
