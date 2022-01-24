package com.gloomy.server.application.image;

import com.gloomy.server.application.image.s3.S3Uploader;
import com.gloomy.server.application.user.TestUserDTO;
import com.gloomy.server.domain.common.Status;
import com.gloomy.server.domain.image.UserProfileImage;
import com.gloomy.server.domain.user.Password;
import com.gloomy.server.domain.user.Sex;
import com.gloomy.server.domain.user.User;
import com.gloomy.server.domain.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest(properties = {
        "spring.config.location=classpath:test-application.yml,classpath:aws.yml"
})
public class UserProfileImageServiceTest {
    @Autowired
    private UserService userService;
    @Autowired
    private ImageService imageService;
    @Autowired
    private UserProfileImageService userProfileImageService;
    @Autowired
    private UserProfileImageRepository userProfileImageRepository;

    @Autowired
    S3Uploader s3Uploader;
    private User testUser;
    private TestImage testImage;
    private static String defaultImageURL = "https://gl00my-bucket.s3.ap-northeast-2.amazonaws.com/user/default/bc1e908a-e345-4941-8445-0a9bf0849a49s3_tmp.jpg";

    @BeforeEach
    void beforeEach() {
        this.testUser = TestUserDTO.TestUser.makeTestUser();
        userService.createUser(testUser);
        testImage = new TestImage();
    }

    @AfterEach
    void after() {
        userProfileImageService.deleteAll(testUser);
    }

    @Test
    public void 유저_디폴트_이미지_조회() {
        testUser.changeId(10000L);
        UserProfileImage image = userProfileImageService.findImageByUserId(testUser);
        Assertions.assertEquals(image.getImageUrl().getImageUrl(), defaultImageURL);
    }

    @Test
    public void 유저_이미지_업로드_성공() {
        ArrayList<MultipartFile> image = testImage.makeImages(1);
        userProfileImageService.uploadUserImage(testUser, image.get(0));

        UserProfileImage image1 = userProfileImageService.findImageByUserId(testUser);

        Assertions.assertEquals(image1.getStatus(), Status.ACTIVE);
        Assertions.assertEquals(userProfileImageRepository.findAll().size(), 1);
    }

    @Test
    public void 유저_이미지_전체_삭제() {

        userProfileImageService.deleteAll(testUser);

        List<UserProfileImage> imageList = userProfileImageRepository.findAll();

        Assertions.assertEquals(imageList.size(), 0);
    }

    @Test
    public void test() {
        System.out.println(s3Uploader.upload("user/default", testImage.makeImages(1).get(0)));
    }


}
