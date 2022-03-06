package com.gloomy.server.domain.notice.fcm;

import com.gloomy.server.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FcmRepository extends JpaRepository<FcmToken, Long> {

    FcmToken save(FcmToken fcmToken);
}
