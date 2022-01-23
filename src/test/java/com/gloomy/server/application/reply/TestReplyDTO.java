package com.gloomy.server.application.reply;

import lombok.Getter;

@Getter
public class TestReplyDTO {
    private final String content;
    private final Long userId;
    private final String password;
    private final Long commentId;

    TestReplyDTO(Long userId, Long commentId) {
        this.content = "새 대댓글";
        this.userId = userId;
        this.password = "12345";
        this.commentId = commentId;
    }

    ReplyDTO.Request makeUserReplyDTO() {
        return new ReplyDTO.Request(content, commentId);
    }

    ReplyDTO.Request makeNonUserReplyDTO() {
        return new ReplyDTO.Request(content, commentId, password);
    }
}
