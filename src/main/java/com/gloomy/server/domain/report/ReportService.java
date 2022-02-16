package com.gloomy.server.domain.report;

import com.gloomy.server.application.feed.FeedRepository;
import com.gloomy.server.application.feed.FeedService;
import com.gloomy.server.application.report.ReportDTO;
import com.gloomy.server.domain.feed.Feed;
import com.gloomy.server.domain.user.User;
import com.gloomy.server.domain.user.UserRepository;
import com.gloomy.server.domain.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final UserService userService;
    private final FeedService feedService;
    private final FeedRepository feedRepository;


    public Report saveReport(ReportDTO.Request request, Long userId){
        Feed reportedFeed=feedService.findOneFeed(request.getFeedId());
        User reportUser=userService.findUser(userId);
        List<Report> reportList=reportRepository.findByFeedId(reportedFeed);

        if(reportList.size()>=4){
            reportedFeed.report();
            feedRepository.save(reportedFeed);
        }

        Report report=Report.of(reportedFeed,reportUser,ReportCategory.valueOf(request.getReportCategory()));

        return reportRepository.save(report);
    }

    public Optional<Report> findReportById(Long reportId){
        return reportRepository.findById(reportId);
    }

    public void deleteAll(){
        reportRepository.deleteAll();
    }
}