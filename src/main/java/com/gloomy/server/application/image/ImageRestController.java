package com.gloomy.server.application.image;

import com.gloomy.server.application.feed.FeedService;
import com.gloomy.server.domain.user.UserService;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/feed/image")
public class ImageRestController {
    private final FeedService feedService;
    private final UserService userService;

    public ImageRestController(FeedService feedService, UserService userService) {
        this.feedService = feedService;
        this.userService = userService;
    }

    @PostMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ImageDTO.Response createFeedImages(@Validated @ModelAttribute ImageDTO.Request imageDTO) {
        Long userId = userService.getMyInfo();
        Images images = feedService.uploadImages(imageDTO.getFeedId(), userId, imageDTO.getImages());
        return makeImageDTOResponse(images);
    }

    @GetMapping("/{feedId}")
    public ImageDTO.Response getAllActiveFeedImages(@PathVariable Long feedId) {
        Images allActiveImages = feedService.findAllActiveImages(feedId);
        return makeImageDTOResponse(allActiveImages);
    }

    @PostMapping("/{feedId}")
    public ImageDTO.Response updateFeedImages(@PathVariable Long feedId, @RequestParam List<MultipartFile> images) {
        Images updatedImages = feedService.updateImages(feedId, images);
        return makeImageDTOResponse(updatedImages);
    }

    @DeleteMapping("/{feedId}")
    public void deleteFeedImages(@PathVariable Long feedId) {
        feedService.deleteImages(feedId);
    }

    private ImageDTO.Response makeImageDTOResponse(Images images) {
        return ImageDTO.Response.of(images);
    }
}
