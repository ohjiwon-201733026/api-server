package com.gloomy.server.domain.feed;

import com.gloomy.server.domain.user.User;
import lombok.AccessLevel;
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
    private IsUser isUser;

    @Embedded
    private Ip ip;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User userId;

    @Embedded
    private Password password;

    @Column(name = "status", nullable = false)
    private FEED_STATUS status;

    @Embedded
    private Content content;

    Feed() {
    }

    @Builder(builderClassName = "userFeedBuilder", builderMethodName = "userFeedBuilder", access = AccessLevel.PRIVATE)
    private Feed(IsUser isUser, Ip ip, User userId, FEED_STATUS status, Content content) {
        this.isUser = isUser;
        this.ip = ip;
        this.userId = userId;
        this.status = status;
        this.content = content;
    }

    @Builder(builderClassName = "nonUserFeedBuilder", builderMethodName = "nonUserFeedBuilder", access = AccessLevel.PRIVATE)
    private Feed(IsUser isUser, Ip ip, Password password, FEED_STATUS status, Content content) {
        this.isUser = isUser;
        this.ip = ip;
        this.password = password;
        this.status = status;
        this.content = content;
    }

    public static Feed of(String ip, User userId, String content) {
        return Feed.userFeedBuilder()
                .isUser(new IsUser(true))
                .ip(new Ip(ip))
                .userId(userId)
                .status(FEED_STATUS.ACTIVE)
                .content(new Content(content))
                .build();
    }

    public static Feed of(String ip, String password, String content) {
        return Feed.nonUserFeedBuilder()
                .isUser(new IsUser(false))
                .ip(new Ip(ip))
                .password(new Password(password))
                .status(FEED_STATUS.ACTIVE)
                .content(new Content(content))
                .build();
    }

    public void setStatus(FEED_STATUS status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Feed) {
            Feed targetFeed = (Feed) o;
            boolean result = Objects.equals(id, targetFeed.id)
                    && Objects.equals(isUser.getIsUser(), targetFeed.isUser.getIsUser())
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
