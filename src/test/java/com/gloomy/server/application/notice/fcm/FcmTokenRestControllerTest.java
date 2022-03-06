package com.gloomy.server.application.notice.fcm;

import com.gloomy.server.application.AbstractControllerTest;
import com.gloomy.server.application.user.TestUserDTO;
import com.gloomy.server.domain.jwt.JWTSerializer;
import com.gloomy.server.domain.notice.fcm.FcmTokenService;
import com.gloomy.server.domain.user.User;
import com.gloomy.server.domain.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(SpringExtension.class)
@SpringBootTest(properties = {
        "spring.config.location=classpath:test-application.yml,classpath:aws.yml"
})
@Transactional
public class FcmTokenRestControllerTest extends AbstractControllerTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    JWTSerializer jwtSerializer;

    private User saveUser;
    private String fcmToken;
    private FcmDto.Request fcmDto;

    @BeforeEach
    public void setUp(){
        User user= TestUserDTO.TestUser.makeTestUser();
        saveUser=userRepository.save(user);
        fcmToken="fcmToken";
        fcmDto=new FcmDto.Request(saveUser.getId(),fcmToken);
    }

    @DisplayName("fcm token save")
    @Test
    public void fcmToken_save() throws Exception {

        String token=jwtSerializer.jwtFromUser(saveUser);

        mockMvc.perform(post("/fcm/save")
                .header("Authorization", "Bearer " + token)
                .content(objectMapper.writeValueAsString(fcmDto))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("사용자 토큰")),
                        requestFields(
                                fieldWithPath("userId").description("user id ( 필수 )"),
                                fieldWithPath("fcmToken").description("fcm token ( 필수 )")
                        ),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("Http 상태 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("상태 설명 메시지"),
                                fieldWithPath("responseTime").type(JsonFieldType.STRING).description("응답 시간"),
                                fieldWithPath("result").type(JsonFieldType.STRING).description("결과").optional()
                        )
                ));
    }
}
