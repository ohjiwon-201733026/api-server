package com.gloomy.server.application.user;

import com.gloomy.server.domain.comment.Comment;
import com.gloomy.server.domain.common.entity.CreatedAt;
import com.gloomy.server.domain.common.entity.DeletedAt;
import com.gloomy.server.domain.common.entity.Status;
import com.gloomy.server.domain.common.entity.UpdatedAt;
import com.gloomy.server.domain.feed.Content;
import com.gloomy.server.domain.feed.Feed;
import com.gloomy.server.domain.user.User;

public class MyPageTestFixture {

    public static class CommentTestFixture{
        public static Comment createComment(Feed feedId, User userId){
            return Comment.of(new Content("test_content"),
                    feedId,userId);
        }
    }
}
