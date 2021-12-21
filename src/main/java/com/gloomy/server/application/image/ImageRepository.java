package com.gloomy.server.application.image;

import com.gloomy.server.domain.feed.Feed;
import com.gloomy.server.domain.image.IMAGE_STATUS;
import com.gloomy.server.domain.image.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {
    List<Image> findAllByFeedId(Feed feedId);
    List<Image> findAllByFeedIdAndStatus(Feed feedId, IMAGE_STATUS status);
}

