package com.gloomy.server.application.notice;

import com.gloomy.server.application.comment.CommentService;
import com.gloomy.server.application.comment.TestCommentDTO;
import com.gloomy.server.application.feed.FeedService;
import com.gloomy.server.application.feed.TestFeedDTO;
import com.gloomy.server.application.feed.TestUserDTO;
import com.gloomy.server.application.feedlike.FeedLikeDTO;
import com.gloomy.server.application.feedlike.FeedLikeService;
import com.gloomy.server.application.reply.ReplyDTO;
import com.gloomy.server.application.reply.ReplyService;
import com.gloomy.server.domain.comment.Comment;
import com.gloomy.server.domain.common.entity.BaseEntity;
import com.gloomy.server.domain.feed.Feed;
import com.gloomy.server.domain.feedlike.FeedLike;
import com.gloomy.server.domain.notice.Notice;
import com.gloomy.server.domain.notice.Type;
import com.gloomy.server.domain.reply.Reply;
import com.gloomy.server.domain.user.User;
import com.gloomy.server.domain.user.UserService;
import groovy.util.logging.Slf4j;
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
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest(properties = {
        "spring.config.location=classpath:test-application.yml,classpath:aws.yml"
})
class NoticeServiceTest {
    @Autowired
    private UserService userService;
    @Autowired
    private FeedService feedService;
    @Autowired
    private FeedLikeService feedLikeService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private ReplyService replyService;
    @Autowired
    private NoticeService noticeService;

    private TestNotice testNotice;

    @BeforeEach
    void beforeEach() {
        User testUser = userService.createUser(TestUserDTO.makeTestUser());
        TestFeedDTO testFeedDTO = new TestFeedDTO(testUser, 0);
        Feed testFeed = feedService.createFeed(testUser.getId(), testFeedDTO.makeUserFeedDTO());
        TestCommentDTO testCommentDTO = new TestCommentDTO(testFeed.getId(), null);
        Comment testComment = commentService.createComment(null, testCommentDTO.makeNonUserCommentDTO());
        Reply testReply = replyService.createReply(null, new ReplyDTO.Request("대댓글", testComment.getId(), "12345"));
        FeedLike testFeedLike = feedLikeService.createFeedLike(null, new FeedLikeDTO.Request(testFeed.getId()));
        testNotice = new TestNotice(testFeed, testComment, testReply, testFeedLike);
        noticeService.deleteAll();
    }

    @AfterEach
    void afterEach() {
        noticeService.deleteAll();
        feedLikeService.deleteAll();
        replyService.deleteAll();
        commentService.deleteAll();
        feedService.deleteAll();
        userService.deleteAll();
    }

    @Transactional
    @Test
    void 댓글_알림_생성_성공() {
        Notice notice = Notice.of(testNotice.feed, testNotice.comment, Type.COMMENT);
        Notice createdNotice = noticeService.createNotice(testNotice.feed, testNotice.comment, Type.COMMENT);

        checkCreatedNoticeSuccess(createdNotice, notice);
    }

    @Transactional
    @Test
    void 댓글_알림_생성_실패() {
        Reply reply = testNotice.reply;

        checkCreatedNoticeFail(testNotice.feed, reply, Type.COMMENT, "[NoticeService] 댓글이 유효하지 않습니다.");
    }

    @Transactional
    @Test
    void 대댓글_알림_생성_성공() {
        Notice notice = Notice.of(testNotice.feed, testNotice.reply, Type.REPLY);
        Notice createdNotice = noticeService.createNotice(testNotice.feed, testNotice.reply, Type.REPLY);

        checkCreatedNoticeSuccess(createdNotice, notice);
    }

    @Transactional
    @Test
    void 대댓글_알림_생성_실패() {
        FeedLike feedLike = testNotice.feedLike;

        checkCreatedNoticeFail(testNotice.feed, feedLike, Type.REPLY, "[NoticeService] 대댓글이 유효하지 않습니다.");
    }

    @Transactional
    @Test
    void 좋아요_알림_생성_성공() {
        Notice notice = Notice.of(testNotice.feed, testNotice.feedLike, Type.LIKE);
        Notice createdNotice = noticeService.createNotice(testNotice.feed, testNotice.feedLike, Type.LIKE);

        checkCreatedNoticeSuccess(createdNotice, notice);
    }

    @Transactional
    @Test
    void 좋아요_알림_생성_실패() {
        Comment comment = testNotice.comment;

        checkCreatedNoticeFail(testNotice.feed, comment, Type.LIKE, "[NoticeService] 좋아요가 유효하지 않습니다.");
    }

    @Transactional
    @Test
    void 알림_생성_공통_실패() {
        Comment commentId = testNotice.comment;

        checkCreatedNoticeFail(null, commentId, Type.COMMENT, "[NoticeService] 알림 생성 파라미터가 유효하지 않습니다.");
        checkCreatedNoticeFail(testNotice.feed, null, Type.COMMENT, "[NoticeService] 알림 생성 파라미터가 유효하지 않습니다.");
        checkCreatedNoticeFail(testNotice.feed, commentId, null, "[NoticeService] 알림 생성 파라미터가 유효하지 않습니다.");
    }

