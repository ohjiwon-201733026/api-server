package com.gloomy.server.application.comment;

import com.gloomy.server.domain.comment.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

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
            return new ResponseEntity<>(makeCommentDTOResponse(createdComment), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(makeErrorMessage(e.getMessage(), commentDTO.toString()), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/feed/{feedId}")
    public Object getFeedAllComments(@PageableDefault(size = 10) Pageable pageable, @PathVariable Long feedId) {
        try {
            Page<Comment> feedAllComments = commentService.getFeedAllComments(pageable, feedId);
            return new ResponseEntity<>(makeResult(feedAllComments), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(makeErrorMessage(e.getMessage(), null), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{commentId}")
    public Object getComment(@PathVariable Long commentId) {
        try {
            Comment foundComment = commentService.findComment(commentId);
            return new ResponseEntity<>(makeCommentDTOResponse(foundComment), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(makeErrorMessage(e.getMessage(), commentId), HttpStatus.BAD_REQUEST);
        }
    }

    @PatchMapping(value = "/{commentId}")
    public Object updateComment(@PathVariable Long commentId, @Validated @RequestBody UpdateCommentDTO.Request updateCommentDTO) {
        try {
            Comment updatedComment = commentService.updateComment(commentId, updateCommentDTO);
            return new ResponseEntity<>(makeCommentDTOResponse(updatedComment), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(makeErrorMessage(e.getMessage(), updateCommentDTO.toString()), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{commentId}")
    public Object deleteComment(@PathVariable Long commentId) {
        try {
            commentService.deleteComment(commentId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
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

    private List<String> makeErrorMessage(String errorMessage, Object errorObject) {
        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(errorMessage);
        errorMessages.add(errorObject.toString());
        return errorMessages;
    }
}
