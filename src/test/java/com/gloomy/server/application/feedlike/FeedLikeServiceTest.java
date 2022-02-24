package com.gloomy.server.application.feedlike;

import com.gloomy.server.application.feed.*;
import com.gloomy.server.application.notice.NoticeService;
import com.gloomy.server.domain.feed.Feed;
import com.gloomy.server.domain.feedlike.FeedLike;
import com.gloomy.server.domain.user.User;
import com.gloomy.server.domain.user.UserService;
import groovy.util.logging.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest(properties = {
        "spring.config.location=classpath:test-application.yml,classpath:aws.yml"
})
class FeedLikeServiceTest {
    @Autowired
    private FeedService feedService;
    @Autowired
    private FeedLikeService feedLikeService;
    @Autowired
    private UserService userService;
    @Autowired
    private NoticeService noticeService;

    private Feed testFeed;

    @BeforeEach
    void beforeEach() {
        User testUser = userService.createUser(TestUserDTO.makeTestUser());
        TestFeedDTO testFeedDTO = new TestFeedDTO(testUser, 1);
        testFeed = feedService.createFeed(null, testFeedDTO.makeNonUserFeedDTO());
    }

    @AfterEach
    void afterEach() {
        noticeService.deleteAll();
        feedLikeService.deleteAll();
        feedService.deleteAll();
        userService.deleteAll();
    }

    @Transactional
    @Test
    void 피드_비회원_좋아요_생성_성공() {
        FeedLikeDTO.Request feedLikeDTO = new FeedLikeDTO.Request(testFeed.getId());

        FeedLike feedLike = feedLikeService.createFeedLike(null, feedLikeDTO);

        assertEquals(feedLike.getFeedId(), testFeed);
    }

    @Test
    void 피드_비회원_좋아요_생성_실패() {
        FeedLikeDTO.Request feedLikeDTOWithFeedIdNull = new FeedLikeDTO.Request(null);
        FeedLikeDTO.Request feedLikeDTOWithFeedIdZeroOrLess = new FeedLikeDTO.Request(0L);

        checkCreatedFeedLikeFail(null, feedLikeDTOWithFeedIdNull, "[FeedLikeService] feedId가 유효하지 않습니다.");
        checkCreatedFeedLikeFail(null, feedLikeDTOWithFeedIdZeroOrLess, "[FeedLikeService] feedId가 유효하지 않습니다.");
    }

    @Transactional
    @Test
    void 피드_회원_좋아요_생성_성공() {
        FeedLikeDTO.Request feedLikeDTO = new FeedLikeDTO.Request(testFeed.getId());
        User feedLikeUser = userService.createUser(TestUserDTO.makeTestUser());

        FeedLike feedLike = feedLikeService.createFeedLike(feedLikeUser.getId(), feedLikeDTO);

        assertEquals(feedLike.getFeedId(), testFeed);
    }

    @Test
    void 피드_회원_좋아요_생성_실패() {
        FeedLikeDTO.Request feedLikeDTO = new FeedLikeDTO.Request(testFeed.getId());

        checkCreatedFeedLikeFail(0L, feedLikeDTO, "[FeedLikeService] userId가 유효하지 않습니다.");
    }

    @Transactional
    @Test
    void 피드_좋아요_수_조회_성공() {
        FeedLikeDTO.Request feedLikeDTO = new FeedLikeDTO.Request(testFeed.getId());

        feedLikeService.createFeedLike(null, feedLikeDTO);
        feedLikeService.createFeedLike(null, feedLikeDTO);
        Integer feedLikeCount = feedLikeService.getFeedLikeCount(testFeed);

        assertEquals(feedLikeCount, 2);
    }

    private void checkCreatedFeedLikeFail(Long userId, FeedLikeDTO.Request feedLikeDTO, String errorMessage) {
        assertEquals(
                assertThrows(IllegalArgumentException.class, () -> {
                    feedLikeService.createFeedLike(userId, feedLikeDTO);
                }).getMessage(),
                errorMessage);
    }
}
