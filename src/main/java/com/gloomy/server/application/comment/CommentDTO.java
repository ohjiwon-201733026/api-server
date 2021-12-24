package com.gloomy.server.application.comment;

import com.gloomy.server.domain.comment.Comment;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Objects;

public class CommentDTO {
    @Getter
    @Setter
    @NoArgsConstructor
    public static class Request {
        @NotBlank
        private String content;
        @NotNull
        private Long feedId;
        private Long userId;
        private String password;

        public Request(String content, Long feedId, Long userId) {
            this.content = content;
            this.feedId = feedId;
            this.userId = userId;
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

        @Builder(builderClassName = "userCommentResponse", builderMethodName = "userCommentResponse")
        public Response(Long id, String content, Long feedId, Long userId) {
            this.id = id;
            this.content = content;
            this.feedId = feedId;
            this.userId = userId;
        }

        @Builder(builderClassName = "nonUserCommentResponse", builderMethodName = "nonUserCommentResponse")
        public Response(Long id, String content, Long feedId, String password) {
            this.id = id;
            this.content = content;
            this.feedId = feedId;
            this.password = password;
        }

        public static CommentDTO.Response of(Comment comment) {
            if (Objects.nonNull(comment.getUserId())) {
                return userCommentResponse()
                        .id(comment.getId())
                        .content(comment.getContent().getContent())
                        .feedId(comment.getFeedId().getId())
                        .userId(comment.getUserId().getId())
                        .build();
            }
            return nonUserCommentResponse()
                    .id(comment.getId())
                    .content(comment.getContent().getContent())
                    .feedId(comment.getFeedId().getId())
                    .password(comment.getPassword().getPassword())
                    .build();
        }
    }
}
