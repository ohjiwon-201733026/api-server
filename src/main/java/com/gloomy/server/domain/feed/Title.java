package com.gloomy.server.domain.feed;

import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Getter
@Embeddable
public class Title {
    @Column(name = "title", nullable = false)
    private String title;

    private Title() {
    }

    public Title(String title) {
        this.title = title;
    }
}
