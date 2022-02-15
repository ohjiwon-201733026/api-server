package com.gloomy.server.domain.user.login;

import com.gloomy.server.application.redis.RedisService;
import com.gloomy.server.application.user.UserDTO;
import com.gloomy.server.domain.common.Status;
import com.gloomy.server.domain.jwt.JWTDeserializer;
import com.gloomy.server.domain.jwt.JWTSerializer;
import com.gloomy.server.domain.user.User;
import com.gloomy.server.domain.user.UserRepository;
import com.gloomy.server.domain.user.UserService;
import com.gloomy.server.domain.user.kakao.KakaoApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static java.time.Instant.now;

@RequiredArgsConstructor
@Service
@Transactional
public class LoginService {

    private final LoginApiService<UserDTO.KakaoToken, UserDTO.KakaoUser> kakaoApiService;
    private final UserRepository userRepository;
    private final JWTDeserializer jwtDeserializer;
    private final RedisService redisService;
    private final UserService userService;

    @Transactional(readOnly = true)
    public User kakaoLogin(UserDTO.CodeRequest request) {
        UserDTO.KakaoToken kakaoToken = kakaoApiService.getToken(request).block();
        UserDTO.KakaoUser kakaoUser =  kakaoApiService.getUserInfo(kakaoToken.getAccess_token()).block();

        Optional<User> userOp =
                userRepository.findFirstByEmailAndJoinStatus(kakaoUser.getKakao_account().getEmail(), Status.ACTIVE);
        User user;
        if(userOp.isEmpty()) { // 회원가입
            user=User.of(kakaoUser.getKakao_account().getEmail(), kakaoUser.getKakao_account().getProfile().getNickname(), kakaoToken.getAccess_token());
        }
        else{ // 로그인
            user=userOp.get();
            user.changeKakaoToken(kakaoToken.getAccess_token());
        }
        System.out.println(user.toString());
        userRepository.save(user);

        return user;
    }

    public void logout(){

        Long userId=userService.getMyInfo();

        Optional<User> user =userRepository.findByIdAndJoinStatus(userId, Status.ACTIVE);
        if(user.isEmpty()){
            throw new IllegalArgumentException("[ userService ] 존재하지 않는 user");
        }

        kakaoApiService.logout(userId,user.get().getKakaoToken()); // 카카오 로그아웃 처리
        jwtLogout();
    }

    private void jwtLogout(){
        String token= userService.getToken();
        long expiredTime=jwtDeserializer.jwtPayloadFromJWT(token).getExpiredTime()-now().getEpochSecond();
        redisService.setKey(token,"logout",expiredTime);
    }

}
