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
        private String password;

        public Request(String content, Long commentId) {
            this.content = content;
            this.commentId = commentId;
        }

        public Request(String content, Long commentId, String password) {
            this.content = content;
            this.commentId = commentId;
            this.password = password;
        }
    }

    @Getter
    public static class Response {
        private final Long id;
        private final String content;
        private final Long commentId;
        private final Long userId;
        private final String nickname;
        private final String password;
        private final String status;
        private final String createdAt;
        private final String updatedAt;
        private final String deletedAt;

        @Builder
        public Response(Long id, String content, Long commentId, Long userId, String nickName, String password, String status, String createdAt, String updatedAt, String deletedAt) {
            this.id = id;
            this.content = content;
            this.commentId = commentId;
            this.userId = userId;
            this.nickname = nickName;
            this.password = password;
            this.status = status;
            this.createdAt = createdAt;
            this.updatedAt = updatedAt;
            this.deletedAt = deletedAt;
        }

        public static ReplyDTO.Response of(Reply reply) {
            if (Objects.nonNull(reply.getUserId())) {
                return builder()
                        .id(reply.getId())
                        .content(reply.getContent().getContent())
                        .commentId(reply.getCommentId().getId())
                        .userId(reply.getUserId().getId())
                        .nickName(null)
                        .password(null)
                        .status(reply.getStatus().toString())
                        .createdAt(reply.getCreatedAt().getCreatedAt().toString())
                        .updatedAt(reply.getUpdatedAt().getUpdatedAt().toString())
                        .deletedAt(reply.getDeletedAt().getDeletedAt().toString())
                        .build();
            }
            return builder()
                    .id(reply.getId())
                    .content(reply.getContent().getContent())
                    .commentId(reply.getCommentId().getId())
                    .userId(null)
                    .nickName(reply.getNonUser().getNickname().getNickname())
                    .password(reply.getNonUser().getPassword().getPassword())
                    .status(reply.getStatus().toString())
                    .createdAt(reply.getCreatedAt().getCreatedAt().toString())
                    .updatedAt(reply.getUpdatedAt().getUpdatedAt().toString())
                    .deletedAt(reply.getDeletedAt().getDeletedAt().toString())
                    .build();
        }
    }
}
