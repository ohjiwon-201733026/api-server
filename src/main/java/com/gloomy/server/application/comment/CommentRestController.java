package com.gloomy.server.application.comment;

import com.gloomy.server.application.core.response.RequestContext;
import com.gloomy.server.application.core.response.RestResponse;
import com.gloomy.server.domain.comment.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/comment")
public class CommentRestController {
    private final CommentService commentService;
    private final RequestContext requestContext;

    public CommentRestController(CommentService commentService, RequestContext requestContext) {
        this.commentService = commentService;
        this.requestContext = requestContext;
    }

    @PostMapping(value = "")
    public ResponseEntity<?> createComment(@Validated @RequestBody CommentDTO.Request commentDTO) {
        requestContext.setRequestBody(commentDTO);
        Comment createdComment = commentService.createComment(commentDTO);
        return ok(new RestResponse<>(200, "댓글 생성 성공", makeCommentDTOResponse(createdComment)));
    }

    @GetMapping("/feed/{feedId}")
    public ResponseEntity<?> getFeedAllActiveComments(@PageableDefault(size = 10) Pageable pageable, @PathVariable Long feedId) {
        Page<Comment> feedAllComments = commentService.getFeedAllActiveComments(pageable, feedId);
        return ok(new RestResponse<>(200, "댓글 전체 조회 성공", makeResult(feedAllComments)));
    }

    @GetMapping("/{commentId}")
    public ResponseEntity<?> getComment(@PathVariable Long commentId) {
        Comment foundComment = commentService.findComment(commentId);
        return ok(new RestResponse<>(200, "댓글 상세 조회 성공", makeCommentDTOResponse(foundComment)));
    }

    @PatchMapping(value = "/{commentId}")
    public ResponseEntity<?> updateComment(@PathVariable Long commentId, @Validated @RequestBody UpdateCommentDTO.Request updateCommentDTO) {
        requestContext.setRequestBody(updateCommentDTO);
        Comment updatedComment = commentService.updateComment(commentId, updateCommentDTO);
        return ok(new RestResponse<>(200, "댓글 수정 성공", makeCommentDTOResponse(updatedComment)));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);
        return ok(new RestResponse<>(200, "댓글 삭제 성공", commentId));
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
