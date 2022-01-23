package com.gloomy.server.application.feed;

import com.gloomy.server.domain.feed.Feed;
import com.gloomy.server.domain.common.Status;
import com.gloomy.server.domain.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeedRepository extends JpaRepository<Feed, Long> {
    Page<Feed> findAllByUserId(Pageable pageable, User userId);
    Page<Feed> findAll(Pageable pageable);
    Page<Feed> findAllByStatus(Pageable pageable, Status status);
}
