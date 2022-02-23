package com.gloomy.server.application.feedlike;

import com.gloomy.server.application.feed.FeedService;
import com.gloomy.server.domain.common.entity.Status;
import com.gloomy.server.domain.feed.Feed;
import com.gloomy.server.domain.feedlike.FeedLike;
import com.gloomy.server.domain.user.User;
import com.gloomy.server.domain.user.UserService;
import org.springframework.stereotype.Service;

@Service
public class FeedLikeService {
    private final UserService userService;
    private final FeedService feedService;
    private final FeedLikeRepository feedLikeRepository;

    public FeedLikeService(UserService userService, FeedService feedService, FeedLikeRepository feedLikeRepository) {
        this.userService = userService;
        this.feedService = feedService;
        this.feedLikeRepository = feedLikeRepository;
    }

    public FeedLike createFeedLike(Long userId, FeedLikeDTO.Request feedLikeDTO) {
        validateFeedLikeDTO(userId, feedLikeDTO);
        FeedLike feedLike = makeFeedLike(userId, feedLikeDTO);
        return feedLikeRepository.save(feedLike);
    }

    public Integer getFeedLikeCount(Feed feedId) {
        return feedLikeRepository.countAllByFeedIdAndStatus(feedId, Status.active());
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
