package com.gloomy.server.application.comment;

import com.gloomy.server.application.feed.FeedService;
import com.gloomy.server.application.feed.TestFeedDTO;
import com.gloomy.server.application.feed.TestUserDTO;
import com.gloomy.server.application.image.ImageService;
import com.gloomy.server.application.notice.NoticeService;
import com.gloomy.server.domain.comment.Comment;
import com.gloomy.server.domain.common.entity.Status;
import com.gloomy.server.domain.feed.Feed;
import com.gloomy.server.domain.user.User;
import com.gloomy.server.domain.user.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@SpringBootTest(properties = {
        "spring.config.location=classpath:test-application.yml,classpath:aws.yml"
})
class CommentServiceTest {
    @Autowired
    private FeedService feedService;
    @Autowired
    private UserService userService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private ImageService imageService;
    @Autowired
    private NoticeService noticeService;

    @Value("${cloud.aws.s3.feedTestDir}")
    private String feedTestDir;
    private TestCommentDTO testCommentDTO;

    @BeforeEach
    void beforeEach() {
        User testUser = userService.createUser(TestUserDTO.makeTestUser());
        TestFeedDTO testFeedDTO = new TestFeedDTO(testUser, 1);
        Feed testFeed = feedService.createFeed(null, testFeedDTO.makeNonUserFeedDTO());
        testCommentDTO = new TestCommentDTO(testFeed.getId(), testUser.getId());
    }

    @AfterEach
    void afterEach() {
        noticeService.deleteAll();
        imageService.deleteAll(feedTestDir);
        commentService.deleteAll();
        feedService.deleteAll();
        userService.deleteAll();
    }

    @Transactional
    @Test
    void 댓글_생성_비회원_성공() {
        CommentDTO.Request nonUserCommentDTO = testCommentDTO.makeNonUserCommentDTO();

        Comment createdComment = commentService.createComment(null, nonUserCommentDTO);
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

        checkCreatedCommentFail(null, nonUserCommentDTOWithoutContent);
        checkCreatedCommentFail(null, nonUserCommentDTOWithoutFeedId);
        checkCreatedCommentFail(null, nonUserCommentDTOWithoutPassword);
    }

    @Transactional
    @Test
    void 댓글_생성_회원_성공() {
        CommentDTO.Request userCommentDTO = testCommentDTO.makeUserCommentDTO();

        Comment createdComment = commentService.createComment(testCommentDTO.getUserId(), userCommentDTO);
        Comment foundComment = commentService.findComment(createdComment.getId());
        assertEquals(foundComment, createdComment);
    }

    @Test
    void 댓글_생성_회원_실패() {
        CommentDTO.Request userCommentDTOWithoutContent =
                new CommentDTO.Request(null, testCommentDTO.getFeedId());
        CommentDTO.Request userCommentDTOWithoutFeedId =
                new CommentDTO.Request(testCommentDTO.getContent(), null);

        checkCreatedCommentFail(testCommentDTO.getUserId(), userCommentDTOWithoutContent);
        checkCreatedCommentFail(testCommentDTO.getUserId(), userCommentDTOWithoutFeedId);
    }

    @Transactional
    @Test
    void 댓글_조회_비회원_성공() {
        Comment createdComment = commentService.createComment(null, testCommentDTO.makeNonUserCommentDTO());

        Comment foundComment = commentService.findComment(createdComment.getId());

        assertEquals(foundComment, createdComment);
    }

    @Transactional
    @Test
    void 댓글_조회_비회원_실패() {
        Comment createdComment = commentService.createComment(null, testCommentDTO.makeNonUserCommentDTO());

        noticeService.deleteAll();
        commentService.deleteAll();

        checkFoundCommentFail(createdComment.getId(), "[CommentService] 해당 댓글 ID가 존재하지 않습니다.");
    }

    @Transactional
    @Test
    void 댓글_조회_회원_성공() {
        Comment createdComment = commentService.createComment(testCommentDTO.getUserId(), testCommentDTO.makeUserCommentDTO());

        Comment foundComment = commentService.findComment(createdComment.getId());

        assertEquals(foundComment, createdComment);
    }

    @Test
    void 댓글_조회_회원_실패() {
        Comment createdComment = commentService.createComment(testCommentDTO.getUserId(), testCommentDTO.makeUserCommentDTO());

        noticeService.deleteAll();
        commentService.deleteAll();

        checkFoundCommentFail(createdComment.getId(), "[CommentService] 해당 댓글 ID가 존재하지 않습니다.");
    }

