package com.gloomy.server.application.core.response;

import com.gloomy.server.application.core.util.LocalDateTimeUtil;

public class RestResponse<T> {
    private int code;
    private String message;
    private T result;
    private String responseTime;

    public RestResponse(int code, String message, T result) {
        this.code = code;
        this.message = message;
        this.result = result;
        this.responseTime = LocalDateTimeUtil.getLocalDateTimeNowStringPattern("yyyy-MM-dd hh:mm:ss");
    }
}
