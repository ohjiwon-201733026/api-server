package com.gloomy.server.application.feed;

import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.ArrayList;

public class FeedDTO {
    @Getter
    public static class Request {
        @NotNull
        private Boolean isUser;
        @Pattern(regexp = "[[0-9]{3}.[0-9]{3}.[0-9]{3}.[0-9]{3}")
        private String ip;
        private Long userId;
        private String password;
        @NotBlank
        private String content;
        private ArrayList<MultipartFile> images;

        public Request(Boolean isUser, String ip, Long userId, String content, ArrayList<MultipartFile> images) {
            this.isUser = isUser;
            this.ip = ip;
            this.userId = userId;
            this.content = content;
            this.images = images;
        }

        public Request(Boolean isUser, String ip, String password, String content, ArrayList<MultipartFile> images) {
            this.isUser = isUser;
            this.ip = ip;
            this.password = password;
            this.content = content;
            this.images = images;
        }
    }
}
