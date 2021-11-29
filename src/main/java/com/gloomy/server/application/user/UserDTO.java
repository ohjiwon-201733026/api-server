package com.gloomy.server.application.user;

import com.gloomy.server.domain.user.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

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
    public static class Request {
        @Email(message = "이메일 양식에 맞지 않습니다.")
        String email;
        @NotBlank(message = "유저 이름을 입력하세요.")
        String userName;
        @NotBlank(message = "패스워드를 입력하세요.")
        String password;

//        SingUpRequest toSignUpRequest() {
//            return new User
//        }

//        @Builder(access = AccessLevel.PROTECTED)
//        private Request(String email, String userName, String password) {
//            this.email = email;
//            this.userName = userName;
//            this.password = password;
//        }
//
//        public static
    }

    //    public static class SingUpRequest {
    //        private
    //    }

    @Getter
    public static class KakaoCodeRequest {
        @NotBlank(message = "코드값을 입력하세요.")
        String code;
        String grantType;
        String clientId;
        String redirectUri;
//        String clientSecret;
    }


    /**
     * Response
     **/
    public static class Response {
        String email;
        String username;
        String token;
        String bio;
        String image;

        @Builder(access = AccessLevel.PROTECTED)
        private Response(String email, String username, String token, String bio, String image) {
            this.email = email;
            this.username = username;
            this.token = token;
            this.bio = bio;
            this.image = image;
        }

        public static Response fromUserAndToken(User user, String token) {
            return Response.builder()
                    .email(user.getEmail())
                    .username(user.getName())
                    .token(token)
                    .bio("")
                    .image("")
                    .build();
        }
    }
}
