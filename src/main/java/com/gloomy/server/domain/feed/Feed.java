package com.gloomy.server.domain.feed;

import com.gloomy.server.application.feed.FeedDTO;
import com.gloomy.server.domain.common.entity.*;
import com.gloomy.server.domain.user.User;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import javax.persistence.*;
import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = false)
@Getter
@Entity
public class Feed extends BaseEntity {
    @Embedded
    private Ip ip;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User userId;

    @Embedded
    private NonUser nonUser;

    @Column(name = "category", nullable = false)
    @Enumerated(EnumType.STRING)
    private Category category;

    @Embedded
    private Title title;

    @Embedded
    private Content content;

    protected Feed() {
    }

    @Builder
    private Feed(Ip ip, User userId, NonUser nonUser, Category category, Title title, Status status, Content content, CreatedAt createdAt, UpdatedAt updatedAt, DeletedAt deletedAt) {
        this.ip = ip;
        this.userId = userId;
        this.nonUser = nonUser;
        this.category = category;
        this.title = title;
        this.content = content;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
    }

    public static Feed from(User userId) {
        if (userId != null) {
            return builder()
                    .ip(new Ip("111.111.111.111"))
                    .userId(userId)
                    .nonUser(null)
                    .category(Category.ALL)
                    .title(new Title(""))
                    .content(new Content(""))
                    .status(Status.active())
                    .createdAt(new CreatedAt())
                    .updatedAt(new UpdatedAt())
                    .deletedAt(new DeletedAt())
                    .build();
        }
        return builder()
                .ip(new Ip("111.111.111.111"))
                .userId(null)
                .nonUser(NonUser.of("익명 친구", ""))
                .category(Category.UNDEFINED)
                .title(new Title(""))
                .content(new Content(""))
                .status(Status.active())
                .createdAt(new CreatedAt())
                .updatedAt(new UpdatedAt())
                .deletedAt(new DeletedAt())
                .build();
    }

    public static Feed of(User userId, FeedDTO.Request feedDTO) {
        if (userId != null) {
            return builder()
                    .ip(new Ip("111.111.111.111"))
                    .userId(userId)
                    .nonUser(null)
                    .category(Category.valueOf(feedDTO.getCategory()))
                    .title(new Title(feedDTO.getTitle()))
                    .content(new Content(feedDTO.getContent()))
                    .status(Status.active())
                    .createdAt(new CreatedAt())
                    .updatedAt(new UpdatedAt())
                    .deletedAt(new DeletedAt())
                    .build();
        }
        return builder()
                .ip(new Ip("111.111.111.111"))
                .userId(null)
                .nonUser(NonUser.of("익명 친구", feedDTO.getPassword()))
                .category(Category.valueOf(feedDTO.getCategory()))
                .title(new Title(feedDTO.getTitle()))
                .content(new Content(feedDTO.getContent()))
                .status(Status.active())
                .createdAt(new CreatedAt())
                .updatedAt(new UpdatedAt())
                .deletedAt(new DeletedAt())
                .build();
    }

    public void setPassword(String password) {
        if (this.getNonUser() == null) {
            throw new IllegalArgumentException("[Feed] 비회원이 아닙니다.");
        }
        this.getNonUser().setPassword(password);
    }

    public void setCategory(String category) {
        this.category = Category.valueOf(category);
    }

    public void setTitle(String title) {
        this.title.setTitle(title);
    }

    public void setContent(String content) {
        this.content.setContent(content);
    }

    public void delete() {
        this.status = Status.inactive();
        this.deletedAt.setDeletedAt(LocalDateTime.now());
    }

    public void report() {
        this.status = Status.invisible();
    }
}
