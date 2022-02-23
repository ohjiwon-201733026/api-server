package com.gloomy.server.domain.feedlike;

import com.gloomy.server.domain.common.entity.*;
import com.gloomy.server.domain.feed.*;
import com.gloomy.server.domain.user.User;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = false)
@Getter
@Entity
public class FeedLike extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feed_id", nullable = false)
    private Feed feedId;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User userId;

    @Embedded
    private Ip ip;

    protected FeedLike() {
    }

    @Builder
    public FeedLike(Feed feedId, User userId, Ip ip, Status status, CreatedAt createdAt, UpdatedAt updatedAt, DeletedAt deletedAt) {
        this.feedId = feedId;
        this.userId = userId;
        this.ip = ip;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
    }

    public static FeedLike of(Feed feedId, User userId) {
        return builder()
                .feedId(feedId)
                .userId(userId)
                .ip(new Ip("111.111.111.111"))
                .status(Status.active())
                .createdAt(new CreatedAt())
                .updatedAt(new UpdatedAt())
                .deletedAt(new DeletedAt())
                .build();
    }

    public static FeedLike from(Feed feedId) {
        return builder()
                .feedId(feedId)
                .userId(null)
                .ip(new Ip("111.111.111.111"))
                .status(Status.active())
                .createdAt(new CreatedAt())
                .updatedAt(new UpdatedAt())
                .deletedAt(new DeletedAt())
                .build();
    }
}
