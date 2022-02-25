package com.gloomy.server.application.comment;

import com.gloomy.server.domain.comment.Comment;
import com.gloomy.server.domain.common.entity.Status;
import com.gloomy.server.domain.feed.Feed;
import com.gloomy.server.domain.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByFeedId(Feed feedId);

    Page<Comment> findAllByFeedIdAndStatus(Pageable pageable, Feed feedId, Status status);

    Page<Comment> findAllByUserIdAndStatus(Pageable pageable, User userId, Status status);

    Integer countCommentByFeedId(Feed feedId);
}
