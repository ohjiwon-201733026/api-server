package com.gloomy.server.domain.image;

public enum ImageStatus {
    ACTIVE(1, "활성"),
    INACTIVE(2, "비활성");

    private Integer statusCode;
    private String statusName;

    ImageStatus(Integer statusCode, String statusName) {
        this.statusCode = statusCode;
        this.statusName = statusName;
    }
}
