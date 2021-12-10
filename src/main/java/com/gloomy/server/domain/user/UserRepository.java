package com.gloomy.server.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.Repository;

import java.util.Optional;

public interface UserRepository extends Repository<User, Long> {

    User save(User user);

    Optional<User> findFirstByEmail(String email);

    Optional<User> findById(long id);
}
