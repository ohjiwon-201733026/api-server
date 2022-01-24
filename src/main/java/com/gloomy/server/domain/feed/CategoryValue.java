package com.gloomy.server.domain.feed;

import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class CategoryValue {
    private final String code;
    private final String title;

    public CategoryValue(Category category) {
        this.code = category.name();
        this.title = category.getTitle();
    }
}
