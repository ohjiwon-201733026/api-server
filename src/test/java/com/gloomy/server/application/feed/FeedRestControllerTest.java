package com.gloomy.server.application.feed;

import com.gloomy.server.application.AbstractControllerTest;
import com.gloomy.server.application.image.ImageService;
import com.gloomy.server.application.image.TestImage;
import com.gloomy.server.domain.feed.Feed;
import com.gloomy.server.domain.user.User;
import com.gloomy.server.domain.user.UserService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.util.MultiValueMap;

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
    private User testUser;
    TestFeedDTO testFeedDTO;

    @BeforeEach
    void beforeEach() {
        User tmpUser = new TestUserDTO().makeTestUser();
        testUser = userService.createUser(tmpUser);
        testFeedDTO = new TestFeedDTO(testUser, 1);
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
        FeedDTO.Request request = testFeedDTO.makeNonUserFeedDTO();

        MockMultipartFile firstImageFile = TestImage.convert(request.getImages(), 0);
        MultiValueMap<String, String> params = TestFeedDTO.convert(request);

        this.mockMvc.perform(fileUpload("/feed")
//                .file(firstImageFile)
                .params(params))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document.document(
                        requestParameters(
                                parameterWithName("isUser").description("회원 여부"),
                                parameterWithName("ip").description("작성자 IP"),
                                parameterWithName("userId").description("회원 ID").optional(),
                                parameterWithName("password").description("비밀번호"),
                                parameterWithName("content").description("게시글 내용")),
                        requestParts(
                                partWithName("images").description("이미지 파일 리스트").optional()),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("피드 ID"),
                                fieldWithPath("isUser").type(JsonFieldType.BOOLEAN).description("회원 여부"),
                                fieldWithPath("ip").type(JsonFieldType.STRING).description("작성자 IP"),
                                fieldWithPath("userId").type(JsonFieldType.NUMBER).description("회원 ID").optional(),
                                fieldWithPath("password").type(JsonFieldType.STRING).description("비밀번호"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("게시글 내용")
                        )
                ));
    }

    @DisplayName("피드_생성_회원")
    @Test
    void createUserFeed() throws Exception {
        FeedDTO.Request request = testFeedDTO.makeUserFeedDTO();

        MockMultipartFile firstImageFile = TestImage.convert(request.getImages(), 0);
        MultiValueMap<String, String> params = TestFeedDTO.convert(request);

        this.mockMvc.perform(fileUpload("/feed")
//                .file(firstImageFile)
                .params(params))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document.document(
                        requestParameters(
                                parameterWithName("isUser").description("회원 여부"),
                                parameterWithName("ip").description("작성자 IP"),
                                parameterWithName("userId").description("회원 ID"),
                                parameterWithName("password").description("비밀번호").optional(),
                                parameterWithName("content").description("게시글 내용")),
                        requestParts(
                                partWithName("images").description("이미지 파일 리스트").optional()),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("피드 ID"),
                                fieldWithPath("isUser").type(JsonFieldType.BOOLEAN).description("회원 여부"),
                                fieldWithPath("ip").type(JsonFieldType.STRING).description("작성자 IP"),
                                fieldWithPath("userId").type(JsonFieldType.NUMBER).description("회원 ID"),
                                fieldWithPath("password").type(JsonFieldType.STRING).description("비밀번호").optional(),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("게시글 내용")
                        )
                ));
    }

    @DisplayName("전체_피드_조회")
    @Test
    void getAllFeeds() throws Exception {
        feedService.createFeed(testFeedDTO.makeUserFeedDTO());
        feedService.createFeed(testFeedDTO.makeNonUserFeedDTO());

        this.mockMvc.perform(get("/feed?page=0")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document.document(
                        requestParameters(
                                parameterWithName("page").description("페이지 넘버")
                        ),
                        responseFields(
                                fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("피드 ID"),
                                fieldWithPath("[].isUser").type(JsonFieldType.BOOLEAN).description("회원 여부"),
                                fieldWithPath("[].ip").type(JsonFieldType.STRING).description("작성자 IP"),
                                fieldWithPath("[].userId").type(JsonFieldType.NUMBER).description("회원 ID").optional(),
                                fieldWithPath("[].password").type(JsonFieldType.STRING).description("비밀번호").optional(),
                                fieldWithPath("[].content").type(JsonFieldType.STRING).description("게시글 내용")
                        )
                ));
    }

    @DisplayName("사용자_피드_조회")
    @Test
    void getUserFeeds() throws Exception {
        feedService.createFeed(testFeedDTO.makeUserFeedDTO());
        feedService.createFeed(testFeedDTO.makeUserFeedDTO());

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
                                fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("피드 ID"),
                                fieldWithPath("[].isUser").type(JsonFieldType.BOOLEAN).description("회원 여부"),
                                fieldWithPath("[].ip").type(JsonFieldType.STRING).description("작성자 IP"),
                                fieldWithPath("[].userId").type(JsonFieldType.NUMBER).description("회원 ID").optional(),
                                fieldWithPath("[].password").type(JsonFieldType.STRING).description("비밀번호").optional(),
                                fieldWithPath("[].content").type(JsonFieldType.STRING).description("게시글 내용")
                        )
                ));
    }

    @DisplayName("피드_조회")
    @Test
    void getOneFeed() throws Exception {
        Feed createdNonUserFeed = feedService.createFeed(testFeedDTO.makeNonUserFeedDTO());

        this.mockMvc.perform(get("/feed/{feedId}", createdNonUserFeed.getId())
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document.document(
                        pathParameters(
                                parameterWithName("feedId").description("조회할 피드 ID")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("피드 ID"),
                                fieldWithPath("isUser").type(JsonFieldType.BOOLEAN).description("회원 여부"),
                                fieldWithPath("ip").type(JsonFieldType.STRING).description("작성자 IP"),
                                fieldWithPath("userId").type(JsonFieldType.NUMBER).description("회원 ID").optional(),
                                fieldWithPath("password").type(JsonFieldType.STRING).description("비밀번호").optional(),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("게시글 내용")
                        )
                ));
    }

    @DisplayName("피드_수정")
    @Test
    void updateFeed() throws Exception {
        String updatePassword = "34567";
        String updateContent = "새 글";
        MockMultipartFile firstUpdateImageFile = TestImage.convert(testFeedDTO.getImages(), 0);

        Feed createdNonUserFeed = feedService.createFeed(testFeedDTO.makeNonUserFeedDTO());
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
                                parameterWithName("password").description("수정할 비회원 비밀번호").optional(),
                                parameterWithName("content").description("수정할 게시글 내용").optional()),
                        requestParts(
                                partWithName("images").description("수정할 이미지 파일 리스트").optional()),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("피드 ID"),
                                fieldWithPath("isUser").type(JsonFieldType.BOOLEAN).description("회원 여부"),
                                fieldWithPath("ip").type(JsonFieldType.STRING).description("작성자 IP"),
                                fieldWithPath("userId").type(JsonFieldType.NUMBER).description("회원 ID").optional(),
                                fieldWithPath("password").type(JsonFieldType.STRING).description("비밀번호").optional(),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("게시글 내용")
                        )
                ));
    }

    @DisplayName("피드 삭제")
    @Test
    void deleteFeed() throws Exception {
        Feed createdNonUserFeed = feedService.createFeed(testFeedDTO.makeNonUserFeedDTO());

        this.mockMvc.perform(delete("/feed/{feedId}", createdNonUserFeed.getId())
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document.document(
                        pathParameters(
                                parameterWithName("feedId").description("삭제할 피드 ID")
                        )
                ));
    }
}
