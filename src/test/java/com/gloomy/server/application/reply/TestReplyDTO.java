package com.gloomy.server.application.reply;

import com.gloomy.server.application.comment.CommentDTO;
import lombok.Getter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Getter
public class TestReplyDTO {
    private final String content;
    private final Long userId;
    private final String password;
    private final Long commentId;
    private String token;

    TestReplyDTO(Long userId, Long commentId) {
        this.content = "새 대댓글";
        this.userId = userId;
        this.password = "12345";
        this.commentId = commentId;
    }

    public void setToken(String token) {
        this.token = token;
    }

    ReplyDTO.Request makeUserReplyDTO() {
        return new ReplyDTO.Request(content, commentId);
    }

    ReplyDTO.Request makeNonUserReplyDTO() {
        return new ReplyDTO.Request(content, commentId, password);
    }
}
