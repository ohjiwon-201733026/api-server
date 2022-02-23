package com.gloomy.server.application.feedlike;

import com.gloomy.server.application.core.response.RequestContext;
import com.gloomy.server.domain.feedlike.FeedLike;
import com.gloomy.server.domain.user.UserService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/like")
public class FeedLikeRestController {
    private final UserService userService;
    private final FeedLikeService feedLikeService;
    private final RequestContext requestContext;

    public FeedLikeRestController(UserService userService, FeedLikeService feedLikeService, RequestContext requestContext) {
        this.userService = userService;
        this.feedLikeService = feedLikeService;
        this.requestContext = requestContext;
    }

    @PostMapping(value = "")
    public FeedLikeDTO.Response createFeedLike(@Validated @RequestBody FeedLikeDTO.Request feedLikeDTO) {
        requestContext.setRequestBody(feedLikeDTO);
        Long userId = userService.getMyInfo();
        FeedLike createdFeedLike = feedLikeService.createFeedLike(userId, feedLikeDTO);
        return makeFeedLikeDTOResponse(createdFeedLike);
    }

    private FeedLikeDTO.Response makeFeedLikeDTOResponse(FeedLike feedLike) {
        return FeedLikeDTO.Response.from(feedLike);
    }
}
