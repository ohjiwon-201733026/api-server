package com.gloomy.server.application.feed;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gloomy.server.application.image.ImageService;
import com.gloomy.server.application.image.TestImage;
import com.gloomy.server.domain.feed.Feed;
import com.gloomy.server.domain.user.User;
import com.gloomy.server.domain.user.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.*;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.MultiValueMap;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@AutoConfigureRestDocs
@ExtendWith({ RestDocumentationExtension.class, SpringExtension.class })
@SpringBootTest(properties = {
        "spring.config.location=classpath:application.yml,classpath:aws.yml"
})
class FeedRestControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
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

    @Order(1)
    @Test
    void 피드_생성_비회원() throws Exception {
        FeedDTO.Request request = testFeedDTO.makeNonUserFeedDTO();

        MockMultipartFile firstImageFile = TestImage.convert(request.getImages(), 0);
        MultiValueMap<String, String> params = TestFeedDTO.convert(objectMapper, request);

        this.mockMvc.perform(multipart("/feed")
                .file(firstImageFile)
                .params(params))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("post-create-nonuser-feed",
                        requestParameters(
                                parameterWithName("isUser").description("회원 여부"),
                                parameterWithName("ip").description("작성자 IP"),
                                parameterWithName("userId").description("회원 ID").optional(),
                                parameterWithName("password").description("비밀번호"),
                                parameterWithName("content").description("게시글 내용")),
                        requestParts(
                                partWithName("images").description("이미지 파일").optional()),
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

    @Order(2)
    @Test
    void 피드_생성_회원() throws Exception {
        FeedDTO.Request request = testFeedDTO.makeUserFeedDTO();

        MockMultipartFile firstImageFile = TestImage.convert(request.getImages(), 0);
        MultiValueMap<String, String> params = TestFeedDTO.convert(objectMapper, request);

        this.mockMvc.perform(multipart("/feed")
                .file(firstImageFile)
                .params(params))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("post-create-user-feed",
                        requestParameters(
                                parameterWithName("isUser").description("회원 여부"),
                                parameterWithName("ip").description("작성자 IP"),
                                parameterWithName("userId").description("회원 ID"),
                                parameterWithName("password").description("비밀번호").optional(),
                                parameterWithName("content").description("게시글 내용")),
                        requestParts(
                                partWithName("images").description("이미지 파일").optional()),
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

    @Test
    void 전체_피드_조회() throws Exception {
        this.mockMvc.perform(get("/feed")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("get-all-feeds",
                        responseFields(
                                fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("피드 ID").optional(),
                                fieldWithPath("[].isUser").type(JsonFieldType.BOOLEAN).description("회원 여부").optional(),
                                fieldWithPath("[].ip").type(JsonFieldType.STRING).description("작성자 IP").optional(),
                                fieldWithPath("[].userId").type(JsonFieldType.NUMBER).description("회원 ID").optional(),
                                fieldWithPath("[].password").type(JsonFieldType.STRING).description("비밀번호").optional(),
                                fieldWithPath("[].content").type(JsonFieldType.STRING).description("게시글 내용").optional()
                        )
                ));
    }

    @Test
    void 사용자_피드_조회() throws Exception {
        this.mockMvc.perform(RestDocumentationRequestBuilders.get("/feed/user/{userId}", testUser.getId())
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("get-user-feeds",
                        pathParameters(
                                parameterWithName("userId").description("사용자 ID")
                        ),
                        responseFields(
                                fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("피드 ID").optional(),
                                fieldWithPath("[].isUser").type(JsonFieldType.BOOLEAN).description("회원 여부").optional(),
                                fieldWithPath("[].ip").type(JsonFieldType.STRING).description("작성자 IP").optional(),
                                fieldWithPath("[].userId").type(JsonFieldType.NUMBER).description("회원 ID").optional(),
                                fieldWithPath("[].password").type(JsonFieldType.STRING).description("비밀번호").optional(),
                                fieldWithPath("[].content").type(JsonFieldType.STRING).description("게시글 내용").optional()
                        )
                ));
    }

    @Test
    void 피드_조회() throws Exception {
        FeedDTO.Request request = testFeedDTO.makeNonUserFeedDTO();
        Feed createdNonUserFeed = feedService.createFeed(request);

        this.mockMvc.perform(RestDocumentationRequestBuilders.get("/feed/{feedId}", createdNonUserFeed.getId())
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("get-one-feed",
                        pathParameters(
                                parameterWithName("feedId").description("피드 ID")
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
}
