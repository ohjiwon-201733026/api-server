package com.gloomy.server.application.user;

import com.gloomy.server.application.image.UserProfileImageService;
import com.gloomy.server.domain.jwt.JWTSerializer;
import com.gloomy.server.domain.user.User;
import com.gloomy.server.domain.user.UserService;
import com.gloomy.server.infrastructure.jwt.UserJWTPayload;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

import static com.gloomy.server.application.user.UserDTO.*;

@RestController
@Transactional
public class UserRestController {

    private final UserService userService;
    private final JWTSerializer jwtSerializer;
    private final UserProfileImageService userProfileImageService;

    UserRestController(UserService userService, JWTSerializer jwtSerializer, UserProfileImageService userProfileImageService) {
        this.userService = userService;
        this.jwtSerializer = jwtSerializer;
        this.userProfileImageService = userProfileImageService;
    }

    /**
     * 일반 회원가입
     * @param request
     * @return
     */
    @PostMapping(value = "/user" ,produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Response addUser(@Validated @RequestBody PostRequest request) {
        final User userSaved = userService.signUp(request);
        return Response.fromUserAndToken(userSaved, jwtSerializer.jwtFromUser(userSaved));
    }

    @PostMapping(value = "/user/login", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Response login(@Validated @RequestBody LoginRequest request) {
        User user=userService.login(request).get();
        return Response.fromUserAndToken(user, jwtSerializer.jwtFromUser(user));
    }

//    @PostMapping(value = "/kakao/signUp")
//    public Response kakaoLogin(@Validated @RequestBody KakaoCodeRequest request) {
//        User user=userService.kakaoLogin(request).get();
//        return Response.fromUserAndToken(user, jwtSerializer.jwtFromUser(user));
//    }

    @GetMapping(value = "/kakao/signUp")
    public Response kakaoLogin(@RequestParam String code) {
        KakaoCodeRequest request=new KakaoCodeRequest(code);
        User user=userService.kakaoLogin(request).get();
        return Response.fromUserAndToken(user, jwtSerializer.jwtFromUser(user));
    }

    @GetMapping(value="/logout")
    public void logout(){
        userService.logout();
    }

    @GetMapping(value = "/user")
    public Response getUser(@AuthenticationPrincipal UserJWTPayload jwtPayload) {
        User user=userService.findById(jwtPayload.getUserId()).get();
        return Response.fromUserAndToken(user, getCurrentCredential());
    }

    private static String getCurrentCredential() {
        return SecurityContextHolder.getContext()
                .getAuthentication()
                .getCredentials()
                .toString();
    }

    @PostMapping(value = "/user/update", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public UpdateUserDTO.Response updateUser(@ModelAttribute UpdateUserDTO.Request updateUserDTO){
        Long userId=userService.getMyInfo();
        User updateUser = userService.updateUser(userId,updateUserDTO);
        return makeUpdateUserDTO(updateUser);
    }




    private UpdateUserDTO.Response makeUpdateUserDTO(User user){
        return UpdateUserDTO.Response.builder()
                .email(user.getEmail())
                .sex(user.getSex())
//                .imageUrl(userProfileImageService.findImageByUserId(user).getImageUrl().getImageUrl())
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