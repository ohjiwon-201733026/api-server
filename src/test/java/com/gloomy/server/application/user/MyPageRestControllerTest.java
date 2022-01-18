package com.gloomy.server.application.user;

import com.gloomy.server.application.AbstractControllerTest;
import com.gloomy.server.application.comment.CommentDTO;
import com.gloomy.server.application.comment.CommentService;
import com.gloomy.server.application.feed.FeedDTO;
import com.gloomy.server.application.feed.FeedService;
import com.gloomy.server.application.feed.TestFeedDTO;
import com.gloomy.server.application.image.TestImage;
import com.gloomy.server.domain.feed.Feed;
import com.gloomy.server.domain.user.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.net.Authenticator;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
public class MyPageRestControllerTest extends AbstractControllerTest {

    @Autowired
    UserService userService;
    @Autowired
    FeedService feedService;
    @Autowired
    CommentService commentService;
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

    @DisplayName("find user feed")
    @Test
    public void userFeed() throws Exception {
        feedService.createFeed(testFeedDTO1.makeUserFeedDTO());
        feedService.createFeed(testFeedDTO2.makeUserFeedDTO());

        this.mockMvc.perform(get("/feed/user/{userId}", testUser.getId())
                .param("page", "0")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document.document(
                        requestParameters(
                                parameterWithName("page").description("페이지 넘버")
                        ),
                        pathParameters(
                                parameterWithName("userId").description("조회할 사용자 ID")
                        ),
                        responseFields(
                                fieldWithPath("content[].id").type(JsonFieldType.NUMBER).description("피드 ID"),
                                fieldWithPath("content[].isUser").type(JsonFieldType.BOOLEAN).description("회원 여부"),
                                fieldWithPath("content[].ip").type(JsonFieldType.STRING).description("작성자 IP"),
                                fieldWithPath("content[].userId").type(JsonFieldType.NUMBER).description("회원 ID").optional(),
                                fieldWithPath("content[].password").type(JsonFieldType.STRING).description("비밀번호").optional(),
                                fieldWithPath("content[].content").type(JsonFieldType.STRING).description("게시글 내용"),
                                fieldWithPath("content[].likeCount").type(JsonFieldType.NUMBER).description("좋아요 수"),
                                fieldWithPath("content[].imageURLs").type(JsonFieldType.ARRAY).description("이미지 리스트").optional(),
                                fieldWithPath("content[].commentCount").type(JsonFieldType.NUMBER).description("댓글 수"),

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

    @DisplayName("find user Comment")
    @Test
    @WithMockUser
    public void userComment() throws Exception {
        User saveUser=userService.createUser(user);

        FeedDTO.Request feedDto1=new FeedDTO.Request(true, "111.111.111.111", saveUser.getId(), "test content 1", new TestImage().makeImages(1));

        Feed feed1=feedService.createFeed(feedDto1);

        CommentDTO.Request comment1 = new CommentDTO.Request("test comment 1",feed1.getId(),saveUser.getId());
        CommentDTO.Request comment2 = new CommentDTO.Request("test comment 2",feed1.getId(),saveUser.getId());

        commentService.createComment(comment1);
        commentService.createComment(comment2);

        this.mockMvc.perform(get("/myPage/comment/{userId}", saveUser.getId())
                .with(authentication(authentication))
                .param("page", "0")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document.document(
                        requestParameters(
                                parameterWithName("page").description("페이지 넘버")
                        ),
                        pathParameters(
                                parameterWithName("userId").description("댓글 조회할 사용자 ID")
                        ),
                        responseFields(
                                fieldWithPath("content[].id").type(JsonFieldType.NUMBER).description("댓글 ID"),
                                fieldWithPath("content[].content").type(JsonFieldType.STRING).description("댓글 내용"),
                                fieldWithPath("content[].feedId").type(JsonFieldType.NUMBER).description("해당 피드 ID"),
                                fieldWithPath("content[].userId").type(JsonFieldType.NUMBER).description("회원 ID"),
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
