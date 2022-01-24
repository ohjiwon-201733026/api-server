package com.gloomy.server.domain.report;

import com.gloomy.server.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

public interface ReportRepository extends JpaRepository<Report, Long> {

    Report save(Report report);
    Optional<Report> findById(Long id);
}