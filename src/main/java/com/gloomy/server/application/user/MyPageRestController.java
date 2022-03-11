package com.gloomy.server.application.user;


import com.gloomy.server.application.comment.CommentDTO;
import com.gloomy.server.application.comment.CommentService;
import com.gloomy.server.application.feed.FeedDTO;
import com.gloomy.server.application.feed.FeedService;
import com.gloomy.server.application.feedlike.FeedLikeService;
import com.gloomy.server.application.image.ImageService;
import com.gloomy.server.application.image.Images;
import com.gloomy.server.application.jwt.JwtService;
import com.gloomy.server.domain.comment.Comment;
import com.gloomy.server.domain.feed.Feed;
import com.gloomy.server.domain.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RequestMapping("/myPage")
@RestController
@RequiredArgsConstructor
public class MyPageRestController {

    private final UserService userService;
    private final CommentService commentService;
    private final JwtService jwtService;
    private final FeedService feedService;
    private final ImageService imageService;
    private final FeedLikeService feedLikeService;


    @GetMapping(value ="/comment")
    public Page<CommentDTO.Response> findUserComments(@PageableDefault(size=10)Pageable pageable){
        Long userId=jwtService.getMyInfo();
        Page<Comment> comments=commentService.getCommentByIdAndActive(pageable,userId);
        return makeCommentPage(comments);
    }

    @GetMapping("/feed")
    public Page<FeedDTO.Response> getUserFeeds(@PageableDefault(size = 10) Pageable pageable) {
        Long userId = jwtService.getMyInfo();
        Page<Feed> userFeeds = feedService.findUserFeeds(pageable, userId);
        return makeResult(userFeeds);
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

    private Page<CommentDTO.Response> makeCommentPage(Page<Comment> comments){
        List<CommentDTO.Response> commentDTOList = new ArrayList<>();
        for (Comment comment : comments.getContent()) {
            commentDTOList.add(makeComment(comment));
        }
        return new PageImpl<>(commentDTOList);
    }

    private CommentDTO.Response makeComment(Comment comment){
        return CommentDTO.Response.builder()
                .id(comment.getId())
                .content(comment.getContent().getContent())
                .feedId(comment.getFeedId().getId())
                .userId(comment.getUserId().getId())
                .build();
    }
}
