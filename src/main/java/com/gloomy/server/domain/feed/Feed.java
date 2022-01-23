package com.gloomy.server.domain.feed;

import com.gloomy.server.application.feed.FeedDTO;
import com.gloomy.server.domain.user.User;
import lombok.Builder;
import lombok.Getter;

import javax.persistence.*;
import java.util.Objects;

@Getter
@Entity
public class Feed {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private Ip ip;

    @ManyToOne(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User userId;

    @Embedded
    private Password password;

    @Column
    @Enumerated(EnumType.STRING)
    private Category category;

    @Embedded
    private Title title;

    @Embedded
    private Content content;

    @Embedded
    private LikeCount likeCount;

    @Column
    @Enumerated(EnumType.STRING)
    private FeedStatus status;

    Feed() {
    }

    @Builder
    private Feed(Ip ip, User userId, Password password, Category category, Title title, FeedStatus status, Content content, LikeCount likeCount) {
        this.ip = ip;
        this.userId = userId;
        this.password = password;
        this.category = category;
        this.title = title;
        this.content = content;
        this.likeCount = likeCount;
        this.status = status;
    }

    public static Feed of(User userId, FeedDTO.Request feedDTO) {
        if (userId != null) {
            return builder()
                    .ip(new Ip("111.111.111.111"))
                    .userId(userId)
                    .password(null)
                    .category(Category.valueOf(feedDTO.getCategory()))
                    .title(new Title(feedDTO.getTitle()))
                    .content(new Content(feedDTO.getContent()))
                    .likeCount(new LikeCount(0))
                    .status(FeedStatus.ACTIVE)
                    .build();
        }
        return builder()
                .ip(new Ip("111.111.111.111"))
                .userId(null)
                .password(new Password(feedDTO.getPassword()))
                .category(Category.valueOf(feedDTO.getCategory()))
                .title(new Title(feedDTO.getTitle()))
                .content(new Content(feedDTO.getContent()))
                .likeCount(new LikeCount(0))
                .status(FeedStatus.ACTIVE)
                .build();
    }

    public void setStatus(FeedStatus status) {
        this.status = status;
    }

    public void setPassword(Password password) {
        this.password = password;
    }

    public void setContent(Content content) {
        this.content = content;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Feed) {
            Feed targetFeed = (Feed) o;
            boolean result = Objects.equals(id, targetFeed.id)
                    && Objects.equals(ip.getIp(), targetFeed.ip.getIp())
                    && category == targetFeed.category
                    && Objects.equals(title.getTitle(), targetFeed.title.getTitle())
                    && Objects.equals(content.getContent(), targetFeed.content.getContent())
                    && status == targetFeed.status;
            if (userId != null) {
                result &= Objects.equals(userId.getId(), targetFeed.userId.getId());
                return result;
            }
            result &= Objects.equals(password.getPassword(), targetFeed.password.getPassword());
            return result;
        }
        return false;
    }

    @Override
    public String toString() {
        return "Feed{" +
                "id=" + id +
                ", ip=" + ip +
                ", userId=" + userId +
                ", password=" + password +
                ", category=" + category +
                ", title=" + title +
                ", content=" + content +
                ", likeCount=" + likeCount +
                ", status=" + status +
                '}';
    }
}
