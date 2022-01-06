package com.gloomy.server.application.reply;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gloomy.server.application.AbstractControllerTest;
import com.gloomy.server.application.comment.CommentService;
import com.gloomy.server.application.comment.TestCommentDTO;
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

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ReplyRestControllerTest extends AbstractControllerTest {
    @Autowired
    private UserService userService;
    @Autowired
    private FeedService feedService;
    @Autowired
    private ImageService imageService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private ReplyService replyService;
    @Autowired
    ObjectMapper objectMapper;
    TestReplyDTO testReplyDTO;

    @BeforeEach
    void beforeEach() {
        TestUserDTO testUserDTO = new TestUserDTO();
        User testUser = userService.createUser(testUserDTO.makeTestUser());
        TestFeedDTO testFeedDTO = new TestFeedDTO(testUser, 1);
        Feed testFeed = feedService.createFeed(testFeedDTO.makeNonUserFeedDTO());
        TestCommentDTO testCommentDTO = new TestCommentDTO(testFeed.getId(), testUser.getId());
        Comment testComment = commentService.createComment(testCommentDTO.makeNonUserCommentDTO());
        testReplyDTO = new TestReplyDTO(testComment);
    }

    @AfterEach
    void afterEach() {
        replyService.deleteAll();
        imageService.deleteAll();
        commentService.deleteAll();
        feedService.deleteAll();
        userService.deleteAll();
    }

    @DisplayName("대댓글_생성_비회원")
    @Test
    void createNonUserReply() throws Exception {
        ReplyDTO.Request request = testReplyDTO.makeNonUserReplyDTO();

        String body = objectMapper.writeValueAsString(request);

        this.mockMvc.perform(post("/reply")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(body))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document.document(
                        requestFields(
                                fieldWithPath("content").description("대댓글 내용"),
                                fieldWithPath("commentId").description("댓글 ID"),
                                fieldWithPath("userId").description("회원 ID").optional(),
                                fieldWithPath("password").description("비밀번호").optional()),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("대댓글 ID"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("대댓글 내용"),
                                fieldWithPath("commentId").type(JsonFieldType.NUMBER).description("댓글 ID"),
                                fieldWithPath("userId").type(JsonFieldType.NUMBER).description("회원 ID").optional(),
                                fieldWithPath("password").type(JsonFieldType.STRING).description("비밀번호").optional()
                        )
                ));
    }

    @DisplayName("대댓글_생성_회원")
    @Test
    void createUserReply() throws Exception {
        User replyUser = userService.createUser(new TestUserDTO().makeTestUser());
        ReplyDTO.Request request = testReplyDTO.makeUserReplyDTO(replyUser);

        String body = objectMapper.writeValueAsString(request);

        this.mockMvc.perform(post("/reply")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(body))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document.document(
                        requestFields(
                                fieldWithPath("content").description("대댓글 내용"),
                                fieldWithPath("commentId").description("댓글 ID"),
                                fieldWithPath("userId").description("회원 ID").optional(),
                                fieldWithPath("password").description("비밀번호").optional()),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("대댓글 ID"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("대댓글 내용"),
                                fieldWithPath("commentId").type(JsonFieldType.NUMBER).description("댓글 ID"),
                                fieldWithPath("userId").type(JsonFieldType.NUMBER).description("회원 ID").optional(),
                                fieldWithPath("password").type(JsonFieldType.STRING).description("비밀번호").optional()
                        )
                ));
    }

    @DisplayName("댓글_대댓글_전체_조회")
    @Test
    void getCommentAllReplies() throws Exception {
        ReplyDTO.Request request = testReplyDTO.makeNonUserReplyDTO();

        replyService.createReply(request);
        replyService.createReply(request);

        this.mockMvc.perform(get("/reply/comment/{commentId}", request.getCommentId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document.document(
                        pathParameters(
                                parameterWithName("commentId").description("조회할 대댓글의 댓글 ID")),
                        responseFields(
                                fieldWithPath("content[].id").type(JsonFieldType.NUMBER).description("대댓글 ID"),
                                fieldWithPath("content[].content").type(JsonFieldType.STRING).description("대댓글 내용"),
                                fieldWithPath("content[].commentId").type(JsonFieldType.NUMBER).description("댓글 ID"),
                                fieldWithPath("content[].userId").type(JsonFieldType.NUMBER).description("회원 ID").optional(),
                                fieldWithPath("content[].password").type(JsonFieldType.STRING).description("비밀번호").optional(),

                                fieldWithPath("pageable").type(JsonFieldType.STRING).description("pageable 정보"),
                                fieldWithPath("totalPages").type(JsonFieldType.NUMBER).description("전체 페이지 수"),
                                fieldWithPath("totalElements").type(JsonFieldType.NUMBER).description("전체 페이지 내 요소의 수"),
                                fieldWithPath("last").type(JsonFieldType.BOOLEAN).description("마지막 페이지 여부"),
                                fieldWithPath("numberOfElements").type(JsonFieldType.NUMBER).description("현재 페이지 내 요소의 수"),
                                fieldWithPath("first").type(JsonFieldType.BOOLEAN).description("첫 페이지 여부"),
                                fieldWithPath("size").type(JsonFieldType.NUMBER).description("페이지 당 출력 갯수"),
                                fieldWithPath("sort.sorted").type(JsonFieldType.BOOLEAN).description("정렬 여부"),
                                fieldWithPath("sort.unsorted").type(JsonFieldType.BOOLEAN).description("비정렬 여부"),
                                fieldWithPath("sort.empty").type(JsonFieldType.BOOLEAN).description("정렬 비어있는지 여부"),
                                fieldWithPath("number").type(JsonFieldType.NUMBER).description("현재 페이지 인덱스"),
                                fieldWithPath("empty").type(JsonFieldType.BOOLEAN).description("비어있는지 여부")
                        )
                ));
    }
}
