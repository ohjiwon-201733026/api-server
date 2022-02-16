package com.gloomy.server.application.feed;

import com.gloomy.server.application.image.TestImage;
import com.gloomy.server.domain.user.User;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;

@Slf4j
@Getter
public class TestFeedDTO {
    private final User user;
    private final Long userId;
    private final String password;
    private String category;
    private final String title;
    private final String content;
    private ArrayList<MultipartFile> images;
    private String token;

    public TestFeedDTO(User testUser, int imageNum) {
        this.user = testUser;
        this.userId = testUser.getId();
        this.category = "ALL";
        this.title = "글 제목";
        this.password = "12345";
        this.content = "글 작성 샘플입니다.";
        this.images = TestImage.makeImages(imageNum);
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setImages(int imageNum) {
        this.images = TestImage.makeImages(imageNum);
    }

    public FeedDTO.Request makeUserFeedDTO() {
        return new FeedDTO.Request(category, title, content);
    }

    public FeedDTO.Request makeNonUserFeedDTO() {
        return new FeedDTO.Request(password, category, title, content);
    }
}
