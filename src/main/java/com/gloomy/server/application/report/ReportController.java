package com.gloomy.server.application.report;

import com.gloomy.server.domain.report.ReportService;
import com.gloomy.server.domain.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/report")
@RequiredArgsConstructor
public class ReportController {

    private final UserService userService;
    private final ReportService reportService;

    @GetMapping("")
    public void reportFeed(@RequestParam Long feedId, @RequestParam String reportCategory){
        Long userId=userService.getMyInfo();
        reportService.saveReport(feedId,userId,reportCategory);
    }


}