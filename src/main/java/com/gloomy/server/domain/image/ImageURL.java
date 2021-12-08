package com.gloomy.server.domain.image;

import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Getter
@Embeddable
public class ImageURL {
    @Column(name = "image_url", nullable = false)
    private String image;

    public ImageURL() {
    }

    public ImageURL(String image) {
        this.image = image;
    }
}
