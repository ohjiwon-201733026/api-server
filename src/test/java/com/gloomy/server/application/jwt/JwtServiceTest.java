package com.gloomy.server.application.jwt;

import com.gloomy.server.application.user.TestUserDTO;
import com.gloomy.server.config.TestConfig;
import com.gloomy.server.domain.jwt.JWTDeserializer;
import com.gloomy.server.domain.jwt.JWTSerializer;
import com.gloomy.server.domain.user.User;
import com.gloomy.server.domain.user.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.gloomy.server.application.core.ErrorMessage.invalidTokenErrorMessage;
import static com.gloomy.server.application.core.ErrorMessage.refreshTokenNotEqualsErrorMessage;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@SpringBootTest(properties = {
        "spring.config.location=classpath:test-application.yml,classpath:aws.yml"
})
@Import({TestConfig.class})
@Transactional
public class JwtServiceTest {

    @Autowired
    UserRepository userRepository;
    @Autowired
    JWTSerializer jwtSerializer;
    @Autowired JwtService jwtService;
    @Autowired
    JWTDeserializer jwtDeserializer;
//    @Autowired
//    JWTSerializer expiredTimeTokenHmacSHA256JWTService;
//    @Autowired
//    List<JWTSerializer> jwtSerializerList;

    private String accessToken_O;
    private String refreshToken_O;
    private String accessToken_X;
    private String refreshToken_X;
    private String refreshToken_differ;
    private String refreshToken_expired;

    private User saveUser;
    private Long userId;
    private JwtDTO.Request request;

    @BeforeEach
    public void setUp(){
        User testUser= TestUserDTO.TestUser.makeTestUser();
        saveUser=userRepository.save(testUser);
        accessToken_O=jwtSerializer.jwtFromUser(saveUser);
        refreshToken_O=jwtSerializer.createRefreshToken();
        accessToken_X=accessToken_O.substring(1);
        refreshToken_X=refreshToken_O.substring(1);
        refreshToken_differ=jwtSerializer.jwtFromUser(saveUser);
//        refreshToken_expired= expiredTimeTokenHmacSHA256JWTService.createRefreshToken();

        saveUser.changeRefreshToken(refreshToken_O);
        request=new JwtDTO.Request(accessToken_O,refreshToken_O);
    }

    @Test
    public void test(){

//        System.out.println(expiredTimeTokenHmacSHA256JWTService.getClass());
//        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(TestConfig.class);
//        System.out.println(ctx.getBean("ExpiredTimeTokenHmacSHA256JWTService").getClass());
    }

    @DisplayName("access & refresh token 재발급 받기_성공")
    @Test
    public void reissue_success(){
        userRepository.save(saveUser);

        JwtDTO.Response response=jwtService.reissue(request);

        Assertions.assertEquals(jwtDeserializer.getUserId(response.getAccessToken()),saveUser.getId());
        Assertions.assertEquals(jwtDeserializer.isValidToken(response.getAccessToken()),true);
        Assertions.assertEquals(jwtDeserializer.isValidToken(response.getRefreshToken()),true);
        Assertions.assertEquals(jwtDeserializer.isExpired(response.getAccessToken()),false);
        Assertions.assertEquals(jwtDeserializer.isExpired(response.getRefreshToken()),false);

    }

    @DisplayName("access & refresh token invalid (fail)")
    @Test
    public void reissue_invalid_fail(){
        request=new JwtDTO.Request(accessToken_X,refreshToken_X);

        IllegalArgumentException e=assertThrows(IllegalArgumentException.class,()->jwtService.reissue(request));
        Assertions.assertEquals(e.getMessage(),invalidTokenErrorMessage);
    }

    // TODO: expired refreshToken 만들기
//    @DisplayName("refresh token expired (fail)")
//    @Test
//    public void reissue_refresh_token_expired_fail(){
//        request=new JwtDTO.Request(accessToken_O,refreshToken_expired);
//
//        IllegalArgumentException e=assertThrows(IllegalArgumentException.class,()->jwtService.reissue(request));
//        System.out.println(e.getMessage());
//    }

    @DisplayName("refresh token 불일치 (fail)")
    @Test
    public void reissue_refresh_token_not_equals(){
        userRepository.save(saveUser);

        request=new JwtDTO.Request(accessToken_O,refreshToken_differ);

        IllegalArgumentException e=assertThrows(IllegalArgumentException.class,
                ()->jwtService.reissue(request));
        Assertions.assertEquals(e.getMessage(),refreshTokenNotEqualsErrorMessage);

    }
}
