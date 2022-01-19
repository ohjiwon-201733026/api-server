package com.gloomy.server.application.feed;

import com.gloomy.server.application.image.TestImage;
import com.gloomy.server.domain.user.User;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;

@Slf4j
@Getter
public class TestFeedDTO {
    private final User user;
    private final Long userId;
    private final String password;
    private final String content;
    private ArrayList<MultipartFile> images;

    public TestFeedDTO(User testUser, int imageNum) {
        this.user = testUser;
        this.userId = testUser.getId();
        this.password = "12345";
        this.content = "글 작성 샘플입니다.";
        this.images = new TestImage().makeImages(imageNum);
    }

    public void setImages(int imageNum) {
        this.images = new TestImage().makeImages(imageNum);
    }

    public FeedDTO.Request makeUserFeedDTO() {
        return new FeedDTO.Request(userId, content, images);
    }

    public FeedDTO.Request makeNonUserFeedDTO() {
        return new FeedDTO.Request(password, content, images);
    }

    public static MultiValueMap<String, String> convert(FeedDTO.Request feedDTO) {
        try {
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("content", feedDTO.getContent());
            if (feedDTO.getUserId() != null) {
                params.add("userId", feedDTO.getUserId().toString());
                return params;
            }
            params.add("password", feedDTO.getPassword());
            return params;
        } catch (Exception e) {
            throw new IllegalArgumentException("[TestFeedDTO] 변환 중 오류가 발생했습니다.");
        }
    }

    public static MultiValueMap<String, String> convert(UpdateFeedDTO.Request feedDTO) {
        try {
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            if (feedDTO.getPassword() != null) {
                params.add("password", feedDTO.getPassword());
            }
            if (feedDTO.getContent() != null) {
                params.add("content", feedDTO.getContent());
            }
            return params;
        } catch (Exception e) {
            throw new IllegalArgumentException("[TestFeedDTO] 변환 중 오류가 발생했습니다.");
        }
    }
}
