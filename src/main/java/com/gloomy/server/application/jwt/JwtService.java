package com.gloomy.server.application.jwt;

import com.gloomy.server.domain.jwt.JWTDeserializer;
import com.gloomy.server.domain.jwt.JWTSerializer;
import com.gloomy.server.domain.user.User;
import com.gloomy.server.domain.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public Long getMyInfo(){
        String token=getToken();
        if(token.equals("")) return null;
        return jwtDeserializer.jwtPayloadFromJWT(token).getUserId();
    }

    public String getToken(){
        return SecurityContextHolder.getContext()
                .getAuthentication()
                .getCredentials().toString();
    }
}
