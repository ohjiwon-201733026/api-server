package com.gloomy.server.application.feed;

import com.gloomy.server.application.image.ImageService;
import com.gloomy.server.domain.feed.Feed;
import com.gloomy.server.domain.user.User;
import com.gloomy.server.domain.user.UserService;
import lombok.extern.slf4j.Slf4j;
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
        imageService.uploadMany(createdFeed, feedDTO.getImages());
        return createdFeed;
    }

    private void validateFeedDTO(FeedDTO.Request feedDTO) throws IllegalArgumentException {
        if (feedDTO.getIsUser()) {
            if (feedDTO.getUserId() == null || feedDTO.getPassword() != null) {
                throw new IllegalArgumentException("[FeedService] 회원 피드 등록 요청 메시지가 잘못되었습니다.");
            }
            return;
        }
        if (feedDTO.getPassword() == null || feedDTO.getUserId() != null) {
            throw new IllegalArgumentException("[FeedService] 비회원 피드 등록 요청 메시지가 잘못되었습니다.");
        }
    }

    private Feed makeFeed(FeedDTO.Request feedDTO) {
        if (feedDTO.getIsUser()) {
            User findUser = userService.findUser(feedDTO.getUserId());
            return Feed.of(feedDTO.getIp(), findUser, feedDTO.getContent());
        }
        return Feed.of(feedDTO.getIp(), feedDTO.getPassword(), feedDTO.getContent());
    }

    @Transactional
    public void deleteAll() {
        feedRepository.deleteAll();
    }
}
