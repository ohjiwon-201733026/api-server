package com.gloomy.server.application.reply;

import com.gloomy.server.application.core.response.RequestContext;
import com.gloomy.server.domain.reply.Reply;
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
@RequestMapping("/reply")
public class ReplyRestController {
    private final UserService userService;
    private final ReplyService replyService;
    private final RequestContext requestContext;

    public ReplyRestController(UserService userService, ReplyService replyService, RequestContext requestContext) {
        this.userService = userService;
        this.replyService = replyService;
        this.requestContext = requestContext;
    }

    @PostMapping(value = "")
    public ReplyDTO.Response createReply(@Validated @RequestBody ReplyDTO.Request replyDTO) {
        requestContext.setRequestBody(replyDTO);
        Long userId = userService.getMyInfo();
        Reply createdReply = replyService.createReply(userId, replyDTO);
        return makeReplyDTOResponse(createdReply);
    }

    @GetMapping("/comment/{commentId}")
    public Page<ReplyDTO.Response> getCommentAllActiveReplies(@PageableDefault(size = 10) Pageable pageable, @PathVariable Long commentId) {
        Page<Reply> commentAllReplies = replyService.getCommentAllActiveReplies(pageable, commentId);
        return makeResult(commentAllReplies);
    }

    @GetMapping("/{replyId}")
    public ReplyDTO.Response getReply(@PathVariable Long replyId) {
        Reply foundReply = replyService.findReply(replyId);
        return makeReplyDTOResponse(foundReply);
    }

    @PatchMapping(value = "/{replyId}")
    public ReplyDTO.Response updateReply(@PathVariable Long replyId, @Validated @RequestBody UpdateReplyDTO.Request updateReplyDTO) {
        requestContext.setRequestBody(updateReplyDTO);
        Reply updatedReply = replyService.updateReply(replyId, updateReplyDTO);
        return makeReplyDTOResponse(updatedReply);
    }

    @DeleteMapping("/{replyId}")
    public void deleteReply(@PathVariable Long replyId) {
        replyService.deleteReply(replyId);
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
