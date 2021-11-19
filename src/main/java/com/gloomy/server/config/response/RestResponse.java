package com.gloomy.server.config.response;

import com.gloomy.server.util.LocalDateTimeUtil;

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
