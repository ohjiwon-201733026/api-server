package com.gloomy.server.application.reply;

import com.gloomy.server.application.core.response.ErrorResponse;
import com.gloomy.server.application.core.response.RestResponse;
import com.gloomy.server.domain.reply.Reply;
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
            return ok(new RestResponse<>(200, "대댓글 생성 성공", (makeReplyDTOResponse(createdReply))));
        } catch (IllegalArgumentException e) {
            return badRequest().body(new ErrorResponse(400, "대댓글 생성 실패", makeErrorMessage(e.getMessage(), replyDTO)));
        }
    }

    @GetMapping("/comment/{commentId}")
    public ResponseEntity<?> getCommentAllActiveReplies(@PageableDefault(size = 10) Pageable pageable, @PathVariable Long commentId) {
        try {
            Page<Reply> commentAllReplies = replyService.getCommentAllActiveReplies(pageable, commentId);
            return ok(new RestResponse<>(200, "대댓글 전체 조회 성공", makeResult(commentAllReplies)));
        } catch (IllegalArgumentException e) {
            return badRequest().body(new ErrorResponse(400, "대댓글 전체 조회 실패", makeErrorMessage(e.getMessage(), commentId)));
        }
    }

    @GetMapping("/{replyId}")
    public ResponseEntity<?> getReply(@PathVariable Long replyId) {
        try {
            Reply foundReply = replyService.findReply(replyId);
            return ok(new RestResponse<>(200, "대댓글 상세 조회 성공", makeReplyDTOResponse(foundReply)));
        } catch (IllegalArgumentException e) {
            return badRequest().body(new ErrorResponse(400, "대댓글 상세 조회 실패", makeErrorMessage(e.getMessage(), replyId)));
        }
    }

    @PatchMapping(value = "/{replyId}")
    public ResponseEntity<?> updateReply(@PathVariable Long replyId, @Validated @RequestBody UpdateReplyDTO.Request updateReplyDTO) {
        try {
            Reply updatedReply = replyService.updateReply(replyId, updateReplyDTO);
            return ok(new RestResponse<>(200, "대댓글 수정 성공", makeReplyDTOResponse(updatedReply)));
        } catch (IllegalArgumentException e) {
            return badRequest().body(new ErrorResponse(400, "대댓글 수정 실패", makeErrorMessage(e.getMessage(), updateReplyDTO)));
        }
    }

    @DeleteMapping("/{replyId}")
    public ResponseEntity<?> deleteReply(@PathVariable Long replyId) {
        try {
            replyService.deleteReply(replyId);
            return ok(new RestResponse<>(200, "대댓글 전체 조회 성공", replyId));
        } catch (IllegalArgumentException e) {
            return badRequest().body(new ErrorResponse(400, "대댓글 생성 실패", makeErrorMessage(e.getMessage(), replyId)));
        }
    }

    private Page<ReplyDTO.Response> makeResult(Page<Reply> allReplies) {
        List<ReplyDTO.Response> result = new ArrayList<>();
        for (Reply reply : allReplies.getContent()) {
            result.add(makeReplyDTOResponse(reply));
        }
        return new PageImpl<>(result);
    }

    private ReplyDTO.Response makeReplyDTOResponse(Reply reply) {
        return ReplyDTO.Response.of(reply);
    }

    private List<Object> makeErrorMessage(String errorMessage, Object errorObject) {
        List<Object> errorMessages = new ArrayList<>();
        errorMessages.add(errorMessage);
        errorMessages.add(errorObject);
        return errorMessages;
    }
}
