package com.gloomy.server.application.feed;

import com.gloomy.server.application.image.ImageService;
import com.gloomy.server.domain.feed.*;
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
    public Feed createFeed(Long userId, FeedDTO.Request feedDTO) throws IllegalArgumentException {
        User user = null;
        validateFeedDTO(userId, feedDTO);
        if (userId != null) {
            user = userService.findUser(userId);
        }
        Feed createdFeed = feedRepository.save(Feed.of(user, feedDTO));

        if (feedDTO.getImages() != null) {
            imageService.uploadMany(createdFeed, feedDTO.getImages());
        }
        return createdFeed;
    }

    private void validateFeedDTO(Long userId, FeedDTO.Request feedDTO) throws IllegalArgumentException {
        try {
            if ((userId == null) == (feedDTO.getPassword() == null)
                    || feedDTO.getTitle().length() <= 0
                    || feedDTO.getContent().length() <= 0) {
                throw new IllegalArgumentException();
            }
            Category.from(feedDTO.getCategory());
        } catch (Exception e) {
            throw new IllegalArgumentException("[FeedService] 피드 등록 요청 메시지가 잘못되었습니다.");
        }
        if ((userId == null) && feedDTO.getPassword().length() <= 0) {
            throw new IllegalArgumentException("[FeedService] 비회원 피드 등록 요청 메시지가 잘못되었습니다.");
        }
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
        return feedRepository.findAllByStatus(pageable, FeedStatus.ACTIVE);
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
        foundFeed.setStatus(FeedStatus.INACTIVE);
        return feedRepository.save(foundFeed);
    }

    @Transactional
    public void deleteAll() {
        feedRepository.deleteAll();
    }
}
