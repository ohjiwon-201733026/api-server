package com.gloomy.server.application.user;

import com.gloomy.server.domain.user.User;
import lombok.AccessLevel;
import lombok.Builder;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

public class UserDTO {

    public static class Request {
        @Email
        String email;
        @NotBlank
        String userName;
        @NotBlank
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

    /*public static class SingUpRequest {
        private
    }*/
}
