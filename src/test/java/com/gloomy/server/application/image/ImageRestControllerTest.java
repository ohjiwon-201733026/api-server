package com.gloomy.server.application.image;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gloomy.server.application.AbstractControllerTest;
import com.gloomy.server.application.feed.FeedService;
import com.gloomy.server.application.feed.TestFeedDTO;
import com.gloomy.server.application.feed.TestUserDTO;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.MultiValueMap;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(properties = {
        "spring.config.location=classpath:test-application.yml,classpath:aws.yml"
})
public class ImageRestControllerTest extends AbstractControllerTest {
    @Autowired
    private UserService userService;
    @Autowired
    private FeedService feedService;
    @Autowired
    private ImageService imageService;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    private JWTSerializer jwtSerializer;

    @Value("${cloud.aws.s3.feedTestDir}")
    private String feedTestDir;
    private Feed testFeed;
    private TestFeedDTO testFeedDTO;

    @BeforeEach
    void beforeEach() {
        User testUser = userService.createUser(TestUserDTO.makeTestUser());
        testFeedDTO = new TestFeedDTO(testUser, 1);
        testFeed = feedService.createFeed(null, testFeedDTO.makeNonUserFeedDTO());
        testFeedDTO.setToken(jwtSerializer.jwtFromUser(testUser));
    }

    @AfterEach
    void afterEach() {
        imageService.deleteAll(feedTestDir);
        feedService.deleteAll();
        userService.deleteAll();
    }

