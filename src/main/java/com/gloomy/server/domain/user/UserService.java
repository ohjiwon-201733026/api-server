package com.gloomy.server.domain.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gloomy.server.application.image.UserProfileImageService;
import com.gloomy.server.application.security.JWTAuthenticationProvider;
import com.gloomy.server.domain.comment.Comment;
import com.gloomy.server.domain.feed.Feed;
import com.gloomy.server.domain.jwt.JWTDeserializer;
import com.gloomy.server.infrastructure.jwt.UserJWTPayload;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static com.gloomy.server.application.user.UserDTO.*;

@RequiredArgsConstructor
@Service
@Transactional
public class UserService {
    private final WebClient webClient;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserProfileImageService userProfileImageService;
    private final JWTDeserializer jwtDeserializer;

    @Transactional
    public User signUp(PostRequest postRequest) {
        final Password encodedPassword = Password.of(postRequest.getPassword(), passwordEncoder);
        if(userRepository.findFirstByEmail(postRequest.getEmail()).isPresent())
            throw new IllegalArgumentException("[ userService ] 이미 존재하는 사용자 입니다.");
        User user = User.of(postRequest.getEmail(),
                postRequest.getUserName(),
                encodedPassword);
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public Optional<User> login(LoginRequest request) {
        Optional<User> findUser=userRepository.findFirstByEmail(request.getEmail());
        if(findUser.isEmpty()) throw  new IllegalArgumentException("[ UserService ] : 존재하지 않는 user 입니다.");
        else{
            if(findUser.get().matchesPassword(request.getPassword(), passwordEncoder)) return findUser;
            else throw new IllegalArgumentException("[ UserService ] : password 불일치");
        }
    }

    @Transactional(readOnly = true)
    public Optional<User> findById(long id) {
        return userRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<User> kakaoLogin(KakaoCodeRequest request) {
        KakaoToken kakaoToken = getKakaoToken(request);
        KakaoUser kakaoUser =  getKakaoUser(kakaoToken.getAccess_token());

        Optional<User> user = userRepository.findFirstByEmail(kakaoUser.getEmail());
        if(user.isEmpty()) {
            return user = Optional.of(userRepository.save(User.of(kakaoUser.getEmail(), kakaoUser.getNickname())));
        }
        else throw new IllegalArgumentException("[ UserService ] : 이미 가입된 회원입니다");
    }

    private KakaoToken getKakaoToken(KakaoCodeRequest request) {
        DefaultUriBuilderFactory uriBuilderFactory = new DefaultUriBuilderFactory("https://kauth.kakao.com");
        uriBuilderFactory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.NONE);

        URI uri = uriBuilderFactory.uriString("/oauth/token").build();

        return webClient.post()
                .uri(uri)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData("grant_type", "authorization_code")
                        .with("client_id", "6f503c85b5159190a85b7884ca7dd389")
                        .with("redirect_uri", "http://localhost:8080/kakao/signUp")
                        .with("code", request.getCode()))
                .retrieve()
                .bodyToMono(KakaoToken.class)
                .blockOptional().orElseThrow();
    }

    private KakaoUser getKakaoUser(String accessToken) {
        System.out.println(accessToken);
        DefaultUriBuilderFactory uriBuilderFactory = new DefaultUriBuilderFactory("https://kapi.kakao.com");
        uriBuilderFactory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.NONE);

        URI uri = uriBuilderFactory.uriString("v2/user/me").build();

        ResponseEntity<String> response = webClient.post()
                                        .uri(uri)
                                        .header("Authorization", "Bearer " + accessToken)
                                        .retrieve()
                                        .toEntity(String.class)
                                        .blockOptional().orElseThrow();

        JSONObject obj = new JSONObject(response.getBody());
        System.out.println(obj.toString());
        return KakaoUser.from(obj);
    }

    public User createUser(User user) {
        return userRepository.save(user);
    }

    public User updateUser(Long userId,UpdateUserDTO.Request updateUserDTO){
        Optional<User> updateUser=userRepository.findById(userId);
        if(updateUser.isPresent()){
            User user= updateUserEntity(updateUser.get(),updateUserDTO);
            return userRepository.save(user);
        }
        else throw new IllegalArgumentException("[ UserService ] : 존재하지 않는 user 입니다.");
    }

    public User updateUserEntity(User user,UpdateUserDTO.Request updateUserDTO){

        if(updateUserDTO.getEmail()!=null) user.changeEmail(updateUserDTO.getEmail());
        if(updateUserDTO.getSex()!=null) user.changeSex(updateUserDTO.getSex());
        if(updateUserDTO.getImage()!=null) {
            userProfileImageService.uploadUserImage(user,updateUserDTO.getImage());
        }
        if(updateUserDTO.getDateOfBirth()!=null) user.changeDateOfBirth(LocalDate.parse(updateUserDTO.getDateOfBirth()));
        return user;
    }

    public User findUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> {
            throw new IllegalArgumentException("[ userService ]: 존재하지 않는 user 입니다.");
        });
    }

    public void deleteUser(Long userId) {
        if(userRepository.findById(userId).isEmpty()) throw new IllegalArgumentException();
        else userRepository.delete(findUser(userId));
    }

    public void deleteAll() {
        userRepository.deleteAll();
    }

    public long getMyInfo(){
         Object token=SecurityContextHolder.getContext()
                .getAuthentication()
                .getCredentials();
        if(token==null) throw new IllegalArgumentException("[ UserService ] token이 없는 사용자");
        return jwtDeserializer.getUserId(token.toString());
    }
}
