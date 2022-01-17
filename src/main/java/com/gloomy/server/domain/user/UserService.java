package com.gloomy.server.domain.user;

import com.gloomy.server.application.image.UserProfileImageService;
import com.gloomy.server.domain.comment.Comment;
import com.gloomy.server.domain.feed.Feed;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

    @Transactional
    public User signUp(PostRequest postRequest) {
        final Password encodedPassword = Password.of(postRequest.getPassword(), passwordEncoder);
        User user = User.of(postRequest.getEmail(),
                postRequest.getUserName(),
                encodedPassword);
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public Optional<User> login(LoginRequest request) {
        return userRepository.findFirstByEmail(request.getEmail())
                .filter(user -> user.matchesPassword(request.getPassword(), passwordEncoder));
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
            user = Optional.of(userRepository.save(User.of(kakaoUser.getEmail(), kakaoUser.getNickname())));
        }
        return user;
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
                        .with("redirect_uri", "http://localhost:3030/")
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

    public User updateUser(Long userId, UpdateUserDTO.Request updateUserDTO){
        Optional<User> updateUser=userRepository.findById(userId);
        if(updateUser.isPresent()){
            User user= updateUserEntity(updateUser.get(),updateUserDTO);
            return userRepository.save(user);
        }
        else throw new IllegalArgumentException();
    }

    public User updateUserEntity(User user,UpdateUserDTO.Request updateUserDTO){

        if(updateUserDTO.getEmail()!=null) user.changeEmail(updateUserDTO.getEmail());
        if(updateUserDTO.getSex()!=null) user.changeSex(updateUserDTO.getSex());
//        if(updateUserDTO.getImage()!=null) {
//            userProfileImageService.uploadUserImage(user,updateUserDTO.getImage());
//        }
        if(updateUserDTO.getDateOfBirth()!=null) user.changeDateOfBirth(LocalDate.parse(updateUserDTO.getDateOfBirth()));
        return user;
    }

    public User findUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> {
            throw new IllegalArgumentException();
        });
    }

    public void deleteUser(Long userId) {
        userRepository.delete(findUser(userId));
    }

    public void deleteAll() {
        userRepository.deleteAll();
    }

    @Transactional(readOnly = true)
    public List<Feed> findFeeds(Long userId){
        return findUser(userId).getFeeds();
    }

    @Transactional(readOnly = true)
    public List<Comment> findComments(Long userId){
        return findUser(userId).getComments();
    }
}