    @Test
    void 댓글_조회_공통_실패() {
        checkFoundCommentFail(0L, "[CommentService] 해당 댓글 ID가 유효하지 않습니다.");
        checkFoundCommentFail(null, "[CommentService] 해당 댓글 ID가 유효하지 않습니다.");
    }

    @Transactional
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

        imageService.deleteAll(feedTestDir);
        feedService.deleteAll();

        checkFoundAllCommentFail(pageable, 0L, "[CommentService] 해당 댓글 ID가 유효하지 않습니다.");
        checkFoundAllCommentFail(pageable, null, "[CommentService] 해당 댓글 ID가 유효하지 않습니다.");
        checkFoundAllCommentFail(pageable, testCommentDTO.getFeedId(), "[CommentService] 해당 댓글 ID가 존재하지 않습니다.");
        checkFoundAllCommentFail(null, testCommentDTO.getFeedId(), "[CommentService] pageable이 유효하지 않습니다.");
    }

    @Transactional
    @Test
    void 피드_활성_댓글_전체_조회_성공() {
        Comment activeComment = commentService.createComment(null, testCommentDTO.makeNonUserCommentDTO());
        Comment inactiveComment = commentService.createComment(null, testCommentDTO.makeNonUserCommentDTO());

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

    @Transactional
    @Test
    void 댓글_수정_성공() {
        Comment createdComment = commentService.createComment(null, testCommentDTO.makeNonUserCommentDTO());
        String updateContent = "새 댓글";
        UpdateCommentDTO.Request updateCommentDTO = new UpdateCommentDTO.Request();
        updateCommentDTO.setContent(updateContent);

        Comment updatedComment = commentService.updateComment(createdComment.getId(), updateCommentDTO);
        Comment foundComment = commentService.findComment(createdComment.getId());

        assertEquals(updatedComment, foundComment);
    }

    @Transactional
    @Test
    void 댓글_수정_실패() {
        Comment deletedComment = commentService.createComment(null, testCommentDTO.makeNonUserCommentDTO());
        String updateContent = "새 댓글";
        UpdateCommentDTO.Request updateCommentDTO = new UpdateCommentDTO.Request();
        updateCommentDTO.setContent(updateContent);
        UpdateCommentDTO.Request updateCommentDTOWithoutContent = new UpdateCommentDTO.Request();

        noticeService.deleteAll();
        commentService.deleteAll();

        Comment createdComment = commentService.createComment(null, testCommentDTO.makeNonUserCommentDTO());

        checkUpdatedCommentFail(createdComment.getId(), updateCommentDTOWithoutContent,
                "[CommentService] 댓글 수정 요청 메시지가 잘못되었습니다.");
        checkUpdatedCommentFail(0L, updateCommentDTO,
                "[CommentService] 해당 댓글 ID가 유효하지 않습니다.");
        checkUpdatedCommentFail(null, updateCommentDTO,
                "[CommentService] 해당 댓글 ID가 유효하지 않습니다.");
        checkUpdatedCommentFail(deletedComment.getId(), updateCommentDTO,
                "[CommentService] 해당 댓글 ID가 존재하지 않습니다.");
    }

    @Transactional
    @Test
    void 댓글_삭제_성공() {
        Comment createdComment = commentService.createComment(null, testCommentDTO.makeNonUserCommentDTO());
        Comment deletedComment = commentService.deleteComment(createdComment.getId());
        assertEquals(deletedComment.getStatus(), Status.INACTIVE);
    }

    @Transactional
    @Test
    void 댓글_삭제_실패() {
        Comment createdComment = commentService.createComment(null, testCommentDTO.makeNonUserCommentDTO());

        noticeService.deleteAll();
        commentService.deleteAll();

        checkDeletedCommentFail(0L, "[CommentService] 해당 댓글 ID가 유효하지 않습니다.");
        checkDeletedCommentFail(null, "[CommentService] 해당 댓글 ID가 유효하지 않습니다.");
        checkDeletedCommentFail(createdComment.getId(), "[CommentService] 해당 댓글 ID가 존재하지 않습니다.");
    }

    private void createComments(int commentSize) {
        for (int num = 0; num < commentSize; num++) {
            commentService.createComment(testCommentDTO.getUserId(), testCommentDTO.makeUserCommentDTO());
        }
    }

    private void checkCreatedCommentFail(Long userId, CommentDTO.Request commentDTO) {
        assertThrows(IllegalArgumentException.class, () -> {
            commentService.createComment(userId, commentDTO);
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
}
