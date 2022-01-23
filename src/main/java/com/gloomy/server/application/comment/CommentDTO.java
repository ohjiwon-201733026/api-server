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
        private Long id;
        private String content;
        private Long feedId;
        private Long userId;
        private String password;

        @Builder
        public Response(Long id, String content, Long feedId, Long userId, String password) {
            this.id = id;
            this.content = content;
            this.feedId = feedId;
            this.userId = userId;
            this.password = password;
        }

        public static CommentDTO.Response of(Comment comment) {
            if (comment.getUserId() != null) {
                return builder()
                        .id(comment.getId())
                        .content(comment.getContent().getContent())
                        .feedId(comment.getFeedId().getId())
                        .userId(comment.getUserId().getId())
                        .password(null)
                        .build();
            }
            return builder()
                    .id(comment.getId())
                    .content(comment.getContent().getContent())
                    .feedId(comment.getFeedId().getId())
                    .userId(null)
                    .password(comment.getPassword().getPassword())
                    .build();
        }
    }
}
