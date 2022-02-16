package com.gloomy.server.application.report;

import com.gloomy.server.application.feed.FeedDTO;
import com.gloomy.server.application.feed.FeedService;
import com.gloomy.server.application.feed.TestFeedDTO;
import com.gloomy.server.application.user.TestUserDTO;
import com.gloomy.server.domain.common.entity.Status;
import com.gloomy.server.domain.feed.Feed;
import com.gloomy.server.domain.report.Report;
import com.gloomy.server.domain.report.ReportCategory;
import com.gloomy.server.domain.report.ReportRepository;
import com.gloomy.server.domain.report.ReportService;
import com.gloomy.server.domain.user.User;
import com.gloomy.server.domain.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.gloomy.server.application.user.TestUserDTO.TestUser.makeTestUser;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest(properties = {
        "spring.config.location=classpath:test-application.yml,classpath:aws.yml"
})
@Transactional
public class ReportServiceTest {
    @Autowired private ReportRepository reportRepository;
    @Autowired private ReportService reportService;
    @Autowired private UserService userService;
    @Autowired private FeedService feedService;
    private User testUser;
    private Feed testFeed;
    private ReportDTO.Request testReportDto;

    @BeforeEach
    public void setUp(){
        testUser=userService.createUser(TestUserDTO.TestUser.makeTestUser());
        TestFeedDTO feedDTO=new TestFeedDTO(testUser,1);
        testFeed=feedService.createFeed(testUser.getId(),feedDTO.makeUserFeedDTO());
        testReportDto=ReportDTO.Request.of(testFeed.getId(),"ABUSE");
    }

    @Test
    @DisplayName("신고하기 (5회 미만)")
    public void saveReportUnderFive(){
        Report report=reportService.saveReport(testReportDto,testUser.getId());

        Optional<Report> findReport=reportService.findReportById(report.getId());

        assertEquals(report.getId(),findReport.get().getId());
        assertEquals(report.getFeedId().getStatus(), Status.ACTIVE);
    }

    @Test
    @DisplayName("신고하기 (5회이상)")
    public void saveReportMoreThanFive(){
        Report report=null;
        for(int i=0;i<5;++i){
            report=reportService.saveReport(testReportDto,testUser.getId());
        }

        List<Report> reportList=reportRepository.findByFeedId(testFeed);

        assertEquals(5,reportList.size());
        assertEquals(report.getFeedId().getStatus(), Status.INVISIBLE);
    }

    private void isSameReport(Report expect, Report actual){
    }

}