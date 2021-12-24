package com.gloomy.server.application.feed;

import com.gloomy.server.application.image.ImageService;
import com.gloomy.server.application.image.Images;
import com.gloomy.server.domain.feed.FEED_STATUS;
import com.gloomy.server.domain.feed.Feed;
import com.gloomy.server.domain.user.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

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
    private UpdateFeedDTO.Request updateFeedDTO;

    @BeforeEach
    void beforeEach() {
        User testUser = new TestUserDTO().makeTestUser();
        userService.createUser(testUser);
        testFeedDTO = new TestFeedDTO(testUser, 1);
        updateFeedDTO = new UpdateFeedDTO.Request();
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

        checkCreatedFeedSuccess(userFeedDTO, createdFeed);
    }

    @Test
    void 피드_생성_회원_실패() {
        FeedDTO.Request userFeedDTOWithPassword = new FeedDTO.Request(
                true, testFeedDTO.getIp(), testFeedDTO.getPassword(), testFeedDTO.getContent(), testFeedDTO.getImages());
        FeedDTO.Request userFeedDTOWithNoUserId = new FeedDTO.Request(
                true, testFeedDTO.getIp(), (Long) null, testFeedDTO.getContent(), testFeedDTO.getImages());

        checkCreatedFeedFail(userFeedDTOWithPassword, "[FeedService] 회원 피드 등록 요청 메시지가 잘못되었습니다.");
        checkCreatedFeedFail(userFeedDTOWithNoUserId, "[FeedService] 회원 피드 등록 요청 메시지가 잘못되었습니다.");
    }

    @Test
    void 피드_생성_비회원_성공() {
        FeedDTO.Request nonUserFeedDTO = new FeedDTO.Request(
                false, testFeedDTO.getIp(), testFeedDTO.getPassword(), testFeedDTO.getContent(), testFeedDTO.getImages());

        Feed createdFeed = feedService.createFeed(nonUserFeedDTO);

        checkCreatedFeedSuccess(nonUserFeedDTO, createdFeed);
    }

    @Test
    void 피드_생성_비회원_실패() {
        FeedDTO.Request nonUserFeedDTOWithUserId = new FeedDTO.Request(
                false, testFeedDTO.getIp(), testFeedDTO.getUserId(), testFeedDTO.getContent(), testFeedDTO.getImages());
        FeedDTO.Request nonUserFeedDTOWithNoPassword = new FeedDTO.Request(
                false, testFeedDTO.getIp(), (String) null, testFeedDTO.getContent(), testFeedDTO.getImages());

        checkCreatedFeedFail(nonUserFeedDTOWithUserId, "[FeedService] 비회원 피드 등록 요청 메시지가 잘못되었습니다.");
        checkCreatedFeedFail(nonUserFeedDTOWithNoPassword, "[FeedService] 비회원 피드 등록 요청 메시지가 잘못되었습니다.");
    }

    @Test
    void 피드_전체_조회_성공() {
        final int allNonUserFeedsNum = 3;
        final int allUserFeedsNum = 3;

        List<Feed> createdAllFeeds = addFeeds(allNonUserFeedsNum, allUserFeedsNum);
        Page<Feed> foundAllFeeds = feedService.findAllFeeds(PageRequest.of(0, 10));
        checkFoundAllFeedsSuccess(createdAllFeeds, foundAllFeeds, allNonUserFeedsNum, allUserFeedsNum);
    }

    @Test
    void 피드_전체_조회_실패() {
        checkFoundAllFeedsFail(null, "[FeedService] Pageable이 유효하지 않습니다.");
    }

    @Test
    void 피드_조회_회원_성공() {
        FeedDTO.Request userFeedDTO = testFeedDTO.makeUserFeedDTO();

        Feed createdUserFeedFirst = feedService.createFeed(userFeedDTO);
        Page<Feed> foundUserFeedsFirst = feedService.findUserFeeds(
                PageRequest.of(0, 10), createdUserFeedFirst.getUserId().getId());
        Feed createdUserFeedSecond = feedService.createFeed(userFeedDTO);
        Page<Feed> foundUserFeedsSecond = feedService.findUserFeeds(
                PageRequest.of(0, 10), createdUserFeedFirst.getUserId().getId());

        assertEquals(foundUserFeedsFirst.getContent().size(), 1);
        assertEquals(foundUserFeedsFirst.getContent().get(0), createdUserFeedFirst);
        assertEquals(foundUserFeedsSecond.getContent().size(), 2);
        assertEquals(foundUserFeedsSecond.getContent().get(0), createdUserFeedFirst);
        assertEquals(foundUserFeedsSecond.getContent().get(1), createdUserFeedSecond);
    }

    @Test
    void 피드_조회_회원_실패() {
        User createdUser = userService.createUser(new TestUserDTO().makeTestUser());
        User deletedUser = userService.createUser(new TestUserDTO().makeTestUser());
        PageRequest pageable = PageRequest.of(0, 10);

        userService.deleteUser(deletedUser.getId());

        checkFoundUserFeedFail(pageable, null, "[FeedService] 사용자 ID가 유효하지 않습니다.");
        checkFoundUserFeedFail(pageable, 0L, "[FeedService] 사용자 ID가 유효하지 않습니다.");
        checkFoundUserFeedFail(pageable, deletedUser.getId(), "[FeedService] 해당하는 사용자가 없습니다.");
        checkFoundUserFeedFail(null, createdUser.getId(), "[FeedService] pageable이 유효하지 않습니다.");
    }

    @Test
    void 피드_조회_비회원_성공() {
        FeedDTO.Request nonUserFeedDTO = testFeedDTO.makeNonUserFeedDTO();

        Feed createdUserFeed = feedService.createFeed(nonUserFeedDTO);
        Feed foundNonUserFeed = feedService.findOneFeed(createdUserFeed.getId());

        assertEquals(foundNonUserFeed, createdUserFeed);
    }

    @Test
    void 피드_조회_비회원_실패() {
        Feed createNonUserFeed = feedService.createFeed(testFeedDTO.makeNonUserFeedDTO());

        imageService.deleteAll();
        feedService.deleteAll();

        checkFoundNonUserFeedFail(0L, "[FeedService] 비회원 피드 ID가 유효하지 않습니다.");
        checkFoundNonUserFeedFail(null, "[FeedService] 비회원 피드 ID가 유효하지 않습니다.");
        checkFoundNonUserFeedFail(createNonUserFeed.getId(), "[FeedService] 해당 피드 ID가 존재하지 않습니다.");
    }

    @Test
    void 피드_수정_공통_성공() {
        Feed createdNonUserFeed = feedService.createFeed(testFeedDTO.makeNonUserFeedDTO());
        String updateContent = "새 글";
        ArrayList<MultipartFile> updateImages = testFeedDTO.getImages();
        updateFeedDTO.setContent(updateContent);
        updateFeedDTO.setImages(updateImages);

        Feed updatedNonUserFeed = feedService.updateOneFeed(createdNonUserFeed.getId(), updateFeedDTO);
        Feed foundUpdatedNonUserFeed = feedService.findOneFeed(createdNonUserFeed.getId());
        Images foundActiveImages = imageService.findActiveImages(createdNonUserFeed);

        assertEquals(foundUpdatedNonUserFeed, updatedNonUserFeed);
        assertEquals(foundActiveImages.getSize(), updateImages.size());
    }

    @Test
    void 피드_수정_비회원_성공() {
        String updatePassword = "34567";
        Feed createdNonUserFeed = feedService.createFeed(testFeedDTO.makeNonUserFeedDTO());
        updateFeedDTO.setPassword(updatePassword);

        Feed updatedNonUserFeed = feedService.updateOneFeed(createdNonUserFeed.getId(), updateFeedDTO);
        Feed foundUpdatedNonUserFeed = feedService.findOneFeed(createdNonUserFeed.getId());

        assertEquals(foundUpdatedNonUserFeed, updatedNonUserFeed);
    }

    @Test
    void 피드_수정_회원_실패() {
        String updatePassword = "34567";
        Feed createdUserFeed = feedService.createFeed(testFeedDTO.makeUserFeedDTO());
        updateFeedDTO.setPassword(updatePassword);

        checkUpdatedFeedFail(createdUserFeed.getId(), updateFeedDTO, "[FeedService] 회원 피드 수정 요청 메시지가 잘못되었습니다.");
    }

    @Test
    void 피드_삭제_성공() {
        FeedDTO.Request userFeedDTO = testFeedDTO.makeUserFeedDTO();

        Feed createdUserFeed = feedService.createFeed(userFeedDTO);
        Feed deletedUserFeed = feedService.deleteFeed(createdUserFeed.getId());

        assertEquals(deletedUserFeed.getStatus(), FEED_STATUS.INACTIVE);
    }

    @Test
    void 피드_삭제_비회원_성공() {
        FeedDTO.Request nonUserFeedDTO = testFeedDTO.makeNonUserFeedDTO();

        Feed createdNonUserFeed = feedService.createFeed(nonUserFeedDTO);
        Feed deletedNonUserFeed = feedService.deleteFeed(createdNonUserFeed.getId());

        assertEquals(deletedNonUserFeed.getStatus(), FEED_STATUS.INACTIVE);
    }

    @Test
    void 피드_삭제_공통_실패() {
        Feed createUserFeed = feedService.createFeed(testFeedDTO.makeUserFeedDTO());
        Feed createNonUserFeed = feedService.createFeed(testFeedDTO.makeNonUserFeedDTO());

        imageService.deleteAll();
        feedService.deleteAll();

        checkDeletedFeedFail(0L, "[FeedService] 비회원 피드 ID가 유효하지 않습니다.");
        checkDeletedFeedFail(null, "[FeedService] 비회원 피드 ID가 유효하지 않습니다.");
        checkDeletedFeedFail(createUserFeed.getId(), "[FeedService] 해당 피드 ID가 존재하지 않습니다.");
        checkDeletedFeedFail(createNonUserFeed.getId(), "[FeedService] 해당 피드 ID가 존재하지 않습니다.");
    }

    private void checkCreatedFeedSuccess(FeedDTO.Request feedDTO, Feed createdFeed) {
        assertEquals(createdFeed.getIsUser().getIsUser(), feedDTO.getIsUser());
        assertEquals(createdFeed.getIp().getIp(), feedDTO.getIp());
        assertEquals(createdFeed.getContent().getContent(), feedDTO.getContent());
        assertEquals(imageService.findImages(createdFeed).getSize(), feedDTO.getImages().size());

        if (feedDTO.getIsUser()) {
            assertEquals(createdFeed.getUserId().getId(), feedDTO.getUserId());
            assertNull(createdFeed.getPassword());
            return;
        }
        assertEquals(createdFeed.getPassword().getPassword(), feedDTO.getPassword());
        assertNull(createdFeed.getUserId());
    }

    private void checkCreatedFeedFail(FeedDTO.Request feedDTO, String errorMessage) {
        assertEquals(
                assertThrows(IllegalArgumentException.class, () -> {
                    feedService.createFeed(feedDTO);
                }).getMessage(),
                errorMessage);
    }

    private void checkFoundUserFeedFail(Pageable pageable, Long userId, String errorMessage) {
        assertEquals(
                assertThrows(IllegalArgumentException.class, () -> {
                    feedService.findUserFeeds(pageable, userId);
                }).getMessage(),
                errorMessage);
    }

    private void checkFoundNonUserFeedFail(Long feedId, String errorMessage) {
        assertEquals(
                assertThrows(IllegalArgumentException.class, () -> {
                    feedService.findOneFeed(feedId);
                }).getMessage(),
                errorMessage);
    }

    private List<Feed> addFeeds(int nonUserFeedNum, int userFeedNum) {
        List<Feed> allFeeds = new ArrayList<>();
        FeedDTO.Request nonUserFeedDTO = testFeedDTO.makeNonUserFeedDTO();
        for (int num = 0; num < nonUserFeedNum; num++) {
            allFeeds.add(feedService.createFeed(nonUserFeedDTO));
        }
        FeedDTO.Request userFeedDTO = testFeedDTO.makeNonUserFeedDTO();
        for (int num = 0; num < userFeedNum; num++) {
            allFeeds.add(feedService.createFeed(userFeedDTO));
        }
        return allFeeds;
    }

    private void checkFoundAllFeedsSuccess(List<Feed> cratedAllFeeds, Page<Feed> foundAllFeeds, int allNonUserFeedsNum, int allUserFeedsNum) {
        assertEquals(foundAllFeeds.getContent().size(), allNonUserFeedsNum + allUserFeedsNum);
        for (int num = 0; num < allNonUserFeedsNum + allUserFeedsNum; num++) {
            assertEquals(foundAllFeeds.getContent().get(num), cratedAllFeeds.get(num));
        }
    }

    private void checkFoundAllFeedsFail(Pageable pageable, String errorMessage) {
        assertEquals(
                assertThrows(IllegalArgumentException.class, () -> {
                    feedService.findAllFeeds(pageable);
                }).getMessage(),
                errorMessage);
    }

    private void checkUpdatedFeedFail(Long feedId, UpdateFeedDTO.Request feedDTO, String errorMessage) {
        assertEquals(
                assertThrows(IllegalArgumentException.class, () -> {
                    feedService.updateOneFeed(feedId, feedDTO);
                }).getMessage(),
                errorMessage);
    }

    private void checkDeletedFeedFail(Long feedId, String errorMessage) {
        assertEquals(
                assertThrows(IllegalArgumentException.class, () -> {
                    feedService.deleteFeed(feedId);
                }).getMessage(),
                errorMessage);
    }
}
