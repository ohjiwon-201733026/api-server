package com.gloomy.server.application.feed;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import com.gloomy.server.core.response.ErrorResponse;
import com.gloomy.server.core.response.RestResponse;
import com.gloomy.server.domain.feed.Feed;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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

    @GetMapping("")
    public Object getAllFeeds(@PageableDefault(size = 10) Pageable pageable) {
        try {
            Page<Feed> foundAllFeeds = feedService.findAllFeeds(pageable);
            return new RestResponse<>(200, "전체 피드 조회 성공", makeResult(foundAllFeeds.getContent()));
        } catch (IllegalArgumentException e) {
            return new ErrorResponse(400, "전체 피드 조회 실패", makeErrorMessage(e.getMessage(), null));
        } catch (JsonProcessingException e) {
            return new ErrorResponse(500, "전체 피드 조회 실패", null);
        }
    }

    @GetMapping("/{feedId}")
    public Object getFeed(@PathVariable Long feedId) {
        System.out.println(feedId);
        try {
            Feed foundFeed = feedService.findNonUserFeed(feedId);
            return new RestResponse<>(200, "피드 조회 성공", makeResult(foundFeed));
        } catch (IllegalArgumentException e) {
            return new ErrorResponse(400, "피드 조회 실패", makeErrorMessage(e.getMessage(), feedId));
        } catch (JsonProcessingException e) {
            return new ErrorResponse(500, "피드 조회 실패", null);
        }
    }

    @GetMapping("/user/{userId}")
    public Object getUserFeeds(@PathVariable Long userId) {
        try {
            List<Feed> foundUserFeeds = feedService.findUserFeeds(userId);
            return new RestResponse<>(200, "피드 조회 성공", makeResult(foundUserFeeds));
        } catch (IllegalArgumentException e) {
            return new ErrorResponse(400, "사용자 피드 조회 실패", makeErrorMessage(e.getMessage(), userId));
        } catch (JsonProcessingException e) {
            return new ErrorResponse(500, "사용자 피드 조회 실패", null);
        }
    }

    @DeleteMapping("/{feedId}")
    public Object deleteFeed(@PathVariable Long feedId) {
        try {
            return new RestResponse<>(200, "피드 삭제 성공", null);
        } catch (IllegalArgumentException e) {
            return new ErrorResponse(400, "피드 삭제 실패", null);
        }
    }

    private String makeResult(Object feed) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new Hibernate5Module());
        return mapper.writeValueAsString(feed);
    }

    private List<String> makeErrorMessage(String errorMessage, Object errorObject) {
        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(errorMessage);
        errorMessages.add(errorObject.toString());
        return errorMessages;
    }
}
