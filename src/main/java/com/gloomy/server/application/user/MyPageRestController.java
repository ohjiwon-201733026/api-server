package com.gloomy.server.application.user;


import com.gloomy.server.application.comment.CommentDTO;
import com.gloomy.server.application.comment.CommentService;
import com.gloomy.server.domain.comment.Comment;
import com.gloomy.server.domain.user.UserService;
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
public class MyPageRestController {

    private final UserService userService;
    private final CommentService commentService;

    public MyPageRestController(UserService userService, CommentService commentService) {
        this.userService = userService;
        this.commentService = commentService;
    }


    @GetMapping(value ="/comment")
    public Page<CommentDTO.Response> findUserComments(@PageableDefault(size=10)Pageable pageable){
        Long userId=userService.getMyInfo();
        Page<Comment> comments=commentService.getCommentByIdAndActive(pageable,userId);
        return makeCommentPage(comments);
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
