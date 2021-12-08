package com.gloomy.server.domain.image;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import javax.persistence.*;

@Getter
@Entity
public class Image {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private ImageURL imageUrl;

    private Image() {
    }

    @Builder(access = AccessLevel.PRIVATE)
    private Image(ImageURL imageUrl) {
        this.imageUrl = imageUrl;
    }

    public static Image of(String imageUrl) {
        return Image.builder()
                .imageUrl(new ImageURL(imageUrl))
                .build();
    }
}
