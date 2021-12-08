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

    private Image() {
    }

    @Builder(access = AccessLevel.PRIVATE)
    private Image(Feed feedId, ImageURL imageUrl) {
        this.feedId = feedId;
        this.imageUrl = imageUrl;
    }

    public static Image of(Feed feedId, String imageUrl) {
        return Image.builder()
                .feedId(feedId)
                .imageUrl(new ImageURL(imageUrl))
                .build();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Image) {
            Image targetImage = (Image) o;
            return Objects.equals(id, targetImage.id) && Objects.equals(feedId.getId(), targetImage.feedId.getId()) && Objects.equals(imageUrl.getImageUrl(), targetImage.imageUrl.getImageUrl());
        }
        return false;
    }
}
