package com.gloomy.server.application.comment;

import com.gloomy.server.application.feed.FeedService;
import com.gloomy.server.domain.comment.COMMENT_STATUS;
import com.gloomy.server.domain.comment.Comment;
import com.gloomy.server.domain.feed.Content;
import com.gloomy.server.domain.feed.Feed;
import com.gloomy.server.domain.feed.Password;
import com.gloomy.server.domain.user.User;
import com.gloomy.server.domain.user.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public Page<Comment> getFeedAllComments(Pageable pageable, Long feedId) {
        if (pageable == null) {
            throw new IllegalArgumentException("[CommentService] Pageable이 유효하지 않습니다.");
        }
        if (feedId == null || feedId <= 0L) {
            throw new IllegalArgumentException("[CommentService] 해당 댓글 ID가 유효하지 않습니다.");
        }
        List<Comment> feedAllComments = findAllComments(feedId);
        return new PageImpl<>(feedAllComments, pageable, feedAllComments.size());
    }

    public Page<Comment> getFeedAllActiveComments(Pageable pageable, Long feedId) {
        if (pageable == null) {
            throw new IllegalArgumentException("[CommentService] Pageable이 유효하지 않습니다.");
        }
        if (feedId == null || feedId <= 0L) {
            throw new IllegalArgumentException("[CommentService] 해당 댓글 ID가 유효하지 않습니다.");
        }
        Feed foundFeed = feedService.findOneFeed(feedId);
        return commentRepository.findAllByFeedIdAndStatus(pageable,
                foundFeed, COMMENT_STATUS.ACTIVE);
    }

    public Page<Comment> getCommentByIdAndActive(Pageable pageable,Long userId){
        if (pageable == null) {
            throw new IllegalArgumentException("[CommentService] Pageable이 유효하지 않습니다.");
        }
        if (userId == null || userId <= 0L) {
            throw new IllegalArgumentException("[CommentService] 해당 댓글 ID가 유효하지 않습니다.");
        }
        User findUser=userService.findUser(userId);
        return commentRepository.findAllByUserIdAndStatus(pageable,findUser,COMMENT_STATUS.ACTIVE);
    }

    public List<Comment> findAllComments(Long feedId) {
        Feed foundFeed = feedService.findOneFeed(feedId);
        return commentRepository.findAllByFeedId(foundFeed);
    }
}
