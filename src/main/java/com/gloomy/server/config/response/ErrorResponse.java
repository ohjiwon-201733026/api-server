package com.gloomy.server.config.response;

import com.gloomy.server.util.LocalDateTimeUtil;
import lombok.Getter;

import java.util.List;

public class ErrorResponse {
    private int code;
    private String message;
    private List<String> errorDetails;
    private String responseTime;


    public ErrorResponse(int code, String message, List<String> errorDetails) {
        this.code = code;
        this.message = message;
        this.errorDetails = errorDetails;
        this.responseTime = LocalDateTimeUtil.getLocalDateTimeNowStringPattern("yyyy-MM-dd hh:mm:ss");
    }
}
