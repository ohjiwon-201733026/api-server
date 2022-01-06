package com.gloomy.server.application.reply;

import com.gloomy.server.domain.comment.Comment;
import com.gloomy.server.domain.user.User;
import lombok.Getter;

@Getter
public class TestReplyDTO {
    private final String content;
    private final String password;
    private final Comment commentId;

    TestReplyDTO(Comment commentId) {
        this.content = "새 대댓글";
        this.password = "12345";
        this.commentId = commentId;
    }

    ReplyDTO.Request makeUserReplyDTO(User user) {
        return new ReplyDTO.Request(content, commentId.getId(), user.getId());
    }

    ReplyDTO.Request makeNonUserReplyDTO() {
        return new ReplyDTO.Request(content, commentId.getId(), password);
    }
}
