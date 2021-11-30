package com.gloomy.server.application.user;

import com.gloomy.server.domain.user.User;
import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

public class UserDTO {

    /**
     * Info
     **/
    @Getter
    public static class KakaoToken {
        String token_type;
        String access_token;
        Integer expires_in;
        String refresh_token;
        Integer refresh_token_expires_in;
    }

    @Getter
    public static class KakaoUserInfo {
        String id;
        KakaoAccount kakao_account;

        public String getEmail() {
            return kakao_account.getEmail();
        }

        static class KakaoAccount {
            String email;

            public String getEmail() {
                return email;
            }
        }
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
    public static class KakaoCodeRequest {
        @NotBlank(message = "코드값을 입력하세요.")
        String code;
        String grantType;
        String clientId;
        String redirectUri;
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
        String image;

        public static Response fromUserAndToken(User user, String token) {
            return new Response(user.getId(), user.getEmail(), user.getName(), token, "");
        }
    }
}
