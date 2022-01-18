package com.gloomy.server.application.core.response;

public class ErrorDetails<T> {
    private final String message;
    private final T data;

    public ErrorDetails(String message, T data) {
        this.message = message;
        this.data = data;
    }
}
