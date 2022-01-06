package com.gloomy.server.application.reply;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

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
}
