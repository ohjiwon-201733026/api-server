package com.gloomy.server.application.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gloomy.server.application.core.response.ErrorResponse;
import com.gloomy.server.application.redis.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.ObjectUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.gloomy.server.application.core.ErrorMessage.isLogoutToken;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Slf4j
@RequiredArgsConstructor
class JWTAuthenticationFilter extends OncePerRequestFilter {

    private final RedisService redisService;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try{
            String s = request.getHeader(AUTHORIZATION);
            if (s == null) {
                SecurityContextHolder.getContext().setAuthentication(null);
            } else {
                String token = s.substring("Bearer ".length());
                String isLogout=redisService.getValue(token);

                if (!ObjectUtils.isEmpty(isLogout)) { // 블랙리스트에 없을 경우
                    setErrorResponse(HttpStatus.BAD_REQUEST, response, new Exception(isLogoutToken), request);
                    return;
                }

                SecurityContextHolder.getContext().setAuthentication(new JWT(token));
            }
            filterChain.doFilter(request, response);
        }catch (IllegalArgumentException e){
            setErrorResponse(HttpStatus.FORBIDDEN,response,e,request);
        }

    }

    public void setErrorResponse(HttpStatus status, HttpServletResponse response
            ,Throwable ex,HttpServletRequest request){

        response.setStatus(status.value());
        response.setContentType("application/json");
        ErrorResponse<?> errorResponse=new ErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage(),null);
        errorResponse.setMessage(ex.getMessage());
        try{
            ObjectMapper objectMapper=new ObjectMapper();
            String json = objectMapper.writeValueAsString(errorResponse);
            response.getWriter().write(json);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    @SuppressWarnings("java:S2160")
    static class JWT extends AbstractAuthenticationToken {

        private final String token;

        private JWT(String token) {
            super(null);
            this.token = token;
        }

        @Override
        public Object getPrincipal() {
            return token;
        }

        @Override
        public Object getCredentials() {
            return null;
        }
    }
}