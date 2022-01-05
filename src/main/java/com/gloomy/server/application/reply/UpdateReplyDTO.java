package com.gloomy.server.application.reply;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

public class UpdateReplyDTO {
    @Getter
    @Setter
    @NoArgsConstructor
    public static class Request {
        @NotBlank
        private String content;
    }
}
