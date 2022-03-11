package com.gloomy.server.domain.user.kakao;

import com.gloomy.server.application.user.UserDTO;
import com.gloomy.server.domain.user.login.UriService;
import com.gloomy.server.domain.user.UserRepository;
import com.gloomy.server.domain.user.login.LoginApiService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;
import reactor.core.publisher.Mono;

import java.net.URI;

@Service
@Transactional
public class KakaoApiService implements LoginApiService<UserDTO.KakaoToken, UserDTO.KakaoUser> {

    private final String GRANT_TYPE="grant_type";
    private final String CLIENT_ID="client_id";
    private final String REDIRECT_URI="redirect_uri";
    private final String CODE="code";
    private final String OAUTH_TOKEN_PATH="/oauth/token";
    private final String USER_INFO_PATH="/v2/user/me";
    private final String LOGOUT_PATH="/v1/user/logout";
    private final String AUTHORIZATION="Authorization";

    private final UserRepository userRepository;
    private final UriService uriService;

    private final String authorizeUri;
    private final String apiUri;
    private final String grantTypeValue;
    private final String clientIdValue;

    public KakaoApiService( UserRepository userRepository, UriService uriService,
                           @Value("${secrets.kakao.authorizeUri}") final String authorizeUri,
                           @Value("${secrets.kakao.apiUri}") final String apiUri,
                           @Value("${secrets.kakao.clientId}") final String clientId,
                           @Value("${secrets.kakao.grantType}") final String grantType
                           ){

        this.userRepository=userRepository;
        this.uriService=uriService;
        this.authorizeUri=authorizeUri;
        this.apiUri=apiUri;
        this.clientIdValue=clientId;
        this.grantTypeValue=grantType;
    }

    @Override
    public Mono<UserDTO.KakaoToken> getToken(UserDTO.CodeRequest request){

        DefaultUriBuilderFactory uriBuilderFactory = new DefaultUriBuilderFactory(authorizeUri);
        uriBuilderFactory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.NONE);
        URI uri= uriBuilderFactory.uriString(OAUTH_TOKEN_PATH).build();

        WebClient webClient= WebClient.builder().baseUrl(authorizeUri).build();
        return webClient.post()
                .uri(uri)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(GRANT_TYPE, grantTypeValue)
                        .with(CLIENT_ID, clientIdValue)
                        .with(REDIRECT_URI, request.getRedirect_uri())
                        .with(CODE, request.getCode()))
                .retrieve()
                .bodyToMono(UserDTO.KakaoToken.class);
    }

    @Override
    public Mono<UserDTO.KakaoUser> getUserInfo(String accessToken){

        DefaultUriBuilderFactory uriBuilderFactory = new DefaultUriBuilderFactory(apiUri);
        uriBuilderFactory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.NONE);
        URI uri= uriBuilderFactory.uriString(USER_INFO_PATH).build();

        WebClient webClient= WebClient.builder().baseUrl(apiUri).build();
        return webClient.post()
                .uri(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(UserDTO.KakaoUser.class);
    }

    @Override
    public Long logout(Long userId,String kakaoToken){

        DefaultUriBuilderFactory uriBuilderFactory = new DefaultUriBuilderFactory(apiUri);
        uriBuilderFactory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.NONE);
        URI uri= uriBuilderFactory.uriString(LOGOUT_PATH).build();

        WebClient webClient= WebClient.builder().baseUrl(apiUri).build();

        ResponseEntity<String> response = webClient.post()
                .uri(uri)
                .header(AUTHORIZATION, "Bearer " + kakaoToken)
                .retrieve()
                .toEntity(String.class)
                .blockOptional().orElseThrow();


        return userId;

    }
}