    @DisplayName("비회원_피드_이미지_등록_피드_없을_경우")
    @Test
    void createNonUserFeedImagesWithoutFeed() throws Exception {
        MockMultipartFile imageFile = TestImage.convert(TestImage.makeImages(1), 0);

        this.mockMvc.perform(fileUpload("/feed/image")
                        .file(imageFile)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document.document(
                        requestParts(
                                partWithName("images").description("이미지 파일 리스트 (선택사항)").optional()),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 상태 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                fieldWithPath("result").type(JsonFieldType.OBJECT).description("응답 데이터"),
                                fieldWithPath("result.feedId").type(JsonFieldType.NUMBER).description("이미지의 피드 ID"),
                                fieldWithPath("result.images[]").type(JsonFieldType.ARRAY).description("이미지 리스트"),
                                fieldWithPath("result.images[].id").type(JsonFieldType.NUMBER).description("이미지 ID"),
                                fieldWithPath("result.images[].imageURL").type(JsonFieldType.STRING).description("이미지 URL"),
                                fieldWithPath("result.images[].status").type(JsonFieldType.STRING).description("이미지 상태 (ACTIVE, INACTIVE)"),
                                fieldWithPath("result.images[].createdAt").type(JsonFieldType.STRING).description("이미지 등록시간"),
                                fieldWithPath("result.images[].updatedAt").type(JsonFieldType.STRING).description("이미지 수정시간"),
                                fieldWithPath("result.images[].deletedAt").type(JsonFieldType.STRING).description("이미지 삭제시간"),
                                fieldWithPath("responseTime").type(JsonFieldType.STRING).description("응답 시간")
                        )
                ));
    }

    @DisplayName("회원_피드_이미지_등록_피드_없을_경우")
    @Test
    void createUserFeedImagesWithoutFeed() throws Exception {
        MockMultipartFile imageFile = TestImage.convert(TestImage.makeImages(1), 0);

        this.mockMvc.perform(fileUpload("/feed/image")
                        .file(imageFile)
                        .header("Authorization", "Bearer " + testFeedDTO.getToken())
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("사용자 토큰")),
                        requestParts(
                                partWithName("images").description("이미지 파일 리스트 (선택사항)").optional()),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 상태 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                fieldWithPath("result").type(JsonFieldType.OBJECT).description("응답 데이터"),
                                fieldWithPath("result.feedId").type(JsonFieldType.NUMBER).description("이미지의 피드 ID"),
                                fieldWithPath("result.images[]").type(JsonFieldType.ARRAY).description("이미지 리스트"),
                                fieldWithPath("result.images[].id").type(JsonFieldType.NUMBER).description("이미지 ID"),
                                fieldWithPath("result.images[].imageURL").type(JsonFieldType.STRING).description("이미지 URL"),
                                fieldWithPath("result.images[].status").type(JsonFieldType.STRING).description("이미지 상태 (ACTIVE, INACTIVE)"),
                                fieldWithPath("result.images[].createdAt").type(JsonFieldType.STRING).description("이미지 등록시간"),
                                fieldWithPath("result.images[].updatedAt").type(JsonFieldType.STRING).description("이미지 수정시간"),
                                fieldWithPath("result.images[].deletedAt").type(JsonFieldType.STRING).description("이미지 삭제시간"),
                                fieldWithPath("responseTime").type(JsonFieldType.STRING).description("응답 시간")
                        )
                ));
    }

    @DisplayName("비회원_피드_이미지_등록_피드_있을_경우")
    @Test
    void createNonUserFeedImagesWithFeed() throws Exception {
        MockMultipartFile imageFile = TestImage.convert(TestImage.makeImages(1), 0);
        MultiValueMap<String, String> params = TestImage.convert(testFeed.getId());

        Feed nonUserFeed = feedService.uploadImages(null, null, TestImage.makeImages(1)).getImages().get(0).getFeedId();

        this.mockMvc.perform(fileUpload("/feed/image/{feedId}", nonUserFeed.getId())
                        .file(imageFile)
                        .params(params)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document.document(
                        requestParameters(
                                parameterWithName("feedId").description("등록할 이미지의 피드 ID")),
                        requestParts(
                                partWithName("images").description("이미지 파일 리스트 (선택사항)").optional()),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 상태 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                fieldWithPath("result").type(JsonFieldType.OBJECT).description("응답 데이터"),
                                fieldWithPath("result.feedId").type(JsonFieldType.NUMBER).description("이미지의 피드 ID"),
                                fieldWithPath("result.images[]").type(JsonFieldType.ARRAY).description("이미지 리스트"),
                                fieldWithPath("result.images[].id").type(JsonFieldType.NUMBER).description("이미지 ID"),
                                fieldWithPath("result.images[].imageURL").type(JsonFieldType.STRING).description("이미지 URL"),
                                fieldWithPath("result.images[].status").type(JsonFieldType.STRING).description("이미지 상태 (ACTIVE, INACTIVE)"),
                                fieldWithPath("result.images[].createdAt").type(JsonFieldType.STRING).description("이미지 등록시간"),
                                fieldWithPath("result.images[].updatedAt").type(JsonFieldType.STRING).description("이미지 수정시간"),
                                fieldWithPath("result.images[].deletedAt").type(JsonFieldType.STRING).description("이미지 삭제시간"),
                                fieldWithPath("responseTime").type(JsonFieldType.STRING).description("응답 시간")
                        )
                ));
    }

    @DisplayName("회원_피드_이미지_등록_피드_있을_경우")
    @Test
    void createUserFeedImagesWithFeed() throws Exception {
        MockMultipartFile imageFile = TestImage.convert(TestImage.makeImages(1), 0);
        MultiValueMap<String, String> params = TestImage.convert(testFeed.getId());

        Feed userFeed = feedService.uploadImages(null, testFeedDTO.getUserId(), TestImage.makeImages(1)).getImages().get(0).getFeedId();

        this.mockMvc.perform(fileUpload("/feed/image/{feedId}", userFeed.getId())
                        .file(imageFile)
                        .header("Authorization", "Bearer " + testFeedDTO.getToken())
                        .params(params)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("사용자 토큰")),
                        requestParameters(
                                parameterWithName("feedId").description("등록할 이미지의 피드 ID")),
                        requestParts(
                                partWithName("images").description("이미지 파일 리스트 (선택사항)").optional()),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 상태 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                fieldWithPath("result").type(JsonFieldType.OBJECT).description("응답 데이터"),
                                fieldWithPath("result.feedId").type(JsonFieldType.NUMBER).description("이미지의 피드 ID"),
                                fieldWithPath("result.images[]").type(JsonFieldType.ARRAY).description("이미지 리스트"),
                                fieldWithPath("result.images[].id").type(JsonFieldType.NUMBER).description("이미지 ID"),
                                fieldWithPath("result.images[].imageURL").type(JsonFieldType.STRING).description("이미지 URL"),
                                fieldWithPath("result.images[].status").type(JsonFieldType.STRING).description("이미지 상태 (ACTIVE, INACTIVE)"),
                                fieldWithPath("result.images[].createdAt").type(JsonFieldType.STRING).description("이미지 등록시간"),
                                fieldWithPath("result.images[].updatedAt").type(JsonFieldType.STRING).description("이미지 수정시간"),
                                fieldWithPath("result.images[].deletedAt").type(JsonFieldType.STRING).description("이미지 삭제시간"),
                                fieldWithPath("responseTime").type(JsonFieldType.STRING).description("응답 시간")
                        )
                ));
    }

    @DisplayName("피드_이미지_전체_조회")
    @Test
    void getAllActiveFeeds() throws Exception {
        imageService.uploadImages(testFeed, TestImage.makeImages(2));

        this.mockMvc.perform(get("/feed/image/{feedId}", testFeed.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document.document(
                        pathParameters(
                                parameterWithName("feedId").description("조회할 이미지의 피드 ID")),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 상태 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                fieldWithPath("result").type(JsonFieldType.OBJECT).description("응답 데이터"),
                                fieldWithPath("result.feedId").type(JsonFieldType.NUMBER).description("이미지의 피드 ID"),
                                fieldWithPath("result.images[]").type(JsonFieldType.ARRAY).description("이미지 리스트"),
                                fieldWithPath("result.images[].id").type(JsonFieldType.NUMBER).description("이미지 ID"),
                                fieldWithPath("result.images[].imageURL").type(JsonFieldType.STRING).description("이미지 URL"),
                                fieldWithPath("result.images[].status").type(JsonFieldType.STRING).description("이미지 상태 (ACTIVE, INACTIVE)"),
                                fieldWithPath("result.images[].createdAt").type(JsonFieldType.STRING).description("이미지 등록시간"),
                                fieldWithPath("result.images[].updatedAt").type(JsonFieldType.STRING).description("이미지 수정시간"),
                                fieldWithPath("result.images[].deletedAt").type(JsonFieldType.STRING).description("이미지 삭제시간"),
                                fieldWithPath("responseTime").type(JsonFieldType.STRING).description("응답 시간")
                        )));
    }

    @DisplayName("피드_이미지_수정")
    @Test
    void updateFeedImages() throws Exception {
        MockMultipartFile imageFile = TestImage.convert(TestImage.makeImages(1), 0);
        imageService.uploadImages(testFeed, TestImage.makeImages(1));

        this.mockMvc.perform(fileUpload("/feed/image/{feedId}", testFeed.getId())
                        .file(imageFile)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document.document(
                        pathParameters(
                                parameterWithName("feedId").description("수정할 이미지의 피드 ID")),
                        requestParts(
                                partWithName("images").description("이미지 파일 리스트 (선택사항)").optional()),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 상태 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                fieldWithPath("result").type(JsonFieldType.OBJECT).description("응답 데이터"),
                                fieldWithPath("result.feedId").type(JsonFieldType.NUMBER).description("이미지의 피드 ID"),
                                fieldWithPath("result.images[]").type(JsonFieldType.ARRAY).description("이미지 리스트"),
                                fieldWithPath("result.images[].id").type(JsonFieldType.NUMBER).description("이미지 ID"),
                                fieldWithPath("result.images[].imageURL").type(JsonFieldType.STRING).description("이미지 URL"),
                                fieldWithPath("result.images[].status").type(JsonFieldType.STRING).description("이미지 상태 (ACTIVE, INACTIVE)"),
                                fieldWithPath("result.images[].createdAt").type(JsonFieldType.STRING).description("이미지 등록시간"),
                                fieldWithPath("result.images[].updatedAt").type(JsonFieldType.STRING).description("이미지 수정시간"),
                                fieldWithPath("result.images[].deletedAt").type(JsonFieldType.STRING).description("이미지 삭제시간"),
                                fieldWithPath("responseTime").type(JsonFieldType.STRING).description("응답 시간")
                        )));
    }

    @DisplayName("피드_이미지_삭제")
    @Test
    void deleteFeedImages() throws Exception {
        imageService.uploadImages(testFeed, TestImage.makeImages(1));

        this.mockMvc.perform(delete("/feed/image/{feedId}", testFeed.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document.document(
                        pathParameters(
                                parameterWithName("feedId").description("삭제할 이미지의 피드 ID")),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 상태 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                fieldWithPath("result").type(JsonFieldType.NULL).description("없음"),
                                fieldWithPath("responseTime").type(JsonFieldType.STRING).description("응답 시간")
                        )
                ));
    }
}
