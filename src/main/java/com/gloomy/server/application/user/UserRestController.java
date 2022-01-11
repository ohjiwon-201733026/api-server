package com.gloomy.server.application.user;

import com.gloomy.server.domain.feed.Feed;
import com.gloomy.server.domain.jwt.JWTSerializer;
import com.gloomy.server.domain.user.User;
import com.gloomy.server.domain.user.UserService;
import com.gloomy.server.infrastructure.jwt.UserJWTPayload;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.gloomy.server.application.user.UserDTO.*;
import static org.springframework.http.ResponseEntity.of;
import static org.springframework.http.ResponseEntity.ok;

@RequestMapping("/user")
@RestController
public class UserRestController {

    private final UserService userService;
    private final JWTSerializer jwtSerializer;

    UserRestController(UserService userService, JWTSerializer jwtSerializer) {
        this.userService = userService;
        this.jwtSerializer = jwtSerializer;
    }

    /**
     * 일반 회원가입
     * @param request
     * @return
     */
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Response> addUser(@Validated @RequestBody PostRequest request) {
        final User userSaved = userService.signUp(request);
        return ok(Response.fromUserAndToken(userSaved, jwtSerializer.jwtFromUser(userSaved)));
    }

    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Response> login(@Validated @RequestBody LoginRequest request) {
        return of(userService.login(request)
                .map(user -> Response.fromUserAndToken(user, jwtSerializer.jwtFromUser(user))));
    }

    @PostMapping(value = "/login/kakao", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Response> kakaoLogin(@Validated @RequestBody KakaoCodeRequest request) {
        System.out.println(request.code);
        return of(userService.kakaoLogin(request)
                .map(user -> Response.fromUserAndToken(user, jwtSerializer.jwtFromUser(user))));
    }

    @GetMapping
    public ResponseEntity<Response> getUser(@AuthenticationPrincipal UserJWTPayload jwtPayload) {
        return of(userService.findById(jwtPayload.getUserId())
                .map(user -> Response.fromUserAndToken(user, getCurrentCredential())));
    }

    private static String getCurrentCredential() {
        return SecurityContextHolder.getContext()
                .getAuthentication()
                .getCredentials()
                .toString();
    }


    @PostMapping(value = "/update/{userId}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UpdateUserDTO.Response> updateUser(@PathVariable("userId") Long userId,
                                                    @RequestBody UpdateUserDTO.Request updateUserDTO, Model model){
        try {
            User updateUser = userService.updateUser(userId,updateUserDTO);
            model.addAttribute("updateUserDTO",updateUserDTO);
            return ResponseEntity.ok().body(makeUpdateUserDTO(updateUser));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    private UpdateUserDTO.Response makeUpdateUserDTO(User user){
        return UpdateUserDTO.Response.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .sex(user.getSex())
                .image(user.getProfile().getImage().getImage())
                .dateOfBirth(user.getDateOfBirth())
                .build();

    }

    @GetMapping(value ="/detail/{userId}")
    public ResponseEntity<UpdateUserDTO.Response> userDetail(@PathVariable("userId")Long userId,Model model){
        try {
            User findUser = userService.findUser(userId);
            return ResponseEntity.ok().body(makeUpdateUserDTO(findUser));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

}