    @Transactional
    @Test
    void 댓글_작성시_알림_생성_성공() {
        TestCommentDTO testCommentDTO = new TestCommentDTO(testNotice.feed.getId(), null);

        Comment createdComment = commentService.createComment(null, testCommentDTO.makeNonUserCommentDTO());
        Notice commentNotice = noticeService.findOneNotice(createdComment);
        Integer noticeSize = noticeService.countAllNotices(testNotice.feed.getUserId().getId());

        assertEquals(commentNotice.getCommentId(), createdComment);
        assertEquals(noticeSize, 1);
    }

    @Transactional
    @Test
    void 대댓글_작성시_알림_생성_성공() {
        ReplyDTO.Request replyDTO = new ReplyDTO.Request("대댓글", testNotice.comment.getId(), "12345");

        Reply createdReply = replyService.createReply(null, replyDTO);
        Notice replyNotice = noticeService.findOneNotice(createdReply);
        Integer noticeSize = noticeService.countAllNotices(testNotice.feed.getUserId().getId());

        assertEquals(replyNotice.getReplyId(), createdReply);
        assertEquals(noticeSize, 1);
    }

    @Transactional
    @Test
    void 좋아요_작성시_알림_생성_성공() {
        FeedLikeDTO.Request feedLikeDTO = new FeedLikeDTO.Request(testNotice.feed.getId());

        FeedLike createdFeedLike = feedLikeService.createFeedLike(null, feedLikeDTO);
        Notice feedLikeNotice = noticeService.findOneNotice(createdFeedLike);
        Integer noticeSize = noticeService.countAllNotices(testNotice.feed.getUserId().getId());

        assertEquals(feedLikeNotice.getFeedLikeId(), createdFeedLike);
        assertEquals(noticeSize, 1);
    }

    @Transactional
    @Test
    void 회원_알림_조회_성공() {
        Pageable pageable = PageRequest.of(0, 10);

        Notice commentNotice = noticeService.createNotice(testNotice.feed, testNotice.comment, Type.COMMENT);
        Notice replyNotice = noticeService.createNotice(testNotice.feed, testNotice.reply, Type.REPLY);
        Notice feedLikeNotice = noticeService.createNotice(testNotice.feed, testNotice.feedLike, Type.LIKE);
        Page<Notice> allNotices = noticeService.getAllNotices(pageable, testNotice.feed.getUserId().getId());

        assertEquals(allNotices.getContent().size(), 3);
        assertEquals(allNotices.getContent().get(0), feedLikeNotice);
        assertEquals(allNotices.getContent().get(1), replyNotice);
        assertEquals(allNotices.getContent().get(2), commentNotice);
    }

    @Transactional
    @Test
    void 회원_알림_조회_실패() {
        Pageable pageable = PageRequest.of(0, 10);

        checkGetAllNoticesFail(null, testNotice.feed.getUserId().getId(), "[NoticeService] pageable이 유효하지 않습니다.");
        checkGetAllNoticesFail(pageable, null, "[NoticeService] userId가 유효하지 않습니다.");
        checkGetAllNoticesFail(pageable, 0L, "[NoticeService] userId가 유효하지 않습니다.");
    }

    @Transactional
    @Test
    void 알림_읽음_처리_성공() {
        Notice createdNotice = noticeService.createNotice(testNotice.feed, testNotice.reply, Type.REPLY);

        Notice readNotice = noticeService.readNotice(createdNotice.getId());

        assertEquals(readNotice.getIsRead().getIsRead(), true);
    }

    @Transactional
    @Test
    void 알림_읽음_처리_실패() {
        checkReadNoticeFail(null, "[NoticeService] userId가 유효하지 않습니다.");
        checkReadNoticeFail(0L, "[NoticeService] userId가 유효하지 않습니다.");
    }

    private void checkCreatedNoticeSuccess(Notice expectedNotice, Notice actualNotice) {
        assertEquals(expectedNotice.getFeedId(), actualNotice.getFeedId());
        assertEquals(expectedNotice.getCommentId(), actualNotice.getCommentId());
        assertEquals(expectedNotice.getReplyId(), actualNotice.getReplyId());
        assertEquals(expectedNotice.getFeedLikeId(), actualNotice.getFeedLikeId());
        assertEquals(expectedNotice.getType(), actualNotice.getType());
        assertEquals(expectedNotice.getIsRead(), actualNotice.getIsRead());
    }

    private void checkCreatedNoticeFail(Feed feed, BaseEntity entity, Type entityType, String errorMessage) {
        assertEquals(
                assertThrows(IllegalArgumentException.class, () -> {
                    noticeService.createNotice(feed, entity, entityType);
                }).getMessage(),
                errorMessage);
    }

    private void checkReadNoticeFail(Long noticeId, String errorMessage) {
        assertEquals(
                assertThrows(IllegalArgumentException.class, () -> {
                    noticeService.readNotice(noticeId);
                }).getMessage(),
                errorMessage);
    }

    private void checkGetAllNoticesFail(Pageable pageable, Long userId, String errorMessage) {
        assertEquals(
                assertThrows(IllegalArgumentException.class, () -> {
                    noticeService.getAllNotices(pageable, userId);
                }).getMessage(),
                errorMessage);
    }

    static class TestNotice {
        private final Feed feed;
        private final Comment comment;
        private final Reply reply;
        private final FeedLike feedLike;

        public TestNotice(Feed feed, Comment comment, Reply reply, FeedLike feedLike) {
            this.feed = feed;
            this.comment = comment;
            this.reply = reply;
            this.feedLike = feedLike;
        }
    }
}
