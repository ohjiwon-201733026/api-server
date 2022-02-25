package com.gloomy.server.domain.notice;

import com.gloomy.server.domain.comment.Comment;
import com.gloomy.server.domain.common.entity.*;
import com.gloomy.server.domain.feed.*;
import com.gloomy.server.domain.feedlike.FeedLike;
import com.gloomy.server.domain.reply.Reply;
import com.gloomy.server.domain.user.User;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = false)
@Getter
@Entity
public class Notice extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feed_id", nullable = false)
    private Feed feedId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private Comment commentId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reply_id")
    private Reply replyId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "like_id")
    private FeedLike feedLikeId;

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private Type type;

    @Embedded
    private IsRead isRead;

    protected Notice() {
    }

    @Builder
    public Notice(User userId, Feed feedId, Comment commentId, Reply replyId, FeedLike feedLikeId, Type type, IsRead isRead, Status status, CreatedAt createdAt, UpdatedAt updatedAt, DeletedAt deletedAt) {
        this.userId = userId;
        this.feedId = feedId;
        this.commentId = commentId;
        this.replyId = replyId;
        this.feedLikeId = feedLikeId;
        this.type = type;
        this.isRead = isRead;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
    }

    public static Notice of(Feed feedId, Comment commentId, Type type) {
        return builder()
                .userId(feedId.getUserId())
                .feedId(feedId)
                .commentId(commentId)
                .replyId(null)
                .feedLikeId(null)
                .type(type)
                .isRead(new IsRead())
                .status(Status.active())
                .createdAt(new CreatedAt())
                .updatedAt(new UpdatedAt())
                .deletedAt(new DeletedAt())
                .build();
    }

    public static Notice of(Feed feedId, Reply replyId, Type type) {
        return builder()
                .userId(feedId.getUserId())
                .feedId(feedId)
                .commentId(null)
                .replyId(replyId)
                .feedLikeId(null)
                .type(type)
                .isRead(new IsRead())
                .status(Status.active())
                .createdAt(new CreatedAt())
                .updatedAt(new UpdatedAt())
                .deletedAt(new DeletedAt())
                .build();
    }

    public static Notice of(Feed feedId, FeedLike feedLikeId, Type type) {
        return builder()
                .userId(feedId.getUserId())
                .feedId(feedId)
                .commentId(null)
                .replyId(null)
                .feedLikeId(feedLikeId)
                .type(type)
                .isRead(new IsRead())
                .status(Status.active())
                .createdAt(new CreatedAt())
                .updatedAt(new UpdatedAt())
                .deletedAt(new DeletedAt())
                .build();
    }

    public void read() {
        this.isRead.read();
    }
}
