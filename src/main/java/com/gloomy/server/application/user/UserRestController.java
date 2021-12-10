package com.gloomy.server.application.user;

import com.gloomy.server.domain.jwt.JWTSerializer;
import com.gloomy.server.domain.user.User;
import com.gloomy.server.domain.user.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Object addUser(@Validated @RequestBody PostRequest request) {
        final User userSaved = userService.signUp(request);
        return Response.fromUserAndToken(userSaved, jwtSerializer.jwtFromUser(userSaved));
    }

    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDTO.Response> login(@Validated @RequestBody LoginRequest request) {
        return of(userService.login(request)
                .map(user -> Response.fromUserAndToken(user, jwtSerializer.jwtFromUser(user))));
    }

    @PostMapping(value = "/login/kakao", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDTO.Response> kakaoLogin(@Validated @RequestBody KakaoCodeRequest request) {
        return of(userService.kakaoLogin(request)
                .map(user -> Response.fromUserAndToken(user, jwtSerializer.jwtFromUser(user))));
    }

}
