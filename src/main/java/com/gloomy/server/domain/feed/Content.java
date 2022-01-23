package com.gloomy.server.domain.feed;

import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Getter
@Embeddable
public class Content {
    @Column(name = "content", nullable = false)
    private String content;

    protected Content() {
    }

    public Content(String content) {
        this.content = content;
    }
}
