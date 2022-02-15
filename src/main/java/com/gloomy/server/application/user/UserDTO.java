package com.gloomy.server.application.user;

import com.gloomy.server.domain.user.Image;
import com.gloomy.server.domain.user.Sex;
import com.gloomy.server.domain.user.User;
import lombok.*;
import org.joda.time.DateTime;
import org.json.JSONObject;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Nullable;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.Objects;

public class UserDTO {


    /**
     * Info
     **/
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Getter
    @ToString
    @Setter
    public static class KakaoToken {
        String token_type;
        String access_token;
        Integer expires_in;
        String refresh_token;
        Integer refresh_token_expires_in;
        String scope;


    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Getter
    @ToString
    public static class KakaoUser {
        Long id;
        String connected_at;
        KakaoAccount kakao_account;

        @Builder
        public KakaoUser(long id,
                         String connected_at,
                         boolean email_needs_agreement,
                         boolean profile_nickname_needs_agreement,
                         String nickname,
                         boolean is_email_valid,
                         boolean is_email_verified,
                         boolean has_email,
                         String email) {
            this.id = id;
            this.connected_at = connected_at;
            kakao_account=new KakaoAccount(email_needs_agreement,profile_nickname_needs_agreement,new Profile(nickname),is_email_valid,is_email_verified
            ,has_email,email);
        }
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Builder
    @ToString
    public static class KakaoAccount{
        boolean email_needs_agreement;
        boolean profile_nickname_needs_agreement;
        Profile profile;
        boolean is_email_valid;
        boolean is_email_verified;
        boolean has_email;
        String email;

    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Getter
    @ToString
    public static class Profile{
        String nickname;
    }


    /**
     * Request
     **/
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Getter
    @ToString
    public static class PostRequest {
        @Email(message = "이메일 양식에 맞지 않습니다.")
        String email;
        @NotBlank(message = "유저 이름을 입력하세요.")
        String userName;
        @NotBlank(message = "패스워드를 입력하세요.")
        String password;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Getter
    @ToString
    public static class LoginRequest {
        @Email(message = "이메일 양식에 맞지 않습니다.")
        String email;
        @NotBlank(message = "패스워드를 입력하세요.")
        String password;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Getter
    @ToString
    public static class CodeRequest {
        @NotBlank(message = "코드값을 입력하세요.")
        String code;
        String redirect_uri;

        public CodeRequest(String code){
            this.code=code;
        }
    }


    /**
     * Response
     **/
    @Value
    public static class Response {
        long id;
        String email;
        String username;
        String token;
//        String image;

        public static Response fromUserAndToken(User user, String token) {
            return new Response(user.getId(), user.getEmail(), user.getName(), token);
        }

    }


    public static class UpdateUserDTO{
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        @Getter
        @Setter
        @ToString
        public static class Request{
            @Email
            String email;
            Sex sex;
            MultipartFile image;
            String dateOfBirth;
        }

        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        @Getter
        @ToString
        public static class Response{
            @Email
            String email;
            Sex sex;
            String imageUrl;
            String dateOfBirth;
        }

    }
}