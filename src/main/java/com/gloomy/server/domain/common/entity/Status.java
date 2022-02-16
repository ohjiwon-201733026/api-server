package com.gloomy.server.domain.common.entity;

import com.gloomy.server.domain.common.EnumModel;

public enum Status implements EnumModel {
    ACTIVE("활성"),
    INACTIVE("비활성"),
    INVISIBLE("숨김");

    private String title;

    Status(String title) {
        this.title = title;
    }

    public static Status active() {
        return ACTIVE;
    }

    public static Status inactive() {
        return INACTIVE;
    }

    public static Status invisible() {
        return INVISIBLE;
    }

    public String getCode() {
        return name();
    }

    public String getTitle() {
        return title;
    }
}
