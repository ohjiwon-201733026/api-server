package com.gloomy.server.domain.report;

import com.gloomy.server.domain.feed.Feed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.List;
import java.util.Optional;
@EnableJpaRepositories
public interface ReportRepository extends JpaRepository<Report, Long> {

    Report save(Report report);
    Optional<Report> findById(Long id);
    List<Report> findByFeedId(Feed feedId);
    void deleteAll();
}