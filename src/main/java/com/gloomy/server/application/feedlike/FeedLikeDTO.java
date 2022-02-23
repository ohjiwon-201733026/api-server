package com.gloomy.server.application.feedlike;

import com.gloomy.server.domain.feedlike.FeedLike;
import lombok.*;

import javax.validation.constraints.NotNull;

public class FeedLikeDTO {
    @Getter
    @Setter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Request {
        @NotNull
        private Long feedId;

        public Request(Long feedId) {
            this.feedId = feedId;
        }
    }

    @Getter
    public static class Response {
        private final Long id;
        private final Long feedId;
        private final Long userId;
        private final String ip;
        private final String status;
        private final String createdAt;
        private final String updatedAt;
        private final String deletedAt;

        @Builder
        public Response(Long id, Long feedId, Long userId, String ip, String status, String createdAt, String updatedAt, String deletedAt) {
            this.id = id;
            this.feedId = feedId;
            this.userId = userId;
            this.ip = ip;
            this.status = status;
            this.createdAt = createdAt;
            this.updatedAt = updatedAt;
            this.deletedAt = deletedAt;
        }

        public static Response from(FeedLike feedLike) {
            if (feedLike.getUserId() != null) {
                return builder()
                        .id(feedLike.getId())
                        .feedId(feedLike.getFeedId().getId())
                        .userId(feedLike.getUserId().getId())
                        .ip(feedLike.getIp().getIp())
                        .status(feedLike.getStatus().toString())
                        .createdAt(feedLike.getCreatedAt().getCreatedAt().toString())
                        .updatedAt(feedLike.getUpdatedAt().getUpdatedAt().toString())
                        .deletedAt(feedLike.getDeletedAt().getDeletedAt().toString())
                        .build();
            }
            return builder()
                    .id(feedLike.getId())
                    .feedId(feedLike.getFeedId().getId())
                    .userId(null)
                    .ip(feedLike.getIp().getIp())
                    .status(feedLike.getStatus().toString())
                    .createdAt(feedLike.getCreatedAt().getCreatedAt().toString())
                    .updatedAt(feedLike.getUpdatedAt().getUpdatedAt().toString())
                    .deletedAt(feedLike.getDeletedAt().getDeletedAt().toString())
                    .build();
        }
    }
}
