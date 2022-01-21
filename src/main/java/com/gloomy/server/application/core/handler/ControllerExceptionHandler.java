package com.gloomy.server.application.core.handler;

import com.gloomy.server.application.core.response.ErrorResponse;
import com.gloomy.server.application.core.response.RequestContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.net.BindException;

@Slf4j
@RestControllerAdvice
public class ControllerExceptionHandler {

    private final RequestContext requestContext;

    public ControllerExceptionHandler(RequestContext requestContext) {
        this.requestContext = requestContext;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public ErrorResponse<?> handlerIllegalArgumentException(IllegalArgumentException e) {
        e.printStackTrace();
        return new ErrorResponse<>(HttpStatus.BAD_REQUEST.value(), e.getMessage(), requestContext.getRequestBody());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BindException.class)
    public ErrorResponse<?> handlerBindException(BindException e) {
        e.printStackTrace();
        return new ErrorResponse<>(HttpStatus.BAD_REQUEST.value(), e.getMessage(), null);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ErrorResponse<?> handlerMethodArgumentMismatchException(MethodArgumentTypeMismatchException e) {
        return new ErrorResponse<>(HttpStatus.BAD_REQUEST.value(), e.getMessage(), e.getParameter());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponse<?> handlerMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        return new ErrorResponse<>(HttpStatus.BAD_REQUEST.value(), e.getMessage(), e.getBindingResult().getAllErrors().get(0).getDefaultMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ErrorResponse<?> handlerHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException e) {
        return new ErrorResponse<>(HttpStatus.BAD_REQUEST.value(), e.getMessage(), e.getSupportedMediaTypes());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ErrorResponse<?> handlerHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        return new ErrorResponse<>(HttpStatus.BAD_REQUEST.value(), e.getMessage(), e.getSupportedHttpMethods());
    }
}
