package com.gloomy.server.domain.feed;

import lombok.Getter;

@Getter
public enum FeedStatus {
    ACTIVE(1, "활성"),
    INACTIVE(2, "비활성");

    private Integer statusCode;
    private String statusName;

    FeedStatus(Integer statusCode, String statusName) {
        this.statusCode = statusCode;
        this.statusName = statusName;
    }
}
