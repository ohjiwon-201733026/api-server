package com.gloomy.server.application.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.gloomy.server.application.jwt.JwtService;
import com.gloomy.server.domain.user.login.LoginService;
import com.gloomy.server.domain.jwt.JWTSerializer;
import com.gloomy.server.domain.user.User;
import com.gloomy.server.domain.user.UserService;
import com.gloomy.server.infrastructure.jwt.UserJWTPayload;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.gloomy.server.application.user.UserDTO.*;

@RestController
@RequiredArgsConstructor
public class UserRestController {
    private final LoginService loginService;
    private final UserService userService;
    private final JWTSerializer jwtSerializer;
    private final JwtService jwtService;


    @PostMapping(value = "/kakao/signUp")
    public Response kakaoLogin(@Validated @RequestBody CodeRequest request) {
        User user=loginService.login(request);
        return Response.fromUserAndToken(user, jwtSerializer.jwtFromUser(user), user.getRefreshToken());
    }

    @GetMapping(value = "/kakao/signUp")
    public Response kakaoLogin(@RequestParam String code) {
        CodeRequest request=new CodeRequest(code);
        User user=loginService.login(request);
        return Response.fromUserAndToken(user, jwtSerializer.jwtFromUser(user), user.getRefreshToken());
    }


    @PostMapping(value="/kakao/logout")
    public void logout() throws JsonProcessingException {
        loginService.logout();
    }

    private static String getCurrentCredential() {
        return SecurityContextHolder.getContext()
                .getAuthentication()
                .getCredentials()
                .toString();
    }

    private UpdateUserDTO.Response makeUpdateUserDTO(User user){
        return UpdateUserDTO.Response.builder()
                .id(user.getId())
                .nickname(user.getProfile().getUserNickName())
                .email(user.getEmail())
                .build();

    }

    @GetMapping(value ="/user/detail")
    public UpdateUserDTO.Response userDetail(){
        System.out.println(SecurityContextHolder.getContext().getAuthentication().getClass());
        Long userId=jwtService.getMyInfo();
        User findUser = userService.findUser(userId);
        return makeUpdateUserDTO(findUser);
    }

    @PutMapping(value = "/user/inactive")
    public void inactiveUser(){
        Long userId=jwtService.getMyInfo();
        userService.inactiveUser(userId);
    }

}