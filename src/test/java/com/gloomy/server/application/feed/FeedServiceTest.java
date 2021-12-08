package com.gloomy.server.application.feed;

import com.gloomy.server.application.image.ImageService;
import com.gloomy.server.domain.feed.Feed;
import com.gloomy.server.domain.user.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest(properties = {
        "spring.config.location=classpath:application.yml,classpath:aws.yml"
})
class FeedServiceTest {
    @Autowired
    private FeedService feedService;
    @Autowired
    private ImageService imageService;
    @Autowired
    private UserService userService;

    private TestFeedDTO testFeedDTO;

    @BeforeEach
    void beforeEach() {
        User testUser = new TestUserDTO().makeTestUser();
        userService.createUser(testUser);
        testFeedDTO = new TestFeedDTO(testUser);
    }

    @AfterEach
    void afterEach() {
        imageService.deleteAll();
        feedService.deleteAll();
        userService.deleteAll();
    }

    @Test
    void 피드_생성_회원_성공() {
        FeedDTO.Request userFeedDTO = new FeedDTO.Request(
                true, testFeedDTO.getIp(), testFeedDTO.getUserId(), testFeedDTO.getContent(), testFeedDTO.getImages());

        Feed createdFeed = feedService.createFeed(userFeedDTO);

        checkFeedSuccess(userFeedDTO, createdFeed);
    }

    @Test
    void 피드_생성_회원_실패() {
        FeedDTO.Request userFeedDTOWithPassword = new FeedDTO.Request(
                true, testFeedDTO.getIp(), testFeedDTO.getPassword(), testFeedDTO.getContent(), testFeedDTO.getImages());
        FeedDTO.Request userFeedDTOWithNoUserId = new FeedDTO.Request(
                true, testFeedDTO.getIp(), (Long) null, testFeedDTO.getContent(), testFeedDTO.getImages());

        checkFeedFail(userFeedDTOWithPassword, "[FeedService] 회원 피드 등록 요청 메시지가 잘못되었습니다.");
        checkFeedFail(userFeedDTOWithNoUserId, "[FeedService] 회원 피드 등록 요청 메시지가 잘못되었습니다.");
    }

    @Test
    void 피드_생성_비회원_성공() {
        FeedDTO.Request nonUserFeedDTO = new FeedDTO.Request(
                false, testFeedDTO.getIp(), testFeedDTO.getPassword(), testFeedDTO.getContent(), testFeedDTO.getImages());

        Feed createdFeed = feedService.createFeed(nonUserFeedDTO);

        checkFeedSuccess(nonUserFeedDTO, createdFeed);
    }

    @Test
    void 피드_생성_비회원_실패() {
        FeedDTO.Request nonUserFeedDTOWithUserId = new FeedDTO.Request(
                false, testFeedDTO.getIp(), testFeedDTO.getUserId(), testFeedDTO.getContent(), testFeedDTO.getImages());
        FeedDTO.Request nonUserFeedDTOWithNoPassword = new FeedDTO.Request(
                false, testFeedDTO.getIp(), (String) null, testFeedDTO.getContent(), testFeedDTO.getImages());

        checkFeedFail(nonUserFeedDTOWithUserId, "[FeedService] 비회원 피드 등록 요청 메시지가 잘못되었습니다.");
        checkFeedFail(nonUserFeedDTOWithNoPassword, "[FeedService] 비회원 피드 등록 요청 메시지가 잘못되었습니다.");
    }

    private void checkFeedSuccess(FeedDTO.Request feedDTO, Feed createdFeed) {
        final int imageSize = 1;

        assertEquals(createdFeed.getIsUser().getIsUser(), feedDTO.getIsUser());
        assertEquals(createdFeed.getIp().getIp(), feedDTO.getIp());
        assertEquals(createdFeed.getContent().getContent(), feedDTO.getContent());
        assertEquals(imageService.findImages(createdFeed).getSize(), imageSize);

        if (feedDTO.getIsUser()) {
            assertEquals(createdFeed.getUserId().getId(), feedDTO.getUserId());
            assertNull(createdFeed.getPassword());
            return;
        }
        assertEquals(createdFeed.getPassword().getPassword(), feedDTO.getPassword());
        assertNull(createdFeed.getUserId());
    }

    private void checkFeedFail(FeedDTO.Request feedDTO, String errorMessage) {
        assertEquals(
                assertThrows(IllegalArgumentException.class, () -> {
                    feedService.createFeed(feedDTO);
                }).getMessage(),
                errorMessage);
    }
}
