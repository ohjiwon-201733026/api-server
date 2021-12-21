package com.gloomy.server.domain.image;

import com.gloomy.server.domain.feed.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import javax.persistence.*;
import java.util.Objects;

@Getter
@Entity
public class Image {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "feed_id")
    private Feed feedId;

    @Embedded
    private ImageURL imageUrl;

    @Column(name = "status", nullable = false)
    private IMAGE_STATUS status;

    private Image() {
    }

    @Builder(access = AccessLevel.PRIVATE)
    private Image(Feed feedId, ImageURL imageUrl, IMAGE_STATUS status) {
        this.feedId = feedId;
        this.imageUrl = imageUrl;
        this.status = status;
    }

    public static Image of(Feed feedId, String imageUrl) {
        return Image.builder()
                .feedId(feedId)
                .imageUrl(new ImageURL(imageUrl))
                .status(IMAGE_STATUS.ACTIVE)
                .build();
    }

    public void setStatus(IMAGE_STATUS status) {
        this.status = status;
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
