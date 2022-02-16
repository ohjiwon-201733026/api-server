package com.gloomy.server.application.user;

import com.gloomy.server.application.image.TestImage;
import com.gloomy.server.domain.user.Password;
import com.gloomy.server.domain.user.Sex;
import com.gloomy.server.domain.user.User;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.time.LocalDate;

public class TestUserDTO {

    public static class TestUser{
        public static User makeTestUser(){
            return User.of("test@email.com","testName",new Password("test")
                    , Sex.MALE,2020,01,01);
        }
    }

    static class UpdateUserTestDTO{

        static UserDTO.UpdateUserDTO.Request makeUpdateUserDtoRequest(){
            TestImage testImage=new TestImage();

            return UserDTO.UpdateUserDTO.Request.builder()
                    .email("updateEmail@email.com")
                    .sex(Sex.FEMALE)
                    .dateOfBirth(LocalDate.of(2022,01,01).toString())
                    .image(testImage.makeImages(1).get(0))
                    .build();
        }

        static MultiValueMap<String,String> generateParamMap(UserDTO.UpdateUserDTO.Request request){

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            if (request.getEmail() != null) {
                params.add("email", request.getEmail());
            }
            if (request.getSex() != null) {
                params.add("sex", request.getSex().name());
            }
            if (request.getDateOfBirth() != null) {
                params.add("dateOfBirth", request.getDateOfBirth().toString());
            }

            return params;
        }
    }

}