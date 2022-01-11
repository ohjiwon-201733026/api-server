package com.gloomy.server.domain.comment;

import com.gloomy.server.domain.feed.*;
import com.gloomy.server.domain.image.IMAGE_STATUS;
import com.gloomy.server.domain.user.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import javax.persistence.*;
import java.util.Objects;

@Getter
@Entity
public class Comment {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private Content content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feed_id")
    private Feed feedId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User userId;

    @Embedded
    private Password password;

    @Column(name = "status", nullable = false)
    private COMMENT_STATUS status;

    Comment() {
    }

    @Builder(builderClassName = "userCommentBuilder", builderMethodName = "userCommentBuilder", access = AccessLevel.PRIVATE)
    private Comment(Content content, Feed feedId, User userId, COMMENT_STATUS status) {
        this.content = content;
        this.feedId = feedId;
        this.userId=userId;
        this.status = status;
    }

    @Builder(builderClassName = "nonUserCommentBuilder", builderMethodName = "nonUserCommentBuilder", access = AccessLevel.PRIVATE)
    private Comment(Content content, Feed feedId, Password password, COMMENT_STATUS status) {
        this.content = content;
        this.feedId = feedId;
        this.password = password;
        this.status = status;
    }

    public static Comment of(Content content, Feed feedId, User userId) {
        return Comment.userCommentBuilder()
                .content(content)
                .feedId(feedId)
                .userId(userId)
                .status(COMMENT_STATUS.ACTIVE)
                .build();
    }

    public static Comment of(Content content, Feed feedId, Password password) {
        return Comment.nonUserCommentBuilder()
                .content(content)
                .feedId(feedId)
                .password(password)
                .status(COMMENT_STATUS.ACTIVE)
                .build();
    }

    /**
     * User - Comment 연관관계
     */
    public void changeUser(User user){
        this.userId=user;
        this.userId.getComments().add(this);
    }

    public void setContent(Content content) {
        this.content = content;
    }

    public void setStatus(COMMENT_STATUS status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Comment comment = (Comment) o;
        boolean result = Objects.equals(id, comment.getId())
                && Objects.equals(content.getContent(), comment.getContent().getContent())
                && Objects.equals(feedId.getId(), comment.getFeedId().getId())
                && status == comment.getStatus();
        if (Objects.nonNull(userId)) {
            return result && Objects.equals(userId.getId(), comment.getUserId().getId());
        }
        return result && Objects.equals(password.getPassword(), comment.getPassword().getPassword());
    }
}
