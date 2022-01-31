package com.gloomy.server.application.image;

import com.gloomy.server.application.feed.FeedService;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/{feedId}")
    public ImageDTO.Response getAllActiveFeedImages(@PathVariable Long feedId) {
        Images allActiveImages = feedService.findAllActiveImages(feedId);
        return makeImageDTOResponse(allActiveImages);
    }

    private ImageDTO.Response makeImageDTOResponse(Images images) {
        return ImageDTO.Response.of(images);
    }
}
