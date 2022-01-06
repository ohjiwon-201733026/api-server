package com.gloomy.server.application.user;

import com.gloomy.server.application.AbstractControllerTest;
import com.gloomy.server.application.comment.CommentDTO;
import com.gloomy.server.application.comment.CommentService;
import com.gloomy.server.application.feed.FeedDTO;
import com.gloomy.server.application.feed.FeedService;
import com.gloomy.server.application.image.TestImage;
import com.gloomy.server.domain.feed.Feed;
import com.gloomy.server.domain.user.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
public class MyPageRestControllerTest extends AbstractControllerTest {

    @Autowired
    UserService userService;
    @Autowired
    FeedService feedService;
    @Autowired
    CommentService commentService;
    @Autowired
    WebApplicationContext webApplicationContext;
    User user;
    UserDTO.UpdateUserDTO updateUserDTO;

    @BeforeEach
    public void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        this.user = User.of("test@email.com", "testName", new Password("test")
                , Sex.MALE, 2020, 01, 01, JoinStatus.JOIN);
    }

    @DisplayName("find user feed")
    @Test
    public void userFeed() throws Exception {
        User saveUser=userService.createUser(user);
        FeedDTO.Request feedDto1=new FeedDTO.Request(true, "111.111.111.111", saveUser.getId(), "test content 1", new TestImage().makeImages(1));
        FeedDTO.Request feedDto2=new FeedDTO.Request(true, "222.222.222.222", saveUser.getId(), "test content 2", new TestImage().makeImages(1));

        feedService.createFeed(feedDto1);
        feedService.createFeed(feedDto2);

        this.mockMvc.perform(get("/myPage/feed/{userId}", saveUser.getId())
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("find user Comment")
    @Test
    public void userComment() throws Exception {
        User saveUser=userService.createUser(user);
        FeedDTO.Request feedDto1=new FeedDTO.Request(true, "111.111.111.111", saveUser.getId(), "test content 1", new TestImage().makeImages(1));

        Feed feed1=feedService.createFeed(feedDto1);

        CommentDTO.Request comment1 = new CommentDTO.Request("test comment 1",feed1.getId(),saveUser.getId());
        CommentDTO.Request comment2 = new CommentDTO.Request("test comment 2",feed1.getId(),saveUser.getId());

        commentService.createComment(comment1);
        commentService.createComment(comment2);

        this.mockMvc.perform(get("/myPage/comment/{userId}", saveUser.getId())
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

}
