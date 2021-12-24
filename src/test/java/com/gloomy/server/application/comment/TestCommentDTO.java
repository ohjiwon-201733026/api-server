package com.gloomy.server.application.comment;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TestCommentDTO {
    private final String content;
    private final Long feedId;
    private final Long userId;
    private final String password;

    public TestCommentDTO(Long feedId, Long userId) {
        this.content = "댓글 작성 샘플입니다.";
        this.feedId = feedId;
        this.userId = userId;
        this.password = "12345";
    }

    public CommentDTO.Request makeUserCommentDTO() {
        return new CommentDTO.Request(content, feedId, userId);
    }

    public CommentDTO.Request makeNonUserCommentDTO() {
        return new CommentDTO.Request(content, feedId, password);
    }
}
