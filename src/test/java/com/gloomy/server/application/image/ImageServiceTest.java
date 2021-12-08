package com.gloomy.server.application.image;

import com.gloomy.server.application.feed.FeedDTO;
import com.gloomy.server.application.feed.FeedService;
import com.gloomy.server.application.feed.TestFeedDTO;
import com.gloomy.server.application.feed.TestUserDTO;
import com.gloomy.server.domain.feed.Feed;
import com.gloomy.server.domain.user.User;
import com.gloomy.server.domain.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest(properties = {
        "spring.config.location=classpath:application.yml,classpath:aws.yml"
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
    void setUp() {
        User testUser = new TestUserDTO().makeTestUser();
        userService.createUser(testUser);
        FeedDTO.Request testFeedDTO = new TestFeedDTO(testUser).makeUserFeedDTO();
        testFeed = feedService.createFeed(testFeedDTO);
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

        checkImageSuccess(imagesOne, createdImagesOne);
        checkImageSuccess(imagesTwo, createdImagesTwo);
    }

    @Test
    void 이미지_업로드_실패() {
        ArrayList<MultipartFile> images = testImage.makeImages(1);

        checkImageFail(testFeed, null, "[ImageService] 이미지 파일이 존재하지 않습니다.");
        checkImageFail(null, images, "[ImageService] 피드가 존재하지 않습니다.");
    }

    private void checkImageFail(Feed feed, ArrayList<MultipartFile> images, String errorMessage) {
        assertEquals(
                assertThrows(IllegalArgumentException.class, () -> {
                    imageService.uploadMany(feed, images);
                }).getMessage(),
                errorMessage);
    }

    private void checkImageSuccess(ArrayList<MultipartFile> images, Images createdImages) {
        assertEquals(createdImages.getSize(), images.size());
        for (int i = 0; i < images.size(); i++) {
            assertEquals(createdImages.getImages().get(i).getFeedId().getId(), testFeed.getId());
        }
    }
}
