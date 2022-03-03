package com.gloomy.server.application.jwt;

import com.gloomy.server.application.user.TestUserDTO;
import com.gloomy.server.domain.jwt.JWTDeserializer;
import com.gloomy.server.domain.jwt.JWTSerializer;
import com.gloomy.server.domain.user.User;
import com.gloomy.server.domain.user.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static com.gloomy.server.application.core.ErrorMessage.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.util.AssertionErrors.assertEquals;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class JwtServiceMockTest {

    @InjectMocks
    JwtService jwtService;

    @Mock
    JWTSerializer jwtSerializer;
    @Mock
    JWTDeserializer jwtDeserializer;
    @Mock
    UserService userService;

    private JwtDTO.Request request;
    private JwtDTO.Response response;
    private Long userId;
    private User user;


    @BeforeEach
    public void setUp(){
        request=new JwtDTO.Request("accessToken","refreshToken");
        response=new JwtDTO.Response("newAccessToken","newRefreshToken");
        userId=123L;
        user= TestUserDTO.TestUser.makeTestUser();
    }

    @DisplayName("reissue_success")
    @Test
    public void reissue_success(){
        // given
        doReturn(true).when(jwtDeserializer).isValidToken(request.getAccessToken());
        doReturn(true).when(jwtDeserializer).isValidToken(request.getRefreshToken());
        doReturn(false).when(jwtDeserializer).isExpired(request.getRefreshToken());
        doReturn(userId).when(jwtDeserializer).getUserId(request.getAccessToken());
        doReturn(user).when(userService).findUser(userId);
        doReturn(response.getAccessToken()).when(jwtSerializer).jwtFromUser(user);
        doReturn(response.getRefreshToken()).when(jwtSerializer).createRefreshToken();

        // when
        JwtDTO.Response actualRes=jwtService.reissue(request);

        // then
        Assertions.assertEquals(actualRes.getAccessToken(),response.getAccessToken());
        Assertions.assertEquals(actualRes.getRefreshToken(),response.getRefreshToken());

    }

    @DisplayName("reissue_invalidToken_fail")
    @Test
    public void reissue_invalid_token_fail(){

        doReturn(true).when(jwtDeserializer).isValidToken(request.getAccessToken());

        IllegalArgumentException e=assertThrows(IllegalArgumentException.class,
                ()->jwtService.reissue(request));

        Assertions.assertEquals(e.getMessage(),invalidTokenErrorMessage);
    }

    @DisplayName("reissue_refreshToken_expired_fail")
    @Test
    public void reissue_refreshToken_expired_fail(){
        doReturn(true).when(jwtDeserializer).isValidToken(request.getAccessToken());
        doReturn(true).when(jwtDeserializer).isValidToken(request.getRefreshToken());
        doReturn(true).when(jwtDeserializer).isExpired(request.getRefreshToken());

        IllegalArgumentException e=assertThrows(IllegalArgumentException.class,
                ()->jwtService.reissue(request));

        Assertions.assertEquals(e.getMessage(),refreshTokenExpiredErrorMessage);
    }

    @DisplayName("reissue_refreshToken_notEquals_fail")
    @Test
    public void reissue_refreshToken_notEquals_fail(){
        request.setRefreshToken("notEqualsRefreshToken");
        doReturn(true).when(jwtDeserializer).isValidToken(request.getAccessToken());
        doReturn(true).when(jwtDeserializer).isValidToken(request.getRefreshToken());
        doReturn(false).when(jwtDeserializer).isExpired(request.getRefreshToken());
        doReturn(userId).when(jwtDeserializer).getUserId(request.getAccessToken());
        doReturn(user).when(userService).findUser(userId);

        IllegalArgumentException e=assertThrows(IllegalArgumentException.class,
                ()->jwtService.reissue(request));
        Assertions.assertEquals(e.getMessage(),refreshTokenNotEqualsErrorMessage);
    }
}
