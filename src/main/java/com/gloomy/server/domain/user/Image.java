package com.gloomy.server.domain.user;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Image {

    @Column(name = "image")
    private String image;
}
