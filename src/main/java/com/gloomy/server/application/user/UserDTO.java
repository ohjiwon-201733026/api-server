package com.gloomy.server.application.user;

import com.gloomy.server.domain.user.Image;
import com.gloomy.server.domain.user.Sex;
import com.gloomy.server.domain.user.User;
import lombok.*;
import org.json.JSONObject;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;

public class UserDTO {

    /**
     * Info
     **/
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Getter
    @ToString
    public static class KakaoToken {
        String token_type;
        String access_token;
        Integer expires_in;
        String refresh_token;
        Integer refresh_token_expires_in;
    }

    @Getter
    public static class KakaoUser {
        String nickname;
        String email;

        private KakaoUser(String nickname, String email) {
            this.nickname = nickname;
            this.email = email;
        }

        public static KakaoUser from(JSONObject obj) {
            return new KakaoUser(obj.getJSONObject("properties").getString("nickname"),
                    obj.getJSONObject("kakao_account").getString("email"));
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

    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Getter
    @ToString
    public static class UpdateUserDTO{
        Long userId;
        @Email
        String email;
        Sex sex;
        Image image;
        LocalDate dateOfBirth;
    }
}
