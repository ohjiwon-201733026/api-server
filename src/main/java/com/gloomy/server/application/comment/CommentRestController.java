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


