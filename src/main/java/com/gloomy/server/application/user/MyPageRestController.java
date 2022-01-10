package com.gloomy.server.application.user;


import com.gloomy.server.application.comment.CommentDTO;
import com.gloomy.server.application.comment.CommentService;
import com.gloomy.server.application.feed.FeedDTO;
import com.gloomy.server.domain.comment.Comment;
import com.gloomy.server.domain.feed.Feed;
import com.gloomy.server.domain.user.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
public class MyPageRestController {

    private final UserService userService;
    private final CommentService commentService;

    public MyPageRestController(UserService userService, CommentService commentService) {
        this.userService = userService;
        this.commentService = commentService;
    }

//    @GetMapping(value ="/feed/{userId}")
//    public ResponseEntity<List<FeedDTO.Response>> findMyFeed(@PathVariable("userId")Long userId, @PageableDefault(size = 10) Pageable pageable,Model model){
//        try {
//            List<Feed> feeds=userService.findFeeds(userId);
//            return ResponseEntity.ok().body(makeFeedDTOList(feeds));
//        } catch (IllegalArgumentException e) {
//            return ResponseEntity.badRequest().build();
//        }
//    }
//
//    private List<FeedDTO.Response> makeFeedDTOList(List<Feed> feeds){
//        List<FeedDTO.Response> feedDTOList=new ArrayList<>();
//        for (Feed feed : feeds) {
//            feedDTOList.add(makeFeedDTO(feed));
//        }
//        return feedDTOList;
//    }
//
//    private FeedDTO.Response makeFeedDTO(Feed feed){
//        return FeedDTO.Response.userFeedResponse()
//                .id(feed.getId())
//                .isUser(feed.getIsUser().getIsUser())
//                .ip(feed.getIp().getIp())
//                .userId(feed.getUserId().getId())
//                .content(feed.getContent().getContent())
//                .likeCount(feed.getLikeCount().getLikeCount())
//                .build();
//
//    }

    @GetMapping(value ="/comment/{userId}")
    public ResponseEntity<Page<CommentDTO.Response>> findUserComments(@PathVariable("userId")Long userId,@PageableDefault(size=10)Pageable pageable){
        try {
            Page<Comment> comments=commentService.getCommentByIdAndActive(pageable,userId);
            return ResponseEntity.ok().body(makeCommentPage(comments));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    private Page<CommentDTO.Response> makeCommentPage(Page<Comment> comments){
        List<CommentDTO.Response> commentDTOList = new ArrayList<>();
        for (Comment comment : comments.getContent()) {
            commentDTOList.add(makeComment(comment));
        }
        return new PageImpl<>(commentDTOList);
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
