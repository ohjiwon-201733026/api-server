package com.gloomy.server.application.feed;

import com.gloomy.server.application.image.Images;
import com.gloomy.server.domain.feed.Feed;
import com.gloomy.server.domain.image.Image;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

public class FeedDTO {
    @Getter
    @Setter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Request {
        @NotNull
        private Boolean isUser;
        private Long userId;
        private String password;
        @NotBlank
        private String content;
        private List<MultipartFile> images;

        public Request(Boolean isUser, Long userId, String content, List<MultipartFile> images) {
            this.isUser = isUser;
            this.userId = userId;
            this.content = content;
            this.images = images;
        }

        public Request(Boolean isUser, String password, String content, List<MultipartFile> images) {
            this.isUser = isUser;
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
        private Integer likeCount;
        private List<String> imageURLs;
        private Integer commentCount;

        @Builder(builderClassName = "userFeedResponse", builderMethodName = "userFeedResponse")
        public Response(Long id, Boolean isUser, String ip, Long userId, String content, Integer likeCount, List<String> imageURLs, Integer commentCount) {
            this.id = id;
            this.isUser = isUser;
            this.ip = ip;
            this.userId = userId;
            this.content = content;
            this.likeCount = likeCount;
            this.imageURLs = imageURLs;
            this.commentCount = commentCount;
        }

        @Builder(builderClassName = "nonUserFeedResponse", builderMethodName = "nonUserFeedResponse")
        public Response(Long id, Boolean isUser, String ip, String password, String content, Integer likeCount, List<String> imageURLs, Integer commentCount) {
            this.id = id;
            this.isUser = isUser;
            this.ip = ip;
            this.password = password;
            this.content = content;
            this.likeCount = likeCount;
            this.imageURLs = imageURLs;
            this.commentCount = commentCount;
        }

        public static Response of(Feed feed, Images images, Integer commentCount) {
            List<String> imageURLs = new ArrayList<>();
            for (Image image : images.getImages()) {
                imageURLs.add(image.getImageUrl().getImageUrl());
            }
            if (feed.getIsUser().getIsUser()) {
                return userFeedResponse()
                        .id(feed.getId())
                        .isUser(feed.getIsUser().getIsUser())
                        .ip(feed.getIp().getIp())
                        .userId(feed.getUserId().getId())
                        .content(feed.getContent().getContent())
                        .likeCount(feed.getLikeCount().getLikeCount())
                        .imageURLs(imageURLs)
                        .commentCount(commentCount)
                        .build();
            }
            return new nonUserFeedResponse()
                    .id(feed.getId())
                    .isUser(feed.getIsUser().getIsUser())
                    .ip(feed.getIp().getIp())
                    .password(feed.getPassword().getPassword())
                    .content(feed.getContent().getContent())
                    .likeCount(feed.getLikeCount().getLikeCount())
                    .imageURLs(imageURLs)
                    .commentCount(commentCount)
                    .build();
        }
    }
}
