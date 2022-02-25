package com.gloomy.server.application.notice;

import com.gloomy.server.application.AbstractControllerTest;
import com.gloomy.server.application.comment.CommentService;
import com.gloomy.server.application.comment.TestCommentDTO;
import com.gloomy.server.application.feed.FeedService;
import com.gloomy.server.application.feed.TestFeedDTO;
import com.gloomy.server.application.feed.TestUserDTO;
import com.gloomy.server.application.feedlike.FeedLikeDTO;
import com.gloomy.server.application.feedlike.FeedLikeService;
import com.gloomy.server.application.reply.ReplyDTO;
import com.gloomy.server.application.reply.ReplyService;
import com.gloomy.server.domain.comment.Comment;
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
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(properties = {
        "spring.config.location=classpath:test-application.yml,classpath:aws.yml"
})
public class NoticeRestControllerTest extends AbstractControllerTest {
    @Autowired
    private UserService userService;
    @Autowired
    private FeedService feedService;
    @Autowired
    private FeedLikeService feedLikeService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private ReplyService replyService;
    @Autowired
    private NoticeService noticeService;
    @Autowired
    private JWTSerializer jwtSerializer;

    private User testUser;
    private String token;

    @BeforeEach
    void beforeEach() {
        testUser = userService.createUser(TestUserDTO.makeTestUser());
        token = jwtSerializer.jwtFromUser(testUser);
        TestFeedDTO testFeedDTO = new TestFeedDTO(testUser, 0);
        Feed testFeed = feedService.createFeed(testUser.getId(), testFeedDTO.makeUserFeedDTO());
        TestCommentDTO testCommentDTO = new TestCommentDTO(testFeed.getId(), null);
        Comment testComment = commentService.createComment(null, testCommentDTO.makeNonUserCommentDTO());
        replyService.createReply(null, new ReplyDTO.Request("대댓글", testComment.getId(), "12345"));
        feedLikeService.createFeedLike(null, new FeedLikeDTO.Request(testFeed.getId()));
    }

    @AfterEach
    void afterEach() {
        noticeService.deleteAll();
        feedLikeService.deleteAll();
        replyService.deleteAll();
        commentService.deleteAll();
        feedService.deleteAll();
        userService.deleteAll();
    }

    @DisplayName("사용자_알림_조회")
    @Transactional
    @Test
    void getAllNotices() throws Exception {
        this.mockMvc.perform(get("/notice")
                        .header("Authorization", "Bearer " + token)
                        .param("page", "0")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("사용자 토큰")),
                        requestParameters(
                                parameterWithName("page").description("페이지 넘버")),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 상태 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                fieldWithPath("result").description("응답 데이터"),
                                fieldWithPath("result.content[]").type(JsonFieldType.ARRAY).description("응답 데이터 페이지"),
                                fieldWithPath("result.content[].id").type(JsonFieldType.NUMBER).description("알림 ID"),
                                fieldWithPath("result.content[].userId").type(JsonFieldType.NUMBER).description("사용자 ID"),
                                fieldWithPath("result.content[].feedId").type(JsonFieldType.NUMBER).description("피드 ID"),
                                fieldWithPath("result.content[].commentId").optional().type(JsonFieldType.NUMBER).description("(댓글 알림일 경우) 댓글 ID"),
                                fieldWithPath("result.content[].replyId").optional().type(JsonFieldType.NUMBER).description("(대댓글 알림일 경우) 댓글 ID"),
                                fieldWithPath("result.content[].likeId").optional().type(JsonFieldType.NUMBER).description("(좋아요 알림일 경우) 댓글 ID"),
                                fieldWithPath("result.content[].type").type(JsonFieldType.STRING).description("알림 타입 (COMMENT, REPLY, LIKE)"),
                                fieldWithPath("result.content[].isRead").type(JsonFieldType.BOOLEAN).description("알림 읽음 여부"),
                                fieldWithPath("result.content[].status").type(JsonFieldType.STRING).description("알림 상태 (ACTIVE, INACTIVE)"),
                                fieldWithPath("result.content[].commentCount").type(JsonFieldType.NUMBER).description("알림 상태 (ACTIVE, INACTIVE)"),
                                fieldWithPath("result.content[].likeCount").type(JsonFieldType.NUMBER).description("알림 상태 (ACTIVE, INACTIVE)"),
                                fieldWithPath("result.content[].title").type(JsonFieldType.STRING).description("알림 상태 (ACTIVE, INACTIVE)"),
                                fieldWithPath("result.content[].createdAt").type(JsonFieldType.STRING).description("알림 생성시간"),
                                fieldWithPath("result.content[].updatedAt").type(JsonFieldType.STRING).description("알림 수정시간"),
                                fieldWithPath("result.content[].deletedAt").type(JsonFieldType.STRING).description("알림 삭제시간"),

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
}
