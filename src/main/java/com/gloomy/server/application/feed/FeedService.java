package com.gloomy.server.application.feed;

import com.gloomy.server.application.feed.sort.FeedSort;
import com.gloomy.server.application.image.ImageService;
import com.gloomy.server.application.image.Images;
import com.gloomy.server.domain.common.entity.Status;
import com.gloomy.server.domain.feed.Category;
import com.gloomy.server.domain.feed.Feed;
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
        validateFeedDTO(userId, feedDTO);
        User user = getUser(userId);
        return feedRepository.save(Feed.of(user, feedDTO));
    }

    @Transactional
    Feed createFeed(User userId) {
        return feedRepository.save(Feed.from(userId));
    }

    public Feed createUndefinedFeed(Long feedId, Long userId, FeedDTO.Request feedDTO) {
        validateFeedDTO(userId, feedDTO);
        Feed foundFeed = findOneFeed(feedId);
        if (foundFeed.getUserId() == null) {
            foundFeed.setPassword(feedDTO.getPassword());
        }
        foundFeed.setCategory(feedDTO.getCategory());
        foundFeed.setTitle(feedDTO.getTitle());
        foundFeed.setContent(feedDTO.getContent());
        return feedRepository.save(foundFeed);
    }

    @Transactional(readOnly = true)
    public Page<Feed> findAllFeeds(Pageable pageable) throws IllegalArgumentException {
        if (pageable == null) {
            throw new IllegalArgumentException("[FeedService] Pageable이 유효하지 않습니다.");
        }
        return feedRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Page<Feed> findAllActiveFeeds(Pageable originPageable, Long userId, String category) {
        validatePageableAndSortAndCategory(originPageable, category);
        Optional<Sort.Order> order = originPageable.getSort().stream().findFirst();
        Pageable pageable = PageRequest.of(0, 10);

        if (category == null || Category.valueOf(category) == Category.ALL) {
            return findAllActiveFeedsWithoutCategory(pageable, order, userId);
        }
        return findAllActiveFeedsWithCategory(pageable, order, userId, category);
    }

    @Transactional(readOnly = true)
    public Page<Feed> findAllActiveFeedsWithoutCategory(Pageable pageable, Optional<Sort.Order> order, Long userId) {
        if (userId == null) {
            return findAllActiveFeedsWithoutReport(pageable, order);
        }
        return findAllActiveFeedsWithReport(pageable, order, userId);
    }

    @Transactional(readOnly = true)
    public Page<Feed> findAllActiveFeedsWithCategory(Pageable pageable, Optional<Sort.Order> order, Long userId, String originCategory) {
        Category category = originCategory == null ? Category.ALL : Category.valueOf(originCategory);
        if (userId == null) {
            return findAllActiveFeedsByCategoryWithoutReport(pageable, order, category);
        }
        return findAllActiveFeedsByCategoryWithReport(pageable, order, userId, category);
    }

    private Page<Feed> findAllActiveFeedsByCategoryWithoutReport(Pageable pageable, Optional<Sort.Order> order, Category category) {
        if (order.isEmpty() || FeedSort.from(order.get().getProperty()) == FeedSort.DATE) {
            return feedRepository.findByStatusAndCategoryOrderByCreatedAtDesc(pageable, Status.active(), category);
        }
        return feedRepository.findByStatusAndCategoryOrderByLikeCountDesc(pageable, Status.active(), category);
    }

    private Page<Feed> findAllActiveFeedsByCategoryWithReport(Pageable pageable, Optional<Sort.Order> order, Long userId, Category category) {
        User user = userService.findUser(userId);
        if (order.isEmpty() || FeedSort.from(order.get().getProperty()) == FeedSort.DATE) {
            return feedRepository.findByStatusAndCategoryWithReportOrderByCreatedDesc(pageable, user, Status.active(), category);
        }
        return feedRepository.findByStatusAndCategoryWithReportOrderByLikeCountDesc(pageable, user, Status.active(), category);
    }

    private Page<Feed> findAllActiveFeedsWithoutReport(Pageable pageable, Optional<Sort.Order> order) {
        if (order.isEmpty() || FeedSort.from(order.get().getProperty()) == FeedSort.DATE) {
            return feedRepository.findByStatusOrderByCreatedAtDesc(pageable, Status.active());
        }
        return feedRepository.findByStatusOrderByLikeCountDesc(pageable, Status.active());
    }

    private Page<Feed> findAllActiveFeedsWithReport(Pageable pageable, Optional<Sort.Order> order, Long userId) {
        User user = userService.findUser(userId);
        if (order.isEmpty() || FeedSort.from(order.get().getProperty()) == FeedSort.DATE) {
            return feedRepository.findByStatusWithReportOrderByCreatedDesc(pageable, user, Status.active());
        }
        return feedRepository.findByStatusWithReportOrderByLikeCountDesc(pageable, user, Status.active());
    }

    @Transactional(readOnly = true)
    public Page<Feed> findUserFeeds(Pageable pageable, Long userId) throws IllegalArgumentException {
        if (pageable == null) {
            throw new IllegalArgumentException("[FeedService] pageable이 유효하지 않습니다.");
        }
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("[FeedService] 회원 ID가 유효하지 않습니다.");
        }
        try {
            User foundUser = userService.findUser(userId);
            return feedRepository.findByUserId(pageable, foundUser);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("[FeedService] 해당하는 회원이 없습니다.");
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
            foundFeed.setPassword(feedDTO.getPassword());
        }
        if (feedDTO.getContent() != null) {
            foundFeed.setContent(feedDTO.getContent());
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

    @Transactional
    public Images uploadImages(Long feedId, Long userId, List<MultipartFile> images) {
        return imageService.uploadImages(getFeed(feedId, userId), images);
    }

    private Feed getFeed(Long feedId, Long userId) {
        User user = getUser(userId);
        if (feedId == null) {
            return createFeed(user);
        }
        Feed foundFeed = findOneFeed(feedId);
        if (foundFeed.getUserId() == null && user != null) {
            throw new IllegalArgumentException("[FeedService] 비회원 피드에 요청 메시지가 잘못되었습니다.");
        }
        if (foundFeed.getUserId() != null && user == null) {
            throw new IllegalArgumentException("[FeedService] 회원 피드에 요청 메시지가 잘못되었습니다.");
        }
        if (foundFeed.getUserId() != user) {
            throw new IllegalArgumentException("[FeedService] 피드 ID의 회원 ID가 일치하지 않습니다.");
        }
        return foundFeed;
    }

    @Transactional(readOnly = true)
    public Images findAllActiveImages(Long feedId) {
        return imageService.findAllActiveImages(findOneFeed(feedId));
    }

    @Transactional
    public Images updateImages(Long feedId, List<MultipartFile> updateImages) {
        return imageService.updateImages(findOneFeed(feedId), updateImages);
    }

    @Transactional
    public void deleteImages(Long feedId) {
        imageService.deleteImages(findOneFeed(feedId));
    }

    private void validateFeedDTO(Long userId, FeedDTO.Request feedDTO) throws IllegalArgumentException {
        try {
            if ((userId == null) == (feedDTO.getPassword() == null)
                    || feedDTO.getTitle().length() <= 0
                    || feedDTO.getContent().length() <= 0
                    || !Category.isValidCategory(feedDTO.getCategory())) {
                throw new IllegalArgumentException();
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("[FeedService] 피드 등록 요청 메시지가 잘못되었습니다.");
        }
        if ((userId == null) && feedDTO.getPassword().length() <= 0) {
            throw new IllegalArgumentException("[FeedService] 비회원 피드 등록 요청 메시지가 잘못되었습니다.");
        }
    }

    private void validatePageableAndSortAndCategory(Pageable pageable, String category) {
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
        if (category != null && !Category.isValidCategory(category)) {
            throw new IllegalArgumentException("[FeedService] category가 유효하지 않습니다.");
        }
    }

    private void validateUpdateFeedDTO(Feed foundFeed, UpdateFeedDTO.Request feedDTO) {
        if ((foundFeed.getUserId() != null) && feedDTO.getPassword() != null) {
            throw new IllegalArgumentException("[FeedService] 회원 피드 수정 요청 메시지가 잘못되었습니다.");
        }
    }

    private User getUser(Long userId) {
        if (userId == null) {
            return null;
        }
        return userService.findUser(userId);
    }
}
