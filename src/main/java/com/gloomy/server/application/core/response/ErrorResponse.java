package com.gloomy.server.application.core.response;

import com.gloomy.server.application.core.util.LocalDateTimeUtil;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
public class ErrorResponse<T> {
    private int code;
    private String message;
    private List<T> data;
    private String responseTime;

    public ErrorResponse(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = init(data);
        this.responseTime = LocalDateTimeUtil.getLocalDateTimeNowStringPattern("yyyy-MM-dd hh:mm:ss");
    }

    public ErrorResponse(int code, String message, List<T> data) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.responseTime = LocalDateTimeUtil.getLocalDateTimeNowStringPattern("yyyy-MM-dd hh:mm:ss");
    }

    private List<T> init(T data) {
        List<T> init = new ArrayList<>();
        init.add(data);
        return init;
    }
}
