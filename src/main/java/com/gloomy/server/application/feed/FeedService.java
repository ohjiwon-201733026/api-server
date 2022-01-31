package com.gloomy.server.application.feed;

import com.gloomy.server.application.feed.sort.FeedSort;
import com.gloomy.server.application.image.ImageService;
import com.gloomy.server.application.image.Images;
import com.gloomy.server.domain.common.entity.Status;
import com.gloomy.server.domain.feed.Category;
import com.gloomy.server.domain.feed.Content;
import com.gloomy.server.domain.feed.Feed;
import com.gloomy.server.domain.feed.Password;
import com.gloomy.server.domain.user.User;
import com.gloomy.server.domain.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

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
        return feedRepository.save(Feed.of(user, feedDTO));
    }

    @Transactional(readOnly = true)
    public Page<Feed> findAllFeeds(Pageable pageable) throws IllegalArgumentException {
        if (pageable == null) {
            throw new IllegalArgumentException("[FeedService] Pageable이 유효하지 않습니다.");
        }
        return feedRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Page<Feed> findAllActiveFeeds(Pageable originPageable) {
        validatePageableAndSort(originPageable);
        Optional<Sort.Order> order = originPageable.getSort().stream().findFirst();
        Pageable pageable = PageRequest.of(0, 10);

        if (order.isEmpty() || FeedSort.from(order.get().getProperty()) == FeedSort.DATE) {
            return feedRepository.findByStatusOrderByCreatedAtDesc(pageable, Status.ACTIVE);
        }
        return feedRepository.findByStatusOrderByLikeCountDesc(pageable, Status.ACTIVE);
    }

    @Transactional(readOnly = true)
    public Page<Feed> findUserFeeds(Pageable pageable, Long userId) throws IllegalArgumentException {
        if (pageable == null) {
            throw new IllegalArgumentException("[FeedService] pageable이 유효하지 않습니다.");
        }
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("[FeedService] 사용자 ID가 유효하지 않습니다.");
        }
        try {
            User foundUser = userService.findUser(userId);
            return feedRepository.findByUserId(pageable, foundUser);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("[FeedService] 해당하는 사용자가 없습니다.");
        }
    }

    @Transactional(readOnly = true)
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
    }

    @Transactional
    public Feed deleteFeed(Long feedId) {
        Feed foundFeed = findOneFeed(feedId);
        foundFeed.delete();
        return feedRepository.save(foundFeed);
    }

    @Transactional
    public void deleteAll() {
        feedRepository.deleteAll();
    }

    @Transactional
    public Feed addLikeCount(Long feedId) {
        Feed foundFeed = findOneFeed(feedId);
        foundFeed.addLikeCount();
        return feedRepository.save(foundFeed);
    }

    public Images uploadImages(Long feedId, List<MultipartFile> images) {
        return imageService.uploadImages(findOneFeed(feedId), images);
    }

    public Images findAllActiveImages(Long feedId) {
        return imageService.findAllActiveImages(findOneFeed(feedId));
    }

    public Images updateImages(Long feedId, List<MultipartFile> updateImages) {
        return imageService.updateImages(findOneFeed(feedId), updateImages);
    }

    public void deleteImages(Long feedId) {
        imageService.deleteImages(findOneFeed(feedId));
    }

    private void validateFeedDTO(Long userId, FeedDTO.Request feedDTO) throws IllegalArgumentException {
        try {
            if ((userId == null) == (feedDTO.getPassword() == null)
                    || feedDTO.getTitle().length() <= 0
                    || feedDTO.getContent().length() <= 0
                    || !EnumUtils.isValidEnumIgnoreCase(Category.class, feedDTO.getCategory())) {
                throw new IllegalArgumentException();
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("[FeedService] 피드 등록 요청 메시지가 잘못되었습니다.");
        }
        if ((userId == null) && feedDTO.getPassword().length() <= 0) {
            throw new IllegalArgumentException("[FeedService] 비회원 피드 등록 요청 메시지가 잘못되었습니다.");
        }
    }

    private void validatePageableAndSort(Pageable pageable) {
        if (pageable == null) {
            throw new IllegalArgumentException("[FeedService] Pageable이 유효하지 않습니다.");
        }
        Stream<org.springframework.data.domain.Sort.Order> orders = pageable.getSort().stream();
        if (pageable.getSort().stream().count() > 1) {
            throw new IllegalArgumentException("[FeedService] sort는 2개 이상이 될 수 없습니다.");
        }
        Optional<org.springframework.data.domain.Sort.Order> order = orders.findFirst();
        if (order.isPresent() && !EnumUtils.isValidEnumIgnoreCase(FeedSort.class, order.get().getProperty())) {
            throw new IllegalArgumentException("[FeedService] sort가 유효하지 않습니다.");
        }
    }

    private void validateUpdateFeedDTO(Feed foundFeed, UpdateFeedDTO.Request feedDTO) {
        if ((foundFeed.getUserId() != null) && feedDTO.getPassword() != null) {
            throw new IllegalArgumentException("[FeedService] 회원 피드 수정 요청 메시지가 잘못되었습니다.");
        }
    }
}
