package com.gloomy.server.domain.common;

import lombok.Getter;

@Getter
public enum Status {
    ACTIVE(1, "활성"),
    INACTIVE(2, "비활성");

    private Integer statusCode;
    private String statusName;

    Status(Integer statusCode, String statusName) {
        this.statusCode = statusCode;
        this.statusName = statusName;
    }
}
