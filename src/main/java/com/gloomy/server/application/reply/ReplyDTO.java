package com.gloomy.server.application.reply;

import com.gloomy.server.domain.reply.Reply;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Objects;

public class ReplyDTO {
    @Getter
    @Setter
    @NoArgsConstructor
    public static class Request {
        @NotBlank
        private String content;
        @NotNull
        private Long commentId;
        private Long userId;
        private String password;

        public Request(String content, Long commentId, Long userId) {
            this.content = content;
            this.commentId = commentId;
            this.userId = userId;
        }

        public Request(String content, Long commentId, String password) {
            this.content = content;
            this.commentId = commentId;
            this.password = password;
        }
    }

    @Getter
    public static class Response {
        private Long id;
        private String content;
        private Long commentId;
        private Long userId;
        private String password;

        @Builder(builderClassName = "userReplyResponse", builderMethodName = "userReplyResponse")
        public Response(Long id, String content, Long commentId, Long userId) {
            this.id = id;
            this.content = content;
            this.commentId = commentId;
            this.userId = userId;
        }

        @Builder(builderClassName = "nonUserReplyResponse", builderMethodName = "nonUserReplyResponse")
        public Response(Long id, String content, Long commentId, String password) {
            this.id = id;
            this.content = content;
            this.commentId = commentId;
            this.password = password;
        }

        public static ReplyDTO.Response of(Reply reply) {
            if (Objects.nonNull(reply.getUserId())) {
                return userReplyResponse()
                        .id(reply.getId())
                        .content(reply.getContent().getContent())
                        .commentId(reply.getCommentId().getId())
                        .userId(reply.getUserId().getId())
                        .build();
            }
            return nonUserReplyResponse()
                    .id(reply.getId())
                    .content(reply.getContent().getContent())
                    .commentId(reply.getCommentId().getId())
                    .password(reply.getPassword().getPassword())
                    .build();
        }
    }
}
