package com.gloomy.server.application.jwt;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.gloomy.server.domain.common.entity.Status;
import com.gloomy.server.domain.jwt.JWTDeserializer;
import com.gloomy.server.domain.jwt.JWTSerializer;
import com.gloomy.server.domain.user.User;
import com.gloomy.server.domain.user.UserRepository;
import com.gloomy.server.domain.user.UserService;
import com.gloomy.server.domain.user.login.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.gloomy.server.application.core.ErrorMessage.*;

@Transactional
@Service
@RequiredArgsConstructor
public class JwtService {


    private final JWTSerializer jwtSerializer;
    private final JWTDeserializer jwtDeserializer;
    private final UserService userService;


    public JwtDTO.Response reissue(JwtDTO.Request request){

        String accessToken= request.accessToken;
        // 둘다 invalid한 경우
        if(!jwtDeserializer.isValidToken(request.getRefreshToken())
                || !jwtDeserializer.isValidToken(request.getAccessToken()))
            throw new IllegalArgumentException(invalidTokenErrorMessage);
        // refresh token expire
        if(jwtDeserializer.isExpired(request.refreshToken))
            throw new IllegalArgumentException(refreshTokenExpiredErrorMessage);
        // accessToken valid & accessToken expire
        Long userId=jwtDeserializer.getUserId(accessToken);
        User user=userService.findUser(userId);
        // DB에 저장했던 refreshToken이랑 일치
        if(!user.getRefreshToken().equals(request.refreshToken)) 
            throw new IllegalArgumentException(refreshTokenNotEqualsErrorMessage);

        String newAccessToken=jwtSerializer.jwtFromUser(user);
        String newRefreshToken= jwtSerializer.createRefreshToken();
        user.changeRefreshToken(newRefreshToken);
        userService.createUser(user);

        return JwtDTO.Response.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken).build();

    }
}
