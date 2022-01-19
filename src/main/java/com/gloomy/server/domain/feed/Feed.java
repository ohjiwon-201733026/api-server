package com.gloomy.server.domain.feed;

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

    @Column(name = "status", nullable = false)
    private FEED_STATUS status;

    @Embedded
    private Content content;

    @Embedded
    private LikeCount likeCount;

    Feed() {
    }

    @Builder
    private Feed(Ip ip, User userId, Password password, FEED_STATUS status, Content content, LikeCount likeCount) {
        this.ip = ip;
        this.userId = userId;
        this.password = password;
        this.status = status;
        this.content = content;
        this.likeCount = likeCount;
    }

    public static Feed of(User userId, String content) {
        return builder()
                .ip(new Ip("111.111.111.111"))
                .userId(userId)
                .password(null)
                .status(FEED_STATUS.ACTIVE)
                .content(new Content(content))
                .likeCount(new LikeCount(0))
                .build();
    }

    public static Feed of(String password, String content) {
        return builder()
                .ip(new Ip("111.111.111.111"))
                .userId(null)
                .password(new Password(password))
                .status(FEED_STATUS.ACTIVE)
                .content(new Content(content))
                .likeCount(new LikeCount(0))
                .build();
    }

    public void setStatus(FEED_STATUS status) {
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
                    && status == targetFeed.status
                    && Objects.equals(content.getContent(), targetFeed.content.getContent());
            if (userId != null) {
                result &= Objects.equals(userId.getId(), targetFeed.userId.getId());
                return result;
            }
            result &= Objects.equals(password.getPassword(), targetFeed.password.getPassword());
            return result;
        }
        return false;
    }
}
