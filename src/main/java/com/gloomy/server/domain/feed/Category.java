package com.gloomy.server.domain.feed;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.gloomy.server.domain.common.EnumModel;

public enum Category implements EnumModel {
    ALL("모든 고민"),
    CHAT("잡담"),
    FAMILY("가족"),
    FRIEND("친구"),
    COMPANY("직장/이직"),
    EMPLOY("취업"),
    STUDY("학업/시험"),
    HETEROLOVE("연애(이성애)"),
    HOMOLOVE("연애(동성애)"),
    UNDEFINED("미정");

    private String title;

    Category(String title) {
        this.title = title;
    }

    @JsonCreator
    public static Category from(String category) {
        return Category.valueOf(category.toUpperCase());
    }

    public String getCode() {
        return name();
    }

    public String getTitle() {
        return title;
    }
}
