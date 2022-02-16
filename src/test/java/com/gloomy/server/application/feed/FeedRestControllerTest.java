package com.gloomy.server.application.feed;

import com.gloomy.server.application.AbstractControllerTest;
import com.gloomy.server.application.image.ImageService;
import com.gloomy.server.application.image.TestImage;
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
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(properties = {
        "spring.config.location=classpath:test-application.yml,classpath:aws.yml"
})
class FeedRestControllerTest extends AbstractControllerTest {

    @Autowired
    private FeedService feedService;
    @Autowired
    private ImageService imageService;
    @Autowired
    private UserService userService;
    @Autowired
    private JWTSerializer jwtSerializer;

    @Value("${cloud.aws.s3.feedTestDir}")
    private String feedTestDir;
    private User testUser;
    TestFeedDTO testFeedDTO;

    @BeforeEach
    void beforeEach() {
        testUser = userService.createUser(TestUserDTO.makeTestUser());
        testFeedDTO = new TestFeedDTO(testUser, 1);
        testFeedDTO.setToken(jwtSerializer.jwtFromUser(testUser));
    }

    @AfterEach
    void afterEach() {
        imageService.deleteAll(feedTestDir);
        feedService.deleteAll();
        userService.deleteAll();
    }

    @DisplayName("비회원_피드_생성")
    @Test
    void createNonuserFeed() throws Exception {
        FeedDTO.Request request = testFeedDTO.makeNonUserFeedDTO();

        String body = objectMapper.writeValueAsString(request);

        this.mockMvc.perform(post("/feed")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document.document(
                        requestFields(
                                fieldWithPath("password").type(JsonFieldType.STRING).description("비밀번호"),
                                fieldWithPath("category").type(JsonFieldType.STRING).description("카테고리"),
                                fieldWithPath("title").type(JsonFieldType.STRING).description("게시글 제목"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("게시글 내용")),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("상태 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                fieldWithPath("result").description("응답 데이터"),
                                fieldWithPath("result.id").type(JsonFieldType.NUMBER).description("피드 ID"),
                                fieldWithPath("result.ip").type(JsonFieldType.STRING).description("작성자 IP"),
                                fieldWithPath("result.userId").type(JsonFieldType.NULL).description("(회원일 경우) 회원 ID"),
                                fieldWithPath("result.nickname").type(JsonFieldType.STRING).description("(비회원일 경우) 닉네임"),
                                fieldWithPath("result.password").type(JsonFieldType.STRING).description("(비회원일 경우) 비밀번호"),
                                fieldWithPath("result.category").type(JsonFieldType.STRING).description("피드 카테고리"),
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

    @DisplayName("회원_피드_생성")
    @Test
    void createUserFeed() throws Exception {
        FeedDTO.Request request = testFeedDTO.makeUserFeedDTO();

        String body = objectMapper.writeValueAsString(request);

        this.mockMvc.perform(post("/feed")
                        .header("Authorization", "Bearer " + testFeedDTO.getToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("사용자 토큰")),
                        requestFields(
                                fieldWithPath("password").type(JsonFieldType.NULL).description("비밀번호 (NULL)"),
                                fieldWithPath("category").type(JsonFieldType.STRING).description("카테고리"),
                                fieldWithPath("title").type(JsonFieldType.STRING).description("게시글 제목"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("게시글 내용")),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 상태 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                fieldWithPath("result").description("응답 데이터"),
                                fieldWithPath("result.id").type(JsonFieldType.NUMBER).description("피드 ID"),
                                fieldWithPath("result.ip").type(JsonFieldType.STRING).description("작성자 IP"),
                                fieldWithPath("result.userId").type(JsonFieldType.NUMBER).description("(회원일 경우) 회원 ID"),
                                fieldWithPath("result.nickname").type(JsonFieldType.NULL).description("(비회원일 경우) 닉네임"),
                                fieldWithPath("result.password").type(JsonFieldType.NULL).description("(비회원일 경우) 비밀번호"),
                                fieldWithPath("result.category").type(JsonFieldType.STRING).description("피드 카테고리"),
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

    @DisplayName("피드_이미지_등록_후_비회원_피드_생성")
    @Test
    void createUndefinedNonUserFeed() throws Exception {
        FeedDTO.Request request = testFeedDTO.makeNonUserFeedDTO();

        Feed undefinedNonUserFeed = feedService.uploadImages(null, null, TestImage.makeUpdateImages(1)).getImages().get(0).getFeedId();
        String body = objectMapper.writeValueAsString(request);

        this.mockMvc.perform(put("/feed/{feedId}", undefinedNonUserFeed.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document.document(
                        pathParameters(
                                parameterWithName("feedId").description("등록할 피드 ID")),
                        requestFields(
                                fieldWithPath("password").type(JsonFieldType.STRING).description("비밀번호"),
                                fieldWithPath("category").type(JsonFieldType.STRING).description("카테고리"),
                                fieldWithPath("title").type(JsonFieldType.STRING).description("게시글 제목"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("게시글 내용")),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("상태 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                fieldWithPath("result").description("응답 데이터"),
                                fieldWithPath("result.id").type(JsonFieldType.NUMBER).description("피드 ID"),
                                fieldWithPath("result.ip").type(JsonFieldType.STRING).description("작성자 IP"),
                                fieldWithPath("result.userId").type(JsonFieldType.NULL).description("(회원일 경우) 회원 ID"),
                                fieldWithPath("result.nickname").type(JsonFieldType.STRING).description("(비회원일 경우) 닉네임"),
                                fieldWithPath("result.password").type(JsonFieldType.STRING).description("(비회원일 경우) 비밀번호"),
                                fieldWithPath("result.category").type(JsonFieldType.STRING).description("피드 카테고리"),
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

    @DisplayName("피드_이미지_등록_후_회원_피드_생성")
    @Test
    void createUndefinedUserFeed() throws Exception {
        FeedDTO.Request request = testFeedDTO.makeUserFeedDTO();

        Feed undefinedUserFeed = feedService.uploadImages(null, testFeedDTO.getUserId(), TestImage.makeUpdateImages(1)).getImages().get(0).getFeedId();
        String body = objectMapper.writeValueAsString(request);

        this.mockMvc.perform(put("/feed/{feedId}", undefinedUserFeed.getId())
                        .header("Authorization", "Bearer " + testFeedDTO.getToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("사용자 토큰")),
                        pathParameters(
                                parameterWithName("feedId").description("등록할 피드 ID")),
                        requestFields(
                                fieldWithPath("password").type(JsonFieldType.NULL).description("비밀번호 (NULL)"),
                                fieldWithPath("category").type(JsonFieldType.STRING).description("카테고리"),
                                fieldWithPath("title").type(JsonFieldType.STRING).description("게시글 제목"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("게시글 내용")),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 상태 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                fieldWithPath("result").description("응답 데이터"),
                                fieldWithPath("result.id").type(JsonFieldType.NUMBER).description("피드 ID"),
                                fieldWithPath("result.ip").type(JsonFieldType.STRING).description("작성자 IP"),
                                fieldWithPath("result.userId").type(JsonFieldType.NUMBER).description("(회원일 경우) 회원 ID"),
                                fieldWithPath("result.nickname").type(JsonFieldType.NULL).description("(비회원일 경우) 닉네임"),
                                fieldWithPath("result.password").type(JsonFieldType.NULL).description("(비회원일 경우) 비밀번호"),
                                fieldWithPath("result.category").type(JsonFieldType.STRING).description("피드 카테고리"),
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
        Feed createdFeedFirst = feedService.createFeed(null, testFeedDTO.makeNonUserFeedDTO());
        Feed createdFeedSecond = feedService.createFeed(null, testFeedDTO.makeNonUserFeedDTO());
        imageService.uploadImages(createdFeedFirst, testFeedDTO.getImages());
        imageService.uploadImages(createdFeedSecond, testFeedDTO.getImages());

        this.mockMvc.perform(get("/feed")
                        .param("page", "0")
                        .param("sort", "date")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document.document(
                        requestParameters(
                                parameterWithName("sort").description("피드 정렬기준 (date(기본), like)"),
                                parameterWithName("page").description("페이지 넘버")),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 상태 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                fieldWithPath("result").description("응답 데이터"),
                                fieldWithPath("result.content[]").type(JsonFieldType.ARRAY).description("응답 데이터 페이지"),
                                fieldWithPath("result.content[].id").type(JsonFieldType.NUMBER).description("피드 ID"),
                                fieldWithPath("result.content[].ip").type(JsonFieldType.STRING).description("작성자 IP"),
                                fieldWithPath("result.content[].userId").description("(회원일 경우) 회원 ID"),
                                fieldWithPath("result.content[].nickname").type(JsonFieldType.STRING).description("(비회원일 경우) 닉네임"),
                                fieldWithPath("result.content[].password").description("(비회원일 경우) 비밀번호"),
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

    @DisplayName("사용자_피드_조회")
    @Test
    void getUserFeeds() throws Exception {
        Feed createdFeedFirst = feedService.createFeed(testFeedDTO.getUserId(), testFeedDTO.makeUserFeedDTO());
        Feed createdFeedSecond = feedService.createFeed(testFeedDTO.getUserId(), testFeedDTO.makeUserFeedDTO());
        imageService.uploadImages(createdFeedFirst, testFeedDTO.getImages());
        imageService.uploadImages(createdFeedSecond, testFeedDTO.getImages());

        this.mockMvc.perform(get("/feed/user/{userId}", testUser.getId())
                        .param("page", "0")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document.document(
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

    @DisplayName("피드_조회")
    @Test
    void getOneFeed() throws Exception {
        Feed createdNonUserFeed = feedService.createFeed(null, testFeedDTO.makeNonUserFeedDTO());
        imageService.uploadImages(createdNonUserFeed, testFeedDTO.getImages());

        this.mockMvc.perform(get("/feed/{feedId}", createdNonUserFeed.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document.document(
                        pathParameters(
                                parameterWithName("feedId").description("조회할 피드 ID")),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 상태 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                fieldWithPath("result").description("응답 데이터"),
                                fieldWithPath("result.id").type(JsonFieldType.NUMBER).description("피드 ID"),
                                fieldWithPath("result.ip").type(JsonFieldType.STRING).description("작성자 IP"),
                                fieldWithPath("result.userId").description("(회원일 경우) 회원 ID"),
                                fieldWithPath("result.nickname").description("(비회원일 경우) 닉네임"),
                                fieldWithPath("result.password").description("(비회원일 경우) 비밀번호"),
                                fieldWithPath("result.category").type(JsonFieldType.STRING).description("피드 카테고리"),
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
        Feed createdNonUserFeed = feedService.createFeed(null, testFeedDTO.makeNonUserFeedDTO());
        imageService.uploadImages(createdNonUserFeed, testFeedDTO.getImages());

        UpdateFeedDTO.Request request = new UpdateFeedDTO.Request();
        request.setPassword(updatePassword);
        request.setContent(updateContent);
        String body = objectMapper.writeValueAsString(request);

        this.mockMvc.perform(post("/feed/{feedId}", createdNonUserFeed.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document.document(
                        pathParameters(
                                parameterWithName("feedId").description("수정할 피드 ID")),
                        requestFields(
                                fieldWithPath("password").type(JsonFieldType.STRING).description("수정할 비회원 비밀번호 (선택사항)").optional(),
                                fieldWithPath("category").type(JsonFieldType.STRING).description("수정할 카테고리 (선택사항)").optional(),
                                fieldWithPath("title").type(JsonFieldType.STRING).description("수정할 게시글 제목 (선택사항)").optional(),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("수정할 게시글 내용 (선택사항)").optional()),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 상태 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                fieldWithPath("result").description("응답 데이터"),
                                fieldWithPath("result.id").type(JsonFieldType.NUMBER).description("피드 ID"),
                                fieldWithPath("result.ip").type(JsonFieldType.STRING).description("작성자 IP"),
                                fieldWithPath("result.userId").description("(회원일 경우) 회원 ID"),
                                fieldWithPath("result.nickname").description("(비회원일 경우) 닉네임"),
                                fieldWithPath("result.password").description("(비회원일 경우) 비밀번호"),
                                fieldWithPath("result.category").type(JsonFieldType.STRING).description("피드 카테고리"),
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
                                fieldWithPath("result").type(JsonFieldType.NULL).description("없음"),
                                fieldWithPath("responseTime").type(JsonFieldType.STRING).description("응답 시간")
                        )
                ));
    }
}
