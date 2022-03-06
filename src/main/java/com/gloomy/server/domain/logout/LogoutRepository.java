package com.gloomy.server.domain.logout;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LogoutRepository extends JpaRepository<Logout,Long> {

    Logout save(Logout logout);
    Optional<Logout> findByLogoutToken(String logoutToken);
}
