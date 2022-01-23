package com.gloomy.server.application.feed;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class UpdateFeedDTO {
    @Getter
    @Setter
    @NoArgsConstructor
    public static class Request {
        private String password;
        private String category;
        private String title;
        private String content;
        private List<MultipartFile> images;
    }
}
