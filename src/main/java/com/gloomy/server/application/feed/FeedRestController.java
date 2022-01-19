package com.gloomy.server.application.feed;

import com.gloomy.server.application.comment.CommentService;
import com.gloomy.server.application.core.response.ErrorResponse;
import com.gloomy.server.application.core.response.RestResponse;
import com.gloomy.server.application.image.ImageService;
import com.gloomy.server.application.image.Images;
import com.gloomy.server.domain.comment.Comment;
import com.gloomy.server.domain.feed.Feed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.ResponseEntity.badRequest;
import static org.springframework.http.ResponseEntity.ok;

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
            return ok(new RestResponse<>(200, "피드 생성 성공", makeFeedDTOResponse(createFeed)));
        } catch (IllegalArgumentException e) {
            return badRequest().body(new ErrorResponse(400, "피드 생성 실패", e.getMessage(), feedDTO));
        }
    }

    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAllActiveFeeds(@PageableDefault(size = 10) Pageable pageable) {
        try {
            Page<Feed> allFeeds = feedService.findAllActiveFeeds(pageable);
            return ok(new RestResponse<>(200, "피드 전체 조회 성공", makeResult(allFeeds)));
        } catch (IllegalArgumentException e) {
            return badRequest().body(new ErrorResponse(400, "피드 전체 조회 실패", e.getMessage(), null));
        }
    }

    @GetMapping("/{feedId}")
    public ResponseEntity<?> getFeed(@PathVariable Long feedId) {
        try {
            Feed foundFeed = feedService.findOneFeed(feedId);
            return ok(new RestResponse<>(200, "피드 상세 조회 성공", makeFeedDTOResponse(foundFeed)));
        } catch (IllegalArgumentException e) {
            return badRequest().body(new ErrorResponse(400, "피드 상세 조회 실패", e.getMessage(), feedId));
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserFeeds(@PathVariable Long userId, @PageableDefault(size = 10) Pageable pageable) {
        try {
            Page<Feed> userFeeds = feedService.findUserFeeds(pageable, userId);
            return ok(new RestResponse<>(200, "사용자 피드 상세 조회 성공", makeResult(userFeeds)));
        } catch (IllegalArgumentException e) {
            return badRequest().body(new ErrorResponse(400, "사용자 피드 상세 조회 실패", e.getMessage(), userId));
        }
    }

    @PostMapping(value = "/{feedId}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateFeed(@PathVariable Long feedId, @ModelAttribute UpdateFeedDTO.Request feedDTO) {
        try {
            Feed updatedFeed = feedService.updateOneFeed(feedId, feedDTO);
            return ok(new RestResponse<>(200, "피드 수정 성공", makeFeedDTOResponse(updatedFeed)));
        } catch (IllegalArgumentException e) {
            return badRequest().body(new ErrorResponse(400, "피드 수정 실패", e.getMessage(), feedDTO));
        }
    }

    @DeleteMapping("/{feedId}")
    public ResponseEntity<?> deleteFeed(@PathVariable Long feedId) {
        try {
            feedService.deleteFeed(feedId);
            return ok(new RestResponse<>(200, "피드 삭제 성공", feedId));
        } catch (IllegalArgumentException e) {
            return badRequest().body(new ErrorResponse(400, "피드 삭제 실패", e.getMessage(), feedId));
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
}
