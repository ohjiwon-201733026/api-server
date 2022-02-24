package com.gloomy.server.application.notice;

import com.gloomy.server.domain.comment.Comment;
import com.gloomy.server.domain.common.entity.BaseEntity;
import com.gloomy.server.domain.feed.Feed;
import com.gloomy.server.domain.feedlike.FeedLike;
import com.gloomy.server.domain.notice.Notice;
import com.gloomy.server.domain.notice.Type;
import com.gloomy.server.domain.reply.Reply;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NoticeService {
    private final NoticeRepository noticeRepository;

    public NoticeService(NoticeRepository noticeRepository) {
        this.noticeRepository = noticeRepository;
    }

    public Notice createNotice(Feed feedId, BaseEntity entityId, Type entityType) {
        validateIdAndType(feedId, entityId, entityType);
        return createEntityNotice(feedId, entityId, entityType);
    }

    private Notice createEntityNotice(Feed feedId, BaseEntity entityId, Type entityType) {
        if (entityType == Type.COMMENT) {
            return createCommentNotice(feedId, (Comment) entityId, entityType);
        }
        if (entityType == Type.REPLY) {
            return createReplyNotice(feedId, (Reply) entityId, entityType);
        }
        return createFeedLikeNotice(feedId, (FeedLike) entityId, entityType);
    }

    private Notice createCommentNotice(Feed feedId, Comment commentId, Type entityType) {
        Notice commentNotice = Notice.of(feedId, commentId, entityType);
        return noticeRepository.save(commentNotice);
    }

    private Notice createReplyNotice(Feed feedId, Reply replyId, Type entityType) {
        Notice replyNotice = Notice.of(feedId, replyId, entityType);
        return noticeRepository.save(replyNotice);
    }

    private Notice createFeedLikeNotice(Feed feedId, FeedLike feedLikeId, Type entityType) {
        Notice feedLikeNotice = Notice.of(feedId, feedLikeId, entityType);
        return noticeRepository.save(feedLikeNotice);
    }

    @Transactional
    public void deleteAll() {
        noticeRepository.deleteAll();
    }

    private void validateIdAndType(Feed feedId, BaseEntity entity, Type entityType) {
        if (feedId == null || entity == null || entityType == null) {
            throw new IllegalArgumentException("[NoticeService] 알림 생성 파라미터가 유효하지 않습니다.");
        }
        if (entityType == Type.COMMENT && !(entity instanceof Comment)) {
            throw new IllegalArgumentException("[NoticeService] 댓글이 유효하지 않습니다.");
        }
        if (entityType == Type.REPLY && !(entity instanceof Reply)) {
            throw new IllegalArgumentException("[NoticeService] 대댓글이 유효하지 않습니다.");
        }
        if (entityType == Type.LIKE && !(entity instanceof FeedLike)) {
            throw new IllegalArgumentException("[NoticeService] 좋아요가 유효하지 않습니다.");
        }
    }
}
