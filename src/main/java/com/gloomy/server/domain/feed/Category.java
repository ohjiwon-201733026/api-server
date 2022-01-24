package com.gloomy.server.domain.feed;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum Category {
    ALL("카테고리");

    private String title;

    Category(String title) {
        this.title = title;
    }

    @JsonCreator
    public static Category from(String category) {
        return Category.valueOf(category.toUpperCase());
    }

    public static List<CategoryValue> getAllCategories() {
        return Arrays.stream(Category.values())
                .map(CategoryValue::new).collect(Collectors.toList());
    }

    public String getCode() {
        return name();
    }

    public String getTitle() {
        return title;
    }
}
