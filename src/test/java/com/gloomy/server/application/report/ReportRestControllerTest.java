package com.gloomy.server.application.report;

import com.gloomy.server.application.AbstractControllerTest;
import com.gloomy.server.application.feed.FeedDTO;
import com.gloomy.server.application.feed.FeedService;
import com.gloomy.server.application.feed.TestFeedDTO;
import com.gloomy.server.domain.feed.Feed;
import com.gloomy.server.domain.jwt.JWTSerializer;
import com.gloomy.server.domain.report.Report;
import com.gloomy.server.domain.report.ReportService;
import com.gloomy.server.domain.user.User;
import com.gloomy.server.domain.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import static com.gloomy.server.application.user.TestUserDTO.TestUser.makeTestUser;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(properties = {
        "spring.config.location=classpath:test-application.yml,classpath:aws.yml"
})
@Transactional
public class ReportRestControllerTest extends AbstractControllerTest{
    @Autowired
    ReportService reportService;
    @Autowired
    UserService userService;
    @Autowired
    FeedService feedService;
    @Autowired
    JWTSerializer jwtSerializer;

    private User testUser;
    private Report testReport;
    private TestFeedDTO testFeedDTO;
    FeedDTO.Request userFeedDTO;

    @BeforeEach
    public void setUp(){
        testUser= makeTestUser();
        testFeedDTO = new TestFeedDTO(testUser, 1);
        userFeedDTO = new FeedDTO.Request(
                testFeedDTO.getCategory(), testFeedDTO.getTitle(), testFeedDTO.getContent(), testFeedDTO.getImages());
    }
/*
    @Test
    public void reportFeed() throws Exception {

        User saveUser=userService.createUser(testUser);
        Feed saveFeed = feedService.createFeed(saveUser.getId(), userFeedDTO);

        String token=jwtSerializer.jwtFromUser(saveUser);

        mockMvc.perform(get("/report")
                .header("Authorization","Bearer "+token)
                .param("feedId",String.valueOf(saveFeed.getId()))
                .param("reportCategory","category"))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("사용자 토큰")),
                        requestParameters(
                                parameterWithName("feedId").description("신고 피드 id ( 필수 )"),
                                parameterWithName("reportCategory").description("신고 사유 카테고리 ( 필수 )")
                        ),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("Http 상태 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("상태 설명 메시지"),
                                fieldWithPath("responseTime").type(JsonFieldType.STRING).description("응답 시간"),
                                fieldWithPath("result").type(JsonFieldType.STRING).description("결과").optional()
                        )
                        )
                );

    }

 */
}