package com.gloomy.server.application.feed;

import com.gloomy.server.domain.feed.Feed;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.List;

public class FeedDTO {
    @Getter
    @Setter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Request {
        @NotNull
        private Boolean isUser;
        @Pattern(regexp = "[0-9]{3}.[0-9]{3}.[0-9]{3}.[0-9]{3}")
        private String ip;
        private Long userId;
        private String password;
        @NotBlank
        private String content;
        private List<MultipartFile> images;

        public Request(Boolean isUser, String ip, Long userId, String content, List<MultipartFile> images) {
            this.isUser = isUser;
            this.ip = ip;
            this.userId = userId;
            this.content = content;
            this.images = images;
        }

        public Request(Boolean isUser, String ip, String password, String content, List<MultipartFile> images) {
            this.isUser = isUser;
            this.ip = ip;
            this.password = password;
            this.content = content;
            this.images = images;
        }
    }

    @Getter
    public static class Response {
        private Long id;
        private Boolean isUser;
        private String ip;
        private Long userId;
        private String password;
        private String content;

        @Builder(builderClassName = "userFeedResponse", builderMethodName = "userFeedResponse")
        public Response(Long id, Boolean isUser, String ip, Long userId, String content) {
            this.id = id;
            this.isUser = isUser;
            this.ip = ip;
            this.userId = userId;
            this.content = content;
        }

        @Builder(builderClassName = "nonUserFeedResponse", builderMethodName = "nonUserFeedResponse")
        public Response(Long id, Boolean isUser, String ip, String password, String content) {
            this.id = id;
            this.isUser = isUser;
            this.ip = ip;
            this.password = password;
            this.content = content;
        }

        public static Response of(Feed feed) {
            if (feed.getIsUser().getIsUser()) {
                return userFeedResponse()
                        .id(feed.getId())
                        .isUser(feed.getIsUser().getIsUser())
                        .ip(feed.getIp().getIp())
                        .userId(feed.getUserId().getId())
                        .content(feed.getContent().getContent())
                        .build();
            }
            return new nonUserFeedResponse()
                    .id(feed.getId())
                    .isUser(feed.getIsUser().getIsUser())
                    .ip(feed.getIp().getIp())
                    .password(feed.getPassword().getPassword())
                    .content(feed.getContent().getContent())
                    .build();
        }
    }
}
