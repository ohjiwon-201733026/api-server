package com.gloomy.server.application.security;

import com.gloomy.server.domain.jwt.JWTDeserializer;
import com.gloomy.server.domain.logout.LogoutRepository;
import com.gloomy.server.domain.logout.LogoutService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@EnableConfigurationProperties(SecurityConfigurationProperties.class)
@Configuration
@RequiredArgsConstructor
public class SecurityConfiguration extends WebSecurityConfigurerAdapter implements WebMvcConfigurer {

    private final LogoutService logoutService;
    private final SecurityConfigurationProperties properties;
    private final JWTDeserializer jwtDeserializer;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.headers().frameOptions().sameOrigin();
        http.csrf().disable();
        http.cors();
        http.logout().disable();
        http.addFilterBefore(new JwtExceptionFilter(),UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new JWTAuthenticationFilter(logoutService), UsernamePasswordAuthenticationFilter.class);
        http.authorizeRequests()
                .anyRequest().permitAll()
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .formLogin()
                .disable();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth){
        auth.authenticationProvider(jwtAuthenticationProvider(jwtDeserializer));
    }


    @Bean
    JWTAuthenticationProvider jwtAuthenticationProvider(JWTDeserializer jwtDeserializer) {
        return new JWTAuthenticationProvider(jwtDeserializer);
    }


    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedHeaders("GET", "POST", "DELETE", "PUT","PATCH")
                .allowedOrigins(properties.getAllowedOrigins().toArray(new String[0]))
                .allowedHeaders("*")
                .allowCredentials(true);
    }


}

@ConstructorBinding
@ConfigurationProperties("security")
class SecurityConfigurationProperties {
    private final List<String> allowedOrigins;

    public SecurityConfigurationProperties(List<String> allowedOrigins) {
        this.allowedOrigins = allowedOrigins;
    }

    public List<String> getAllowedOrigins (){
        return allowedOrigins;
    }
}