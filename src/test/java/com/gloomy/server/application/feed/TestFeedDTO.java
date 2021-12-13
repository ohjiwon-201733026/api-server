package com.gloomy.server.application.feed;

import com.fasterxml.jackson.databind.ObjectMapper;
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
    private final String ip;
    private final User user;
    private final Long userId;
    private final String password;
    private final String content;
    private ArrayList<MultipartFile> images;

    public TestFeedDTO(User testUser, int imageNum) {
        this.ip = "123.456.789.012";
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
        return new FeedDTO.Request(true, ip, userId, content, images);
    }

    public FeedDTO.Request makeNonUserFeedDTO() {
        return new FeedDTO.Request(false, ip, password, content, images);
    }

    public static MultiValueMap<String, String> convert(ObjectMapper objectMapper, FeedDTO.Request feedDTO) {
        try {
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("isUser", feedDTO.getIsUser().toString());
            params.add("ip", feedDTO.getIp());
            params.add("content", feedDTO.getContent());
            if (feedDTO.getIsUser()) {
                params.add("userId", feedDTO.getUserId().toString());
                return params;
            }
            params.add("password", feedDTO.getPassword());
            return params;
        } catch (Exception e) {
            throw new IllegalArgumentException("[TestFeedDTO] 변환 중 오류가 발생했습니다.");
        }
    }
}