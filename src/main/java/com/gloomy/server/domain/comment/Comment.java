package com.gloomy.server.domain.comment;

import com.gloomy.server.domain.common.entity.*;
import com.gloomy.server.domain.feed.Content;
import com.gloomy.server.domain.feed.Feed;
import com.gloomy.server.domain.feed.Password;
import com.gloomy.server.domain.user.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Entity
public class Comment extends BaseEntity {
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

    protected Comment() {
    }

    @Builder(builderClassName = "userCommentBuilder", builderMethodName = "userCommentBuilder", access = AccessLevel.PRIVATE)
    private Comment(Content content, Feed feedId, User userId, Status status, CreatedAt createdAt, UpdatedAt updatedAt, DeletedAt deletedAt) {
        this.content = content;
        this.feedId = feedId;
        this.userId = userId;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
    }

    @Builder(builderClassName = "nonUserCommentBuilder", builderMethodName = "nonUserCommentBuilder", access = AccessLevel.PRIVATE)
    private Comment(Content content, Feed feedId, Password password, Status status, CreatedAt createdAt, UpdatedAt updatedAt, DeletedAt deletedAt) {
        this.content = content;
        this.feedId = feedId;
        this.password = password;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
    }

    public static Comment of(Content content, Feed feedId, User userId) {
        return Comment.userCommentBuilder()
                .content(content)
                .feedId(feedId)
                .userId(userId)
                .status(Status.ACTIVE)
                .createdAt(new CreatedAt())
                .updatedAt(new UpdatedAt())
                .deletedAt(new DeletedAt())
                .build();
    }

    public static Comment of(Content content, Feed feedId, Password password) {
        return Comment.nonUserCommentBuilder()
                .content(content)
                .feedId(feedId)
                .password(password)
                .status(Status.ACTIVE)
                .createdAt(new CreatedAt())
                .updatedAt(new UpdatedAt())
                .deletedAt(new DeletedAt())
                .build();
    }

    public void setContent(Content content) {
        this.content = content;
    }

    public void delete() {
        this.status = Status.INACTIVE;
        this.deletedAt.setDeletedAt(LocalDateTime.now());
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
