package com.gloomy.server.application.user;

import com.gloomy.server.application.AbstractControllerTest;
import com.gloomy.server.application.comment.CommentDTO;
import com.gloomy.server.application.comment.CommentService;
import com.gloomy.server.application.feed.FeedDTO;
import com.gloomy.server.application.feed.FeedService;
import com.gloomy.server.application.feed.TestFeedDTO;
import com.gloomy.server.application.image.TestImage;
import com.gloomy.server.domain.feed.Feed;
import com.gloomy.server.domain.jwt.JWTSerializer;
import com.gloomy.server.domain.user.User;
import com.gloomy.server.domain.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(properties = {
        "spring.config.location=classpath:test-application.yml,classpath:aws.yml"
})
@Transactional
public class MyPageRestControllerTest extends AbstractControllerTest {

    @Autowired
    UserService userService;
    @Autowired
    FeedService feedService;
    @Autowired
    CommentService commentService;
    @Autowired
    JWTSerializer jwtSerializer;
    User user;
    private User testUser;
    TestFeedDTO testFeedDTO1;
    TestFeedDTO testFeedDTO2;
    Authentication authentication;

    @BeforeEach
    public void setUp() {
        authentication= SecurityContextHolder.getContext().getAuthentication();
        this.user = TestUserDTO.TestUser.makeTestUser();
        testUser = userService.createUser(user);
        testFeedDTO1 = new TestFeedDTO(testUser, 1);
        testFeedDTO2 = new TestFeedDTO(testUser, 2);
    }

    @DisplayName("사용자_피드_조회")
    @Test
    void getUserFeeds() throws Exception {
        User saveUser=userService.createUser(user);
        feedService.createFeed(testFeedDTO1.getUserId(), testFeedDTO1.makeUserFeedDTO());
        feedService.createFeed(testFeedDTO2.getUserId(), testFeedDTO2.makeUserFeedDTO());

        String token=jwtSerializer.jwtFromUser(saveUser);
        mockMvc.perform(get("/feed/user")
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
                                fieldWithPath("result.content[].id").type(JsonFieldType.NUMBER).description("피드 ID"),
                                fieldWithPath("result.content[].ip").type(JsonFieldType.STRING).description("작성자 IP"),
                                fieldWithPath("result.content[].userId").type(JsonFieldType.NUMBER).description("(회원일 경우) 회원 ID"),
                                fieldWithPath("result.content[].nickname").type(JsonFieldType.NULL).description("(비회원일 경우) 닉네임"),
                                fieldWithPath("result.content[].password").type(JsonFieldType.NULL).description("(비회원일 경우) 비밀번호"),
                                fieldWithPath("result.content[].category").type(JsonFieldType.STRING).description("피드 카테고리"),
                                fieldWithPath("result.content[].title").type(JsonFieldType.STRING).description("피드 제목"),
                                fieldWithPath("result.content[].content").type(JsonFieldType.STRING).description("피드 내용"),
                                fieldWithPath("result.content[].likeCount").type(JsonFieldType.NUMBER).description("피드 좋아요 수"),
                                fieldWithPath("result.content[].imageURLs").type(JsonFieldType.ARRAY).description("피드 이미지 리스트"),
                                fieldWithPath("result.content[].commentCount").type(JsonFieldType.NUMBER).description("피드의 댓글 수"),
                                fieldWithPath("result.content[].status").type(JsonFieldType.STRING).description("피드 상태 (ACTIVE, INACTIVE)"),
                                fieldWithPath("result.content[].createdAt").type(JsonFieldType.STRING).description("피드 생성시간"),
                                fieldWithPath("result.content[].updatedAt").type(JsonFieldType.STRING).description("피드 수정시간"),
                                fieldWithPath("result.content[].deletedAt").type(JsonFieldType.STRING).description("피드 삭제시간"),

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

    @DisplayName("find user Comment")
    @Test
    @WithMockUser
    public void userComment() throws Exception {
        User saveUser=userService.createUser(user);

        FeedDTO.Request feedDto1=new FeedDTO.Request("ALL", "test Title 1", "test content 1");

        Feed feed1=feedService.createFeed(saveUser.getId(), feedDto1);

        CommentDTO.Request comment1 = new CommentDTO.Request("test comment 1",feed1.getId());
        CommentDTO.Request comment2 = new CommentDTO.Request("test comment 2",feed1.getId());

        commentService.createComment(saveUser.getId(), comment1);
        commentService.createComment(saveUser.getId(), comment2);

        String token=jwtSerializer.jwtFromUser(saveUser);
        System.out.println(token);


        this.mockMvc.perform(get("/myPage/comment")
                .header("Authorization","Bearer "+token)
                .param("page", "0")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("사용자 토큰 ( 필수 )")),
                        requestParameters(
                                parameterWithName("page").description("페이지 넘버 (필수)")
                        ),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 상태 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                fieldWithPath("result.content[].id").type(JsonFieldType.NUMBER).description("댓글 ID"),
                                fieldWithPath("result.content[].content").type(JsonFieldType.STRING).description("댓글 내용"),
                                fieldWithPath("result.content[].feedId").type(JsonFieldType.NUMBER).description("피드 ID"),
                                fieldWithPath("result.content[].userId").description("회원 ID"),
                                fieldWithPath("result.content[].nickname").description("(비회원일 경우) 닉네임"),
                                fieldWithPath("result.content[].password").description("(비회원일 경우) 비밀번호"),
                                fieldWithPath("result.content[].status").description("댓글 상태"),
                                fieldWithPath("result.content[].createdAt").description("작성 시간"),
                                fieldWithPath("result.content[].updatedAt").description("수정 시간"),
                                fieldWithPath("result.content[].deletedAt").description("삭제 시간"),

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