package com.gloomy.server.application.comment;

import com.gloomy.server.domain.comment.Comment;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class CommentDTO {
    @Getter
    @Setter
    @NoArgsConstructor
    public static class Request {
        @NotBlank
        private String content;
        @NotNull
        private Long feedId;
        private String password;

        public Request(String content, Long feedId) {
            this.content = content;
            this.feedId = feedId;
        }

        public Request(String content, Long feedId, String password) {
            this.content = content;
            this.feedId = feedId;
            this.password = password;
        }
    }

    @Getter
    public static class Response {
        private final Long id;
        private final String content;
        private final Long feedId;
        private final Long userId;
        private final String password;
        private final String status;
        private final String createdAt;
        private final String updatedAt;
        private final String deletedAt;

        @Builder
        public Response(Long id, String content, Long feedId, Long userId, String password, String status, String createdAt, String updatedAt, String deletedAt) {
            this.id = id;
            this.content = content;
            this.feedId = feedId;
            this.userId = userId;
            this.password = password;
            this.status = status;
            this.createdAt = createdAt;
            this.updatedAt = updatedAt;
            this.deletedAt = deletedAt;
        }

        public static CommentDTO.Response of(Comment comment) {
            if (comment.getUserId() != null) {
                return builder()
                        .id(comment.getId())
                        .content(comment.getContent().getContent())
                        .feedId(comment.getFeedId().getId())
                        .userId(comment.getUserId().getId())
                        .password(null)
                        .status(comment.getStatus().toString())
                        .createdAt(comment.getCreatedAt().getCreatedAt().toString())
                        .updatedAt(comment.getUpdatedAt().getUpdatedAt().toString())
                        .deletedAt(comment.getDeletedAt().getDeletedAt().toString())
                        .build();
            }
            return builder()
                    .id(comment.getId())
                    .content(comment.getContent().getContent())
                    .feedId(comment.getFeedId().getId())
                    .userId(null)
                    .password(comment.getPassword().getPassword())
                    .status(comment.getStatus().toString())
                    .createdAt(comment.getCreatedAt().getCreatedAt().toString())
                    .updatedAt(comment.getUpdatedAt().getUpdatedAt().toString())
                    .deletedAt(comment.getDeletedAt().getDeletedAt().toString())
                    .build();
        }
    }
}
