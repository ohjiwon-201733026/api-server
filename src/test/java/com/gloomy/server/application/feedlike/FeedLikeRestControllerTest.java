package com.gloomy.server.application.feedlike;

import com.gloomy.server.application.AbstractControllerTest;
import com.gloomy.server.application.feed.FeedDTO;
import com.gloomy.server.application.feed.FeedService;
import com.gloomy.server.application.feed.TestFeedDTO;
import com.gloomy.server.application.feed.TestUserDTO;
import com.gloomy.server.domain.feed.Feed;
import com.gloomy.server.domain.jwt.JWTSerializer;
import com.gloomy.server.domain.user.User;
import com.gloomy.server.domain.user.UserService;
import org.junit.jupiter.api.AfterEach;
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
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(properties = {
        "spring.config.location=classpath:test-application.yml,classpath:aws.yml"
})
class FeedLikeRestControllerTest extends AbstractControllerTest {
    @Autowired
    private FeedService feedService;
    @Autowired
    private UserService userService;
    @Autowired
    private JWTSerializer jwtSerializer;

    private Feed testFeed;

    @BeforeEach
    void beforeEach() {
        User testUser = userService.createUser(TestUserDTO.makeTestUser());
        TestFeedDTO testFeedDTO = new TestFeedDTO(testUser, 1);
        testFeed = feedService.createFeed(null, testFeedDTO.makeNonUserFeedDTO());
    }

    @AfterEach
    void afterEach() {
        feedService.deleteAll();
        userService.deleteAll();
    }

    @DisplayName("비회원_좋아요_생성")
    @Transactional
    @Test
    void createNonuserFeedLike() throws Exception {
        FeedLikeDTO.Request request = new FeedLikeDTO.Request(testFeed.getId());

        String body = objectMapper.writeValueAsString(request);

        this.mockMvc.perform(post("/like")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document.document(
                        requestFields(
                                fieldWithPath("feedId").type(JsonFieldType.NUMBER).description("피드 ID")),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("상태 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                fieldWithPath("result").description("응답 데이터"),
                                fieldWithPath("result.id").type(JsonFieldType.NUMBER).description("좋아요 ID"),
                                fieldWithPath("result.feedId").type(JsonFieldType.NUMBER).description("피드 ID"),
                                fieldWithPath("result.userId").type(JsonFieldType.NULL).description("좋아요한 회원 ID (NULL)"),
                                fieldWithPath("result.ip").type(JsonFieldType.STRING).description("좋아요한 IP"),
                                fieldWithPath("result.status").type(JsonFieldType.STRING).description("좋아요 상태 (ACTIVE, INACTIVE)"),
                                fieldWithPath("result.createdAt").type(JsonFieldType.STRING).description("좋아요 생성시간"),
                                fieldWithPath("result.updatedAt").type(JsonFieldType.STRING).description("좋아요 수정시간"),
                                fieldWithPath("result.deletedAt").type(JsonFieldType.STRING).description("좋아요 삭제시간"),
                                fieldWithPath("responseTime").type(JsonFieldType.STRING).description("응답 시간")
                        )
                ));
    }

    @DisplayName("회원_좋아요_생성")
    @Transactional
    @Test
    void createUserFeedLike() throws Exception {
        User testUser = userService.createUser(TestUserDTO.makeTestUser());
        String token = jwtSerializer.jwtFromUser(testUser);
        FeedLikeDTO.Request request = new FeedLikeDTO.Request(testFeed.getId());

        String body = objectMapper.writeValueAsString(request);

        this.mockMvc.perform(post("/like")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("사용자 토큰")),
                        requestFields(
                                fieldWithPath("feedId").type(JsonFieldType.NUMBER).description("피드 ID")),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("상태 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                fieldWithPath("result").description("응답 데이터"),
                                fieldWithPath("result.id").type(JsonFieldType.NUMBER).description("좋아요 ID"),
                                fieldWithPath("result.feedId").type(JsonFieldType.NUMBER).description("피드 ID"),
                                fieldWithPath("result.userId").type(JsonFieldType.NUMBER).description("좋아요한 회원 ID"),
                                fieldWithPath("result.ip").type(JsonFieldType.STRING).description("좋아요한 IP"),
                                fieldWithPath("result.status").type(JsonFieldType.STRING).description("좋아요 상태 (ACTIVE, INACTIVE)"),
                                fieldWithPath("result.createdAt").type(JsonFieldType.STRING).description("좋아요 생성시간"),
                                fieldWithPath("result.updatedAt").type(JsonFieldType.STRING).description("좋아요 수정시간"),
                                fieldWithPath("result.deletedAt").type(JsonFieldType.STRING).description("좋아요 삭제시간"),
                                fieldWithPath("responseTime").type(JsonFieldType.STRING).description("응답 시간")
                        )
                ));
    }
}
