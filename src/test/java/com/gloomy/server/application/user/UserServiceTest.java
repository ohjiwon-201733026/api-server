package com.gloomy.server.application.user;

import com.gloomy.server.application.comment.CommentDTO;
import com.gloomy.server.application.comment.CommentService;
import com.gloomy.server.application.feed.FeedDTO;
import com.gloomy.server.application.feed.FeedService;
import com.gloomy.server.application.feed.TestFeedDTO;
import com.gloomy.server.application.image.TestImage;
import com.gloomy.server.domain.comment.Comment;
import com.gloomy.server.domain.feed.Feed;
import com.gloomy.server.domain.user.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.parameters.P;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest(properties = {
        "spring.config.location=classpath:application.yml,classpath:aws.yml"
})
public class UserServiceTest {

    @Autowired
    UserService userService;
    @Autowired
    FeedService feedService;
    @Autowired
    CommentService commentService;
    User user;
    UserDTO.UpdateUserDTO.Request updateUserDTO;
    TestFeedDTO testFeedDTO;



    @BeforeEach
    public void setUp(){
        this.user= User.of("test@email.com","testName",new Password("test")
        , Sex.MALE,2020,01,01, JoinStatus.JOIN);
        this.updateUserDTO= UserDTO.UpdateUserDTO.Request.builder()
                .email("updateEmail@email.com")
                .sex(Sex.FEMALE)
                .dateOfBirth(LocalDate.of(2022,01,01).toString())
//                .image("testImg")
                .build();
        testFeedDTO = new TestFeedDTO(user, 1);

    }
    @DisplayName("update")
    @Test
    public void update(){
        User saveUser=userService.createUser(user);

        userService.updateUser(saveUser.getId(),updateUserDTO);

        User updateUser=userService.findUser(saveUser.getId());
        checkUpdateUser(updateUser);
    }

    private void checkUpdateUser(User user){
        Assertions.assertEquals(user.getEmail(),updateUserDTO.getEmail());
        Assertions.assertEquals(user.getSex(),updateUserDTO.getSex());
        Assertions.assertEquals(user.getDateOfBirth(),updateUserDTO.getDateOfBirth());
//        Assertions.assertEquals(user.getProfile().getImage().getImage(),updateUserDTO.getImage());
    }

//    @DisplayName("[ MyPage ] find user Feed")
//    @Test
//    @Transactional
//    public void userFeed(){
//        User saveUser=userService.createUser(user);
//        FeedDTO.Request feedDto1=new FeedDTO.Request(true, "111.111.111.111", saveUser.getId(), "test content 1", new TestImage().makeImages(1));
//        FeedDTO.Request feedDto2=new FeedDTO.Request(true, "222.222.222.222", saveUser.getId(), "test content 2", new TestImage().makeImages(1));
//
//        feedService.createFeed(feedDto1);
//        feedService.createFeed(feedDto2);
//
//        List<Feed> feedList=userService.findFeeds(saveUser.getId());
//
//        Assertions.assertEquals(feedList.size(),2);
//        checkFeed(feedList.get(0),feedDto1);
//        checkFeed(feedList.get(1),feedDto2);
//
//    }
//
//    private void checkFeed(Feed feed, FeedDTO.Request feedDTO){
//        Assertions.assertEquals(feedDTO.getIsUser(),feed.getIsUser().getIsUser());
//        Assertions.assertEquals(feedDTO.getIp(),feed.getIp().getIp());
//        Assertions.assertEquals(feedDTO.getUserId(),feed.getUserId().getId());
//        Assertions.assertEquals(feedDTO.getContent(),feed.getContent().getContent());
//    }
//
//    @DisplayName("[ MyPage ] find user Comment")
//    @Test
//    @Transactional
//    public void userComment() {
//        User saveUser = userService.createUser(user);
//
//        FeedDTO.Request feedDto1=new FeedDTO.Request(true, "111.111.111.111", saveUser.getId(), "test content 1", new TestImage().makeImages(1));
//        Feed feed1=feedService.createFeed(feedDto1);
//
//        CommentDTO.Request comment1 = new CommentDTO.Request("test comment 1",feed1.getId(),saveUser.getId());
//        CommentDTO.Request comment2 = new CommentDTO.Request("test comment 2",feed1.getId(),saveUser.getId());
//
//        commentService.createComment(comment1);
//        commentService.createComment(comment2);
//
//        List<Comment> comments=userService.findComments(saveUser.getId());
//
//        Assertions.assertEquals(comments.size(),2);
//        checkComment(comments.get(0),comment1);
//        checkComment(comments.get(1),comment2);
//    }
//
//    private void checkComment(Comment com,CommentDTO.Request request){
//        Assertions.assertEquals(com.getContent().getContent(),request.getContent());
//        Assertions.assertEquals(com.getFeedId().getId(),request.getFeedId());
//        Assertions.assertEquals(com.getUserId().getId(),request.getUserId());
//    }
}
