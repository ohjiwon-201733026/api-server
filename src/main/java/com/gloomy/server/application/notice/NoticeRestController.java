package com.gloomy.server.application.notice;

import com.gloomy.server.application.comment.CommentService;
import com.gloomy.server.application.feedlike.FeedLikeService;
import com.gloomy.server.domain.feed.Feed;
import com.gloomy.server.domain.notice.Notice;
import com.gloomy.server.domain.user.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/notice")
public class NoticeRestController {
    private final UserService userService;
    private final FeedLikeService feedLikeService;
    private final CommentService commentService;
    private final NoticeService noticeService;

    public NoticeRestController(UserService userService, FeedLikeService feedLikeService, CommentService commentService, NoticeService noticeService) {
        this.userService = userService;
        this.feedLikeService = feedLikeService;
        this.commentService = commentService;
        this.noticeService = noticeService;
    }

    @GetMapping(value = "")
    public Page<NoticeDTO.Response> getAllNotices(@PageableDefault(size = 10) Pageable pageable) {
        Long userId = userService.getMyInfo();
        Page<Notice> allNotices = noticeService.getAllNotices(pageable, userId);
        return makeResult(allNotices);
    }

    private Page<NoticeDTO.Response> makeResult(Page<Notice> allNotices) {
        List<NoticeDTO.Response> result = new ArrayList<>();
        for (Notice notice : allNotices.getContent()) {
            result.add(makeNoticeDTOResponse(notice));
        }
        return new PageImpl<>(result);
    }

    private NoticeDTO.Response makeNoticeDTOResponse(Notice notice) {
        Feed feed = notice.getFeedId();
        Integer likeCount = feedLikeService.getFeedLikeCount(notice.getFeedId());
        Integer commentCount = commentService.getFeedAllActiveCommentsCount(feed);
        return NoticeDTO.Response.of(notice, likeCount, commentCount);
    }
}
