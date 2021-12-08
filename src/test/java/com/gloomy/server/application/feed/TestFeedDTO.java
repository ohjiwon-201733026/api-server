package com.gloomy.server.application.feed;

import com.gloomy.server.application.image.TestImage;
import com.gloomy.server.domain.feed.Feed;
import com.gloomy.server.domain.user.User;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;

@Getter
public class TestFeedDTO {
    private final String ip;
    private final User user;
    private final Long userId;
    private final String password;
    private final String content;
    private final ArrayList<MultipartFile> images;

    public TestFeedDTO(User testUser) {
        this.ip = "123.456.789.012";
        this.user = testUser;
        this.userId = testUser.getId();
        this.password = "12345";
        this.content = "글 작성 샘플입니다.";
        this.images = new TestImage().makeImages(1);
    }

    public FeedDTO.Request makeUserFeedDTO() {
        return new FeedDTO.Request(true, ip, userId, content, images);
    }

    public FeedDTO.Request makeNonUserFeedDTOO() {
        return new FeedDTO.Request(false, ip, password, content, images);
    }
}
