package com.gloomy.server.application.comment;

import com.gloomy.server.application.feed.FeedService;
import com.gloomy.server.domain.comment.COMMENT_STATUS;
import com.gloomy.server.domain.comment.Comment;
import com.gloomy.server.domain.feed.Content;
import com.gloomy.server.domain.feed.Feed;
import com.gloomy.server.domain.feed.Password;
import com.gloomy.server.domain.user.User;
import com.gloomy.server.domain.user.UserService;
import org.springframework.stereotype.Service;

@Service
public class CommentService {
    private UserService userService;
    private FeedService feedService;
    private CommentRepository commentRepository;

    public CommentService(UserService userService, FeedService feedService, CommentRepository commentRepository) {
        this.userService = userService;
        this.feedService = feedService;
        this.commentRepository = commentRepository;
    }

    public Comment createComment(CommentDTO.Request commentDTO) throws IllegalArgumentException {
        validateCommentDTO(commentDTO);
        return commentRepository.save(makeComment(commentDTO));
    }

    private Comment makeComment(CommentDTO.Request commentDTO) {
        Feed feedId = feedService.findOneFeed(commentDTO.getFeedId());
        if (commentDTO.getUserId() != null) {
            User userId = userService.findUser(commentDTO.getUserId());
            return Comment.of(new Content(commentDTO.getContent()), feedId, userId);
        }
        return Comment.of(new Content(commentDTO.getContent()), feedId, new Password(commentDTO.getPassword()));
    }

    private void validateCommentDTO(CommentDTO.Request commentDTO) throws IllegalArgumentException {
        if ((commentDTO.getContent() == null || commentDTO.getFeedId() == null)
                || (commentDTO.getUserId() == null && commentDTO.getPassword() == null)
                || (commentDTO.getUserId() != null && commentDTO.getPassword() != null)) {
            throw new IllegalArgumentException("[CommentService] 회원 댓글 등록 요청 메시지가 잘못되었습니다.");
        }
    }

    public Comment findComment(Long commentId) {
        if (commentId == null || commentId <= 0L) {
            throw new IllegalArgumentException("[CommentService] 해당 댓글 ID가 유효하지 않습니다.");
        }
        return commentRepository.findById(commentId).orElseThrow(() -> {
            throw new IllegalArgumentException("[CommentService] 해당 댓글 ID가 존재하지 않습니다.");
        });
    }

    public Comment updateComment(Long commentId, UpdateCommentDTO.Request updateCommentDTO) {
        validateUpdateCommentRequest(commentId, updateCommentDTO);
        Comment foundComment = findComment(commentId);
        foundComment.setContent(new Content(updateCommentDTO.getContent()));
        return commentRepository.save(foundComment);
    }

    private void validateUpdateCommentRequest(Long commentId, UpdateCommentDTO.Request updateCommentDTO) {
        if (commentId == null || commentId <= 0L) {
            throw new IllegalArgumentException("[CommentService] 해당 댓글 ID가 유효하지 않습니다.");
        }
        if (updateCommentDTO.getContent() == null) {
            throw new IllegalArgumentException("[CommentService] 댓글 수정 요청 메시지가 잘못되었습니다.");
        }
    }

    public Comment deleteComment(Long commentId) {
        Comment foundComment = findComment(commentId);
        foundComment.setStatus(COMMENT_STATUS.INACTIVE);
        return commentRepository.save(foundComment);
    }

    public void deleteAll() {
        commentRepository.deleteAll();
    }
}
