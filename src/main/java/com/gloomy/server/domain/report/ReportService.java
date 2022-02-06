package com.gloomy.server.domain.report;

import com.gloomy.server.application.feed.FeedService;
import com.gloomy.server.application.report.ReportDTO;
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


    public Report saveReport(ReportDTO.Request request){
        User user=userService.findUser(userService.getMyInfo());
        Feed feed=feedService.findOneFeed(request.getFeedId());
        // feed 상태 변경
        Report report=Report.of(feed,user, request.getReportCategory());
        return reportRepository.save(report);
    }

    public Optional<Report> findReportById(Long reportId){
        return reportRepository.findById(reportId);
    }
}