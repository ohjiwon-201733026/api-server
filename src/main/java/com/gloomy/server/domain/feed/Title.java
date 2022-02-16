package com.gloomy.server.domain.feed;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@EqualsAndHashCode
@Getter
@Embeddable
public class Title {
    @Column(name = "title", nullable = false)
    private String title;

    protected Title() {
    }

    public Title(String title) {
        this.title = title;
    }

    void setTitle(String title) {
        this.title = title;
    }
}
