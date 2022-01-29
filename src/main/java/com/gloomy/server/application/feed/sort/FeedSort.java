package com.gloomy.server.application.feed.sort;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.gloomy.server.domain.common.EnumModel;

public enum FeedSort implements EnumModel {
    DATE("최신순"), LIKE("인기순");

    private String title;

    FeedSort(String title) {
        this.title = title;
    }

    @JsonCreator
    public static FeedSort from(String sort) {
        return FeedSort.valueOf(sort.toUpperCase());
    }

    public String getCode() {
        return name();
    }

    public String getTitle() {
        return title;
    }
}
