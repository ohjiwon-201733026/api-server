package com.gloomy.server.application.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gloomy.server.application.core.response.ErrorResponse;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@NoArgsConstructor
public class ResponseMessageFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try{
            filterChain.doFilter(request,response);
        }
        catch (IllegalArgumentException e){
            setErrorResponse(HttpStatus.FORBIDDEN,response,e,request);
        }
    }

    public void setErrorResponse(HttpStatus status, HttpServletResponse response
            ,Throwable ex,HttpServletRequest request){

        response.setStatus(status.value());
        response.setContentType("application/json");
        ErrorResponse<?> errorResponse=new ErrorResponse(status.value(), ex.getMessage(),null);
        errorResponse.setMessage(ex.getMessage());
        try{
            ObjectMapper objectMapper=new ObjectMapper();
            String json = objectMapper.writeValueAsString(errorResponse);
            response.getWriter().write(json);
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
