package com.gloomy.server.application.feed;

import com.gloomy.server.application.comment.CommentService;
import com.gloomy.server.application.core.response.RequestContext;
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

import static org.springframework.http.ResponseEntity.ok;

@Slf4j
@RestController
@RequestMapping("/feed")
public class FeedRestController {
    private final FeedService feedService;
    private final ImageService imageService;
    private final CommentService commentService;
    private final RequestContext requestContext;

    public FeedRestController(FeedService feedService, ImageService imageService, CommentService commentService, RequestContext requestContext) {
        this.feedService = feedService;
        this.imageService = imageService;
        this.commentService = commentService;
        this.requestContext = requestContext;
    }

    @PostMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createFeed(@Validated @ModelAttribute FeedDTO.Request feedDTO) {
        requestContext.setRequestBody(feedDTO);
        Feed createFeed = feedService.createFeed(feedDTO);
        return ok(new RestResponse<>(200, "피드 생성 성공", makeFeedDTOResponse(createFeed)));
    }

    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAllActiveFeeds(@PageableDefault(size = 10) Pageable pageable) {
        Page<Feed> allFeeds = feedService.findAllActiveFeeds(pageable);
        return ok(new RestResponse<>(200, "피드 전체 조회 성공", makeResult(allFeeds)));
    }

    @GetMapping("/{feedId}")
    public ResponseEntity<?> getFeed(@PathVariable Long feedId) {
        Feed foundFeed = feedService.findOneFeed(feedId);
        return ok(new RestResponse<>(200, "피드 상세 조회 성공", makeFeedDTOResponse(foundFeed)));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserFeeds(@PathVariable Long userId, @PageableDefault(size = 10) Pageable pageable) {
        Page<Feed> userFeeds = feedService.findUserFeeds(pageable, userId);
        return ok(new RestResponse<>(200, "사용자 피드 상세 조회 성공", makeResult(userFeeds)));
    }

    @PostMapping(value = "/{feedId}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateFeed(@PathVariable Long feedId, @ModelAttribute UpdateFeedDTO.Request updateFeedDTO) {
        requestContext.setRequestBody(updateFeedDTO);
        Feed updatedFeed = feedService.updateOneFeed(feedId, updateFeedDTO);
        return ok(new RestResponse<>(200, "피드 수정 성공", makeFeedDTOResponse(updatedFeed)));
    }

    @DeleteMapping("/{feedId}")
    public ResponseEntity<?> deleteFeed(@PathVariable Long feedId) {
        feedService.deleteFeed(feedId);
        return ok(new RestResponse<>(200, "피드 삭제 성공", feedId));
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
