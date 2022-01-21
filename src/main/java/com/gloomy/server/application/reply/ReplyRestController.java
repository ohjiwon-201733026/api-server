package com.gloomy.server.application.reply;

import com.gloomy.server.application.core.response.RequestContext;
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

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/reply")
public class ReplyRestController {
    private final ReplyService replyService;
    private final RequestContext requestContext;

    public ReplyRestController(ReplyService replyService, RequestContext requestContext) {
        this.replyService = replyService;
        this.requestContext = requestContext;
    }

    @PostMapping(value = "")
    public ResponseEntity<?> createReply(@Validated @RequestBody ReplyDTO.Request replyDTO) {
        requestContext.setRequestBody(replyDTO);
        Reply createdReply = replyService.createReply(replyDTO);
        return ok(new RestResponse<>(200, "대댓글 생성 성공", (makeReplyDTOResponse(createdReply))));
    }

    @GetMapping("/comment/{commentId}")
    public ResponseEntity<?> getCommentAllActiveReplies(@PageableDefault(size = 10) Pageable pageable, @PathVariable Long commentId) {
        Page<Reply> commentAllReplies = replyService.getCommentAllActiveReplies(pageable, commentId);
        return ok(new RestResponse<>(200, "대댓글 전체 조회 성공", makeResult(commentAllReplies)));
    }

    @GetMapping("/{replyId}")
    public ResponseEntity<?> getReply(@PathVariable Long replyId) {
        Reply foundReply = replyService.findReply(replyId);
        return ok(new RestResponse<>(200, "대댓글 상세 조회 성공", makeReplyDTOResponse(foundReply)));
    }

    @PatchMapping(value = "/{replyId}")
    public ResponseEntity<?> updateReply(@PathVariable Long replyId, @Validated @RequestBody UpdateReplyDTO.Request updateReplyDTO) {
        requestContext.setRequestBody(updateReplyDTO);
        Reply updatedReply = replyService.updateReply(replyId, updateReplyDTO);
        return ok(new RestResponse<>(200, "대댓글 수정 성공", makeReplyDTOResponse(updatedReply)));
    }

    @DeleteMapping("/{replyId}")
    public ResponseEntity<?> deleteReply(@PathVariable Long replyId) {
        replyService.deleteReply(replyId);
        return ok(new RestResponse<>(200, "대댓글 전체 조회 성공", replyId));
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
}
