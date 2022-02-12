package com.gloomy.server.domain.user.kakao;

import com.gloomy.server.application.user.UserDTO;
import com.gloomy.server.domain.common.Status;
import com.gloomy.server.domain.user.UriService;
import com.gloomy.server.domain.user.User;
import com.gloomy.server.domain.user.UserRepository;
import com.gloomy.server.domain.user.login.LoginApiService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URI;
import java.util.Optional;

@Service
@Transactional
public class KakaoApiService implements LoginApiService<UserDTO.KakaoToken, UserDTO.KakaoUser> {

    private final String GRANT_TYPE="grant_type";
    private final String CLIENT_ID="client_id";
    private final String REDIRECT_URI="redirect_uri";
    private final String CODE="code";
    private final String OAUTH_TOKEN_PATH="/oauth/token";
    private final String USER_INFO_PATH="v2/user/me";
    private final String LOGOUT_PATH="/v1/user/logout";
    private final String AUTHORIZATION="Authorization";

    private final WebClient webClient;
    private final UserRepository userRepository;
    private final UriService uriService;

    private final String authorizeUri;
    private final String apiUri;
    private final String grantTypeValue;
    private final String clientIdValue;

    public KakaoApiService(WebClient webClient, UserRepository userRepository, UriService uriService,
                           @Value("${secrets.kakao.authorizeUri}") final String authorizeUri,
                           @Value("${secrets.kakao.apiUri}") final String apiUri,
                           @Value("${secrets.kakao.clientId}") final String clientId,
                           @Value("${secrets.kakao.grantType}") final String grantType
                           ){

        this.webClient=webClient;
        this.userRepository=userRepository;
        this.uriService=uriService;
        this.authorizeUri=authorizeUri;
        this.apiUri=apiUri;
        this.clientIdValue=clientId;
        this.grantTypeValue=grantType;
    }

    public UserDTO.KakaoToken getToken(UserDTO.CodeRequest request){

        URI uri=uriService.getUri(authorizeUri,OAUTH_TOKEN_PATH,null);

        return webClient.post()
                .uri(uri)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(GRANT_TYPE, grantTypeValue)
                        .with(CLIENT_ID, clientIdValue)
                        .with("redirect_uri", request.getRedirect_uri())
//                        .with(REDIRECT_URI, "http://localhost:8080/kakao/signUp")
                        .with(CODE, request.getCode()))
                .retrieve()
                .bodyToMono(UserDTO.KakaoToken.class)
                .blockOptional().orElseThrow();
    }

    public UserDTO.KakaoUser getUserInfo(String accessToken){
        URI uri=uriService.getUri(apiUri,USER_INFO_PATH,null);

        ResponseEntity<String> response = webClient.post()
                .uri(uri)
                .header(AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .toEntity(String.class)
                .blockOptional().orElseThrow();

        JSONObject obj = new JSONObject(response.getBody());
        return UserDTO.KakaoUser.from(obj);
    }

    public Long logout(Long userId){

        Optional<User> user =userRepository.findByIdAndJoinStatus(userId, Status.ACTIVE);
        if(user.isEmpty()){
            throw new IllegalArgumentException("[ userService ] 존재하지 않는 user");
        }

        URI uri=uriService.getUri(apiUri,LOGOUT_PATH,null);

        ResponseEntity<String> response = webClient.post()
                .uri(uri)
                .header(AUTHORIZATION, "Bearer " + user.get().getKakaoToken())
                .retrieve()
                .toEntity(String.class)
                .blockOptional().orElseThrow();

        JSONObject obj = new JSONObject(response.getBody());

        return userId;
    }
}
