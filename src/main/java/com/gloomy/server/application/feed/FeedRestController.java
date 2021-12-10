package com.gloomy.server.application.feed;

import com.gloomy.server.core.response.ErrorResponse;
import com.gloomy.server.core.response.RestResponse;
import com.gloomy.server.domain.feed.Feed;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/feed")
public class FeedRestController {
    private final FeedService feedService;

    public FeedRestController(FeedService feedService) {
        this.feedService = feedService;
    }

    @PostMapping("")
    public Object createFeed(@Validated @RequestBody FeedDTO.Request feedDTO) {
        try {
            Feed createFeed = feedService.createFeed(feedDTO);
            return new RestResponse<>(200, "업로드 성공", createFeed);
        } catch (IllegalArgumentException e) {
            return new ErrorResponse(400, "업로드 실패", makeErrorMessage(e.getMessage(), feedDTO.toString()));
        }
    }

    private List<String> makeErrorMessage(String errorMessage, Object errorObject) {
        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(errorMessage);
        errorMessages.add(errorObject.toString());
        return errorMessages;
    }
}
