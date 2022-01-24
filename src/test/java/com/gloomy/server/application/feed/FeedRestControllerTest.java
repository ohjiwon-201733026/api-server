package com.gloomy.server.application.feed;

import com.gloomy.server.application.AbstractControllerTest;
import com.gloomy.server.application.image.ImageService;
import com.gloomy.server.application.image.TestImage;
import com.gloomy.server.domain.feed.Category;
import com.gloomy.server.domain.feed.CategoryValue;
import com.gloomy.server.domain.feed.Feed;
import com.gloomy.server.domain.jwt.JWTSerializer;
import com.gloomy.server.domain.user.User;
import com.gloomy.server.domain.user.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.util.MultiValueMap;

import java.util.List;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class FeedRestControllerTest extends AbstractControllerTest {

    @Autowired
    private FeedService feedService;
    @Autowired
    private ImageService imageService;
    @Autowired
    private UserService userService;
    @Autowired
    private JWTSerializer jwtSerializer;
    private User testUser;
    TestFeedDTO testFeedDTO;

    @BeforeEach
    void beforeEach() {
        User tmpUser = new TestUserDTO().makeTestUser();
        testUser = userService.createUser(tmpUser);
        testFeedDTO = new TestFeedDTO(testUser, 1);
        testFeedDTO.setToken(jwtSerializer.jwtFromUser(testUser));
    }

    @AfterEach
    void afterEach() {
        imageService.deleteAll();
        feedService.deleteAll();
        userService.deleteAll();
    }

    @DisplayName("피드_생성_비회원")
    @Test
    void createNonuserFeed() throws Exception {
        MockMultipartFile firstImageFile = TestImage.convert(testFeedDTO.getImages(), 0);
        MultiValueMap<String, String> params = testFeedDTO.convert(false);

        this.mockMvc.perform(fileUpload("/feed")
//                      .file(firstImageFile)
                        .params(params))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document.document(
                        requestParameters(
                                parameterWithName("password").description("비밀번호"),
                                parameterWithName("category").description("카테고리 (ALL)"),
                                parameterWithName("title").description("게시글 제목"),
                                parameterWithName("content").description("게시글 내용")),
                        requestParts(
                                partWithName("images").description("이미지 파일 리스트 (선택사항)").optional()),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("상태 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                fieldWithPath("result").description("응답 데이터"),
                                fieldWithPath("result.id").type(JsonFieldType.NUMBER).description("피드 ID"),
                                fieldWithPath("result.ip").type(JsonFieldType.STRING).description("작성자 IP"),
                                fieldWithPath("result.userId").type(JsonFieldType.NULL).description("(회원일 경우) 회원 ID"),
                                fieldWithPath("result.password").type(JsonFieldType.STRING).description("(비회원일 경우) 비밀번호"),
                                fieldWithPath("result.category").type(JsonFieldType.STRING).description("피드 카테고리 (ALL)"),
                                fieldWithPath("result.title").type(JsonFieldType.STRING).description("피드 제목"),
                                fieldWithPath("result.content").type(JsonFieldType.STRING).description("피드 내용"),
                                fieldWithPath("result.likeCount").type(JsonFieldType.NUMBER).description("피드 좋아요 수"),
                                fieldWithPath("result.imageURLs").type(JsonFieldType.ARRAY).description("피드 이미지 리스트"),
                                fieldWithPath("result.commentCount").type(JsonFieldType.NUMBER).description("피드의 댓글 수"),
                                fieldWithPath("result.status").type(JsonFieldType.STRING).description("피드 상태 (ACTIVE, INACTIVE)"),
                                fieldWithPath("result.createdAt").type(JsonFieldType.STRING).description("피드 생성시간"),
                                fieldWithPath("result.updatedAt").type(JsonFieldType.STRING).description("피드 수정시간"),
                                fieldWithPath("result.deletedAt").type(JsonFieldType.STRING).description("피드 삭제시간"),
                                fieldWithPath("responseTime").type(JsonFieldType.STRING).description("응답 시간")
                        )
                ));
    }

    @DisplayName("피드_생성_회원")
    @Test
    void createUserFeed() throws Exception {
        MockMultipartFile firstImageFile = TestImage.convert(testFeedDTO.getImages(), 0);
        MultiValueMap<String, String> params = testFeedDTO.convert(true);

        this.mockMvc.perform(fileUpload("/feed")
                        .header("Authorization", "Bearer " + testFeedDTO.getToken())
//                      .file(firstImageFile)
                        .params(params))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("사용자 토큰")),
                        requestParameters(
                                parameterWithName("category").description("카테고리 (ALL)"),
                                parameterWithName("title").description("게시글 제목"),
                                parameterWithName("content").description("게시글 내용")),
                        requestParts(
                                partWithName("images").description("이미지 파일 리스트 (선택사항)").optional()),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 상태 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                fieldWithPath("result").description("응답 데이터"),
                                fieldWithPath("result.id").type(JsonFieldType.NUMBER).description("피드 ID"),
                                fieldWithPath("result.ip").type(JsonFieldType.STRING).description("작성자 IP"),
                                fieldWithPath("result.userId").type(JsonFieldType.NUMBER).description("(회원일 경우) 회원 ID"),
                                fieldWithPath("result.password").type(JsonFieldType.NULL).description("(비회원일 경우) 비밀번호"),
                                fieldWithPath("result.category").type(JsonFieldType.STRING).description("피드 카테고리 (ALL)"),
                                fieldWithPath("result.title").type(JsonFieldType.STRING).description("피드 제목"),
                                fieldWithPath("result.content").type(JsonFieldType.STRING).description("피드 내용"),
                                fieldWithPath("result.likeCount").type(JsonFieldType.NUMBER).description("피드 좋아요 수"),
                                fieldWithPath("result.imageURLs").type(JsonFieldType.ARRAY).description("피드 이미지 리스트"),
                                fieldWithPath("result.commentCount").type(JsonFieldType.NUMBER).description("피드의 댓글 수"),
                                fieldWithPath("result.status").type(JsonFieldType.STRING).description("피드 상태 (ACTIVE, INACTIVE)"),
                                fieldWithPath("result.createdAt").type(JsonFieldType.STRING).description("피드 생성시간"),
                                fieldWithPath("result.updatedAt").type(JsonFieldType.STRING).description("피드 수정시간"),
                                fieldWithPath("result.deletedAt").type(JsonFieldType.STRING).description("피드 삭제시간"),
                                fieldWithPath("responseTime").type(JsonFieldType.STRING).description("응답 시간")
                        )
                ));
    }

    @DisplayName("전체_피드_조회")
    @Test
    void getAllFeeds() throws Exception {
        feedService.createFeed(null, testFeedDTO.makeNonUserFeedDTO());
        feedService.createFeed(null, testFeedDTO.makeNonUserFeedDTO());

        this.mockMvc.perform(get("/feed")
                        .param("page", "0")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document.document(
                        requestParameters(
                                parameterWithName("page").description("페이지 넘버")
                        ),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 상태 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                fieldWithPath("result").description("응답 데이터"),
                                fieldWithPath("result.content[]").type(JsonFieldType.ARRAY).description("응답 데이터 페이지"),
                                fieldWithPath("result.content[].id").type(JsonFieldType.NUMBER).description("피드 ID"),
                                fieldWithPath("result.content[].ip").type(JsonFieldType.STRING).description("작성자 IP"),
                                fieldWithPath("result.content[].userId").description("(회원일 경우) 회원 ID"),
                                fieldWithPath("result.content[].password").description("(비회원일 경우) 비밀번호"),
                                fieldWithPath("result.content[].category").type(JsonFieldType.STRING).description("피드 카테고리 (ALL)"),
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

    @DisplayName("사용자_피드_조회")
    @Test
    void getUserFeeds() throws Exception {
        feedService.createFeed(testFeedDTO.getUserId(), testFeedDTO.makeUserFeedDTO());
        feedService.createFeed(testFeedDTO.getUserId(), testFeedDTO.makeUserFeedDTO());

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
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 상태 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                fieldWithPath("result").description("응답 데이터"),
                                fieldWithPath("result.content[]").type(JsonFieldType.ARRAY).description("응답 데이터 페이지"),
                                fieldWithPath("result.content[].id").type(JsonFieldType.NUMBER).description("피드 ID"),
                                fieldWithPath("result.content[].ip").type(JsonFieldType.STRING).description("작성자 IP"),
                                fieldWithPath("result.content[].userId").type(JsonFieldType.NUMBER).description("(회원일 경우) 회원 ID"),
                                fieldWithPath("result.content[].password").type(JsonFieldType.NULL).description("(비회원일 경우) 비밀번호"),
                                fieldWithPath("result.content[].category").type(JsonFieldType.STRING).description("피드 카테고리 (ALL)"),
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

    @DisplayName("피드_조회")
    @Test
    void getOneFeed() throws Exception {
        Feed createdNonUserFeed = feedService.createFeed(null, testFeedDTO.makeNonUserFeedDTO());

        this.mockMvc.perform(get("/feed/{feedId}", createdNonUserFeed.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document.document(
                        pathParameters(
                                parameterWithName("feedId").description("조회할 피드 ID")
                        ),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 상태 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                fieldWithPath("result").description("응답 데이터"),
                                fieldWithPath("result.id").type(JsonFieldType.NUMBER).description("피드 ID"),
                                fieldWithPath("result.ip").type(JsonFieldType.STRING).description("작성자 IP"),
                                fieldWithPath("result.userId").description("(회원일 경우) 회원 ID"),
                                fieldWithPath("result.password").description("(비회원일 경우) 비밀번호"),
                                fieldWithPath("result.category").type(JsonFieldType.STRING).description("피드 카테고리 (ALL)"),
                                fieldWithPath("result.title").type(JsonFieldType.STRING).description("피드 제목"),
                                fieldWithPath("result.content").type(JsonFieldType.STRING).description("피드 내용"),
                                fieldWithPath("result.likeCount").type(JsonFieldType.NUMBER).description("피드 좋아요 수"),
                                fieldWithPath("result.imageURLs").type(JsonFieldType.ARRAY).description("피드 이미지 리스트"),
                                fieldWithPath("result.commentCount").type(JsonFieldType.NUMBER).description("피드의 댓글 수"),
                                fieldWithPath("result.status").type(JsonFieldType.STRING).description("피드 상태 (ACTIVE, INACTIVE)"),
                                fieldWithPath("result.createdAt").type(JsonFieldType.STRING).description("피드 생성시간"),
                                fieldWithPath("result.updatedAt").type(JsonFieldType.STRING).description("피드 수정시간"),
                                fieldWithPath("result.deletedAt").type(JsonFieldType.STRING).description("피드 삭제시간"),
                                fieldWithPath("responseTime").type(JsonFieldType.STRING).description("응답 시간")
                        )
                ));
    }

    @DisplayName("피드_수정")
    @Test
    void updateFeed() throws Exception {
        String updatePassword = "34567";
        String updateContent = "새 글";
        MockMultipartFile firstUpdateImageFile = TestImage.convert(testFeedDTO.getImages(), 0);

        Feed createdNonUserFeed = feedService.createFeed(null, testFeedDTO.makeNonUserFeedDTO());
        UpdateFeedDTO.Request request = new UpdateFeedDTO.Request();
        request.setPassword(updatePassword);
        request.setContent(updateContent);
        MultiValueMap<String, String> params = TestFeedDTO.convert(request);

        this.mockMvc.perform(fileUpload("/feed/{feedId}", createdNonUserFeed.getId())
//                .file(firstUpdateImageFile)
                        .params(params))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document.document(
                        pathParameters(
                                parameterWithName("feedId").description("수정할 피드 ID")),
                        requestParameters(
                                parameterWithName("password").description("수정할 비회원 비밀번호 (선택사항)").optional(),
                                parameterWithName("category").description("수정할 카테고리 (선택사항)").optional(),
                                parameterWithName("title").description("수정할 게시글 제목 (선택사항)").optional(),
                                parameterWithName("content").description("수정할 게시글 내용 (선택사항)").optional()),
                        requestParts(
                                partWithName("images").description("수정할 이미지 파일 리스트 (선택사항)").optional()),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 상태 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                fieldWithPath("result").description("응답 데이터"),
                                fieldWithPath("result.id").type(JsonFieldType.NUMBER).description("피드 ID"),
                                fieldWithPath("result.ip").type(JsonFieldType.STRING).description("작성자 IP"),
                                fieldWithPath("result.userId").description("(회원일 경우) 회원 ID"),
                                fieldWithPath("result.password").description("(비회원일 경우) 비밀번호"),
                                fieldWithPath("result.category").type(JsonFieldType.STRING).description("피드 카테고리 (ALL)"),
                                fieldWithPath("result.title").type(JsonFieldType.STRING).description("피드 제목"),
                                fieldWithPath("result.content").type(JsonFieldType.STRING).description("피드 내용"),
                                fieldWithPath("result.likeCount").type(JsonFieldType.NUMBER).description("피드 좋아요 수"),
                                fieldWithPath("result.imageURLs").type(JsonFieldType.ARRAY).description("피드 이미지 리스트"),
                                fieldWithPath("result.commentCount").type(JsonFieldType.NUMBER).description("피드의 댓글 수"),
                                fieldWithPath("result.status").type(JsonFieldType.STRING).description("피드 상태 (ACTIVE, INACTIVE)"),
                                fieldWithPath("result.createdAt").type(JsonFieldType.STRING).description("피드 생성시간"),
                                fieldWithPath("result.updatedAt").type(JsonFieldType.STRING).description("피드 수정시간"),
                                fieldWithPath("result.deletedAt").type(JsonFieldType.STRING).description("피드 삭제시간"),
                                fieldWithPath("responseTime").type(JsonFieldType.STRING).description("응답 시간")
                        )
                ));
    }

    @DisplayName("피드 삭제")
    @Test
    void deleteFeed() throws Exception {
        Feed createdNonUserFeed = feedService.createFeed(null, testFeedDTO.makeNonUserFeedDTO());

        this.mockMvc.perform(delete("/feed/{feedId}", createdNonUserFeed.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document.document(
                        pathParameters(
                                parameterWithName("feedId").description("삭제할 피드 ID")),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 상태 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                fieldWithPath("result").type(JsonFieldType.NULL).description("삭제한 피드 ID"),
                                fieldWithPath("responseTime").type(JsonFieldType.STRING).description("응답 시간")
                        )
                ));
    }

    @DisplayName("카테고리 리스트 조회")
    @Test
    void getFeedCategories() throws Exception {
        List<CategoryValue> allCategories = Category.getAllCategories();

        this.mockMvc.perform(get("/feed/category")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document.document(
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 상태 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                fieldWithPath("result[]").type(JsonFieldType.ARRAY).description("카테고리 리스트"),
                                fieldWithPath("result[].code").type(JsonFieldType.STRING).description("카테고리 코드"),
                                fieldWithPath("result[].title").type(JsonFieldType.STRING).description("카테고리 제목"),
                                fieldWithPath("responseTime").type(JsonFieldType.STRING).description("응답 시간")
                        )
                ));
    }
}
