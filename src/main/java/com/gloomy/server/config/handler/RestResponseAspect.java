package com.gloomy.server.config.handler;

import com.gloomy.server.config.response.RestResponse;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class RestResponseAspect {
    @Around("execution(* com.gloomy.server..*.*(..))")
    public RestResponse<Object> restResponseHandler(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        return new RestResponse<>(HttpStatus.OK.value(), "성공하였습니다.", proceedingJoinPoint.proceed());
    }
}
