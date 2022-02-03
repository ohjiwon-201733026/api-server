package com.gloomy.server.domain.blacklList;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LogoutRepository extends JpaRepository<Logout, Long> {

    Logout save(Logout logout);
    Optional<Logout> findByAccessToken(String accessToken);
}
