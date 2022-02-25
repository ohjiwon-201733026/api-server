package com.gloomy.server.application.notice;

import com.gloomy.server.domain.notice.Notice;
import com.gloomy.server.domain.notice.Type;
import lombok.Builder;
import lombok.Getter;

public class NoticeDTO {
    @Getter
    public static class Response {
        private final Long id;
        private final Long userId;
        private final Long feedId;
        private final Long commentId;
        private final Long replyId;
        private final Long likeId;
        private final String type;
        private final Boolean isRead;
        private final Integer commentCount;
        private final Integer likeCount;
        private final String title;
        private final String status;
        private final String createdAt;
        private final String updatedAt;
        private final String deletedAt;

        @Builder
        public Response(Long id, Long userId, Long feedId, Long commentId, Long replyId, Long likeId, String type, Boolean isRead, Integer commentCount, Integer likeCount, String title, String status, String createdAt, String updatedAt, String deletedAt) {
            this.id = id;
            this.userId = userId;
            this.feedId = feedId;
            this.commentId = commentId;
            this.replyId = replyId;
            this.likeId = likeId;
            this.type = type;
            this.isRead = isRead;
            this.commentCount = commentCount;
            this.likeCount = likeCount;
            this.title = title;
            this.status = status;
            this.createdAt = createdAt;
            this.updatedAt = updatedAt;
            this.deletedAt = deletedAt;
        }

        public static NoticeDTO.Response of(Notice notice, Integer likeCount, Integer commentCount) {
            if (notice.getType() == Type.COMMENT) {
                return Response.builder()
                        .id(notice.getId())
                        .userId(notice.getUserId().getId())
                        .feedId(notice.getFeedId().getId())
                        .commentId(notice.getCommentId().getId())
                        .replyId(null)
                        .likeId(null)
                        .type(notice.getType().toString())
                        .isRead(notice.getIsRead().getIsRead())
                        .likeCount(likeCount)
                        .commentCount(commentCount)
                        .title(notice.getFeedId().getTitle().getTitle())
                        .status(notice.getStatus().toString())
                        .createdAt(notice.getCreatedAt().getCreatedAt().toString())
                        .updatedAt(notice.getUpdatedAt().getUpdatedAt().toString())
                        .deletedAt(notice.getDeletedAt().getDeletedAt().toString())
                        .build();
            }
            if (notice.getType() == Type.REPLY) {
                return Response.builder()
                        .id(notice.getId())
                        .userId(notice.getUserId().getId())
                        .feedId(notice.getFeedId().getId())
                        .commentId(null)
                        .replyId(notice.getReplyId().getId())
                        .likeId(null)
                        .type(notice.getType().toString())
                        .isRead(notice.getIsRead().getIsRead())
                        .likeCount(likeCount)
                        .commentCount(commentCount)
                        .title(notice.getFeedId().getTitle().getTitle())
                        .status(notice.getStatus().toString())
                        .createdAt(notice.getCreatedAt().getCreatedAt().toString())
                        .updatedAt(notice.getUpdatedAt().getUpdatedAt().toString())
                        .deletedAt(notice.getDeletedAt().getDeletedAt().toString())
                        .build();
            }
            return Response.builder()
                    .id(notice.getId())
                    .userId(notice.getUserId().getId())
                    .feedId(notice.getFeedId().getId())
                    .commentId(null)
                    .replyId(null)
                    .likeId(notice.getFeedLikeId().getId())
                    .type(notice.getType().toString())
                    .isRead(notice.getIsRead().getIsRead())
                    .likeCount(likeCount)
                    .commentCount(commentCount)
                    .title(notice.getFeedId().getTitle().getTitle())
                    .status(notice.getStatus().toString())
                    .createdAt(notice.getCreatedAt().getCreatedAt().toString())
                    .updatedAt(notice.getUpdatedAt().getUpdatedAt().toString())
                    .deletedAt(notice.getDeletedAt().getDeletedAt().toString())
                    .build();
        }
    }
}
