package com.gloomy.server.application.user;

import com.gloomy.server.domain.jwt.JWTSerializer;
import com.gloomy.server.domain.user.User;
import com.gloomy.server.domain.user.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.ResponseEntity.of;
import static com.gloomy.server.application.user.UserDTO.*;

@RequestMapping("/user")
@RestController
public class UserRestController {

    private final UserService userService;
    private final JWTSerializer jwtSerializer;

    UserRestController(UserService userService, JWTSerializer jwtSerializer) {
        this.userService = userService;
        this.jwtSerializer = jwtSerializer;
    }

    @PostMapping
    public Object postUser(@Validated @RequestBody Request dto) {
        final User userSaved = userService.signUp(dto);
        return Response.fromUserAndToken(userSaved, jwtSerializer.jwtFromUser(userSaved));
    }

    @PostMapping("/login/kakao")
    public ResponseEntity<UserDTO.Response> login(@Validated @RequestBody KakaoCodeRequest dto) {
//    public ResponseEntity<UserDTO.Response> login(@RequestHeader("access_token") String accessToken) {
        return of(userService.kakaoLogin(dto)
                .map(user -> Response.fromUserAndToken(user, jwtSerializer.jwtFromUser(user))));
    }

}
