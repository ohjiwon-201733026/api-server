package com.gloomy.server.application.notice;

import com.gloomy.server.domain.comment.Comment;
import com.gloomy.server.domain.feedlike.FeedLike;
import com.gloomy.server.domain.notice.Notice;
import com.gloomy.server.domain.reply.Reply;
import com.gloomy.server.domain.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {
    Optional<Notice> findById(Long id);

    Optional<Notice> findByCommentId(Comment commentId);

    Optional<Notice> findByReplyId(Reply replyId);

    Optional<Notice> findByFeedLikeId(FeedLike feedLikeId);

    Page<Notice> findAllByUserIdOrderByIdDesc(Pageable pageable, User userId);

    Optional<Notice> findFirstByUserId(User userId);

    Integer countAllByUserId(User userId);
}
