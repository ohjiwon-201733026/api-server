package com.gloomy.server.domain.user.kakao;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gloomy.server.application.user.UserDTO;
import com.gloomy.server.domain.user.UriService;
import com.gloomy.server.domain.user.UserRepository;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import okhttp3.mockwebserver.Dispatcher;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import javax.validation.constraints.NotNull;
import java.io.IOException;

import static com.gloomy.server.domain.user.login.LoginFixture.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class KaKaoApiServiceMockTest {

    private MockWebServer mockWebServer;
    private ObjectMapper objectMapper;
    private KakaoApiService kakaoApiService;
    private MockResponse tokenResponse;
    private MockResponse userResponse;
    private MockResponse logoutResponse;
    private String mockServerUrl;
    private Dispatcher dispatcher;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UriService uriService;


    @BeforeEach
    public void initialize() throws IOException {
        ReactorClientHttpConnector connector = new ReactorClientHttpConnector();
        objectMapper=new ObjectMapper();
        mockWebServer=new MockWebServer();
        mockServerUrl=mockWebServer.url("/").toString();
        kakaoApiService =new KakaoApiService(userRepository,uriService,mockServerUrl,mockServerUrl
                ,CLIENT_ID_VALUE,GRANT_TYPE_VALUE);

        tokenResponse=new MockResponse()
                .addHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .setResponseCode(HttpStatus.OK.value())
                .setBody(objectMapper.writeValueAsString(createMockKakaoTokenResponse()));

        userResponse=new MockResponse()
                .addHeader("Content-Type",MediaType.APPLICATION_JSON_VALUE)
                .setResponseCode(HttpStatus.OK.value())
                .setBody(objectMapper.writeValueAsString(createMockKakaoUserResponse()));

        logoutResponse=new MockResponse()
                .setResponseCode(HttpStatus.OK.value())
                .setBody(objectMapper.writeValueAsString(USER_ID));

        dispatcher=new Dispatcher(){
            @NotNull
            @Override
            public MockResponse dispatch(RecordedRequest request){
                if(request.getPath().contains(OAUTH_TOKEN_PATH)) {
                    return tokenResponse;
                }
                if(request.getPath().contains(USER_INFO_PATH)) return userResponse;
                if(request.getPath().contains(LOGOUT_PATH)) return logoutResponse;

                return new MockResponse().setResponseCode(404);
            }
        };
        mockWebServer.setDispatcher(dispatcher);

    }

    @AfterEach
    public void shutdown() throws IOException{
        mockWebServer.shutdown();
    }

    @DisplayName("카카오 서버에 token 받아오기")
    @Test
    public void getToken(){

        UserDTO.CodeRequest request= UserDTO.CodeRequest.builder()
                .code(CODE_VALUE)
                .redirect_uri("http://localhost:8080/kakao/signUp").build();


        StepVerifier.create(kakaoApiService.getToken(request))
                .assertNext(body->{
                    assertEquals(body.getToken_type(),TOKEN_TYPE);
                    assertEquals(body.getAccess_token(),USER_TOKEN);
                    assertEquals(body.getExpires_in(),EXPIRE);
                    assertEquals(body.getRefresh_token(),USER_TOKEN);
                    assertEquals(body.getRefresh_token_expires_in(),EXPIRE);
                    assertEquals(body.getScope(),SCOPE);
                })
                .verifyComplete();

    }


    @DisplayName("카카오 서버에 User Info 받아오기")
    @Test
    public void getUserInfo(){

        StepVerifier.create(kakaoApiService.getUserInfo(CODE_VALUE))
                .assertNext(body->{
                    assertEquals(body.getId(),KAKAO_ID);
                    assertEquals(body.getConnected_at(),CONNECTED_AT);
                    assertEquals(body.getKakao_account().isEmail_needs_agreement(),false);
                    assertEquals(body.getKakao_account().isProfile_nickname_needs_agreement(),false);
                    assertEquals(body.getKakao_account().getProfile().getNickname(),NICKNAME);
                    assertEquals(body.getKakao_account().is_email_valid(),false);
                    assertEquals(body.getKakao_account().is_email_verified(),false);
                    assertEquals(body.getKakao_account().isHas_email(),false);
                    assertEquals(body.getKakao_account().getEmail(),EMAIL);
                })
                .verifyComplete();
    }

    @DisplayName("kakao logout 하기")
    @Test
    public void logout(){

        StepVerifier.create(Mono.just(kakaoApiService.logout(USER_ID,ACCESS_TOKEN)))
                .assertNext(body-> assertEquals(body,USER_ID))
                .verifyComplete();
    }

}