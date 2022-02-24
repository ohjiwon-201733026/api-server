package com.gloomy.server.domain.notice;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.gloomy.server.domain.common.EnumModel;
import com.gloomy.server.domain.feed.Category;
import org.apache.commons.lang3.EnumUtils;

public enum Type implements EnumModel {
    COMMENT("댓글"),
    REPLY("대댓글"),
    LIKE("공감");

    private String title;

    Type(String title) {
        this.title = title;
    }

    @JsonCreator
    public static Type from(String type) {
        return Type.valueOf(type.toUpperCase());
    }

    public String getCode() {
        return name();
    }

    public String getTitle() {
        return title;
    }

    public static Boolean isValidType(String type) {
        return EnumUtils.isValidEnumIgnoreCase(Type.class, type);
    }
}
