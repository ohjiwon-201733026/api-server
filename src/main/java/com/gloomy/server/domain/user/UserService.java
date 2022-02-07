package com.gloomy.server.domain.user;

import com.gloomy.server.domain.common.entity.Status;
import com.gloomy.server.domain.blacklList.Logout;
import com.gloomy.server.domain.blacklList.LogoutRepository;
import com.gloomy.server.domain.jwt.JWTDeserializer;
import static java.time.Instant.now;
import com.gloomy.server.domain.jwt.JWTPayload;
import com.gloomy.server.domain.jwt.JWTSerializer;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
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
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static com.gloomy.server.application.user.UserDTO.*;

@RequiredArgsConstructor
@Service
@Transactional
public class UserService {
    private final WebClient webClient;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTDeserializer jwtDeserializer;
    private final StringRedisTemplate redisTemplate;

    @Transactional
    public User signUp(PostRequest postRequest) {
        final Password encodedPassword = Password.of(postRequest.getPassword(), passwordEncoder);
        if(userRepository.findFirstByEmailAndJoinStatus(postRequest.getEmail(), Status.ACTIVE).isPresent())
            throw new IllegalArgumentException("[ userService ] 이미 존재하는 사용자 입니다.");
        User user = User.of(postRequest.getEmail(),
                postRequest.getUserName(),
                encodedPassword);
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public Optional<User> login(LoginRequest request) {
        Optional<User> findUser=userRepository.findFirstByEmailAndJoinStatus(request.getEmail(), Status.ACTIVE);
        if(findUser.isEmpty()) throw  new IllegalArgumentException("[ UserService ] : 존재하지 않는 user 입니다.");
        else{
            if(findUser.get().matchesPassword(request.getPassword(), passwordEncoder)) return findUser;
            else throw new IllegalArgumentException("[ UserService ] : password 불일치");
        }
    }

    @Transactional(readOnly = true)
    public Optional<User> findById(long id) {
        return userRepository.findByIdAndJoinStatus(id,Status.ACTIVE);
    }

    @Transactional(readOnly = true)
    public Optional<User> kakaoLogin(KakaoCodeRequest request) {
        KakaoToken kakaoToken = getKakaoToken(request);
        KakaoUser kakaoUser =  getKakaoUser(kakaoToken.getAccess_token());


        Optional<User> userOp = userRepository.findFirstByEmailAndJoinStatus(kakaoUser.getEmail(),Status.ACTIVE);
        User user;
        if(userOp.isEmpty()) {
            user=User.of(kakaoUser.getEmail(), kakaoUser.getNickname(), kakaoToken.getAccess_token());
        }
        else{
            user=userOp.get();
            user.setKakaoToken(kakaoToken.getAccess_token());
        }

        return Optional.of(userRepository.save(user));
    }

    private KakaoToken getKakaoToken(KakaoCodeRequest request) {
        DefaultUriBuilderFactory uriBuilderFactory = new DefaultUriBuilderFactory("https://kauth.kakao.com");
        uriBuilderFactory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.NONE);
        URI uri = uriBuilderFactory.uriString("/oauth/token").build();

        return webClient.post()
                .uri(uri)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData("grant_type", "authorization_code")
                        .with("client_id", "76867f47209a454ed88ccf1080c4238c")
                        .with("redirect_uri", request.getRedirect_uri())
//                        .with("redirect_uri", "http://localhost:8080/kakao/signUp")
                        .with("code", request.getCode()))
                .retrieve()
                .bodyToMono(KakaoToken.class)
                .blockOptional().orElseThrow();

    }

    private KakaoUser getKakaoUser(String accessToken) {
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
        return KakaoUser.from(obj);
    }

    public User createUser(User user) {
        return userRepository.save(user);
    }

    public void logout(){
        kakaoLogout(); // 카카오 로그아웃 처리
        String token= getToken();
        long expiredTime=jwtDeserializer.jwtPayloadFromJWT(token).getExpiredTime()-now().getEpochSecond();
        ValueOperations<String,String> logoutValueOperation=redisTemplate.opsForValue();
        logoutValueOperation.set(token,"logout",expiredTime, TimeUnit.SECONDS);

    }

    private Long kakaoLogout(){
        Long userId=getMyInfo();
        Optional<User> user =userRepository.findByIdAndJoinStatus(userId,Status.ACTIVE);
        if(user.isEmpty()){
            throw new IllegalArgumentException("[ userService ] 존재하지 않는 user");
        }
        DefaultUriBuilderFactory uriBuilderFactory = new DefaultUriBuilderFactory("https://kapi.kakao.com");
        uriBuilderFactory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.NONE);

        URI uri = uriBuilderFactory.uriString("/v1/user/logout").build();

        ResponseEntity<String> response = webClient.post()
                .uri(uri)
                .header("Authorization", "Bearer " + user.get().getKakaoToken())
                .retrieve()
                .toEntity(String.class)
                .blockOptional().orElseThrow();

        JSONObject obj = new JSONObject(response.getBody());

        return userId;
    }

    public Object createNickName(){
        DefaultUriBuilderFactory uriBuilderFactory = new DefaultUriBuilderFactory("https://nickname.hwanmoo.kr");
        uriBuilderFactory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.NONE);

        URI uri = uriBuilderFactory.uriString("/")
                .queryParam("format","json").build();

        ResponseEntity<String> response = webClient.get()
                .uri(uri)
                .retrieve()
                .toEntity(String.class)
                .blockOptional().orElseThrow();

        JSONObject words = new JSONObject(response.getBody());
        JSONArray arr=(JSONArray)words.get("words");

        return arr.get(0);
    }

    public User updateUser(Long userId,UpdateUserDTO.Request updateUserDTO){
        Optional<User> updateUser=userRepository.findByIdAndJoinStatus(userId,Status.ACTIVE);
        if(updateUser.isPresent()){
            User user= updateUserEntity(updateUser.get(),updateUserDTO);
            return userRepository.save(user);
        }
        else throw new IllegalArgumentException("[ UserService ] : 존재하지 않는 user 입니다.");
    }

    public User updateUserEntity(User user,UpdateUserDTO.Request updateUserDTO){

        if(updateUserDTO.getEmail()!=null) user.changeEmail(updateUserDTO.getEmail());
        if(updateUserDTO.getSex()!=null) user.changeSex(updateUserDTO.getSex());
        if(updateUserDTO.getDateOfBirth()!=null) user.changeDateOfBirth(LocalDate.parse(updateUserDTO.getDateOfBirth()));
        return user;
    }

    public User findUser(Long userId) {
        return userRepository.findByIdAndJoinStatus(userId,Status.ACTIVE).orElseThrow(() -> {
            throw new IllegalArgumentException("[ userService ]: 존재하지 않는 user 입니다.");
        });
    }

    public void deleteUser(Long userId) {
        if(userRepository.findByIdAndJoinStatus(userId,Status.ACTIVE).isEmpty()) throw new IllegalArgumentException();
        else userRepository.delete(findUser(userId));
    }

    public void deleteAll() {
        userRepository.deleteAll();
    }

    public void inactiveUser(Long userId){
        Optional<User> findUser=userRepository.findByIdAndJoinStatus(userId,Status.ACTIVE);
        if(findUser.isPresent()){
            User user=findUser.get();
            user.inactiveUser();
            userRepository.save(user);
        }
        else throw new IllegalArgumentException("[ UserService ] 존재하지 않는 사용자");
    }

    public Long getMyInfo(){
        Object token=getToken();
        if(token.equals("")) return null;
        return jwtDeserializer.getUserId(token.toString());
    }

    public String getToken(){
        return SecurityContextHolder.getContext()
                .getAuthentication()
                .getCredentials().toString();
    }

}