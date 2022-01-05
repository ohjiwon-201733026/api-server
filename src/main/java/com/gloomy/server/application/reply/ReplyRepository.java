package com.gloomy.server.application.reply;

import com.gloomy.server.domain.comment.Comment;
import com.gloomy.server.domain.reply.Reply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReplyRepository extends JpaRepository<Reply, Long> {
    List<Reply> findAllByCommentId(Comment commentId);
}
