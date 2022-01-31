package com.gloomy.server.application.image;

import com.gloomy.server.application.feed.FeedService;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/feed/image")
public class ImageRestController {
    private final FeedService feedService;

    public ImageRestController(FeedService feedService) {
        this.feedService = feedService;
    }

    @PostMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ImageDTO.Response createFeedImages(@Validated @ModelAttribute ImageDTO.Request imageDTO) {
        Images images = feedService.uploadImages(imageDTO.getFeedId(), imageDTO.getImages());
        return makeImageDTOResponse(images);
    }

    private ImageDTO.Response makeImageDTOResponse(Images images) {
        return ImageDTO.Response.of(images);
    }
}
