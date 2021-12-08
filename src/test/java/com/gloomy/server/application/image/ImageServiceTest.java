package com.gloomy.server.application.image;

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
    private ImageService imageService;

    private TestImage testImage;

    @BeforeEach
    void setUp() {
        testImage = new TestImage();
    }

    @AfterEach
    void afterEach() {
        imageService.deleteAll();
    }

    @Test
    void 이미지_업로드_성공() {
        ArrayList<MultipartFile> imagesOne = testImage.makeImages(1);
        ArrayList<MultipartFile> imagesTwo = testImage.makeImages(2);

        Images createdImagesOne = imageService.uploadMany(imagesOne);
        Images createdImagesTwo = imageService.uploadMany(imagesTwo);

        checkImageSuccess(imagesOne, createdImagesOne);
        checkImageSuccess(imagesTwo, createdImagesTwo);
    }

    @Test
    void 이미지_업로드_실패() {
        checkImageFail(null, "[ImageService] 이미지 파일이 존재하지 않습니다.");
    }

    private void checkImageFail(ArrayList<MultipartFile> images, String errorMessage) {
        assertEquals(
                assertThrows(IllegalArgumentException.class, () -> {
                    imageService.uploadMany(images);
                }).getMessage(),
                errorMessage);
    }

    private void checkImageSuccess(ArrayList<MultipartFile> images, Images createdImages) {
        assertEquals(createdImages.getSize(), images.size());
    }
}