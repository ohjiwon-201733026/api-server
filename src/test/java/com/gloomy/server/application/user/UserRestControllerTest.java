package com.gloomy.server.application.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.gloomy.server.application.AbstractControllerTest;
import com.gloomy.server.application.feed.TestFeedDTO;
import com.gloomy.server.application.feed.UpdateFeedDTO;
import com.gloomy.server.application.image.TestImage;
import com.gloomy.server.domain.feed.Feed;
import com.gloomy.server.domain.user.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.gloomy.server.application.user.UserDTO.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.fileUpload;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


class UserRestControllerTest extends AbstractControllerTest {

    @Autowired
    UserService userService;
    TestImage testImage;
    User user;
    UpdateUserDTO.Request updateUserDTO;
    Authentication authentication;
    MultipartFile profileImage;

    @BeforeEach
    public void setUp(){
        authentication= SecurityContextHolder.getContext().getAuthentication();
        this.user= TestUserDTO.TestUser.makeTestUser();
        user.changeId(1L);
        testImage=new TestImage();
        profileImage=testImage.makeImages(1).get(0);
        this.updateUserDTO= TestUserDTO.UpdateUserTestDTO.makeUpdateUserDtoRequest();
    }


    @Order(1)
    @DisplayName("일반 회원가입")
    @Test
    void postUser() throws Exception {

        PostRequest postRequest = PostRequest.builder()
                .email("test1@gmail.com")
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
                                fieldWithPath("email").type(JsonFieldType.STRING).description("유저 로그인 이메일 (필수)"),
                                fieldWithPath("userName").type(JsonFieldType.STRING).description("유저 이름 (필수)"),
                                fieldWithPath("password").type(JsonFieldType.STRING).description("유저 로그인 패스워드 (필수)")
                        ),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("Http 상태 코드 (필수)"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("상태 설명 메시지 (필수)"),
                                fieldWithPath("responseTime").type(JsonFieldType.STRING).description("응답 시간 (필수)"),
                                fieldWithPath("result.id").type(JsonFieldType.NUMBER).description("유저 번호").optional(),
                                fieldWithPath("result.email").type(JsonFieldType.STRING).description("유저 이메일").optional(),
                                fieldWithPath("result.username").type(JsonFieldType.STRING).description("유저 이름").optional(),
                                fieldWithPath("result.token").type(JsonFieldType.STRING).description("토큰").optional(),
                                fieldWithPath("result.image").type(JsonFieldType.STRING).description("이미지 링크")
                        )
                ))
                .andReturn();
    }
    /*
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
                                fieldWithPath("email").type(JsonFieldType.STRING).description("유저 로그인 이메일 (필수)"),
                                fieldWithPath("password").type(JsonFieldType.STRING).description("유저 로그인 패스워드(필수)")
                        ),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("Http 상태 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("상태 설명 메시지"),
                                fieldWithPath("responseTime").type(JsonFieldType.STRING).description("응답 시간"),
                                fieldWithPath("result.id").type(JsonFieldType.NUMBER).description("유저 번호").optional(),
                                fieldWithPath("result.email").type(JsonFieldType.STRING).description("유저 이메일").optional(),
                                fieldWithPath("result.username").type(JsonFieldType.STRING).description("유저 이름").optional(),
                                fieldWithPath("result.token").type(JsonFieldType.STRING).description("토큰").optional(),
                                fieldWithPath("result.image").type(JsonFieldType.STRING).description("이미지 링크").optional()
                        )
                        )
                );
    }
    /*
    @Order(3)
    @DisplayName("카카오 로그인")
    @Test
    void kakaoLogin() throws Exception {
        KakaoCodeRequest kakaoCodeRequest = KakaoCodeRequest.builder()
                .code("GuRNQ9tc3Fa9QbZ6zw-D-HAAagDVPAE2GHCTiLyHEy0JwlErMY8DjoPuvAAfEvLekIhZ2wo9dJkAAAF-bip6uQ")
                .build();

        mockMvc.perform(post("/kakao/signUp")
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(kakaoCodeRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document.document(
                                requestFields(
                                        fieldWithPath("code").type(JsonFieldType.STRING).description("인가 코드 (필수)")
                                ),
                                responseFields(
                                        fieldWithPath("code").type(JsonFieldType.NUMBER).description("Http 상태 코드"),
                                        fieldWithPath("message").type(JsonFieldType.STRING).description("상태 설명 메시지"),
                                        fieldWithPath("responseTime").type(JsonFieldType.STRING).description("응답 시간"),
                                        fieldWithPath("result.id").type(JsonFieldType.NUMBER).description("유저 번호").optional(),
                                        fieldWithPath("result.email").type(JsonFieldType.STRING).description("유저 이메일").optional(),
                                        fieldWithPath("result.username").type(JsonFieldType.STRING).description("유저 이름").optional(),
                                        fieldWithPath("result.token").type(JsonFieldType.STRING).description("토큰").optional(),
                                        fieldWithPath("result.image").type(JsonFieldType.STRING).description("이미지 링크")
                                )
                        )
                );
    }

     */

