package com.gloomy.server.domain.logout;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.gloomy.server.application.core.ErrorMessage.isLogoutToken;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LogoutService {

    private final LogoutRepository logoutRepository;

    public Optional<Logout> getToken(String logoutToken){

        Optional<Logout> logoutOptional=logoutRepository.findByLogoutToken(logoutToken);
        if(logoutOptional.isPresent()) throw new IllegalArgumentException(isLogoutToken);

        return logoutOptional;
    }



}
