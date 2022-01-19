package com.gloomy.server.application.comment;

import com.gloomy.server.application.feed.FeedService;
import com.gloomy.server.application.feed.TestFeedDTO;
import com.gloomy.server.application.feed.TestUserDTO;
import com.gloomy.server.application.image.ImageService;
import com.gloomy.server.domain.comment.COMMENT_STATUS;
import com.gloomy.server.domain.comment.Comment;
import com.gloomy.server.domain.feed.Feed;
import com.gloomy.server.domain.user.User;
import com.gloomy.server.domain.user.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(properties = {
        "spring.config.location=classpath:application.yml,classpath:aws.yml"
})
class CommentServiceTest {
    /*
    @Autowired
    private FeedService feedService;
    @Autowired
    private UserService userService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private ImageService imageService;

    private TestCommentDTO testCommentDTO;

    @BeforeEach
    void beforeEach() {
        User testUser = new TestUserDTO().makeTestUser();
        userService.createUser(testUser);
        TestFeedDTO testFeedDTO = new TestFeedDTO(testUser, 1);
        Feed testFeed = feedService.createFeed(testFeedDTO.makeNonUserFeedDTO());
        testCommentDTO = new TestCommentDTO(testUser.getId(), testFeed.getId());
    }

    @AfterEach
    void afterEach() {
        imageService.deleteAll();
        commentService.deleteAll();
        feedService.deleteAll();
        userService.deleteAll();
    }

    @Test
    void 댓글_생성_비회원_성공() {
        CommentDTO.Request nonUserCommentDTO = testCommentDTO.makeNonUserCommentDTO();

        Comment createdComment = commentService.createComment(nonUserCommentDTO);
        Comment foundComment = commentService.findComment(createdComment.getId());
        assertEquals(foundComment, createdComment);
    }

    @Test
    void 댓글_생성_비회원_실패() {
        CommentDTO.Request nonUserCommentDTOWithoutContent =
                new CommentDTO.Request(null, testCommentDTO.getFeedId(), testCommentDTO.getPassword());
        CommentDTO.Request nonUserCommentDTOWithoutFeedId =
                new CommentDTO.Request(testCommentDTO.getContent(), null, testCommentDTO.getPassword());
        CommentDTO.Request nonUserCommentDTOWithoutPassword =
                new CommentDTO.Request(testCommentDTO.getContent(), testCommentDTO.getFeedId(), (String) null);

        checkCreatedCommentFail(nonUserCommentDTOWithoutContent);
        checkCreatedCommentFail(nonUserCommentDTOWithoutFeedId);
        checkCreatedCommentFail(nonUserCommentDTOWithoutPassword);
    }

    @Test
    void 댓글_생성_회원_성공() {
        CommentDTO.Request userCommentDTO = testCommentDTO.makeUserCommentDTO();

        Comment createdComment = commentService.createComment(userCommentDTO);
        Comment foundComment = commentService.findComment(createdComment.getId());
        assertEquals(foundComment, createdComment);
    }

    @Test
    void 댓글_생성_회원_실패() {
        CommentDTO.Request userCommentDTOWithoutContent =
                new CommentDTO.Request(null, testCommentDTO.getFeedId(), testCommentDTO.getUserId());
        CommentDTO.Request userCommentDTOWithoutFeedId =
                new CommentDTO.Request(testCommentDTO.getContent(), null, testCommentDTO.getUserId());
        CommentDTO.Request userCommentDTOWithoutUserId =
                new CommentDTO.Request(testCommentDTO.getContent(), testCommentDTO.getFeedId(), (Long) null);

        checkCreatedCommentFail(userCommentDTOWithoutContent);
        checkCreatedCommentFail(userCommentDTOWithoutFeedId);
        checkCreatedCommentFail(userCommentDTOWithoutUserId);
    }

    @Test
    void 댓글_조회_비회원_성공() {
        Comment createdComment = commentService.createComment(testCommentDTO.makeNonUserCommentDTO());

        Comment foundComment = commentService.findComment(createdComment.getId());

        assertEquals(foundComment, createdComment);
    }

    @Test
    void 댓글_조회_비회원_실패() {
        Comment createdComment = commentService.createComment(testCommentDTO.makeNonUserCommentDTO());

        commentService.deleteAll();

        checkFoundCommentFail(createdComment.getId(), "[CommentService] 해당 댓글 ID가 존재하지 않습니다.");
    }

    @Test
    void 댓글_조회_회원_성공() {
        Comment createdComment = commentService.createComment(testCommentDTO.makeUserCommentDTO());

        Comment foundComment = commentService.findComment(createdComment.getId());

        assertEquals(foundComment, createdComment);
    }

    @Test
    void 댓글_조회_회원_실패() {
        Comment createdComment = commentService.createComment(testCommentDTO.makeUserCommentDTO());

        commentService.deleteAll();

        checkFoundCommentFail(createdComment.getId(), "[CommentService] 해당 댓글 ID가 존재하지 않습니다.");
    }

    @Test
    void 댓글_조회_공통_실패() {
        checkFoundCommentFail(0L, "[CommentService] 해당 댓글 ID가 유효하지 않습니다.");
        checkFoundCommentFail(null, "[CommentService] 해당 댓글 ID가 유효하지 않습니다.");
    }

    @Test
    void 피드_댓글_전체_조회_성공() {
        createComments(3);

        Page<Comment> foundAllComments = commentService.getFeedAllComments(
                PageRequest.of(0, 10), testCommentDTO.getFeedId());

        assertEquals(foundAllComments.getContent().size(), 3);
    }

    @Test
    void 피드_댓글_전체_조회_실패() {
        PageRequest pageable = PageRequest.of(0, 10);

        imageService.deleteAll();
        feedService.deleteAll();

        checkFoundAllCommentFail(pageable, 0L, "[CommentService] 해당 댓글 ID가 유효하지 않습니다.");
        checkFoundAllCommentFail(pageable, null, "[CommentService] 해당 댓글 ID가 유효하지 않습니다.");
        checkFoundAllCommentFail(pageable, testCommentDTO.getFeedId(), "[CommentService] 해당 댓글 ID가 존재하지 않습니다.");
        checkFoundAllCommentFail(null, testCommentDTO.getFeedId(), "[CommentService] pageable이 유효하지 않습니다.");
    }

    @Test
    void 피드_활성_댓글_전체_조회_성공() {
        Comment activeComment = commentService.createComment(testCommentDTO.makeNonUserCommentDTO());
        Comment inactiveComment = commentService.createComment(testCommentDTO.makeNonUserCommentDTO());

        commentService.deleteComment(inactiveComment.getId());
        Page<Comment> feedAllActiveComments = commentService.getFeedAllActiveComments(
                PageRequest.of(0, 10), testCommentDTO.getFeedId());

        assertEquals(feedAllActiveComments.getContent().size(), 1);
        assertEquals(feedAllActiveComments.getContent().get(0), activeComment);
    }
    
    @Test
    void 피드_활성_댓글_전체_조회_실패() {
        Pageable pageable = PageRequest.of(0, 10);

        checkFoundAllActiveCommentFail(null, testCommentDTO.getFeedId(),
                "[CommentService] Pageable이 유효하지 않습니다.");
        checkFoundAllActiveCommentFail(pageable, 0L,
                "[CommentService] Pageable이 유효하지 않습니다.");
        checkFoundAllActiveCommentFail(pageable, null,
                "[CommentService] Pageable이 유효하지 않습니다.");
    }

    @Test
    void 댓글_수정_성공() {
        Comment createdComment = commentService.createComment(testCommentDTO.makeNonUserCommentDTO());
        String updateContent = "새 댓글";
        UpdateCommentDTO.Request updateCommentDTO = new UpdateCommentDTO.Request();
        updateCommentDTO.setContent(updateContent);

        Comment updatedComment = commentService.updateComment(createdComment.getId(), updateCommentDTO);
        Comment foundComment = commentService.findComment(createdComment.getId());

        assertEquals(updatedComment, foundComment);
    }

    @Test
    void 댓글_수정_실패() {
        Comment deletedComment = commentService.createComment(testCommentDTO.makeNonUserCommentDTO());
        String updateContent = "새 댓글";
        UpdateCommentDTO.Request updateCommentDTO = new UpdateCommentDTO.Request();
        updateCommentDTO.setContent(updateContent);
        UpdateCommentDTO.Request updateCommentDTOWithoutContent = new UpdateCommentDTO.Request();

        commentService.deleteAll();
        Comment createdComment = commentService.createComment(testCommentDTO.makeNonUserCommentDTO());

        checkUpdatedCommentFail(createdComment.getId(), updateCommentDTOWithoutContent,
                "[CommentService] 댓글 수정 요청 메시지가 잘못되었습니다.");
        checkUpdatedCommentFail(0L, updateCommentDTO,
                "[CommentService] 해당 댓글 ID가 유효하지 않습니다.");
        checkUpdatedCommentFail(null, updateCommentDTO,
                "[CommentService] 해당 댓글 ID가 유효하지 않습니다.");
        checkUpdatedCommentFail(deletedComment.getId(), updateCommentDTO,
                "[CommentService] 해당 댓글 ID가 존재하지 않습니다.");
    }

    @Test
    void 댓글_삭제_성공() {
        Comment createdComment = commentService.createComment(testCommentDTO.makeNonUserCommentDTO());
        Comment deletedComment = commentService.deleteComment(createdComment.getId());
        assertEquals(deletedComment.getStatus(), COMMENT_STATUS.INACTIVE);
    }

    @Test
    void 댓글_삭제_실패() {
        Comment createdComment = commentService.createComment(testCommentDTO.makeNonUserCommentDTO());

        commentService.deleteAll();

        checkDeletedCommentFail(0L, "[CommentService] 해당 댓글 ID가 유효하지 않습니다.");
        checkDeletedCommentFail(null, "[CommentService] 해당 댓글 ID가 유효하지 않습니다.");
        checkDeletedCommentFail(createdComment.getId(), "[CommentService] 해당 댓글 ID가 존재하지 않습니다.");
    }

    private void createComments(int commentSize) {
        for (int num = 0; num < commentSize; num++) {
            commentService.createComment(testCommentDTO.makeUserCommentDTO());
        }
    }

    private void checkCreatedCommentFail(CommentDTO.Request commentDTO) {
        assertThrows(IllegalArgumentException.class, () -> {
            commentService.createComment(commentDTO);
        }, "[CommentService] 비회원 댓글 등록 요청 메시지가 잘못되었습니다.");
    }

    private void checkFoundCommentFail(Long commentId, String errorMessage) {
        assertThrows(IllegalArgumentException.class, () -> {
            commentService.findComment(commentId);
        }, errorMessage);
    }

    private void checkFoundAllCommentFail(Pageable pageable, Long feedId, String errorMessage) {
        assertThrows(IllegalArgumentException.class, () -> {
            commentService.getFeedAllComments(pageable, feedId);
        }, errorMessage);
    }

    private void checkFoundAllActiveCommentFail(Pageable pageable, Long feedId, String errorMessage) {
        assertThrows(IllegalArgumentException.class, () -> {
            commentService.getFeedAllActiveComments(pageable, feedId);
        }, errorMessage);
    }

    private void checkUpdatedCommentFail(Long commentId, UpdateCommentDTO.Request updateCommentDTO, String errorMessage) {
        assertThrows(IllegalArgumentException.class, () -> {
            commentService.updateComment(commentId, updateCommentDTO);
        }, errorMessage);
    }

    private void checkDeletedCommentFail(Long feedId, String errorMessage) {
        assertThrows(IllegalArgumentException.class, () -> {
            commentService.deleteComment(feedId);
        }, errorMessage);
    }
    */
}
