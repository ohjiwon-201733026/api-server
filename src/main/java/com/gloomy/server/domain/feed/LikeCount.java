package com.gloomy.server.domain.feed;

import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Getter
@Embeddable
public class LikeCount {
    @Column(name = "like_count", nullable = false)
    private Integer likeCount;

    protected LikeCount() {
    }

    public LikeCount(Integer likeCount) {
        this.likeCount = likeCount;
    }
}
