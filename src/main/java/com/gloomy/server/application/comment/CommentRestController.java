package com.gloomy.server.application.comment;

import com.gloomy.server.application.core.response.ErrorResponse;
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

import static org.springframework.http.ResponseEntity.badRequest;
import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/comment")
public class CommentRestController {
    private final CommentService commentService;

    public CommentRestController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping(value = "")
    public ResponseEntity<?> createComment(@Validated @RequestBody CommentDTO.Request commentDTO) {
        try {
            Comment createdComment = commentService.createComment(commentDTO);
            return ok(new RestResponse<>(200, "댓글 생성 성공", makeCommentDTOResponse(createdComment)));
        } catch (IllegalArgumentException e) {
            return badRequest().body(new ErrorResponse(400, "댓글 생성 실패", e.getMessage(), commentDTO));
        }
    }

    @GetMapping("/feed/{feedId}")
    public ResponseEntity<?> getFeedAllActiveComments(@PageableDefault(size = 10) Pageable pageable, @PathVariable Long feedId) {
        try {
            Page<Comment> feedAllComments = commentService.getFeedAllActiveComments(pageable, feedId);
            return ok(new RestResponse<>(200, "댓글 전체 조회 성공", makeResult(feedAllComments)));
        } catch (IllegalArgumentException e) {
            return badRequest().body(new ErrorResponse(400, "댓글 전체 조회 실패", e.getMessage(), feedId));
        }
    }

    @GetMapping("/{commentId}")
    public ResponseEntity<?> getComment(@PathVariable Long commentId) {
        try {
            Comment foundComment = commentService.findComment(commentId);
            return ok(new RestResponse<>(200, "댓글 상세 조회 성공", makeCommentDTOResponse(foundComment)));
        } catch (IllegalArgumentException e) {
            return badRequest().body(new ErrorResponse(400, "댓글 상세 조회 실패", e.getMessage(), commentId));
        }
    }

    @PatchMapping(value = "/{commentId}")
    public ResponseEntity<?> updateComment(@PathVariable Long commentId, @Validated @RequestBody UpdateCommentDTO.Request updateCommentDTO) {
        try {
            Comment updatedComment = commentService.updateComment(commentId, updateCommentDTO);
            return ok(new RestResponse<>(200, "댓글 수정 성공", makeCommentDTOResponse(updatedComment)));
        } catch (IllegalArgumentException e) {
            return badRequest().body(new ErrorResponse(400, "댓글 수정 실패", e.getMessage(), updateCommentDTO));
        }
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable Long commentId) {
        try {
            commentService.deleteComment(commentId);
            return ok(new RestResponse<>(200, "댓글 삭제 성공", commentId));
        } catch (IllegalArgumentException e) {
            return badRequest().body(new ErrorResponse(400, "댓글 생성 실패", e.getMessage(), commentId));
        }
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
