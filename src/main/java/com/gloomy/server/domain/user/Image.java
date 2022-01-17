package com.gloomy.server.domain.user;

import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.OneToOne;

@Embeddable
@Getter
public class Image {

    @Column(name = "image")
    private String image;

    public Image(){
        image="defaultImg";
    }

    public void changeImage(String image){
        this.image=image;
    }
}
