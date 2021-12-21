package com.gloomy.server.application.feed;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import com.gloomy.server.domain.feed.Feed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
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

    public FeedRestController(FeedService feedService) {
        this.feedService = feedService;
    }

    @PostMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createFeed(@Validated @ModelAttribute FeedDTO.Request feedDTO) {
        try {
            Feed createFeed = feedService.createFeed(feedDTO);
            return new ResponseEntity<>(FeedDTO.Response.of(createFeed), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(makeErrorMessage(e.getMessage(), feedDTO.toString()), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("")
    public Object getAllFeeds(@PageableDefault(size = 10) Pageable pageable) {
        try {
            Page<FeedDTO.Response> allFeeds = feedService.findAllFeeds(pageable);
            return new ResponseEntity<>(allFeeds.getContent(), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(makeErrorMessage(e.getMessage(), null), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{feedId}")
    public Object getFeed(@PathVariable Long feedId) {
        try {
            Feed foundFeed = feedService.findOneFeed(feedId);
            return new ResponseEntity<>(FeedDTO.Response.of(foundFeed), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(makeErrorMessage(e.getMessage(), feedId), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/user/{userId}")
    public Object getUserFeeds(@PathVariable Long userId, @PageableDefault(size = 10) Pageable pageable) {
        try {
            Page<FeedDTO.Response> userFeeds = feedService.findUserFeeds(pageable, userId);
            return new ResponseEntity<>(makeResult(userFeeds.getContent()), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(makeErrorMessage(e.getMessage(), userId), HttpStatus.BAD_REQUEST);
        } catch (JsonProcessingException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "/{feedId}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Object updateFeed(@PathVariable Long feedId, @ModelAttribute UpdateFeedDTO.Request feedDTO) {
        try {
            Feed updatedFeed = feedService.updateOneFeed(feedId, feedDTO);
            System.out.println(feedId);
            System.out.println(feedDTO.getPassword());
            return new ResponseEntity<>(FeedDTO.Response.of(updatedFeed), HttpStatus.OK);
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
