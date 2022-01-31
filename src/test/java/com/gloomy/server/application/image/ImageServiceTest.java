package com.gloomy.server.application.image;

import com.gloomy.server.application.feed.FeedDTO;
import com.gloomy.server.application.feed.FeedService;
import com.gloomy.server.application.feed.TestFeedDTO;
import com.gloomy.server.application.feed.TestUserDTO;
import com.gloomy.server.domain.common.entity.Status;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

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

    @Value("${cloud.aws.s3.feedTestDir}")
    private String feedTestDir;
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
        imageService.deleteAll(feedTestDir);
        feedService.deleteAll();
        userService.deleteAll();
    }

    @Test
    void 이미지_업로드_성공() {
        ArrayList<MultipartFile> imagesOne = testImage.makeImages(1);
        ArrayList<MultipartFile> imagesTwo = testImage.makeImages(2);

        Images createdImagesOne = imageService.uploadImages(testFeed, imagesOne);
        Images createdImagesTwo = imageService.uploadImages(testFeed, imagesTwo);

        checkUploadedImageSuccess(imagesOne, createdImagesOne);
        checkUploadedImageSuccess(imagesTwo, createdImagesTwo);
    }

    @Test
    void 이미지_업로드_실패() {
        ArrayList<MultipartFile> images = testImage.makeImages(1);

        checkUploadedImageFail(null, images, "[ImageService] 해당 피드가 유효하지 않습니다.");
    }

    @Test
    void 이미지_전체_조회_성공() {
        ArrayList<MultipartFile> imagesFirst = testImage.makeImages(1);
        ArrayList<MultipartFile> imagesSecond = testImage.makeImages(2);

        Images createdImagesFirst = imageService.uploadImages(testFeed, imagesFirst);
        Images foundImagesFirst = imageService.findAllImages(testFeed);
        Images createdImagesSecond = imageService.uploadImages(testFeed, imagesSecond);
        Images foundImagesSecond = imageService.findAllImages(testFeed);

        assertEquals(foundImagesFirst.getSize(), imagesFirst.size());
        assertEquals(foundImagesFirst.getImages().get(0), createdImagesFirst.getImages().get(0));
        assertEquals(foundImagesSecond.getSize(), imagesFirst.size() + imagesSecond.size());
        assertEquals(foundImagesSecond.getImages().get(1), createdImagesSecond.getImages().get(0));
        assertEquals(foundImagesSecond.getImages().get(2), createdImagesSecond.getImages().get(1));
    }

    @Test
    void 이미지_전체_조회_실패() {
        checkFoundAllImagesFail(null, "[ImageService] 해당 피드가 유효하지 않습니다.");
    }

    @Test
    void 이미지_조회_성공() {
        ArrayList<MultipartFile> images = testImage.makeImages(1);

        Images createdImages = imageService.uploadImages(testFeed, images);
        Image foundImage = imageService.findOneImage(createdImages.getImages().get(0).getId());

        assertEquals(foundImage, createdImages.getImages().get(0));
    }

    @Test
    void 이미지_조회_실패() {
        ArrayList<MultipartFile> images = testImage.makeImages(1);

        Image createdImage = imageService.updateImages(testFeed, images).getImages().get(0);
        imageService.deleteAll(feedTestDir);

        checkFoundImageFail(null, "[ImageService] 해당 이미지 ID가 유효하지 않습니다.");
        checkFoundImageFail(0L, "[ImageService] 해당 이미지 ID가 유효하지 않습니다.");
        checkFoundImageFail(createdImage.getId(), "[ImageService] 해당 이미지 ID가 존재하지 않습니다.");
    }

    @Test
    void 활성_이미지_전체_조회_성공() {
        ArrayList<MultipartFile> images = testImage.makeImages(2);

        Images createdImages = imageService.uploadImages(testFeed, images);
        imageService.deleteImage(createdImages.getImages().get(0).getId());
        Images deletedImages = imageService.findAllImages(testFeed);

        assertEquals(deletedImages.getSize(), 1);
        assertEquals(deletedImages.getImages().get(0), createdImages.getImages().get(1));
    }

    @Test
    void 활성_이미지_전체_조회_실패() {
        checkFoundAllActiveImagesFail(null, "[ImageService] 해당 피드가 유효하지 않습니다.");
    }

    @Transactional
    @Test
    void 이미지_수정_성공() {
        ArrayList<MultipartFile> images = testImage.makeImages(1);
        ArrayList<MultipartFile> updateImages = testImage.makeUpdateImages(2);

        Images createdImages = imageService.uploadImages(testFeed, images);
        Images updatedImages = imageService.updateImages(testFeed, updateImages);
        Images foundImages = imageService.findAllActiveImages(testFeed);

        checkUpdatedImageSuccess(updateImages, createdImages, updatedImages, foundImages);
    }

    @Test
    void 이미지_수정_실패() {
        ArrayList<MultipartFile> images = testImage.makeImages(1);

        checkUpdatedImageFail(null, images, "[ImageService] 해당 피드가 유효하지 않습니다.");
    }

    @Test
    void 이미지_삭제_성공() {
        ArrayList<MultipartFile> images = testImage.makeImages(3);

        imageService.uploadImages(testFeed, images);
        imageService.deleteImages(testFeed);
        Images deletedImages = imageService.findAllActiveImages(testFeed);

        checkDeletedImageSuccess(deletedImages);
    }

    @Test
    void 이미지_삭제_실패() {
        checkDeletedImageFail(null, "[ImageService] 해당 피드가 유효하지 않습니다.");
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
                    imageService.uploadImages(feed, images);
                }).getMessage(),
                errorMessage);
    }

    private void checkFoundAllImagesFail(Feed feedId, String errorMessage) {
        assertEquals(
                assertThrows(IllegalArgumentException.class, () -> {
                    imageService.findAllImages(feedId);
                }).getMessage(),
                errorMessage);
    }

    private void checkFoundImageFail(Long imageId, String errorMessage) {
        assertEquals(
                assertThrows(IllegalArgumentException.class, () -> {
                    imageService.findOneImage(imageId);
                }).getMessage(),
                errorMessage);
    }

    private void checkFoundAllActiveImagesFail(Feed feedId, String errorMessage) {
        assertEquals(
                assertThrows(IllegalArgumentException.class, () -> {
                    imageService.findAllActiveImages(feedId);
                }).getMessage(),
                errorMessage);
    }

    private void checkUpdatedImageSuccess(ArrayList<MultipartFile> updateImages, Images createdImages, Images updatedImages, Images foundImages) {
        assertEquals(foundImages.getSize(), updateImages.size());
        for (int num = 0; num < foundImages.getSize(); num++) {
            assertEquals(foundImages.getImages().get(num).getFeedId(), createdImages.getImages().get(0).getFeedId());
            assertEquals(foundImages.getImages().get(num).getImageUrl().getImageUrl(), updatedImages.getImages().get(num).getImageUrl().getImageUrl());
        }
    }

    private void checkUpdatedImageFail(Feed feedId, List<MultipartFile> images, String errorMessage) {
        assertEquals(
                assertThrows(IllegalArgumentException.class, () -> {
                    imageService.updateImages(feedId, images);
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
