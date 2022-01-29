package com.gloomy.server.domain.user;

import com.gloomy.server.domain.common.entity.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    User save(User user);
    Optional<User> findFirstByEmailAndJoinStatus(String email, Status joinStatus);
    Optional<User> findByIdAndJoinStatus(long id, Status joinStatus);
}