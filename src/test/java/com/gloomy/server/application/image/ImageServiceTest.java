package com.gloomy.server.application.image;

import com.gloomy.server.application.feed.FeedDTO;
import com.gloomy.server.application.feed.FeedService;
import com.gloomy.server.application.feed.TestFeedDTO;
import com.gloomy.server.application.feed.TestUserDTO;
import com.gloomy.server.domain.common.Status;
import com.gloomy.server.domain.feed.Feed;
import com.gloomy.server.domain.image.Image;
import com.gloomy.server.domain.user.User;
import com.gloomy.server.domain.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest(properties = {
        "spring.config.location=classpath:test-application.yml,classpath:aws.yml"
})
class ImageServiceTest {
    @Autowired
    private UserService userService;
    @Autowired
    private FeedService feedService;
    @Autowired
    private ImageService imageService;

    private TestImage testImage;
    private Feed testFeed;

    @BeforeEach
    void beforeEach() {
        User testUser = new TestUserDTO().makeTestUser();
        userService.createUser(testUser);
        FeedDTO.Request testFeedDTO = new TestFeedDTO(testUser, 0).makeUserFeedDTO();
        testFeed = feedService.createFeed(testUser.getId(), testFeedDTO);
        testImage = new TestImage();
    }

    @AfterEach
    void afterEach() {
        imageService.deleteAll();
        feedService.deleteAll();
        userService.deleteAll();
    }

    @Test
    void 이미지_업로드_성공() {
        ArrayList<MultipartFile> imagesOne = testImage.makeImages(1);
        ArrayList<MultipartFile> imagesTwo = testImage.makeImages(2);

        Images createdImagesOne = imageService.uploadMany(testFeed, imagesOne);
        Images createdImagesTwo = imageService.uploadMany(testFeed, imagesTwo);

        checkUploadedImageSuccess(imagesOne, createdImagesOne);
        checkUploadedImageSuccess(imagesTwo, createdImagesTwo);
    }

    @Test
    void 이미지_업로드_실패() {
        ArrayList<MultipartFile> images = testImage.makeImages(1);

        checkUploadedImageFail(testFeed, null, "[ImageService] 이미지 파일이 존재하지 않습니다.");
        checkUploadedImageFail(null, images, "[ImageService] 피드가 존재하지 않습니다.");
    }

    @Test
    void 이미지_조회_성공() {
        ArrayList<MultipartFile> imagesFirst = testImage.makeImages(1);
        ArrayList<MultipartFile> imagesSecond = testImage.makeImages(2);

        Images createdImagesFirst = imageService.uploadMany(testFeed, imagesFirst);
        Images foundImagesFirst = imageService.findImages(testFeed);
        Images createdImagesSecond = imageService.uploadMany(testFeed, imagesSecond);
        Images foundImagesSecond = imageService.findImages(testFeed);

        assertEquals(foundImagesFirst.getSize(), imagesFirst.size());
        assertEquals(foundImagesFirst.getImages().get(0), createdImagesFirst.getImages().get(0));
        assertEquals(foundImagesSecond.getSize(), imagesFirst.size() + imagesSecond.size());
        assertEquals(foundImagesSecond.getImages().get(1), createdImagesSecond.getImages().get(0));
        assertEquals(foundImagesSecond.getImages().get(2), createdImagesSecond.getImages().get(1));
    }

    @Test
    void 이미지_조회_실패() {
        checkFoundImageFail(null, "[ImageService] 피드가 유효하지 않습니다.");
    }

    @Test
    void 이미지_삭제_성공() {
        ArrayList<MultipartFile> images = testImage.makeImages(3);

        imageService.uploadMany(testFeed, images);
        Images deleteImages = imageService.deleteImages(testFeed);

        checkDeletedImageSuccess(deleteImages);
    }

    @Test
    void 이미지_삭제_실패() {
        checkDeletedImageFail(null, "[ImageService] 피드가 유효하지 않습니다.");
    }

    private void checkUploadedImageSuccess(ArrayList<MultipartFile> images, Images createdImages) {
        assertEquals(createdImages.getSize(), images.size());
        for (int i = 0; i < images.size(); i++) {
            assertEquals(createdImages.getImages().get(i).getFeedId().getId(), testFeed.getId());
        }
    }

    private void checkUploadedImageFail(Feed feed, ArrayList<MultipartFile> images, String errorMessage) {
        assertEquals(
                assertThrows(IllegalArgumentException.class, () -> {
                    imageService.uploadMany(feed, images);
                }).getMessage(),
                errorMessage);
    }

    private void checkFoundImageFail(Feed feedId, String errorMessage) {
        assertEquals(
                assertThrows(IllegalArgumentException.class, () -> {
                    imageService.findImages(feedId);
                }).getMessage(),
                errorMessage);
    }

    private void checkDeletedImageSuccess(Images deletedImages) {
        for (Image deletedImage : deletedImages.getImages()) {
            assertEquals(deletedImage.getStatus(), Status.INACTIVE);
        }
    }

    private void checkDeletedImageFail(Feed feedId, String errorMessage) {
        assertEquals(
                assertThrows(IllegalArgumentException.class, () -> {
                    imageService.deleteImages(feedId);
                }).getMessage(),
                errorMessage);
    }
}