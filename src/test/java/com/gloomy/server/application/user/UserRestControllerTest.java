package com.gloomy.server.application.user;

import com.gloomy.server.application.AbstractControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MvcResult;

import static com.gloomy.server.application.user.UserDTO.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserRestControllerTest extends AbstractControllerTest {

    @Order(1)
    @DisplayName("일반 회원가입")
    @Test
    void postUser() throws Exception {

        PostRequest postRequest = PostRequest.builder()
                                            .email("test1@gamil.com")
                                            .userName("test1")
                                            .password("test1234")
                                            .build();

        MvcResult mvcResult = mockMvc.perform(post("/user")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(postRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document.document(
                        requestFields(
                                fieldWithPath("email").type(JsonFieldType.STRING).description("유저 로그인 이메일"),
                                fieldWithPath("userName").type(JsonFieldType.STRING).description("유저 이름"),
                                fieldWithPath("password").type(JsonFieldType.STRING).description("유저 로그인 패스워드")
                                ),
                        responseFields(
//                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("Http 상태 코드"),
//                                fieldWithPath("message").type(JsonFieldType.STRING).description("상태 설명 메시지"),
//                                fieldWithPath("responseTime").type(JsonFieldType.STRING).description("응답 시간"),
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("유저 번호").optional(),
                                fieldWithPath("email").type(JsonFieldType.STRING).description("유저 이메일").optional(),
                                fieldWithPath("username").type(JsonFieldType.STRING).description("유저 이름").optional(),
                                fieldWithPath("token").type(JsonFieldType.STRING).description("토큰").optional(),
                                fieldWithPath("image").type(JsonFieldType.STRING).description("이미지 링크")
                        )
                ))
                .andReturn();
    }

    @Order(2)
    @DisplayName("일반 로그인")
    @Test
    void login() throws Exception {

        LoginRequest loginRequest = LoginRequest.builder()
                .email("test2@gamil.com")
                .password("test234")
                .build();

        mockMvc.perform(post("/user/login")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document.document(
                        requestFields(
                                fieldWithPath("email").type(JsonFieldType.STRING).description("유저 로그인 이메일"),
                                fieldWithPath("password").type(JsonFieldType.STRING).description("유저 로그인 패스워드")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("유저 번호").optional(),
                                fieldWithPath("email").type(JsonFieldType.STRING).description("유저 이메일").optional(),
                                fieldWithPath("username").type(JsonFieldType.STRING).description("유저 이름").optional(),
                                fieldWithPath("token").type(JsonFieldType.STRING).description("토큰").optional(),
                                fieldWithPath("image").type(JsonFieldType.STRING).description("이미지 링크")
                        )
                )
        );
    }

    @Order(3)
    @DisplayName("카카오 로그인")
    @Test
    void kakaoLogin() throws Exception {
        KakaoCodeRequest kakaoCodeRequest = KakaoCodeRequest.builder()
                .code("Pj1NBbO3PyTEDiKUX3L5vtks3MY_hr3dkhY0sOmiUbpoIvgrTsxEhYe3YH1juvGXUJ9Fogo9dRsAAAF9n6Ky1Q")
                .build();

        mockMvc.perform(post("/user/login/kakao")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(kakaoCodeRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document.document(
                                requestFields(
                                        fieldWithPath("code").type(JsonFieldType.STRING).description("인가 코드")
                                ),
                                responseFields(
                                        fieldWithPath("id").type(JsonFieldType.NUMBER).description("유저 번호").optional(),
                                        fieldWithPath("email").type(JsonFieldType.STRING).description("유저 이메일").optional(),
                                        fieldWithPath("username").type(JsonFieldType.STRING).description("유저 이름").optional(),
                                        fieldWithPath("token").type(JsonFieldType.STRING).description("토큰").optional(),
                                        fieldWithPath("image").type(JsonFieldType.STRING).description("이미지 링크")
                                )
                        )
                );
    }

//    @Test
//    public void test() throws JsonProcessingException {
//        String decodedPayload = "{\"sub\":1,\"name\":\"jaesungahn91@kakao.com\",\"iat\":1639410941}";
//        UserJWTPayload jwtPayload = objectMapper.readValue(decodedPayload, UserJWTPayload.class);
//
//        System.out.println(jwtPayload);
//    }
}