package com.gloomy.server.domain.reply;

import com.gloomy.server.domain.comment.Comment;
import com.gloomy.server.domain.common.entity.*;
import com.gloomy.server.domain.feed.Content;
import com.gloomy.server.domain.feed.NonUser;
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
    private NonUser nonUser;

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
    private Reply(Content content, Comment commentId, NonUser nonUser, Status status, CreatedAt createdAt, UpdatedAt updatedAt, DeletedAt deletedAt) {
        this.content = content;
        this.commentId = commentId;
        this.nonUser = nonUser;
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
                .status(Status.active())
                .createdAt(new CreatedAt())
                .updatedAt(new UpdatedAt())
                .deletedAt(new DeletedAt())
                .build();
    }

    public static Reply of(String content, Comment commentId, String password) {
        return Reply.nonUserReplyBuilder()
                .content(new Content(content))
                .commentId(commentId)
                .nonUser(NonUser.of("익명 친구", password))
                .status(Status.active())
                .createdAt(new CreatedAt())
                .updatedAt(new UpdatedAt())
                .deletedAt(new DeletedAt())
                .build();
    }

    public void setContent(Content content) {
        this.content = content;
    }

    public void delete() {
        this.status = Status.inactive();
        this.deletedAt.setDeletedAt(LocalDateTime.now());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reply targetReply = (Reply) o;
        boolean result = Objects.equals(id, targetReply.getId())
                && Objects.equals(content.getContent(), targetReply.getContent().getContent())
                && Objects.equals(commentId.getId(), targetReply.getCommentId().getId())
                && status == targetReply.getStatus();
        if (Objects.nonNull(userId)) {
            return result && Objects.equals(userId.getId(), targetReply.getUserId().getId());
        }
        return result && Objects.equals(nonUser, targetReply.getNonUser());
    }
}
