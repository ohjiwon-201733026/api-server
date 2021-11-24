package com.gloomy.server.core.handler;

import com.gloomy.server.core.response.RestResponse;
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
