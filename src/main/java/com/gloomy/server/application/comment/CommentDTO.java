package com.gloomy.server.application.comment;

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
}
