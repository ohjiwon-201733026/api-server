package com.gloomy.server.domain.user;


import com.gloomy.server.domain.common.entity.Status;
import com.gloomy.server.domain.jwt.JWTDeserializer;
import com.gloomy.server.domain.user.login.UriService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URI;
import java.util.Optional;



@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final JWTDeserializer jwtDeserializer;
    private final UriService uriService;

    @Transactional(readOnly = true)
    public Optional<User> findById(long id) {
        return userRepository.findByIdAndJoinStatus(id, Status.ACTIVE);
    }

    public User createUser(User user) {
        return userRepository.save(user);
    }

    public String createNickName(){

        MultiValueMap<String,String> params=new LinkedMultiValueMap<>();
        params.set("format","json");

        WebClient webClient= WebClient.builder().baseUrl("https://nickname.hwanmoo.kr").build();

        URI uri=uriService.getUri("https://nickname.hwanmoo.kr","/",params);

        ResponseEntity<String> response = webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/").queryParam("format","text").build())
                .retrieve()
                .toEntity(String.class)
                .blockOptional().orElseThrow();

        return response.getBody();
    }

    @Transactional(readOnly = true)
    public User findUser(Long userId) {
        return userRepository.findByIdAndJoinStatus(userId,Status.ACTIVE).orElseThrow(() -> {
            throw new IllegalArgumentException("[ userService ]: 존재하지 않는 user 입니다.");
        });
    }

    public void deleteUser(Long userId) {
        if(userRepository.findByIdAndJoinStatus(userId,Status.ACTIVE).isEmpty()) throw new IllegalArgumentException("[ userService ]: 존재하지 않는 user 입니다.");
        else userRepository.delete(findUser(userId));
    }

    public void deleteAll() {
        userRepository.deleteAll();
    }

    public User inactiveUser(Long userId){
        Optional<User> findUser=userRepository.findByIdAndJoinStatus(userId,Status.ACTIVE);

        if(findUser.isPresent()) {
            User user=findUser.get();
            user.inactiveUser();
            return userRepository.save(user);
        }

        throw new IllegalArgumentException("[ UserService ] 존재하지 않는 사용자");

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