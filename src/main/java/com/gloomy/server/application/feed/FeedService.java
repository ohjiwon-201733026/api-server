package com.gloomy.server.application.feed;

import com.gloomy.server.application.image.ImageService;
import com.gloomy.server.domain.feed.Content;
import com.gloomy.server.domain.feed.FEED_STATUS;
import com.gloomy.server.domain.feed.Feed;
import com.gloomy.server.domain.feed.Password;
import com.gloomy.server.domain.user.User;
import com.gloomy.server.domain.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class FeedService {
    private final ImageService imageService;
    private final UserService userService;
    private final FeedRepository feedRepository;

    public FeedService(ImageService imageService, UserService userService, FeedRepository feedRepository) {
        this.imageService = imageService;
        this.userService = userService;
        this.feedRepository = feedRepository;
    }

    @Transactional
    public Feed createFeed(FeedDTO.Request feedDTO) throws IllegalArgumentException {
        validateFeedDTO(feedDTO);
        Feed createdFeed = feedRepository.save(makeFeed(feedDTO));
        if (feedDTO.getImages() != null) {
            imageService.uploadMany(createdFeed, feedDTO.getImages());
        }
        return createdFeed;
    }

    private void validateFeedDTO(FeedDTO.Request feedDTO) throws IllegalArgumentException {
        if ((feedDTO.getUserId() == null) == (feedDTO.getPassword() == null)
            || feedDTO.getContent().length() <= 0) {
            throw new IllegalArgumentException("[FeedService] 피드 등록 요청 메시지가 잘못되었습니다.");
        }
        if (feedDTO.getUserId() != null) {
            try {
                userService.findUser(feedDTO.getUserId());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("[FeedService] 회원 피드 등록 요청 메시지가 잘못되었습니다.");
            }
            return;
        }
        if (feedDTO.getPassword().length() <= 0) {
            throw new IllegalArgumentException("[FeedService] 비회원 피드 등록 요청 메시지가 잘못되었습니다.");
        }
    }

    private Feed makeFeed(FeedDTO.Request feedDTO) {
        if (feedDTO.getUserId() != null) {
            User findUser = userService.findUser(feedDTO.getUserId());
            return Feed.of(findUser, feedDTO.getContent());
        }
        return Feed.of(feedDTO.getPassword(), feedDTO.getContent());
    }

    public Page<Feed> findAllFeeds(Pageable pageable) throws IllegalArgumentException {
        if (pageable == null) {
            throw new IllegalArgumentException("[FeedService] Pageable이 유효하지 않습니다.");
        }
        return feedRepository.findAll(pageable);
    }

    public Page<Feed> findAllActiveFeeds(Pageable pageable) {
        if (pageable == null) {
            throw new IllegalArgumentException("[FeedService] Pageable이 유효하지 않습니다.");
        }
        return feedRepository.findAllByStatus(pageable, FEED_STATUS.ACTIVE);
    }

    public Page<Feed> findUserFeeds(Pageable pageable, Long userId) throws IllegalArgumentException {
        if (pageable == null) {
            throw new IllegalArgumentException("[FeedService] pageable이 유효하지 않습니다.");
        }
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("[FeedService] 사용자 ID가 유효하지 않습니다.");
        }
        try {
            User foundUser = userService.findUser(userId);
            return feedRepository.findAllByUserId(pageable, foundUser);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("[FeedService] 해당하는 사용자가 없습니다.");
        }
    }

    public Feed findOneFeed(Long feedId) throws IllegalArgumentException {
        if (feedId == null || feedId <= 0) {
            throw new IllegalArgumentException("[FeedService] 비회원 피드 ID가 유효하지 않습니다.");
        }
        return feedRepository.findById(feedId).orElseThrow(() -> {
            throw new IllegalArgumentException("[FeedService] 해당 피드 ID가 존재하지 않습니다.");
        });
    }

    @Transactional
    public Feed updateOneFeed(Long feedId, UpdateFeedDTO.Request feedDTO) {
        Feed foundFeed = findOneFeed(feedId);
        validateUpdateFeedDTO(foundFeed, feedDTO);
        updateFeed(foundFeed, feedDTO);
        return feedRepository.save(foundFeed);
    }

    private void updateFeed(Feed foundFeed, UpdateFeedDTO.Request feedDTO) {
        if (feedDTO.getPassword() != null) {
            foundFeed.setPassword(new Password(feedDTO.getPassword()));
        }
        if (feedDTO.getContent() != null) {
            foundFeed.setContent(new Content(feedDTO.getContent()));
        }
        if (feedDTO.getImages() != null) {
            imageService.updateImages(foundFeed, feedDTO.getImages());
        }
    }

    private void validateUpdateFeedDTO(Feed foundFeed, UpdateFeedDTO.Request feedDTO) {
        if ((foundFeed.getUserId() != null) && feedDTO.getPassword() != null) {
            throw new IllegalArgumentException("[FeedService] 회원 피드 수정 요청 메시지가 잘못되었습니다.");
        }
    }

    @Transactional
    public Feed deleteFeed(Long feedId) {
        Feed foundFeed = findOneFeed(feedId);
        foundFeed.setStatus(FEED_STATUS.INACTIVE);
        return feedRepository.save(foundFeed);
    }

    @Transactional
    public void deleteAll() {
        feedRepository.deleteAll();
    }
}