//    @Test
//    public void test() throws JsonProcessingException {
//        String decodedPayload = "{\"sub\":1,\"name\":\"jaesungahn91@kakao.com\",\"iat\":1639410941}";
//        UserJWTPayload jwtPayload = objectMapper.readValue(decodedPayload, UserJWTPayload.class);
//
//        System.out.println(jwtPayload);
//    }


    @Test
    @WithMockUser
    public void updateUser() throws Exception {
        User saveUser=userService.createUser(user);

        MockMultipartFile firstUpdateImageFile = TestImage.convertOne(profileImage);

        MultiValueMap<String, String> params=
                TestUserDTO.UpdateUserTestDTO.generateParamMap(updateUserDTO);


        this.mockMvc.perform(fileUpload("/user/update/{userId}", saveUser.getId())
                .file(firstUpdateImageFile)
                .params(params)
                .with(authentication(authentication)))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document.document(
                        pathParameters(
                                parameterWithName("userId").description("수정 유저 ID (필수)")),
                        requestParameters(
                                parameterWithName("email").description("수정 유저 이메일").optional(),
                                parameterWithName("sex").description("수정 유저 성별").optional(),
                                parameterWithName("dateOfBirth").description("수정 유저 생년월일").optional()
                                ),
                        requestParts(
                                partWithName("image").description("수정 프로필 이미지").optional()),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("상태 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("메세지"),
                                fieldWithPath("result.userId").type(JsonFieldType.NUMBER).description("수정된 유저 ID"),
                                fieldWithPath("result.email").type(JsonFieldType.STRING).description("수정된 유저 이메일"),
                                fieldWithPath("result.sex").type(JsonFieldType.STRING).description("수정된 유저 성별"),
                                fieldWithPath("result.imageUrl").type(JsonFieldType.STRING).description("수정된 유저 이미지 url"),
                                fieldWithPath("result.dateOfBirth").type(JsonFieldType.STRING).description("수정된 유저 생년월일"),
                                fieldWithPath("responseTime").type(JsonFieldType.STRING).description("응답 시간")
                        )
                ));


    }

    @DisplayName("User Detail")
    @Test
    @WithMockUser
    public void userDetail() throws Exception {
        User saveUser=userService.createUser(user);

        this.mockMvc.perform(get("/user/detail/{userId}", saveUser.getId())
                .with(authentication(authentication))
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andDo(document.document(
                        pathParameters(
                                parameterWithName("userId").description("조회할 유저 ID(필수)")
                        ),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("상태 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("메세지"),
                                fieldWithPath("result.userId").type(JsonFieldType.NUMBER).description("유저 아이디"),
                                fieldWithPath("result.email").type(JsonFieldType.STRING).description("유저 이메일"),
                                fieldWithPath("result.sex").type(JsonFieldType.STRING).description("유저 성별"),
                                fieldWithPath("result.imageUrl").type(JsonFieldType.STRING).description("유저 이미지"),
                                fieldWithPath("result.dateOfBirth").type(JsonFieldType.STRING).description("유저 생년월일"),
                                fieldWithPath("responseTime").type(JsonFieldType.STRING).description("응답 시간")
                        )
                        )
                ).andReturn();
    }




}