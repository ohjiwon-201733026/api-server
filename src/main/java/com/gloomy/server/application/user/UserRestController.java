package com.gloomy.server.application.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.gloomy.server.application.image.UserProfileImageService;
import com.gloomy.server.domain.user.login.LoginService;
import com.gloomy.server.domain.jwt.JWTSerializer;
import com.gloomy.server.domain.user.User;
import com.gloomy.server.domain.user.UserService;
import com.gloomy.server.infrastructure.jwt.UserJWTPayload;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.gloomy.server.application.user.UserDTO.*;

@RestController
@Transactional
public class UserRestController {
    private final LoginService loginService;
    private final UserService userService;
    private final JWTSerializer jwtSerializer;
    private final UserProfileImageService userProfileImageService;

    UserRestController(LoginService loginService, UserService userService, JWTSerializer jwtSerializer, UserProfileImageService userProfileImageService) {
        this.loginService = loginService;
        this.userService = userService;
        this.jwtSerializer = jwtSerializer;
        this.userProfileImageService = userProfileImageService;
    }

//    @PostMapping(value = "/kakao/signUp")
//    public Response kakaoLogin(@Validated @RequestBody CodeRequest request) {
//        User user=loginService.login(request);
//        return Response.fromUserAndToken(user, jwtSerializer.jwtFromUser(user), user.getRefreshToken());
//    }

    @GetMapping(value = "/kakao/signUp")
    public Response kakaoLogin(@RequestParam String code) {
        CodeRequest request=new CodeRequest(code);
        User user=loginService.login(request);
        return Response.fromUserAndToken(user, jwtSerializer.jwtFromUser(user),user.getRefreshToken());
    }

    @GetMapping(value="/kakao/logout")
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
                .email(user.getEmail())
                .sex(user.getSex())
                .dateOfBirth(user.getDateOfBirth()==null?"":user.getDateOfBirth().toString())
                .build();

    }

    @GetMapping(value ="/user/detail")
    public UpdateUserDTO.Response userDetail(){
        Long userId=userService.getMyInfo();
        User findUser = userService.findUser(userId);
        return makeUpdateUserDTO(findUser);
    }

    @GetMapping(value = "/user/inactive")
    public void inactiveUser(){
        Long userId=userService.getMyInfo();
        userService.inactiveUser(userId);
    }
}