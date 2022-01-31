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
import com.gloomy.server.domain.jwt.JWTSerializer;
import com.gloomy.server.domain.reply.Reply;
import com.gloomy.server.domain.user.User;
import com.gloomy.server.domain.user.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(properties = {
        "spring.config.location=classpath:test-application.yml,classpath:aws.yml"
})
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
    private JWTSerializer jwtSerializer;
    @Autowired
    ObjectMapper objectMapper;

    @Value("${cloud.aws.s3.feedTestDir}")
    private String feedTestDir;
    TestReplyDTO testReplyDTO;

    @BeforeEach
    void beforeEach() {
        TestUserDTO testUserDTO = new TestUserDTO();
        User testUser = userService.createUser(testUserDTO.makeTestUser());
        TestFeedDTO testFeedDTO = new TestFeedDTO(testUser, 1);
        Feed testFeed = feedService.createFeed(null, testFeedDTO.makeNonUserFeedDTO());
        TestCommentDTO testCommentDTO = new TestCommentDTO(testFeed.getId(), testUser.getId());
        Comment testComment = commentService.createComment(null, testCommentDTO.makeNonUserCommentDTO());
        testReplyDTO = new TestReplyDTO(testCommentDTO.getUserId(), testComment.getId());
        testReplyDTO.setToken(jwtSerializer.jwtFromUser(testUser));
    }

    @AfterEach
    void afterEach() {
        replyService.deleteAll();
        imageService.deleteAll(feedTestDir);
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
                                fieldWithPath("commentId").description("대댓글의 댓글 ID"),
                                fieldWithPath("password").description("비밀번호")),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 상태 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                fieldWithPath("result").type(JsonFieldType.OBJECT).description("응답 데이터"),
                                fieldWithPath("result.id").type(JsonFieldType.NUMBER).description("대댓글 ID"),
                                fieldWithPath("result.content").type(JsonFieldType.STRING).description("대댓글 내용"),
                                fieldWithPath("result.commentId").type(JsonFieldType.NUMBER).description("대댓글의 댓글 ID"),
                                fieldWithPath("result.userId").type(JsonFieldType.NULL).description("(회원일 경우) 회원 ID"),
                                fieldWithPath("result.password").type(JsonFieldType.STRING).description("(비회원일 경우) 비밀번호"),
                                fieldWithPath("result.status").type(JsonFieldType.STRING).description("대댓글 상태 (ACTIVE, INACTIVE)"),
                                fieldWithPath("result.createdAt").type(JsonFieldType.STRING).description("대댓글 생성시간"),
                                fieldWithPath("result.updatedAt").type(JsonFieldType.STRING).description("대댓글 수정시간"),
                                fieldWithPath("result.deletedAt").type(JsonFieldType.STRING).description("대댓글 삭제시간"),
                                fieldWithPath("responseTime").type(JsonFieldType.STRING).description("응답 시간")
                        )
                ));
    }

    @DisplayName("대댓글_생성_회원")
    @Test
    void createUserReply() throws Exception {
        ReplyDTO.Request request = testReplyDTO.makeUserReplyDTO();

        String body = objectMapper.writeValueAsString(request);

        this.mockMvc.perform(post("/reply")
                        .header("Authorization", "Bearer " + testReplyDTO.getToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("사용자 토큰")),
                        requestFields(
                                fieldWithPath("content").description("대댓글 내용"),
                                fieldWithPath("commentId").description("댓글 ID"),
                                fieldWithPath("password").description("비밀번호 (NULL)")),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 상태 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                fieldWithPath("result").type(JsonFieldType.OBJECT).description("응답 데이터"),
                                fieldWithPath("result.id").type(JsonFieldType.NUMBER).description("대댓글 ID"),
                                fieldWithPath("result.content").type(JsonFieldType.STRING).description("대댓글 내용"),
                                fieldWithPath("result.commentId").type(JsonFieldType.NUMBER).description("대댓글의 댓글 ID"),
                                fieldWithPath("result.userId").type(JsonFieldType.NUMBER).description("(회원일 경우) 회원 ID"),
                                fieldWithPath("result.password").type(JsonFieldType.NULL).description("(비회원일 경우) 비밀번호"),
                                fieldWithPath("result.status").type(JsonFieldType.STRING).description("대댓글 상태 (ACTIVE, INACTIVE)"),
                                fieldWithPath("result.createdAt").type(JsonFieldType.STRING).description("대댓글 생성시간"),
                                fieldWithPath("result.updatedAt").type(JsonFieldType.STRING).description("대댓글 수정시간"),
                                fieldWithPath("result.deletedAt").type(JsonFieldType.STRING).description("대댓글 삭제시간"),
                                fieldWithPath("responseTime").type(JsonFieldType.STRING).description("응답 시간")
                        )
                ));
    }

    @DisplayName("댓글_대댓글_전체_조회")
    @Test
    void getCommentAllReplies() throws Exception {
        ReplyDTO.Request request = testReplyDTO.makeNonUserReplyDTO();

        replyService.createReply(null, request);
        replyService.createReply(null, request);

        this.mockMvc.perform(get("/reply/comment/{commentId}", request.getCommentId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document.document(
                        pathParameters(
                                parameterWithName("commentId").description("조회할 대댓글의 댓글 ID")),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 상태 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                fieldWithPath("result").type(JsonFieldType.OBJECT).description("응답 데이터"),
                                fieldWithPath("result.content[]").type(JsonFieldType.ARRAY).description("응답 데이터 페이지"),
                                fieldWithPath("result.content[].id").type(JsonFieldType.NUMBER).description("대댓글 ID"),
                                fieldWithPath("result.content[].content").type(JsonFieldType.STRING).description("대댓글 내용"),
                                fieldWithPath("result.content[].commentId").type(JsonFieldType.NUMBER).description("대댓글의 댓글 ID"),
                                fieldWithPath("result.content[].userId").description("(회원일 경우) 회원 ID"),
                                fieldWithPath("result.content[].password").description("(비회원일 경우) 비밀번호"),
                                fieldWithPath("result.content[].status").type(JsonFieldType.STRING).description("대댓글 상태 (ACTIVE, INACTIVE)"),
                                fieldWithPath("result.content[].createdAt").type(JsonFieldType.STRING).description("대댓글 생성시간"),
                                fieldWithPath("result.content[].updatedAt").type(JsonFieldType.STRING).description("대댓글 수정시간"),
                                fieldWithPath("result.content[].deletedAt").type(JsonFieldType.STRING).description("대댓글 삭제시간"),

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

    @DisplayName("댓글_상세_조회")
    @Test
    void getReply() throws Exception {
        ReplyDTO.Request request = testReplyDTO.makeNonUserReplyDTO();

        Reply createdReply = replyService.createReply(null, request);

        this.mockMvc.perform(get("/reply/{replyId}", createdReply.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document.document(
                        pathParameters(
                                parameterWithName("replyId").description("조회할 대댓글 ID")),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 상태 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                fieldWithPath("result").type(JsonFieldType.OBJECT).description("응답 데이터"),
                                fieldWithPath("result.id").type(JsonFieldType.NUMBER).description("대댓글 ID"),
                                fieldWithPath("result.content").type(JsonFieldType.STRING).description("대댓글 내용"),
                                fieldWithPath("result.commentId").type(JsonFieldType.NUMBER).description("대댓글의 댓글 ID"),
                                fieldWithPath("result.userId").description("(회원일 경우) 회원 ID"),
                                fieldWithPath("result.password").description("(비회원일 경우) 비밀번호"),
                                fieldWithPath("result.status").type(JsonFieldType.STRING).description("대댓글 상태 (ACTIVE, INACTIVE)"),
                                fieldWithPath("result.createdAt").type(JsonFieldType.STRING).description("대댓글 생성시간"),
                                fieldWithPath("result.updatedAt").type(JsonFieldType.STRING).description("대댓글 수정시간"),
                                fieldWithPath("result.deletedAt").type(JsonFieldType.STRING).description("대댓글 삭제시간"),
                                fieldWithPath("responseTime").type(JsonFieldType.STRING).description("응답 시간")
                        )
                ));
    }

    @DisplayName("대댓글_수정")
    @Test
    void updateReply() throws Exception {
        String updateContent = "새 글";
        ReplyDTO.Request request = testReplyDTO.makeNonUserReplyDTO();
        UpdateReplyDTO.Request updateRequest = new UpdateReplyDTO.Request();
        updateRequest.setContent(updateContent);

        Reply createdReply = replyService.createReply(null, request);
        String body = objectMapper.writeValueAsString(updateRequest);

        this.mockMvc.perform(patch("/reply/{replyId}", createdReply.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document.document(
                        pathParameters(
                                parameterWithName("replyId").description("수정할 대댓글 ID")),
                        requestFields(
                                fieldWithPath("content").description("수정할 대댓글 내용 (선택사항)")),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 상태 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                fieldWithPath("result").type(JsonFieldType.OBJECT).description("응답 데이터"),
                                fieldWithPath("result.id").type(JsonFieldType.NUMBER).description("대댓글 ID"),
                                fieldWithPath("result.content").type(JsonFieldType.STRING).description("대댓글 내용"),
                                fieldWithPath("result.commentId").type(JsonFieldType.NUMBER).description("대댓글의 댓글 ID"),
                                fieldWithPath("result.userId").description("(회원일 경우) 회원 ID"),
                                fieldWithPath("result.password").description("(비회원일 경우) 비밀번호"),
                                fieldWithPath("result.status").type(JsonFieldType.STRING).description("대댓글 상태 (ACTIVE, INACTIVE)"),
                                fieldWithPath("result.createdAt").type(JsonFieldType.STRING).description("대댓글 생성시간"),
                                fieldWithPath("result.updatedAt").type(JsonFieldType.STRING).description("대댓글 수정시간"),
                                fieldWithPath("result.deletedAt").type(JsonFieldType.STRING).description("대댓글 삭제시간"),
                                fieldWithPath("responseTime").type(JsonFieldType.STRING).description("응답 시간")
                        )
                ));
    }

    @DisplayName("대댓글 삭제")
    @Test
    void deleteReply() throws Exception {
        ReplyDTO.Request request = testReplyDTO.makeNonUserReplyDTO();

        Reply createdReply = replyService.createReply(null, request);

        this.mockMvc.perform(delete("/reply/{replyId}", createdReply.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document.document(
                        pathParameters(
                                parameterWithName("replyId").description("삭제할 대댓글 ID")),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 상태 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                fieldWithPath("result").type(JsonFieldType.NULL).description("삭제한 대댓글 ID"),
                                fieldWithPath("responseTime").type(JsonFieldType.STRING).description("응답 시간")
                        )
                ));
    }
}
