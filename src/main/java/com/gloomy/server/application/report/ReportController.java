package com.gloomy.server.application.report;

import com.gloomy.server.domain.report.ReportService;
import com.gloomy.server.domain.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ReportController {

    private final UserService userService;
    private final ReportService reportService;

    @PostMapping("/report/feed")
    public void reportFeed(@Validated @RequestBody ReportDTO.Request request){
        Long userId=userService.getMyInfo();
        reportService.saveReport(request,userId);
    }


}