package com.gloomy.server.domain.common;

import lombok.Getter;

@Getter
public enum Status {
    ACTIVE("활성"),
    INACTIVE("비활성");

    private String statusName;

    Status(String statusName) {
        this.statusName = statusName;
    }
}
