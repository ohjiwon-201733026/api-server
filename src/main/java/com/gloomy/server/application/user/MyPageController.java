package com.gloomy.server.application.user;


import com.gloomy.server.application.comment.CommentDTO;
import com.gloomy.server.application.feed.FeedDTO;
import com.gloomy.server.domain.comment.Comment;
import com.gloomy.server.domain.feed.Feed;
import com.gloomy.server.domain.user.UserService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RequestMapping("/myPage")
@RestController
public class MyPageController {

    private final UserService userService;

    public MyPageController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(value ="/feed/{userId}")
    public ResponseEntity<List<FeedDTO.Response>> findMyFeed(@PathVariable("userId")Long userId, @PageableDefault(size = 10) Pageable pageable,Model model){
        try {
            List<Feed> feeds=userService.findFeeds(userId);
            return ResponseEntity.ok().body(makeFeedDTOList(feeds));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    private List<FeedDTO.Response> makeFeedDTOList(List<Feed> feeds){
        List<FeedDTO.Response> feedDTOList=new ArrayList<>();
        for (Feed feed : feeds) {
            feedDTOList.add(makeFeedDTO(feed));
        }
        return feedDTOList;
    }

    private FeedDTO.Response makeFeedDTO(Feed feed){
        return FeedDTO.Response.userFeedResponse()
                .id(feed.getId())
                .isUser(feed.getIsUser().getIsUser())
                .ip(feed.getIp().getIp())
                .userId(feed.getUserId().getId())
                .content(feed.getContent().getContent())
                .likeCount(feed.getLikeCount().getLikeCount())
                .build();

    }

    @GetMapping(value ="/comment/{userId}")
    public ResponseEntity<List<CommentDTO.Response>> findMyComment(@PathVariable("userId")Long userId){
        try {
            List<Comment> comments=userService.findComments(userId);
            return ResponseEntity.ok().body(makeCommentList(comments));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    private List<CommentDTO.Response> makeCommentList(List<Comment> comments){
        List<CommentDTO.Response> commentDTOList = new ArrayList<>();
        for (Comment comment : comments) {
            commentDTOList.add(makeComment(comment));
        }
        return commentDTOList;
    }

    private CommentDTO.Response makeComment(Comment comment){
        return CommentDTO.Response.userCommentResponse()
                .id(comment.getId())
                .content(comment.getContent().getContent())
                .feedId(comment.getFeedId().getId())
                .userId(comment.getUserId().getId())
                .build();
    }


}
