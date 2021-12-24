package com.gloomy.server.application.feed;

import com.gloomy.server.application.comment.CommentService;
import com.gloomy.server.application.image.ImageService;
import com.gloomy.server.application.image.Images;
import com.gloomy.server.domain.comment.Comment;
import com.gloomy.server.domain.feed.Feed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/feed")
public class FeedRestController {
    private final FeedService feedService;
    private final ImageService imageService;
    private final CommentService commentService;

    public FeedRestController(FeedService feedService, ImageService imageService, CommentService commentService) {
        this.feedService = feedService;
        this.imageService = imageService;
        this.commentService = commentService;
    }

    @PostMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createFeed(@Validated @ModelAttribute FeedDTO.Request feedDTO) {
        try {
            Feed createFeed = feedService.createFeed(feedDTO);
            return new ResponseEntity<>(makeFeedDTOResponse(createFeed), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(makeErrorMessage(e.getMessage(), feedDTO.toString()), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("")
    public Object getAllFeeds(@PageableDefault(size = 10) Pageable pageable) {
        try {
            Page<Feed> allFeeds = feedService.findAllFeeds(pageable);
            return new ResponseEntity<>(makeResult(allFeeds), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(makeErrorMessage(e.getMessage(), null), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{feedId}")
    public Object getFeed(@PathVariable Long feedId) {
        try {
            Feed foundFeed = feedService.findOneFeed(feedId);
            return new ResponseEntity<>(makeFeedDTOResponse(foundFeed), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(makeErrorMessage(e.getMessage(), feedId), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/user/{userId}")
    public Object getUserFeeds(@PathVariable Long userId, @PageableDefault(size = 10) Pageable pageable) {
        try {
            Page<Feed> userFeeds = feedService.findUserFeeds(pageable, userId);
            return new ResponseEntity<>(makeResult(userFeeds), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(makeErrorMessage(e.getMessage(), userId), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(value = "/{feedId}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Object updateFeed(@PathVariable Long feedId, @ModelAttribute UpdateFeedDTO.Request feedDTO) {
        try {
            Feed updatedFeed = feedService.updateOneFeed(feedId, feedDTO);
            return new ResponseEntity<>(makeFeedDTOResponse(updatedFeed), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(makeErrorMessage(e.getMessage(), feedDTO.toString()), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{feedId}")
    public Object deleteFeed(@PathVariable Long feedId) {
        try {
            feedService.deleteFeed(feedId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    private Page<FeedDTO.Response> makeResult(Page<Feed> allFeeds) {
        List<FeedDTO.Response> result = new ArrayList<>();
        for (Feed feed : allFeeds.getContent()) {
            result.add(makeFeedDTOResponse(feed));
        }
        return new PageImpl<>(result);
    }

    private FeedDTO.Response makeFeedDTOResponse(Feed feed) {
        Images activeImages = imageService.findActiveImages(feed);
        List<Comment> allComments = commentService.findAllComments(feed.getId());
        return FeedDTO.Response.of(feed, activeImages, allComments.size());
    }

    private List<String> makeErrorMessage(String errorMessage, Object errorObject) {
        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(errorMessage);
        errorMessages.add(errorObject.toString());
        return errorMessages;
    }
}
