package com.gloomy.server.application.image;

import com.gloomy.server.domain.image.Image;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Images {
    private List<Image> images;

    public Images() {
        this.images = new ArrayList<>();
    }

    public Images(List<Image> images) {
        this.images = images;
    }

    public void addImage(Image image) {
        images.add(image);
    }

    public int getSize() {
        return images.size();
    }
}
