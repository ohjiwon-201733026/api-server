package com.gloomy.server.application.feed;

import com.gloomy.server.domain.common.entity.Status;
import com.gloomy.server.domain.feed.Feed;
import com.gloomy.server.domain.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeedRepository extends JpaRepository<Feed, Long> {
    Page<Feed> findByUserId(Pageable pageable, User userId);

    Page<Feed> findAll(Pageable pageable);

    Page<Feed> findByStatusOrderByCreatedAtDesc(Pageable pageable, Status status);

    Page<Feed> findByStatusOrderByLikeCountDesc(Pageable pageable, Status status);
}
