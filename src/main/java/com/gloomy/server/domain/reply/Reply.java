package com.gloomy.server.domain.reply;

import com.gloomy.server.domain.comment.Comment;
import com.gloomy.server.domain.common.*;
import com.gloomy.server.domain.feed.Content;
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
public class Reply extends BaseEntity {
    @Embedded
    private Content content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Comment commentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User userId;

    @Embedded
    private Password password;

    protected Reply() {
    }

    @Builder(builderClassName = "userReplyBuilder", builderMethodName = "userReplyBuilder", access = AccessLevel.PRIVATE)
    private Reply(Content content, Comment commentId, User userId, Status status, CreatedAt createdAt, UpdatedAt updatedAt, DeletedAt deletedAt) {
        this.content = content;
        this.commentId = commentId;
        this.userId = userId;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
    }

    @Builder(builderClassName = "nonUserReplyBuilder", builderMethodName = "nonUserReplyBuilder", access = AccessLevel.PRIVATE)
    private Reply(Content content, Comment commentId, Password password, Status status, CreatedAt createdAt, UpdatedAt updatedAt, DeletedAt deletedAt) {
        this.content = content;
        this.commentId = commentId;
        this.password = password;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
    }

    public static Reply of(String content, Comment commentId, User userId) {
        return Reply.userReplyBuilder()
                .content(new Content(content))
                .commentId(commentId)
                .userId(userId)
                .status(Status.ACTIVE)
                .createdAt(new CreatedAt())
                .updatedAt(new UpdatedAt())
                .deletedAt(new DeletedAt())
                .build();
    }

    public static Reply of(String content, Comment commentId, String password) {
        return Reply.nonUserReplyBuilder()
                .content(new Content(content))
                .commentId(commentId)
                .password(new Password(password))
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
        Reply reply = (Reply) o;
        boolean result = Objects.equals(id, reply.getId())
                && Objects.equals(content.getContent(), reply.getContent().getContent())
                && Objects.equals(commentId.getId(), reply.getCommentId().getId())
                && status == reply.getStatus();
        if (Objects.nonNull(userId)) {
            return result && Objects.equals(userId.getId(), reply.getUserId().getId());
        }
        return result && Objects.equals(password.getPassword(), reply.getPassword().getPassword());
    }
}
