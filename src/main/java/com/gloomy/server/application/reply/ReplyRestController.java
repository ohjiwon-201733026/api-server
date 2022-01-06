package com.gloomy.server.application.reply;

import com.gloomy.server.domain.reply.Reply;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/reply")
public class ReplyRestController {
    private final ReplyService replyService;

    public ReplyRestController(ReplyService replyService) {
        this.replyService = replyService;
    }

    @PostMapping(value = "")
    public ResponseEntity<?> createReply(@Validated @RequestBody ReplyDTO.Request replyDTO) {
        try {
            Reply createdReply = replyService.createReply(replyDTO);
            return new ResponseEntity<>(makeReplyDTOResponse(createdReply), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(makeErrorMessage(e.getMessage(), replyDTO.toString()), HttpStatus.BAD_REQUEST);
        }
    }

    private ReplyDTO.Response makeReplyDTOResponse(Reply reply) {
        return ReplyDTO.Response.of(reply);
    }

    private List<String> makeErrorMessage(String errorMessage, Object errorObject) {
        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(errorMessage);
        errorMessages.add(errorObject.toString());
        return errorMessages;
    }
}
