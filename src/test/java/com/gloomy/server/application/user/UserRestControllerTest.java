package com.gloomy.server.application.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.gloomy.server.application.AbstractControllerTest;
import com.gloomy.server.application.feed.TestFeedDTO;
import com.gloomy.server.application.feed.UpdateFeedDTO;
import com.gloomy.server.application.image.TestImage;
import com.gloomy.server.application.security.JWTAuthenticationProvider;
import com.gloomy.server.domain.jwt.JWTDeserializer;
import com.gloomy.server.domain.jwt.JWTSerializer;
import com.gloomy.server.domain.user.*;
import com.gloomy.server.infrastructure.jwt.UserJWTPayload;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import static com.gloomy.server.application.user.UserDTO.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
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

@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
@SpringBootTest(properties = {
        "spring.config.location=classpath:test-application.yml,classpath:aws.yml"
})
@Transactional
class UserRestControllerTest extends AbstractControllerTest {

    @Autowired
    UserService userService;
    @Autowired
    JWTSerializer jwtSerializer;

    @Autowired JWTAuthenticationProvider jwtAuthenticationProvider;

    TestImage testImage;
    User user;
    UpdateUserDTO.Request updateUserDTO;
    Authentication authentication;
    MultipartFile profileImage;

    @BeforeEach
    public void setUp(){
        authentication= SecurityContextHolder.getContext().getAuthentication();
        this.user= TestUserDTO.TestUser.makeTestUser();
        user.changeId(100L);
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
                                fieldWithPath("result.token").type(JsonFieldType.STRING).description("토큰").optional()
                        )
                ))
                .andReturn();
    }

    @Order(2)
    @DisplayName("일반 로그인")
    @Test
    void login() throws Exception {
        PostRequest postRequest = PostRequest.builder()
                .email("test1@gmail.com")
                .userName("test1")
                .password("test1234")
                .build();

        LoginRequest loginRequest = LoginRequest.builder()
                .email("test1@gmail.com")
                .password("test1234")
                .build();

        userService.signUp(postRequest);

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
                                fieldWithPath("result.token").type(JsonFieldType.STRING).description("토큰").optional()
//                                fieldWithPath("result.image").type(JsonFieldType.STRING).description("이미지 링크").optional()
                        )
                        )
                );
    }

    /*
    @Order(3)
    @DisplayName("카카오 로그인")
    @Test
    void kakaoLogin() throws Exception {
//        KakaoCodeRequest kakaoCodeRequest = KakaoCodeRequest.builder()
//                .code("cBWnEj1Ak9D__ZEthDVehV1_dfFfbgmKf46iFOPhfBkjxjh4pk5vpsUXLADO_yv1cXM5Two9cpgAAAF-i8KLAA")
//                .build();
        String code="alXSrLik4OWw7sKW-01c8I5rmZpYwtOduQ57HF3u8DqJgQJP8jFNspiIDasXXjigDt8NpQorDNMAAAF-i9qr4g";
        mockMvc.perform(get("/kakao/signUp")
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .param("code",code))
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
                                        fieldWithPath("result.token").type(JsonFieldType.STRING).description("토큰").optional()
//                                        fieldWithPath("result.image").type(JsonFieldType.STRING).description("이미지 링크")
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

/*
    @Test
    @WithMockUser
    public void updateUser() throws Exception {
        User saveUser=userService.createUser(user);
        MockMultipartFile firstUpdateImageFile = TestImage.convertOne(profileImage);
        String token=jwtSerializer.jwtFromUser(saveUser);
        MultiValueMap<String, String> params=
                TestUserDTO.UpdateUserTestDTO.generateParamMap(updateUserDTO);
        this.mockMvc.perform(fileUpload("/user/update")
//                .file(firstUpdateImageFile)
                .params(params)
                .header("Authorization","Bearer "+token)
//                )
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document.document(
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
        String token=jwtSerializer.jwtFromUser(saveUser);
        this.mockMvc.perform(get("/user/detail")
                .header("Authorization","Bearer "+token)
                .with(authentication(authentication))
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document.document(
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("상태 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("메세지"),
                                fieldWithPath("result.email").type(JsonFieldType.STRING).description("유저 이메일"),
                                fieldWithPath("result.sex").type(JsonFieldType.STRING).description("유저 성별"),
                                fieldWithPath("result.imageUrl").type(JsonFieldType.STRING).description("유저 이미지"),
                                fieldWithPath("result.dateOfBirth").type(JsonFieldType.STRING).description("유저 생년월일"),
                                fieldWithPath("responseTime").type(JsonFieldType.STRING).description("응답 시간")
                        )
                        )
                ).andReturn();
    }
 */

    @DisplayName("회원 탈퇴")
    @Test
    public void inactiveUser() throws Exception {
        User saveUser=userService.createUser(user);
        String token=jwtSerializer.jwtFromUser(saveUser);

        this.mockMvc.perform(get("/user/inactive")
                .header("Authorization","Bearer "+token)
                .with(authentication(authentication))
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("사용자 토큰 ( 필수 )")),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("Http 상태 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("상태 설명 메시지"),
                                fieldWithPath("responseTime").type(JsonFieldType.STRING).description("응답 시간"),
                                fieldWithPath("result").type(JsonFieldType.STRING).description("결과").optional()
                        )
                        )
                ).andReturn();

    }
/*
    @DisplayName("로그아웃")
    @Test
    public void logout() throws Exception {

        this.mockMvc.perform(get("/logout")
                .header("Authorization","Bearer eyJhbGciOiJIUzI1NiIsInR5cGUiOiJKV1QifQ.eyJzdWIiOjExNDQsIm5hbWUiOiJvanc5NzA3MjVAbmF2ZXIuY29tIiwiaWF0IjoxNjQzODExNDY5fQ.ndpwhE6b1qtyX-nwcVW4KvdmhOMrO6T5bZ1usCSG3tc")
                .with(authentication(authentication))
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

    }


 */



}