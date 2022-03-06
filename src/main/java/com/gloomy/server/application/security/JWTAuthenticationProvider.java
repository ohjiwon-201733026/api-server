package com.gloomy.server.application.security;

import com.gloomy.server.domain.jwt.JWTDeserializer;
import com.gloomy.server.domain.jwt.JWTPayload;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import static java.util.Collections.singleton;
import static java.util.Optional.of;

@Slf4j
public class JWTAuthenticationProvider implements AuthenticationProvider {

    private final JWTDeserializer jwtDeserializer;

    public JWTAuthenticationProvider(JWTDeserializer jwtDeserializer) {
        this.jwtDeserializer = jwtDeserializer;
    }

    @Override
    public JWTAuthentication authenticate(Authentication authentication)
            throws AuthenticationException {
        System.out.println("JWTAuthenticationProvider.authenticate");
        try {
            return of(authentication).map(JWTAuthenticationFilter.JWT.class::cast)
                    .map(JWTAuthenticationFilter.JWT::getPrincipal)
                    .map(Object::toString)
                    .map(token -> new JWTAuthentication(token, jwtDeserializer.jwtPayloadFromJWT(token)))
                    .orElseThrow(IllegalArgumentException::new);
        }catch (IllegalArgumentException e){
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public boolean supports(Class<?> authentication) {
        return JWTAuthenticationFilter.JWT.class.isAssignableFrom(authentication);
    }

    @SuppressWarnings("java:S2160")
    private static class JWTAuthentication extends AbstractAuthenticationToken {

        private final JWTPayload jwtPayload;
        private final String token;

        private JWTAuthentication(String token, JWTPayload jwtPayload) {
            super(singleton(new SimpleGrantedAuthority("USER")));
            super.setAuthenticated(true);
            this.jwtPayload = jwtPayload;
            this.token = token;
        }
        @Override
        public JWTPayload getPrincipal() {
            return jwtPayload;
        }

        @Override
        public Object getCredentials() {
            return token;
        }


    }

}