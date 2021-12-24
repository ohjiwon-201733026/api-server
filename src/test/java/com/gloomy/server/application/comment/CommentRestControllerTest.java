package com.gloomy.server.application.comment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gloomy.server.application.AbstractControllerTest;
import com.gloomy.server.application.feed.FeedService;
import com.gloomy.server.application.feed.TestFeedDTO;
import com.gloomy.server.application.feed.TestUserDTO;
import com.gloomy.server.application.image.ImageService;
import com.gloomy.server.domain.feed.Feed;
import com.gloomy.server.domain.user.User;
import com.gloomy.server.domain.user.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CommentRestControllerTest extends AbstractControllerTest {
    @Autowired
    UserService userService;
    @Autowired
    FeedService feedService;
    @Autowired
    ImageService imageService;
    @Autowired
    CommentService commentService;
    @Autowired
    ObjectMapper objectMapper;
    TestCommentDTO testCommentDTO;

    @BeforeEach
    void beforeEach() {
        User tmpUser = new TestUserDTO().makeTestUser();
        User testUser = userService.createUser(tmpUser);
        TestFeedDTO testFeedDTO = new TestFeedDTO(testUser, 1);
        Feed testFeed = feedService.createFeed(testFeedDTO.makeNonUserFeedDTO());
        testCommentDTO = new TestCommentDTO(testFeed.getId(), testUser.getId());
    }

    @AfterEach
    void afterEach() {
        commentService.deleteAll();
        imageService.deleteAll();
        feedService.deleteAll();
        userService.deleteAll();
    }

    @DisplayName("댓글_생성_비회원")
    @Test
    void createNonUserComment() throws Exception {
        CommentDTO.Request request = testCommentDTO.makeNonUserCommentDTO();

        String body = objectMapper.writeValueAsString(request);

        this.mockMvc.perform(post("/comment")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(body))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document.document(
                        requestFields(
                                fieldWithPath("content").description("댓글 내용"),
                                fieldWithPath("feedId").description("피드 ID"),
                                fieldWithPath("userId").description("회원 ID").optional(),
                                fieldWithPath("password").description("비밀번호").optional()),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("댓글 ID"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("댓글 내용"),
                                fieldWithPath("feedId").type(JsonFieldType.NUMBER).description("피드 ID"),
                                fieldWithPath("userId").type(JsonFieldType.NUMBER).description("회원 ID").optional(),
                                fieldWithPath("password").type(JsonFieldType.STRING).description("비밀번호").optional()
                        )
                ));
    }

    @DisplayName("댓글_생성_회원")
    @Test
    void createUserComment() throws Exception {
        CommentDTO.Request request = testCommentDTO.makeUserCommentDTO();

        String body = objectMapper.writeValueAsString(request);

        this.mockMvc.perform(post("/comment")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(body))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document.document(
                        requestFields(
                                fieldWithPath("content").description("댓글 내용"),
                                fieldWithPath("feedId").description("피드 ID"),
                                fieldWithPath("userId").description("회원 ID").optional(),
                                fieldWithPath("password").description("비밀번호").optional()),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("댓글 ID"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("댓글 내용"),
                                fieldWithPath("feedId").type(JsonFieldType.NUMBER).description("피드 ID"),
                                fieldWithPath("userId").type(JsonFieldType.NUMBER).description("회원 ID").optional(),
                                fieldWithPath("password").type(JsonFieldType.STRING).description("비밀번호").optional()
                        )
                ));
    }
}
