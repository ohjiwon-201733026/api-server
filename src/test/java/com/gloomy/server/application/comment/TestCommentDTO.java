package com.gloomy.server.application.comment;

import com.gloomy.server.application.feed.FeedDTO;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Setter
@Getter
public class TestCommentDTO {
    private final String content;
    private final Long feedId;
    private final Long userId;
    private final String password;
    private String token;

    public TestCommentDTO(Long feedId, Long userId) {
        this.content = "댓글 작성 샘플입니다.";
        this.feedId = feedId;
        this.userId = userId;
        this.password = "12345";
    }

    public void setToken(String token) {
        this.token = token;
    }

    public CommentDTO.Request makeUserCommentDTO() {
        return new CommentDTO.Request(content, feedId);
    }

    public CommentDTO.Request makeNonUserCommentDTO() {
        return new CommentDTO.Request(content, feedId, password);
    }
}
