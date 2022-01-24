package com.gloomy.server.domain.report;

import com.gloomy.server.application.feed.FeedService;
import com.gloomy.server.domain.feed.Feed;
import com.gloomy.server.domain.user.User;
import com.gloomy.server.domain.user.UserRepository;
import com.gloomy.server.domain.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final UserService userService;
    private final FeedService feedService;


    public Report saveReport(Long feedId, Long userId, String reportCategory){
        User user=userService.findUser(userId);
        Feed feed=feedService.findOneFeed(feedId);
        Report report=Report.of(feed,user,reportCategory);
        return reportRepository.save(report);
    }

    public Optional<Report> findReportById(Long reportId){
        return reportRepository.findById(reportId);
    }
}