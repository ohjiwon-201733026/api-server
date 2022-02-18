package com.gloomy.server.application.jwt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.gloomy.server.application.AbstractControllerTest;
import com.gloomy.server.application.user.TestUserDTO;
import com.gloomy.server.application.user.UserDTO;
import com.gloomy.server.domain.jwt.JWTSerializer;
import com.gloomy.server.domain.user.User;
import com.gloomy.server.domain.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
@SpringBootTest(properties = {
        "spring.config.location=classpath:test-application.yml,classpath:aws.yml"
})
@Transactional
public class JwtRestControllerTest extends AbstractControllerTest {


    @Autowired private JwtService jwtService;
    @Autowired private UserRepository userRepository;
    @Autowired private JWTSerializer jwtSerializer;

    private String accessToken;
    private String refreshToken;
    private User saveUser;

    private JwtDTO.Request request;

    @BeforeEach
    public void setUp(){
        User testUser= TestUserDTO.TestUser.makeTestUser();
        saveUser=userRepository.save(testUser);
        accessToken=jwtSerializer.jwtFromUser(saveUser);
        refreshToken=jwtSerializer.createRefreshToken();

        saveUser.changeRefreshToken(refreshToken);
        userRepository.save(saveUser);

        request=new JwtDTO.Request(accessToken,refreshToken);
    }

    @DisplayName("access token 재발급")
    @Test
    public void reissue() throws Exception {

        this.mockMvc.perform(post("/jwt/reissue")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document.document(
                        requestFields(
                                fieldWithPath("accessToken").description("expired AccessToken (필수)"),
                                fieldWithPath("refreshToken").description("refreshToken (필수)")
                        ),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("Http 상태 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("상태 설명 메시지"),
                                fieldWithPath("responseTime").type(JsonFieldType.STRING).description("응답 시간"),
                                fieldWithPath("result").type(JsonFieldType.OBJECT).description("응답 데이터"),
                                fieldWithPath("result.accessToken").type(JsonFieldType.STRING).description("새로 발급된 accessToken"),
                                fieldWithPath("result.refreshToken").type(JsonFieldType.STRING).description("새로 발급된 refreshToken")

                        )
                        )
                );
    }

}
