package com.gloomy.server.application.notice.fcm;

import lombok.*;

public class FcmDto {

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    @ToString
    public static class Request{
        private Long userId;
        private String fcmToken;


    }
}
