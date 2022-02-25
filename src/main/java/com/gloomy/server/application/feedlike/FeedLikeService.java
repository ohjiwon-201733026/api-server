package com.gloomy.server.application.feedlike;

import com.gloomy.server.application.feed.FeedService;
import com.gloomy.server.application.notice.NoticeService;
import com.gloomy.server.domain.common.entity.Status;
import com.gloomy.server.domain.feed.Feed;
import com.gloomy.server.domain.feedlike.FeedLike;
import com.gloomy.server.domain.notice.Type;
import com.gloomy.server.domain.user.User;
import com.gloomy.server.domain.user.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FeedLikeService {
    private final UserService userService;
    private final FeedService feedService;
    private final NoticeService noticeService;
    private final FeedLikeRepository feedLikeRepository;

    public FeedLikeService(UserService userService, FeedService feedService, NoticeService noticeService, FeedLikeRepository feedLikeRepository) {
        this.userService = userService;
        this.feedService = feedService;
        this.noticeService = noticeService;
        this.feedLikeRepository = feedLikeRepository;
    }

    @Transactional
    public FeedLike createFeedLike(Long userId, FeedLikeDTO.Request feedLikeDTO) {
        validateFeedLikeDTO(userId, feedLikeDTO);
        FeedLike feedLike = feedLikeRepository.save(makeFeedLike(userId, feedLikeDTO));
        noticeService.createNotice(feedLike.getFeedId(), feedLike, Type.LIKE);
        return feedLike;
    }

    @Transactional(readOnly = true)
    public Integer getFeedLikeCount(Feed feedId) {
        return feedLikeRepository.countAllByFeedIdAndStatus(feedId, Status.active());
    }

    @Transactional
    public void deleteAll() {
        feedLikeRepository.deleteAll();
    }

    private FeedLike makeFeedLike(Long userId, FeedLikeDTO.Request feedLikeDTO) {
        Feed feed = feedService.findOneFeed(feedLikeDTO.getFeedId());
        if (userId != null) {
            User user = userService.findUser(userId);
            return FeedLike.of(feed, user);
        }
        return FeedLike.from(feed);
    }

    private void validateFeedLikeDTO(Long userId, FeedLikeDTO.Request feedLikeDTO) {
        if (feedLikeDTO.getFeedId() == null || feedLikeDTO.getFeedId() <= 0L) {
            throw new IllegalArgumentException("[FeedLikeService] feedId가 유효하지 않습니다.");
        }
        if (userId != null && userId <= 0L) {
            throw new IllegalArgumentException("[FeedLikeService] userId가 유효하지 않습니다.");
        }
    }
}
