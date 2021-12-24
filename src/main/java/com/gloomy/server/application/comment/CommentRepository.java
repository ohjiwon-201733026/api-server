package com.gloomy.server.application.comment;

import com.gloomy.server.domain.comment.Comment;
import com.gloomy.server.domain.feed.Feed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByFeedId(Feed feedId);
}
