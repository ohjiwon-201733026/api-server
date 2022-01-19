package com.gloomy.server.application.core.response;

import com.gloomy.server.application.core.util.LocalDateTimeUtil;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ErrorResponse<T> {
    private int code;
    private String message;
    private ErrorDetails<T> errorDetails;
    private String responseTime;

    public ErrorResponse(int code, String message, String detailMessage,T data) {
        this.code = code;
        this.message = message;
        this.errorDetails = new ErrorDetails<>(detailMessage,data);
        this.responseTime = LocalDateTimeUtil.getLocalDateTimeNowStringPattern("yyyy-MM-dd hh:mm:ss");
    }
}
