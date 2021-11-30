package com.gloomy.server.domain.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.net.URI;
import java.util.Optional;

import static com.gloomy.server.application.user.UserDTO.*;

@RequiredArgsConstructor
@Service
public class UserService {
    private final String BASE_URL = "https://kapi.kakao.com";

    private final WebClient webClient;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User signUp(PostRequest postRequest) {
        final Password encodedPassword = Password.of(postRequest.getPassword(), passwordEncoder);
        User user = User.of(postRequest.getEmail(),
                postRequest.getUserName(),
                encodedPassword);
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public Optional<User> login(LoginRequest request) {
        return userRepository.findFirstByEmail(request.getEmail())
                .filter(user -> user.matchesPassword(request.getPassword(), passwordEncoder));
    }

    @Transactional(readOnly = true)
    public Optional<User> kakaoLogin(KakaoCodeRequest dto) {
        KakaoToken kakaoToken = getKakaoAccessToken(dto);
        KakaoUserInfo kakaoUserInfo = getKakaoUserInfo(kakaoToken.getAccess_token());
        Optional<User> user = userRepository.findFirstByEmail(kakaoUserInfo.getEmail());
        if(user.isEmpty()) {
            return Optional.ofNullable(userRepository.save(User.of(user.get().getEmail())));
        }
        return user;
    }

    private KakaoToken getKakaoAccessToken(KakaoCodeRequest dto) {
        DefaultUriBuilderFactory uriBuilderFactory = new DefaultUriBuilderFactory(BASE_URL);
        uriBuilderFactory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.NONE);

        URI uri = uriBuilderFactory.uriString("oauth/token").build();

        return webClient.post()
                .uri(uri)
                .body(BodyInserters.fromFormData("grant_type", "authorization_code")
                        .with("client_id", dto.getClientId())
                        .with("redirect_uri", dto.getRedirectUri())
                        .with("code", dto.getCode()))
                .retrieve()
                .bodyToMono(KakaoToken.class)
                .blockOptional().orElseThrow();
    }

    private KakaoUserInfo getKakaoUserInfo(String accessToken) {
        DefaultUriBuilderFactory uriBuilderFactory = new DefaultUriBuilderFactory(BASE_URL);
        uriBuilderFactory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.NONE);

        URI uri = uriBuilderFactory.uriString("v2/user/me").build();

        return webClient.post()
                .uri(uri)
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(KakaoUserInfo.class)
                .blockOptional().orElseThrow();
    }
}
