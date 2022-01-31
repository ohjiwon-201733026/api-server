package com.gloomy.server.application.feed;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class UpdateFeedDTO {
    @Getter
    @Setter
    @NoArgsConstructor
    public static class Request {
        private String password;
        private String category;
        private String title;
        private String content;
    }
}
