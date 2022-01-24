package com.gloomy.server.domain.image;

import com.gloomy.server.domain.common.*;
import com.gloomy.server.domain.feed.Feed;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Entity
public class Image extends BaseEntity {
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "feed_id")
    private Feed feedId;

    @Embedded
    private ImageURL imageUrl;

    protected Image() {
    }

    @Builder(access = AccessLevel.PRIVATE)
    private Image(Feed feedId, ImageURL imageUrl, Status status, CreatedAt createdAt, UpdatedAt updatedAt, DeletedAt deletedAt) {
        this.feedId = feedId;
        this.imageUrl = imageUrl;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
    }

    public static Image of(Feed feedId, String imageUrl) {
        LocalDateTime now = LocalDateTime.now();
        return Image.builder()
                .feedId(feedId)
                .imageUrl(new ImageURL(imageUrl))
                .status(Status.ACTIVE)
                .createdAt(new CreatedAt())
                .updatedAt(new UpdatedAt())
                .deletedAt(new DeletedAt())
                .build();
    }

    public void delete() {
        this.status = Status.INACTIVE;
        this.deletedAt.setDeletedAt(LocalDateTime.now());
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Image) {
            Image targetImage = (Image) o;
            return Objects.equals(id, targetImage.id)
                    && Objects.equals(feedId.getId(), targetImage.feedId.getId())
                    && Objects.equals(imageUrl.getImageUrl(), targetImage.imageUrl.getImageUrl());
        }
        return false;
    }
}
