package com.gloomy.server.domain.user.login;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.gloomy.server.application.jwt.JwtService;
import com.gloomy.server.application.user.TestUserDTO;
import com.gloomy.server.application.user.UserDTO;
import com.gloomy.server.application.user.UserRestController;
import com.gloomy.server.domain.common.entity.Status;
import com.gloomy.server.domain.jwt.JWTDeserializer;
import com.gloomy.server.domain.jwt.JWTSerializer;
import com.gloomy.server.domain.logout.LogoutRepository;
import com.gloomy.server.domain.user.User;
import com.gloomy.server.domain.user.UserRepository;
import com.gloomy.server.domain.user.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import reactor.core.publisher.Mono;

import java.util.Optional;

import static com.gloomy.server.domain.user.login.LoginFixture.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class LoginServiceMockTest {

    @InjectMocks
    LoginService loginService;

    @Mock
    LoginApiService<UserDTO.KakaoToken,UserDTO.KakaoUser> kakaoApiService;
    @Mock
    UserRepository userRepository;
    @Mock
    LogoutRepository logoutRepository;
    @Mock
    UserService userService;
    @Mock
    JwtService jwtService;
    @Mock LoginService loginServiceMock;

    private UserDTO.CodeRequest request;
    private UserDTO.KakaoToken kakaoToken;
    private UserDTO.KakaoUser kakaoUser;
    private User user;
    private Long userId;

    @BeforeEach
    public void setUp(){
        request=new UserDTO.CodeRequest("code");
        kakaoToken=createMockKakaoTokenResponse();
        kakaoUser=createMockKakaoUserResponse();
        user= TestUserDTO.TestUser.makeTestUser();
        userId=USER_ID;
    }

    @DisplayName("login")
    @Test
    public void login(){
        Mono<UserDTO.KakaoToken> kakaoTokenMono=Mono.just(kakaoToken);
        Mono<UserDTO.KakaoUser> kakaoUserMono=Mono.just(kakaoUser);
        Optional<User> userOp= Optional.of(user);
        doReturn(kakaoTokenMono).when(kakaoApiService).getToken(request);
        doReturn(kakaoUserMono).when(kakaoApiService).getUserInfo(kakaoToken.getAccess_token());
        doReturn(userOp).when(userRepository).findFirstByEmailAndJoinStatus(kakaoUser.getKakao_account().getEmail(), Status.ACTIVE);

        User loginUser=loginService.login(request);

        Assertions.assertEquals(user.getName(),loginUser.getName());
    }

    @DisplayName("logout")
    @Test
    public void logout() throws JsonProcessingException {
        doReturn(userId).when(userService).getMyInfo();
        Optional<User> userOp= Optional.of(user);
        doReturn(userOp).when(userRepository).findByIdAndJoinStatus(userId,Status.ACTIVE);
        doReturn(userId).when(kakaoApiService).logout(userId,user.getKakaoToken());
        doReturn(userId).when(jwtService).getMyInfo();
        doNothing().when(loginServiceMock).jwtLogout();

        loginService.logout();
    }

    @DisplayName("logout_fail")
    @Test
    public void logout_fail() throws JsonProcessingException {
        doReturn(userId).when(userService).getMyInfo();
        Optional<User> userOp= Optional.empty();
        doReturn(userOp).when(userRepository).findByIdAndJoinStatus(userId,Status.ACTIVE);
        doReturn(userId).when(kakaoApiService).logout(userId,user.getKakaoToken());
        doReturn(userId).when(jwtService).getMyInfo();
        doNothing().when(loginServiceMock).jwtLogout();

        assertThrows(IllegalArgumentException.class,()->loginService.logout());
    }
}
