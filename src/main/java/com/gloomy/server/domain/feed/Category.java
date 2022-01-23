package com.gloomy.server.domain.feed;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

import javax.persistence.Embeddable;
import java.util.Locale;

@Getter
public enum Category {
    ALL(1, "카테고리");

    private Integer categoryCode;
    private String categoryName;

    Category(Integer categoryCode, String categoryName) {
        this.categoryCode = categoryCode;
        this.categoryName = categoryName;
    }

    @JsonCreator
    public static Category from(String category) {
        return Category.valueOf(category.toUpperCase());
    }
}
