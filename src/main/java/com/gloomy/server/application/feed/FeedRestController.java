package com.gloomy.server.application.feed;

import com.gloomy.server.application.comment.CommentService;
import com.gloomy.server.application.core.response.RequestContext;
import com.gloomy.server.application.feedlike.FeedLikeService;
import com.gloomy.server.application.image.ImageService;
import com.gloomy.server.application.image.Images;
import com.gloomy.server.domain.comment.Comment;
import com.gloomy.server.domain.feed.Feed;
import com.gloomy.server.domain.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/feed")
public class FeedRestController {
    private final UserService userService;
    private final FeedService feedService;
    private final ImageService imageService;
    private final FeedLikeService feedLikeService;
    private final CommentService commentService;
    private final RequestContext requestContext;

    public FeedRestController(UserService userService, FeedService feedService, ImageService imageService, FeedLikeService feedLikeService, CommentService commentService, RequestContext requestContext) {
        this.userService = userService;
        this.feedService = feedService;
        this.imageService = imageService;
        this.feedLikeService = feedLikeService;
        this.commentService = commentService;
        this.requestContext = requestContext;
    }

    @PostMapping(value = "")
    public FeedDTO.Response createFeed(@Validated @RequestBody FeedDTO.Request feedDTO) {
        requestContext.setRequestBody(feedDTO);
        Long userId = userService.getMyInfo();
        Feed createFeed = feedService.createFeed(userId, feedDTO);
        return makeFeedDTOResponse(createFeed);
    }

    @PutMapping(value = "/{feedId}")
    public FeedDTO.Response createUndefinedFeed(@PathVariable Long feedId, @RequestBody FeedDTO.Request feedDTO) {
        requestContext.setRequestBody(feedDTO);
        Long userId = userService.getMyInfo();
        Feed createFeed = feedService.createUndefinedFeed(feedId, userId, feedDTO);
        return makeFeedDTOResponse(createFeed);
    }

    @GetMapping(value = "")
    public Page<FeedDTO.Response> getAllActiveFeeds(@PageableDefault(size = 10) Pageable pageable, @RequestParam(required = false) String category) {
        Long userId = userService.getMyInfo();
        Page<Feed> allFeeds = feedService.findAllActiveFeeds(pageable, userId, category);
        return makeResult(allFeeds);
    }

    @GetMapping("/{feedId}")
    public FeedDTO.Response getFeed(@PathVariable Long feedId) {
        Feed foundFeed = feedService.findOneFeed(feedId);
        return makeFeedDTOResponse(foundFeed);
    }

    @GetMapping("/user")
    public Page<FeedDTO.Response> getUserFeeds(@PageableDefault(size = 10) Pageable pageable) {
        Long userId = userService.getMyInfo();
        Page<Feed> userFeeds = feedService.findUserFeeds(pageable, userId);
        return makeResult(userFeeds);
    }

    @PostMapping(value = "/{feedId}")
    public FeedDTO.Response updateFeed(@PathVariable Long feedId, @RequestBody UpdateFeedDTO.Request updateFeedDTO) {
        requestContext.setRequestBody(updateFeedDTO);
        Feed updatedFeed = feedService.updateOneFeed(feedId, updateFeedDTO);
        return makeFeedDTOResponse(updatedFeed);
    }

    @DeleteMapping("/{feedId}")
    public void deleteFeed(@PathVariable Long feedId) {
        feedService.deleteFeed(feedId);
    }

    private Page<FeedDTO.Response> makeResult(Page<Feed> allFeeds) {
        List<FeedDTO.Response> result = new ArrayList<>();
        for (Feed feed : allFeeds.getContent()) {
            result.add(makeFeedDTOResponse(feed));
        }
        return new PageImpl<>(result);
    }

    private FeedDTO.Response makeFeedDTOResponse(Feed feed) {
        Images activeImages = imageService.findAllActiveImages(feed);
        Integer likeCount = feedLikeService.getFeedLikeCount(feed);
        List<Comment> allComments = commentService.findAllComments(feed.getId());
        return FeedDTO.Response.of(feed, activeImages, likeCount, allComments.size());
    }
}
