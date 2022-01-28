package com.gloomy.server.application.feed;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.gloomy.server.domain.common.EnumModel;

public enum Sort implements EnumModel {
    DATE("최신순"), LIKE("인기순");

    private String title;

    Sort(String title) {
        this.title = title;
    }

    @JsonCreator
    public static Sort from(String sort) {
        return Sort.valueOf(sort.toUpperCase());
    }

    public String getCode() {
        return name();
    }

    public String getTitle() {
        return title;
    }
}
