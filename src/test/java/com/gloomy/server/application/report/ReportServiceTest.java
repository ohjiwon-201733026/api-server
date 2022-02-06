package com.gloomy.server.application.report;

import com.gloomy.server.application.feed.FeedDTO;
import com.gloomy.server.application.feed.FeedService;
import com.gloomy.server.application.feed.TestFeedDTO;
import com.gloomy.server.application.user.TestUserDTO;
import com.gloomy.server.domain.feed.Feed;
import com.gloomy.server.domain.report.Report;
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

import java.util.Optional;

import static com.gloomy.server.application.user.TestUserDTO.TestUser.makeTestUser;

@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest(properties = {
        "spring.config.location=classpath:test-application.yml,classpath:aws.yml"
})
@Transactional
public class ReportServiceTest {

    @Autowired
    ReportService reportService;
    @Autowired
    UserService userService;
    @Autowired
    FeedService feedService;

    private User testUser;
    private Report testReport;
    private TestFeedDTO testFeedDTO;
    FeedDTO.Request userFeedDTO;

    @BeforeEach
    public void setUp(){
        testUser= makeTestUser();
        testFeedDTO = new TestFeedDTO(testUser, 1);
        userFeedDTO = new FeedDTO.Request(
                testFeedDTO.getCategory(), testFeedDTO.getTitle(), testFeedDTO.getContent());
    }

//    @DisplayName("신고하기")
//    @Test
//    public void reportSave(){
//        User saveUser=userService.createUser(testUser);
//        Feed saveFeed = feedService.createFeed(saveUser.getId(), userFeedDTO);

//        Report report=reportService.saveReport(saveFeed.getId(),saveUser.getId(),"category");

//        Report findReport=reportService.findReportById(report.getId()).get();

//        checkSameReport(findReport,report);

//    }

    private void checkSameReport(Report actual, Report expected){
        Assertions.assertEquals(actual.getFeed_id().getId(),expected.getFeed_id().getId());
        Assertions.assertEquals(actual.getId(),expected.getId());
        Assertions.assertEquals(actual.getUser_id().getId(),expected.getUser_id().getId());
        Assertions.assertEquals(actual.getReportCategory(),expected.getReportCategory());
    }
}