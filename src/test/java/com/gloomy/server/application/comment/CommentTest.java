package com.gloomy.server.application.comment;

import com.gloomy.server.domain.comment.COMMENT_STATUS;
import com.gloomy.server.domain.comment.Comment;
import com.gloomy.server.domain.feed.Content;
import com.gloomy.server.domain.feed.Feed;
import com.gloomy.server.domain.user.JoinStatus;
import com.gloomy.server.domain.user.Password;
import com.gloomy.server.domain.user.Sex;
import com.gloomy.server.domain.user.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.crypto.Cipher;
import java.util.List;

public class CommentTest {

    User testUser;
    Feed testFeed1;
    Feed testFeed2;
    Comment comment1;
    Comment comment2;

    @BeforeEach
    public void setUp(){
        this.testUser= User.of("test@email.com","testName",new Password("test")
                , Sex.MALE,2020,01,01, JoinStatus.JOIN);

        this.testFeed1= Feed.of("111.111.111.111",testUser,"testContent1");
        this.testFeed2=Feed.of("222.222.222.222",testUser,"testContent2");
        this.comment1=Comment.of(new Content("testComment1"), testFeed1, testUser);
        this.comment2=Comment.of(new Content("testComment2"), testFeed2, testUser);
    }
    
    @DisplayName("User - Comment 연관관계")
    @Test
    public void userComment(){
        List<Comment> comments= testUser.getComments();

        Assertions.assertEquals(comments.size(),2);
        checkComments(comments.get(0),comment1);
        checkComments(comments.get(1),comment2);
    }

    private void checkComments(Comment a,Comment b){
        Assertions.assertEquals(a.getContent().getContent(),b.getContent().getContent());
        Assertions.assertEquals(a.getFeedId().getId(),b.getFeedId().getId());
    }
}
