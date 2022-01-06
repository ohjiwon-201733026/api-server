package com.gloomy.server.application.feed;

import com.gloomy.server.domain.feed.Feed;
import com.gloomy.server.domain.user.JoinStatus;
import com.gloomy.server.domain.user.Password;
import com.gloomy.server.domain.user.Sex;
import com.gloomy.server.domain.user.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

public class FeedTest {
    User user;
    Feed feed1;
    Feed feed2;

    @BeforeEach
    public void setUp(){
        this.user= User.of("test@email.com","testName",new Password("test")
                , Sex.MALE,2020,01,01, JoinStatus.JOIN);

        this.feed1=Feed.of("111.111.111.111",user,"testContent1");
        this.feed2=Feed.of("222.222.222.222",user,"testContent2");
    }

    @DisplayName("Feed-User 연관관계 test")
    @Test
    public void user(){

        List<Feed> feeds=user.getFeeds();

        Assertions.assertEquals(feeds.size(),1);
        Assertions.assertEquals(feeds.get(0),this.feed1);
    }

    @DisplayName("Feed remove")
    @Test
    public void feedRemove(){
        final Long removeFeedId=2L;
        feed1.setId(1L);
        feed2.setId(2L);

        user.removeFeed(2L);

        Assertions.assertEquals(user.getFeeds().size(),1);
        Assertions.assertEquals(user.getFeeds().get(0).getId(),1L);
    }
}
