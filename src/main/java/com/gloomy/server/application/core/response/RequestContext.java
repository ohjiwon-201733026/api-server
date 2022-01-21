package com.gloomy.server.application.core.response;

import org.springframework.web.context.annotation.RequestScope;

import javax.annotation.ManagedBean;

@ManagedBean
@RequestScope
public class RequestContext {
    private Object requestBody;

    public Object getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(Object requestBody) {
        this.requestBody = requestBody;
    }
}
