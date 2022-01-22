package com.gloomy.server.application.comment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gloomy.server.application.AbstractControllerTest;
import com.gloomy.server.application.feed.FeedService;
import com.gloomy.server.application.feed.TestFeedDTO;
import com.gloomy.server.application.feed.TestUserDTO;
import com.gloomy.server.application.image.ImageService;
import com.gloomy.server.domain.comment.Comment;
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

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
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
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 상태 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                fieldWithPath("result.id").type(JsonFieldType.NUMBER).description("댓글 ID"),
                                fieldWithPath("result.content").type(JsonFieldType.STRING).description("댓글 내용"),
                                fieldWithPath("result.feedId").type(JsonFieldType.NUMBER).description("피드 ID"),
                                fieldWithPath("result.userId").type(JsonFieldType.NULL).description("회원 ID"),
                                fieldWithPath("result.password").type(JsonFieldType.STRING).description("비밀번호"),
                                fieldWithPath("responseTime").type(JsonFieldType.STRING).description("응답 시간")
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
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 상태 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                fieldWithPath("result.id").type(JsonFieldType.NUMBER).description("댓글 ID"),
                                fieldWithPath("result.content").type(JsonFieldType.STRING).description("댓글 내용"),
                                fieldWithPath("result.feedId").type(JsonFieldType.NUMBER).description("피드 ID"),
                                fieldWithPath("result.userId").type(JsonFieldType.NUMBER).description("회원 ID"),
                                fieldWithPath("result.password").type(JsonFieldType.NULL).description("비밀번호"),
                                fieldWithPath("responseTime").type(JsonFieldType.STRING).description("응답 시간")
                        )
                ));
    }

    @DisplayName("피드_댓글_전체_조회")
    @Test
    void getFeedAllComments() throws Exception {
        CommentDTO.Request request = testCommentDTO.makeNonUserCommentDTO();

        commentService.createComment(request);
        commentService.createComment(request);

        this.mockMvc.perform(get("/comment/feed/{feedId}", request.getFeedId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document.document(
                        pathParameters(
                                parameterWithName("feedId").description("조회할 댓글의 피드 ID")),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 상태 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                fieldWithPath("result.content[].id").type(JsonFieldType.NUMBER).description("댓글 ID"),
                                fieldWithPath("result.content[].content").type(JsonFieldType.STRING).description("댓글 내용"),
                                fieldWithPath("result.content[].feedId").type(JsonFieldType.NUMBER).description("피드 ID"),
                                fieldWithPath("result.content[].userId").description("회원 ID"),
                                fieldWithPath("result.content[].password").description("비밀번호"),

                                fieldWithPath("result.pageable").type(JsonFieldType.STRING).description("pageable 정보"),
                                fieldWithPath("result.totalPages").type(JsonFieldType.NUMBER).description("전체 페이지 수"),
                                fieldWithPath("result.totalElements").type(JsonFieldType.NUMBER).description("전체 페이지 내 요소의 수"),
                                fieldWithPath("result.last").type(JsonFieldType.BOOLEAN).description("마지막 페이지 여부"),
                                fieldWithPath("result.numberOfElements").type(JsonFieldType.NUMBER).description("현재 페이지 내 요소의 수"),
                                fieldWithPath("result.first").type(JsonFieldType.BOOLEAN).description("첫 페이지 여부"),
                                fieldWithPath("result.size").type(JsonFieldType.NUMBER).description("페이지 당 출력 갯수"),
                                fieldWithPath("result.sort.sorted").type(JsonFieldType.BOOLEAN).description("정렬 여부"),
                                fieldWithPath("result.sort.unsorted").type(JsonFieldType.BOOLEAN).description("비정렬 여부"),
                                fieldWithPath("result.sort.empty").type(JsonFieldType.BOOLEAN).description("정렬 비어있는지 여부"),
                                fieldWithPath("result.number").type(JsonFieldType.NUMBER).description("현재 페이지 인덱스"),
                                fieldWithPath("result.empty").type(JsonFieldType.BOOLEAN).description("비어있는지 여부"),
                                fieldWithPath("responseTime").type(JsonFieldType.STRING).description("응답 시간")
                        )
                ));
    }

    @DisplayName("댓글_조회")
    @Test
    void getComment() throws Exception {
        CommentDTO.Request request = testCommentDTO.makeNonUserCommentDTO();

        Comment createdComment = commentService.createComment(request);

        this.mockMvc.perform(get("/comment/{commentId}", createdComment.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document.document(
                        pathParameters(
                                parameterWithName("commentId").description("조회할 댓글 ID")),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 상태 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                fieldWithPath("result.id").type(JsonFieldType.NUMBER).description("댓글 ID"),
                                fieldWithPath("result.content").type(JsonFieldType.STRING).description("댓글 내용"),
                                fieldWithPath("result.feedId").type(JsonFieldType.NUMBER).description("피드 ID"),
                                fieldWithPath("result.userId").description("회원 ID"),
                                fieldWithPath("result.password").description("비밀번호"),
                                fieldWithPath("responseTime").type(JsonFieldType.STRING).description("응답 시간")
                        )
                ));
    }

    @DisplayName("댓글_수정")
    @Test
    void updateComment() throws Exception {
        String updateContent = "새 글";
        CommentDTO.Request request = testCommentDTO.makeNonUserCommentDTO();
        UpdateCommentDTO.Request updateRequest = new UpdateCommentDTO.Request();
        updateRequest.setContent(updateContent);

        Comment createdComment = commentService.createComment(request);
        String body = objectMapper.writeValueAsString(updateRequest);

        this.mockMvc.perform(patch("/comment/{commentId}", createdComment.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(body))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document.document(
                        pathParameters(
                                parameterWithName("commentId").description("수정할 댓글 ID")),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 상태 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                fieldWithPath("result.id").type(JsonFieldType.NUMBER).description("댓글 ID"),
                                fieldWithPath("result.content").type(JsonFieldType.STRING).description("댓글 내용"),
                                fieldWithPath("result.feedId").type(JsonFieldType.NUMBER).description("피드 ID"),
                                fieldWithPath("result.userId").description("회원 ID"),
                                fieldWithPath("result.password").description("비밀번호"),
                                fieldWithPath("responseTime").type(JsonFieldType.STRING).description("응답 시간")
                        )
                ));
    }

    @DisplayName("댓글 삭제")
    @Test
    void deleteComment() throws Exception {
        CommentDTO.Request request = testCommentDTO.makeNonUserCommentDTO();

        Comment createdComment = commentService.createComment(request);

        this.mockMvc.perform(delete("/comment/{commentId}", createdComment.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document.document(
                        pathParameters(
                                parameterWithName("commentId").description("삭제할 댓글 ID")),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 상태 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                fieldWithPath("result").type(JsonFieldType.NULL).description("삭제한 댓글 ID"),
                                fieldWithPath("responseTime").type(JsonFieldType.STRING).description("응답 시간")
                        )
                ));
    }
}
