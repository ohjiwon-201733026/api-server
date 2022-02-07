package com.gloomy.server.domain.user.kakao;

import com.gloomy.server.application.user.UserDTO;
import com.gloomy.server.domain.common.Status;
import com.gloomy.server.domain.user.UriService;
import com.gloomy.server.domain.user.User;
import com.gloomy.server.domain.user.UserRepository;
import com.gloomy.server.domain.user.UserService;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.net.URI;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class KakaoService {

    private final WebClient webClient;
    private final UserRepository userRepository;
    private final UriService uriService;

    public UserDTO.KakaoToken getKakaoToken(UserDTO.KakaoCodeRequest request){
        URI uri=uriService.getUri("https://kauth.kakao.com","/oauth/token",null);

        return webClient.post()
                .uri(uri)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData("grant_type", "authorization_code")
                        .with("client_id", "76867f47209a454ed88ccf1080c4238c")
//                        .with("redirect_uri", request.getRedirect_uri())
                        .with("redirect_uri", "http://localhost:8080/kakao/signUp")
                        .with("code", request.getCode()))
                .retrieve()
                .bodyToMono(UserDTO.KakaoToken.class)
                .blockOptional().orElseThrow();
    }

    public UserDTO.KakaoUser getKakaoUser(String accessToken){
        URI uri=uriService.getUri("https://kapi.kakao.com","v2/user/me",null);

        ResponseEntity<String> response = webClient.post()
                .uri(uri)
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .toEntity(String.class)
                .blockOptional().orElseThrow();

        JSONObject obj = new JSONObject(response.getBody());
        return UserDTO.KakaoUser.from(obj);
    }

    public Long kakaoLogout(Long userId){

        Optional<User> user =userRepository.findByIdAndJoinStatus(userId, Status.ACTIVE);
        if(user.isEmpty()){
            throw new IllegalArgumentException("[ userService ] 존재하지 않는 user");
        }

        URI uri=uriService.getUri("https://kapi.kakao.com","/v1/user/logout",null);

        ResponseEntity<String> response = webClient.post()
                .uri(uri)
                .header("Authorization", "Bearer " + user.get().getKakaoToken())
                .retrieve()
                .toEntity(String.class)
                .blockOptional().orElseThrow();

        JSONObject obj = new JSONObject(response.getBody());

        return userId;
    }
}
