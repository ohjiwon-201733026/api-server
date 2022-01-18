package com.gloomy.server.application.core.response;

import com.gloomy.server.application.core.util.LocalDateTimeUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class ErrorResponse {
    private final int code;
    private final String message;
    private final List<String> errorDetails;
    private final String responseTime;


    public ErrorResponse(int code, String message, List<String> errorDetails) {
        this.code = code;
        this.message = message;
        this.errorDetails = errorDetails;
        this.responseTime = LocalDateTimeUtil.getLocalDateTimeNowStringPattern("yyyy-MM-dd hh:mm:ss");
    }
}
