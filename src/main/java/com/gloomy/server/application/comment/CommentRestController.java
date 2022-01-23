package com.gloomy.server.application.comment;

import com.gloomy.server.application.core.response.RequestContext;
import com.gloomy.server.domain.comment.Comment;
import com.gloomy.server.domain.user.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/comment")
public class CommentRestController {
    private final UserService userService;
    private final CommentService commentService;
    private final RequestContext requestContext;

    public CommentRestController(UserService userService, CommentService commentService, RequestContext requestContext) {
        this.userService = userService;
        this.commentService = commentService;
        this.requestContext = requestContext;
    }

    @PostMapping(value = "")
    public CommentDTO.Response createComment(@Validated @RequestBody CommentDTO.Request commentDTO) {
        requestContext.setRequestBody(commentDTO);
        Long userId = userService.getMyInfo();
        Comment createdComment = commentService.createComment(userId, commentDTO);
        return makeCommentDTOResponse(createdComment);
    }

    @GetMapping("/feed/{feedId}")
    public Page<CommentDTO.Response> getFeedAllActiveComments(@PageableDefault(size = 10) Pageable pageable, @PathVariable Long feedId) {
        Page<Comment> feedAllComments = commentService.getFeedAllActiveComments(pageable, feedId);
        return makeResult(feedAllComments);
    }

    @GetMapping("/{commentId}")
    public CommentDTO.Response getComment(@PathVariable Long commentId) {
        Comment foundComment = commentService.findComment(commentId);
        return makeCommentDTOResponse(foundComment);
    }

    @PatchMapping(value = "/{commentId}")
    public CommentDTO.Response updateComment(@PathVariable Long commentId, @Validated @RequestBody UpdateCommentDTO.Request updateCommentDTO) {
        requestContext.setRequestBody(updateCommentDTO);
        Comment updatedComment = commentService.updateComment(commentId, updateCommentDTO);
        return makeCommentDTOResponse(updatedComment);
    }

    @DeleteMapping("/{commentId}")
    public void deleteComment(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);
    }

    private Page<CommentDTO.Response> makeResult(Page<Comment> allComments) {
        List<CommentDTO.Response> result = new ArrayList<>();
        for (Comment comment : allComments.getContent()) {
            result.add(makeCommentDTOResponse(comment));
        }
        return new PageImpl<>(result);
    }

    private CommentDTO.Response makeCommentDTOResponse(Comment comment) {
        return CommentDTO.Response.of(comment);
    }
}
