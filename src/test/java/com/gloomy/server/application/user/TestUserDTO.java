package com.gloomy.server.application.user;

import com.gloomy.server.application.image.TestImage;
import com.gloomy.server.domain.user.Type;
import com.gloomy.server.domain.user.User;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;


public class TestUserDTO {

    public static class TestUser{
        public static User makeTestUser(){
            return User.of("test@email.com", "testName", Type.KAKAO,"kakaoToken","refreshToken");
        }
    }

    public static class UpdateUserTestDTO{

        public static UserDTO.UpdateUserDTO.Request makeUpdateUserDtoRequest(){
            return UserDTO.UpdateUserDTO.Request.builder()
                    .email("updateEmail@email.com")
                    .build();
        }

        static MultiValueMap<String,String> generateParamMap(UserDTO.UpdateUserDTO.Request request){

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            if (request.getEmail() != null) {
                params.add("email", request.getEmail());
            }

            return params;
        }
    }

}