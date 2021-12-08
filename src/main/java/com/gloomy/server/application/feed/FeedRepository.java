package com.gloomy.server.application.feed;

import com.gloomy.server.domain.feed.Feed;
import com.gloomy.server.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedRepository extends JpaRepository<Feed, Long> {
}
